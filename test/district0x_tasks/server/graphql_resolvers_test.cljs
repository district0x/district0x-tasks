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
            [district0x-tasks.server.syncer :as syncer]
            [district0x-tasks.utils :as utils :refer [accounts]]))

(use-fixtures :once utils/prepare-contracts)

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
  (doseq [tx-hash [(district-tasks/add-task "Active 0" feature-in-seconds true {})
                   (district-tasks/add-task "Task 1" feature-in-seconds true {})
                   (district-tasks/add-task "Task 2" feature-in-seconds true {})
                   (district-tasks/add-task "Task 3" feature-in-seconds true {})]]
    (contract->syncer tx-hash :district-tasks :LogAddTask))

  (-> (district-tasks/update-task 0 "Not active 0" feature-in-seconds false {})
      (contract->syncer :district-tasks :LogUpdateTask))

  (doseq [tx-hash [(district-tasks/add-bid 1 "Bid 1.0" "http://example.com/" "Bid to remove" 0.01 {})
                   (district-tasks/add-bid 1 "Bid 1.1" "http://example.com/" "Bid description" 0.11 {:from (second accounts)})
                   (district-tasks/add-bid 1 "Bid 1.2" "http://example.com/" "Bid description" 0.12 {})
                   (district-tasks/add-bid 2 "Bid 2.1" "http://example.com/" "Bid description" 1.32 {})
                   (district-tasks/add-bid 3 "Bid 3.0" "http://example.com/" "Bid description" 278 {})
                   (district-tasks/add-bid 3 "Bid 3.1" "http://example.com/" "Bid description" 254 {})
                   (district-tasks/add-bid 3 "Bid 3.2" "http://example.com/" "Bid description" 289.35 {})]]
    (contract->syncer tx-hash :district-tasks :LogAddBid))

  (-> (district-tasks/remove-bid 1 0 {})
      (contract->syncer :district-tasks :LogRemoveBid))


  (doseq [tx-hash [(district-tasks/add-voter 1 1 {:from (nth accounts 0)})
                   (district-tasks/add-voter 1 1 {:from (nth accounts 1)})
                   (district-tasks/add-voter 1 2 {:from (nth accounts 0)})
                   (district-tasks/add-voter 3 2 {:from (nth accounts 0)})
                   (district-tasks/add-voter 3 2 {:from (nth accounts 1)})
                   (district-tasks/add-voter 3 2 {:from (nth accounts 2)})
                   (district-tasks/add-voter 3 2 {:from (nth accounts 4)})]]
    (contract->syncer tx-hash :district-tasks :LogAddVoter))

  (db/upsert-voter->tokens! {:voter/address (nth accounts 0)
                             :voter/tokens-amount 10})
  (db/upsert-voter->tokens! {:voter/address (nth accounts 1)
                             :voter/tokens-amount 100})
  (db/upsert-voter->tokens! {:voter/address (nth accounts 2)
                             :voter/tokens-amount 200})
  (db/upsert-voter->tokens! {:voter/address (nth accounts 4)
                             :voter/tokens-amount 300})

  ;(dev/print-db)

  (is (= (->> (graphql/run-query
                {:queries [[:active-tasks
                            [:task/id :task/title :task/is-active :task/bidding-ends-on
                             [:task/bids [:bid/id :bid/creator :bid/title :bid/url :bid/description :bid/amount :bid/votes-sum]]]]]})
              :data
              :active-tasks)
         [{:task/id "1" :task/title "Task 1" :task/is-active true :task/bidding-ends-on feature-in-seconds
           :task/bids [{:bid/id "1" :bid/creator (second accounts) :bid/title "Bid 1.1" :bid/url "http://example.com/" :bid/description "Bid description" :bid/amount 0.11 :bid/votes-sum 110}
                       {:bid/id "2" :bid/creator (first accounts) :bid/title "Bid 1.2" :bid/url "http://example.com/" :bid/description "Bid description" :bid/amount 0.12 :bid/votes-sum 10}]}
          {:task/id "2" :task/title "Task 2" :task/is-active true :task/bidding-ends-on feature-in-seconds
           :task/bids [{:bid/id "0" :bid/creator (first accounts) :bid/title "Bid 2.1" :bid/url "http://example.com/" :bid/description "Bid description" :bid/amount 1.32 :bid/votes-sum 0}]}
          {:task/id "3" :task/title "Task 3" :task/is-active true :task/bidding-ends-on feature-in-seconds
           :task/bids [{:bid/id "0" :bid/creator (first accounts) :bid/title "Bid 3.0" :bid/url "http://example.com/" :bid/description "Bid description" :bid/amount 278 :bid/votes-sum 0}
                       {:bid/id "1" :bid/creator (first accounts) :bid/title "Bid 3.1" :bid/url "http://example.com/" :bid/description "Bid description" :bid/amount 254 :bid/votes-sum 0}
                       {:bid/id "2" :bid/creator (first accounts) :bid/title "Bid 3.2" :bid/url "http://example.com/" :bid/description "Bid description" :bid/amount 289.35 :bid/votes-sum 610}]}])
      "active-tasks")

  (is (->> (graphql/run-query
             {:queries [[:active-tasks [:task/created-at]]]})
           :data
           :active-tasks
           (every? :task/created-at))
      "task/created-at")

  (is (->> (graphql/run-query
             {:queries [[:bids {:task/id 1} [:bid/created-at]]]})
           :data
           :bids
           (every? :bid/created-at))
      "bid/created-at"))
