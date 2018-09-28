(ns district0x-tasks.server.db-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests use-fixtures async]]
            [district0x-tasks.server.dev :as dev]
            [district0x-tasks.server.deployer :as deployer]
            [district0x-tasks.server.db :as db]))

(dev/-main)
(deployer/deploy-tasks-contract! {})

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
            :bid/created-at 1514711111})))

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
           '({:task/id 123, :bid/id 111, :voter/address "0x71651917485a651bb9871d62b54507afcca6ca03"}
              {:task/id 123, :bid/id 111, :voter/address "0x71651917485a651bb9871d62b54507afcca11111"}))))

  (district0x-tasks.server.dev/print-db))
