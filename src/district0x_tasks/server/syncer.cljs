(ns district0x-tasks.server.syncer
  (:require
    [bignumber.core :as bn]
    [camel-snake-kebab.core :as cs :include-macros true]
    [cljs-web3.core :as web3]
    [cljs-web3.eth :as web3-eth]
    [district.server.config :refer [config]]
    [district.server.smart-contracts :refer [replay-past-events]]
    [district.server.web3 :refer [web3]]
    [district.web3-utils :as web3-utils]
    [district0x-tasks.server.db :as db]
    [district0x-tasks.server.deployer]
    [district0x-tasks.server.generator]
    [mount.core :as mount :refer [defstate]]
    [taoensso.timbre :as log]
    [cljs-ipfs-api.files :as ifiles]
    [print.foo :refer [look] :include-macros true]
    [district0x-tasks.server.contracts.district-tasks :as district-tasks]
    [contracts.mini-me-token :as mini-me-token]
    [district.server.smart-contracts :refer [contract-call]]
    [clojure.set :refer [rename-keys]]))

(defmulti process-event #(:event %))

(defmethod process-event "LogAddTask" [{:keys [args] :as event}]
  (db/insert-task! {:task/id (:id args)
                    :task/title (:title args)
                    :task/bidding-ends-on (:bidding-ends-on args)
                    :task/active? (:active? args)
                    :task/created-at (:timestamp event)}))

(defmethod process-event "LogUpdateTask" [{:keys [args] :as event}]
  (db/update-task! {:task/id (:id args)
                    :task/title (:title args)
                    :task/bidding-ends-on (:bidding-ends-on args)
                    :task/active? (:active? args)}))

(defmethod process-event "LogAddBid" [{:keys [args] :as event}]
  (db/insert-bid! {:task/id (:task-id args)
                   :bid/id (:bid-id args)
                   :bid/creator (:creator args)
                   :bid/title (:title args)
                   :bid/url (:url args)
                   :bid/description (:description args)
                   :bid/amount (:amount args)
                   :bid/created-at (:timestamp event)}))

(defmethod process-event "LogRemoveBid" [{:keys [args] :as event}]
  (db/remove-bid! {:task/id (:task-id args)
                   :bid/id (:bid-id args)}))

(defmethod process-event "LogAddVoter" [{:keys [args] :as event}]
  (db/insert-voter! {:task/id (:task-id args)
                     :bid/id (:bid-id args)
                     :voter/address (:voter args)})
  (db/upsert-voter->tokens! {:voter/address (:voter args)
                             :voter/tokens-balance (-> (mini-me-token/balance-of (:voter args) {})
                                                       :tokens-balance)}))

(defn dispatch-event [err event]
  (let [event (district-tasks/event->cljs event)
        event (merge event
                     (-> (web3-eth/get-block @web3 (:block-hash event) false)
                         (select-keys [:number :timestamp])
                         (rename-keys {:number :block-number})))]
    (log/debug "Dispatching " event)
    (process-event event)))

(declare start)
(declare stop)

(def watchers [{:watcher [:district-tasks :LogAddTask]
                :on-event dispatch-event}
               {:watcher [:district-tasks :LogUpdateTask]
                :on-event dispatch-event}
               {:watcher [:district-tasks :LogAddBid]
                :on-event dispatch-event}
               {:watcher [:district-tasks :LogRemoveBid]
                :on-event dispatch-event}
               {:watcher [:district-tasks :LogAddVoter]
                :on-event dispatch-event}])

(defstate ^{:on-reload :noop} syncer
  :start (start (merge (:syncer @config)
                       (:syncer (mount/args))))
  :stop (stop syncer))

(defn start [{:keys [:initial-param-query] :as opts}]
  (when-not (web3/connected? @web3)
    (throw (js/Error. "Can't connect to Ethereum node")))

  (when-not (= ::db/started @db/district0x-tasks-db)
    (throw (js/Error. "Database module has not started")))

  (let [last-block-number (web3-eth/block-number @web3)
        watchers->contracts-call (->> watchers
                                      (map (fn [m]
                                             (update m :watcher #(apply partial contract-call %)))))]
    (concat
      ;; Replay every past events (from block 0 to (dec last-block-number))
      (when (pos? last-block-number)
        (->> watchers->contracts-call
             (map (fn [{:keys [watcher on-event]}]
                    (-> (watcher {} {:from-block 0 :to-block (dec last-block-number)})
                        (replay-past-events on-event))))
             (doall)))

      ;; Filters that will watch for last event and dispatch
      (->> watchers->contracts-call
           (map (fn [{:keys [watcher on-event]}]
                  (watcher {} "latest" on-event)))
           (doall)))))

(defn stop [syncer]
  (doseq [filter (remove nil? @syncer)]
    (web3-eth/stop-watching! filter (fn [err]))))
