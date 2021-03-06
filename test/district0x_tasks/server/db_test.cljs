(ns district0x-tasks.server.db-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests use-fixtures async]]
            [district0x-tasks.server.db :as db]
            [district.server.web3 :refer [web3]]
            [district0x-tasks.utils :as utils :refer [accounts]]))

(use-fixtures :once utils/prepare-contracts)

(deftest db-test
  (testing "SQL task"
    (is (= 1 (-> (db/insert-task! {:task/id 123
                                   :task/title "Task title"
                                   :task/bidding-ends-on 1514764800
                                   :task/active? false
                                   :task/created-at 1514700000})
                 :changes)))
    (is (= 1 (-> (db/update-task! {:task/id 123
                                   :task/title "Task title"
                                   :task/bidding-ends-on 1514764999
                                   :task/active? true})
                 :changes)))
    (is (= (db/get-task {:task/id 123} [:*])
           {:task/id 123
            :task/title "Task title"
            :task/bidding-ends-on 1514764999
            :task/active? true
            :task/created-at 1514700000})))

  (testing "SQL bids"
    (is (= 1 (-> (db/insert-bid! {:task/id 123
                                  :bid/id 111
                                  :bid/creator "0x46E286cb00c5e7D5949D18e767447E513d001BBA"
                                  :bid/title "Bid title"
                                  :bid/url "http://example.com"
                                  :bid/description "Bid description"
                                  :bid/amount 243.65
                                  :bid/created-at 1514711111})
                 :changes)))
    (is (= 1 (-> (db/update-bid! {:task/id 123
                                  :bid/id 111
                                  :bid/votes-sum 789})
                 :changes)))
    (is (= (db/get-bid {:task/id 123
                        :bid/id 111} [:*])
           {:task/id 123
            :bid/id 111
            :bid/creator "0x46E286cb00c5e7D5949D18e767447E513d001BBA"
            :bid/title "Bid title"
            :bid/url "http://example.com"
            :bid/description "Bid description"
            :bid/amount 243.65
            :bid/votes-sum 789
            :bid/created-at 1514711111}))
    (db/insert-bid! {:task/id 999
                     :bid/id 1
                     :bid/creator "0x46E286cb00c5e7D5949D18e767447E513d001BBA"
                     :bid/title "remove bid"
                     :bid/url "http://example.com"
                     :bid/description "Bid description"
                     :bid/amount 1
                     :bid/created-at 1514711111})
    (is (= 1 (-> (db/remove-bid! {:task/id 999
                                  :bid/id 1})
                 :changes))))

  (testing "SQL voters"
    (is (= 1 (-> (db/insert-voter! {:task/id 123
                                    :bid/id 111
                                    :voter/address "0x71651917485a651bb9871d62b54507afcca6ca03"})
                 :changes)))
    (is (= 1 (-> (db/insert-voter! {:task/id 123
                                    :bid/id 111
                                    :voter/address "0x71651917485a651bb9871d62b54507afcca11111"})
                 :changes)))
    (is (= (db/get-voters {:task/id 123
                           :bid/id 111})
           '({:task/id 123, :bid/id 111, :voter/address "0x71651917485a651bb9871d62b54507afcca11111"}
              {:task/id 123, :bid/id 111, :voter/address "0x71651917485a651bb9871d62b54507afcca6ca03"}))))

  (testing "SQL voters->tokens"
    (is (= 1 (-> (db/upsert-voter->tokens! {:voter/address "0x71651917485a651bb9871d62b54507afcca6ca03"
                                            :voter/tokens-balance 456})
                 :changes)))
    (is (= 1 (-> (db/upsert-voter->tokens! {:voter/address "0x71651917485a651bb9871d62b54507afcca6ca03"
                                            :voter/tokens-balance 444})
                 :changes)))
    (is (= (db/get-voter->tokens {:voter/address "0x71651917485a651bb9871d62b54507afcca6ca03"} [:*])
           {:voter/address "0x71651917485a651bb9871d62b54507afcca6ca03"
            :voter/tokens-balance 444})))

  (testing "SQL bid sum of tokens"
    (db/insert-bid! {:task/id 124
                     :bid/id 111
                     :bid/creator "0x000000000000000000000000000000000creator"
                     :bid/title "Sum"
                     :bid/url "http://example.com/sum"
                     :bid/description "Sum description"
                     :bid/amount 477.77
                     :bid/created-at 1514700000})
    (db/insert-voter! {:task/id 124
                       :bid/id 111
                       :voter/address "0x0000000000000000000000000000000000000000"})
    (db/insert-voter! {:task/id 124
                       :bid/id 111
                       :voter/address "0x0000000000000000000000000000000000000001"})
    (db/upsert-voter->tokens! {:voter/address "0x0000000000000000000000000000000000000000"
                               :voter/tokens-balance 100})
    (db/upsert-voter->tokens! {:voter/address "0x0000000000000000000000000000000000000001"
                               :voter/tokens-balance 50})
    (is (= 150 (db/sum-voters->tokens {:task/id 124
                                       :bid/id 111}))))

  #_(district0x-tasks.server.dev/print-db))
