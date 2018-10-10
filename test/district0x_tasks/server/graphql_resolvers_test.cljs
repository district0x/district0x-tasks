(ns district0x-tasks.server.graphql-resolvers-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests use-fixtures async]]
            [district.server.graphql :as graphql]
            [district0x-tasks.server.contracts.district-tasks :as district-tasks]
            [contracts.mini-me-token :as mini-me-token]
            [district.server.smart-contracts :refer [contract-event-in-tx]]
            [district0x-tasks.server.syncer :as syncer]
            [district0x-tasks.utils :as utils :refer [accounts inc-eth-time! feature-in-seconds]]))

(use-fixtures :once utils/prepare-contracts)

(def watchers
  (->> syncer/watchers
       (map #(vector (get-in % [:watcher 1]) (:on-event %)))
       (into {})))

(defn contract->syncer [tx-hash contract-key event-name]
  (let [on-event (get watchers event-name)]
    (->> (contract-event-in-tx tx-hash contract-key event-name {})
         (on-event nil))))

(deftest graphql-test
  (let [feature-timestamp (feature-in-seconds)]

    (doseq [tx-hash [(district-tasks/add-task "Active 0" feature-timestamp true {})
                     (district-tasks/add-task "Task 1" feature-timestamp true {})
                     (district-tasks/add-task "Task 2" feature-timestamp true {})
                     (district-tasks/add-task "Task 3" feature-timestamp true {})]]
      (contract->syncer tx-hash :district-tasks :LogAddTask))

    (-> (district-tasks/update-task 0 "Not active 0" feature-timestamp false {})
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

    ;; prepare tokens, no events for this, just call contract
    (mini-me-token/generate-tokens (nth accounts 0) 110 {:gas 130000})
    (mini-me-token/generate-tokens (nth accounts 1) 150 {:gas 130000})
    (mini-me-token/generate-tokens (nth accounts 2) 150 {:gas 130000})
    (mini-me-token/generate-tokens (nth accounts 3) 200 {:gas 130000})

    (doseq [tx-hash [(district-tasks/add-voter 1 1 {:from (nth accounts 0)})
                     (district-tasks/add-voter 1 1 {:from (nth accounts 1)})
                     (district-tasks/add-voter 1 2 {:from (nth accounts 0)})
                     (district-tasks/add-voter 3 2 {:from (nth accounts 0)})
                     (district-tasks/add-voter 3 2 {:from (nth accounts 1)})
                     (district-tasks/add-voter 3 2 {:from (nth accounts 2)})
                     (district-tasks/add-voter 3 2 {:from (nth accounts 3)})]]
      (contract->syncer tx-hash :district-tasks :LogAddVoter))

    (mini-me-token/enable-transfer true {})
    (doseq [tx-hash [(mini-me-token/transfer-tokens (nth accounts 3) 100 {:from (nth accounts 0)})
                     (mini-me-token/transfer-tokens (nth accounts 2) 50 {:from (nth accounts 1)})]]
      (contract->syncer tx-hash :mini-me-token :Transfer))

    ;(dev/print-db)

    (is (= (->> (graphql/run-query
                  {:queries [[:active-tasks
                              [:task/id :task/title :task/is-active :task/bidding-ends-on
                               [:task/bids [:bid/id :bid/creator :bid/title :bid/url :bid/description :bid/amount :bid/votes-sum]]]]]})
                :data
                :active-tasks)
           [{:task/id "1" :task/title "Task 1" :task/is-active true :task/bidding-ends-on feature-timestamp
             :task/bids [{:bid/id "1" :bid/creator (second accounts) :bid/title "Bid 1.1" :bid/url "http://example.com/" :bid/description "Bid description" :bid/amount 0.11 :bid/votes-sum 110}
                         {:bid/id "2" :bid/creator (first accounts) :bid/title "Bid 1.2" :bid/url "http://example.com/" :bid/description "Bid description" :bid/amount 0.12 :bid/votes-sum 10}]}
            {:task/id "2" :task/title "Task 2" :task/is-active true :task/bidding-ends-on feature-timestamp
             :task/bids [{:bid/id "0" :bid/creator (first accounts) :bid/title "Bid 2.1" :bid/url "http://example.com/" :bid/description "Bid description" :bid/amount 1.32 :bid/votes-sum 0}]}
            {:task/id "3" :task/title "Task 3" :task/is-active true :task/bidding-ends-on feature-timestamp
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
        "bid/created-at")))
