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

(defmulti process-event (fn [contract-type ev]
                          [contract-type (:event-type ev)]))

(defmethod process-event :default [contract-type {:keys [:registry-entry :timestamp] :as ev}]
  (println "###################!!!!!!##################")
  (println (pr-str contract-type))
  (println (pr-str ev)))

#_(defmethod process-event [:contract/param-change :constructed]
  [contract-type {:keys [:registry-entry :timestamp] :as ev}]
  #_(try-catch
    (add-registry-entry registry-entry timestamp)
    (add-param-change registry-entry)))

(defn dispatch-event [contract-type err {:keys [args event] :as a}]
  (let [event-type (cond
                     (:event-type args) (cs/->kebab-case-keyword (web3-utils/bytes32->str (:event-type args)))
                     event (cs/->kebab-case-keyword event))
        ev (-> args
               (assoc :contract-address (:address a))
               (assoc :event-type event-type)
               (update :timestamp bn/number)
               (update :version bn/number))]
    (log/info (str "Dispatching " contract-type " " event-type) {:ev ev} ::dispatch-event)
    (process-event contract-type ev)))

(declare start)
(declare stop)

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
        ;watchers [{:watcher (partial eternal-db/change-applied-event [:param-change-registry-db])
        ;           :on-event #(dispatch-event :contract/eternal-db %1 %2)}
        ;          {:watcher (partial registry/registry-entry-event [:meme-registry :meme-registry-fwd])
        ;           :on-event #(dispatch-event :contract/meme %1 %2)}
        ;          {:watcher (partial registry/registry-entry-event [:param-change-registry :param-change-registry-fwd])
        ;           :on-event #(dispatch-event :contract/param-change %1 %2)}
        ;          {:watcher meme-auction-factory/meme-auction-event
        ;           :on-event #(dispatch-event :contract/meme-auction %1 %2)}
        ;          {:watcher meme-token/meme-token-transfer-event
        ;           :on-event #(dispatch-event :contract/meme-token %1 %2)}]
        watchers [{:watcher (fn [& args]
                              (apply contract-call :district-tasks :LogAddTask args))
                   :on-event (partial println :contract/add-task)}
                  #_{:watcher (partial contract-call :district-tasks :LogAddTask)
                   :on-event (partial dispatch-event :contract/add-task)
                   #_(comp (partial dispatch-event :contract/add-task)
                           district-tasks/event->cljs)}]]
    (concat
      ;; Replay every past events (from block 0 to (dec last-block-number))
      (when (pos? last-block-number)
        (->> watchers
             (map (fn [{:keys [watcher on-event]}]
                    (-> (apply watcher [{} {:from-block 0 :to-block (dec last-block-number)}])
                        (replay-past-events on-event))))
             (doall)))

      ;; Filters that will watch for last event and dispatch
      (->> watchers
           (map (fn [{:keys [watcher on-event]}]
                  (apply watcher [{} "latest" on-event])))
           (doall)))))

(defn stop [syncer]
  (doseq [filter (remove nil? @syncer)]
    (web3-eth/stop-watching! filter (fn [err]))))
