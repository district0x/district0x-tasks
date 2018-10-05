(ns district0x-tasks.server.graphql-resolvers-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests use-fixtures async]]
            [district0x-tasks.server.dev :as dev]
            [district0x-tasks.server.deployer :as deployer]
            [district0x-tasks.server.graphql-resolvers :as resolver]
            [district.server.graphql :as graphql]
            [district0x-tasks.server.db :as db]
            [district0x-tasks.server.contracts.district-tasks :as district-tasks]
            [district.server.web3 :refer [web3]]
            [cljs-web3.eth :as web3-eth]
            [district.server.smart-contracts :refer [replay-past-events contract-event-in-tx contract-call]]
            [district0x-tasks.server.syncer :as syncer]))

(dev/-main)

(def accounts (web3-eth/accounts @web3))

(deployer/deploy {:from (first accounts)})
(dev/resync)

(def watchers
  (->> syncer/watchers
       (map #(vector (get-in % [:watcher 1]) (:on-event %)))
       (into {})))

(defn contract->syncer [tx-hash contract-key event-name]
  (let [on-event (get watchers event-name)]
    (->> (contract-event-in-tx tx-hash contract-key event-name {})
         (on-event nil))))

(def feature-in-seconds
  (-> (.getTime (js/Date.))
      (quot 1000)
      (inc)
      (+ 600)))

(deftest graphql-test
  (doseq [tx-hash [(district-tasks/add-task "Not active 0" feature-in-seconds false {})
                   (district-tasks/add-task "Task 1" feature-in-seconds true {})
                   (district-tasks/add-task "Task 2" feature-in-seconds true {})
                   (district-tasks/add-task "Task 3" feature-in-seconds true {})]]
    (contract->syncer tx-hash :district-tasks :LogAddTask))

  (doseq [tx-hash [(district-tasks/add-bid 1 "Bid 1.0" "http://example.com/" "Bid description" 0.01 {})
                   (district-tasks/add-bid 1 "Bid 1.1" "http://example.com/" "Bid description" 0.08 {})
                   (district-tasks/add-bid 2 "Bid 2.1" "http://example.com/" "Bid description" 1.32 {})
                   (district-tasks/add-bid 3 "Bid 3.0" "http://example.com/" "Bid description" 278 {})
                   (district-tasks/add-bid 3 "Bid 3.1" "http://example.com/" "Bid description" 254 {})
                   (district-tasks/add-bid 3 "Bid 3.2" "http://example.com/" "Bid description" 289.35 {})]]
    (contract->syncer tx-hash :district-tasks :LogAddBid))

  (doseq [tx-hash [(district-tasks/add-voter 1 0 {:from (nth accounts 0)})
                   (district-tasks/add-voter 1 0 {:from (nth accounts 1)})
                   (district-tasks/add-voter 1 1 {:from (nth accounts 0)})
                   (district-tasks/add-voter 3 2 {:from (nth accounts 0)})
                   (district-tasks/add-voter 3 2 {:from (nth accounts 1)})
                   (district-tasks/add-voter 3 2 {:from (nth accounts 2)})
                   (district-tasks/add-voter 3 2 {:from (nth accounts 3)})]]
    (contract->syncer tx-hash :district-tasks :LogAddVoter))

  (dev/print-db)
  )

#_(println (pr-str (graphql/run-query
                     {:queries [[:active-tasks
                                 [:task/id :task/title
                                  [:task/bids [:bid/id]]]]]})))

;(println (pr-str (graphql/run-query "{activeTasks {task_id, task_title}}")))
