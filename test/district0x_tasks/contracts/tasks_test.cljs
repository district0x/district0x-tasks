(ns district0x-tasks.contracts.tasks-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests use-fixtures async]]
            [cljs-web3.eth :as web3-eth]
            [bignumber.core :as bn]
            [district.server.web3 :refer [web3]]
            [district.server.smart-contracts :refer [contract-call]]
            [district0x-tasks.server.dev :as dev]
            [district0x-tasks.server.deployer :as deployer]
            [district0x-tasks.server.contracts.district-tasks :as district-tasks]
            [district.server.smart-contracts :refer [replay-past-events]]
            [cljs-web3.evm :as web3-evm]))

(dev/-main)

(def accounts (web3-eth/accounts @web3))

;; {:from (first accounts)} is default account
(deployer/deploy-tasks-contract! {})

;; ganache-cli@6.1.8 block.timestamp is now instead of timestamp of last block
(defn now-in-seconds []
  (-> (.getTime (js/Date.))
      (quot 1000)
      (inc)))

#_(defn last-block-timestamp []
    (->> (web3-eth/block-number @web3)
         (web3-eth/get-block @web3)
         :timestamp))

;; temporary solution, because ganache-cli block.timestamp is now instead of last block timestamp
(defn sleep [seconds]
  (let [deadline (-> (* seconds 1000)
                     (+ (.getTime (js/Date.))))]
    (while (> deadline (.getTime (js/Date.))))))



(deftest tasks-test
  (let [bidding-ends-on (+ (now-in-seconds) 10)]
    (testing "Only Owner can addTasks, updateActive and updateBiddingEndsOn"
      (is (thrown? js/Error
                   (district-tasks/add-task "Title" bidding-ends-on true {:from (last accounts)}))
          "not an owner, should Error")
      (is (district-tasks/add-task "Title" bidding-ends-on true {:from (first accounts)})
          "the owner, should pass")
      (is (= 1 (district-tasks/count-tasks {})))
      (is (= (district-tasks/get-task 0 {})
             {:bidding-ends-on bidding-ends-on
              :active? true}))))

  (let [bidding-ends-on (+ (now-in-seconds) 10)
        bidding-ends-on2 (+ bidding-ends-on 1)]
    (testing "Task update test"
      (is (district-tasks/add-task "Update test" bidding-ends-on false {}))
      (is (district-tasks/update-task-active 1 true {}))
      (is (district-tasks/update-task-bidding-ends-on 1 bidding-ends-on2 {}))
      (is (= (district-tasks/get-task 1 {})
             {:bidding-ends-on bidding-ends-on2
              :active? true}))))

  (testing "Add Bids to created tasks[0]"
    (is (district-tasks/add-bid 0 "Bid title" "Bid description" {}))
    (is (= 1 (district-tasks/count-bids 0 {})))
    (is (= (district-tasks/get-bid 0 0 {})
           {:creator (first accounts)})))

  (testing "Add Voters to created tasks[0]->bids[0]"
    (is (district-tasks/add-voter 0 0 {}))
    (is (thrown? js/Error (district-tasks/add-voter 0 0 {}))
        "should fail, because voter already voted")
    (is (= 1 (district-tasks/count-voters 0 0 {})))
    (is (true? (district-tasks/voted? 0 0 (first accounts) {})))
    (is (false? (district-tasks/voted? 0 0 (last accounts) {})))

    (is (= (district-tasks/get-voters 0 0 {})
           [(first accounts)])))

  (testing "BiddingEndsOn, Active testing"
    (district-tasks/add-task "Title" (+ (now-in-seconds) 1) false {})
    (is (thrown? js/Error
                 (district-tasks/add-bid 2 "Bid title" "Bid description" {}))
        "should not pass, because task is not active")
    (district-tasks/update-task-active 2 true {})
    ;; temporary solution, because ganache-cli block.timestamp is now instead of last block timestamp
    (sleep 1)
    ;; when block.timestamp will became last block timestamp
    ;(web3-evm/increase-time! @web3 [1])
    ;(web3-evm/mine! @web3)
    (is (thrown? js/Error
                 (district-tasks/add-bid 2 "Bid title" "Bid description" {}))
        "should not pass, because BiddingEndsOn expired")))
