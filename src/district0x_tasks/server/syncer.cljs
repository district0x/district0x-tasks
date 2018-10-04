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
    [district.server.smart-contracts :refer [contract-call]]))

(defmulti process-event #(:event %))

(defmethod process-event :default [event]
  (println "DefaultEvent" (pr-str event)))

(defmethod process-event "LogAddTask" [event]
  (println "LogAddTask" (pr-str event)))

(defmethod process-event "LogUpdateTask" [event]
  (println "LogUpdateTask" (pr-str event)))

(defmethod process-event "LogAddBid" [event]
  (println "LogAddBid" (pr-str event)))

(defmethod process-event "LogRemoveBid" [event]
  (println "LogRemoveBid" (pr-str event)))

(defmethod process-event "LogAddVoter" [event]
  (println "LogAddVoter" (pr-str event)))

(defn dispatch-event [err event]
  (let [event (district-tasks/event->cljs event)]
    (log/info "Dispatching " event)
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
