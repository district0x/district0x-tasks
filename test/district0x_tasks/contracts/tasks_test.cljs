(ns district0x-tasks.contracts.tasks-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests use-fixtures async]]
            [cljs.core.async :refer [<! timeout]]
            [cljs-web3.eth :as web3-eth]
            [district.server.web3 :refer [web3]]
            [district.server.smart-contracts :refer [contract-call]]
            [district0x-tasks.server.dev :as dev]
            [district0x-tasks.server.deployer :as deployer]
            [district0x-tasks.server.contracts.district-tasks :as district-tasks]
            [district.server.smart-contracts :refer [replay-past-events contract-event-in-tx]]))

(dev/-main)

;; {:from (first accounts)} is default account
(deployer/deploy-tasks-contract! {})

(def accounts (web3-eth/accounts @web3))

(defn event->test [event]
  (-> (district-tasks/event->cljs event)
      (select-keys [:args :event])))

;; Ganache CLI v6.1.8 (ganache-core: 2.2.1) block.timestamp is now instead of timestamp of last block
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
      (is (district-tasks/add-task "Title exist only in events" bidding-ends-on false {}))
      (is (district-tasks/update-task 1 "New title" (+ bidding-ends-on 1) true {}))
      (is (= (district-tasks/get-task 1 {})
             {:bidding-ends-on bidding-ends-on2
              :active? true}))))

  (testing "Add / remove Bids to created tasks[0]"
    (is (district-tasks/add-bid 0 "Bid title" "http://example.com" "Bid description" 0.01 {}))
    (is (= 1 (district-tasks/count-bids 0 {})))
    (is (= (district-tasks/get-bid 0 0 {})
           {:creator (first accounts)}))
    (is (district-tasks/add-bid 0 "Remove me" "http://example.com" "Remove me" 123.45 {}))
    (is (district-tasks/remove-bid 0 1 {}))
    (is (= 2 (district-tasks/count-bids 0 {}))
        "Still count 2, because we can't change indexes of bids to not mess with events data")
    (is (nil? (district-tasks/get-bid 0 1 {}))
        "Try to get removed bid"))

  (testing "Add Voters to created tasks[0]->bids[0]"
    (is (district-tasks/add-voter 0 0 {}))
    (is (thrown? js/Error (district-tasks/add-voter 0 0 {}))
        "should fail, because voter already voted")
    (is (= 1 (district-tasks/count-voters 0 0 {})))
    (is (true? (district-tasks/voted? 0 0 (first accounts) {})))
    (is (false? (district-tasks/voted? 0 0 (last accounts) {})))

    (is (= (district-tasks/get-voters 0 0 {})
           [(first accounts)])))

  (let [bidding-ends-on (+ (now-in-seconds) 1)]
    (testing "BiddingEndsOn, Active testing"
      (district-tasks/add-task "Title" bidding-ends-on false {})
      (is (thrown? js/Error
                   (district-tasks/add-bid 2 "Bid title" "http://example.com" "Bid description" 121.00 {}))
          "should not pass, because task is not active")
      (is (district-tasks/update-task 2 "Title" bidding-ends-on true {}))
      ;; temporary solution, because ganache-cli block.timestamp is now instead of last block timestamp
      (sleep 1)
      ;; when block.timestamp will became last block timestamp
      ;(web3-evm/increase-time! @web3 [1])
      ;(web3-evm/mine! @web3)
      (is (thrown? js/Error
                   (district-tasks/add-bid 2 "Bid title" "https://example.org" "Bid description" 678.90 {}))
          "should not pass, because BiddingEndsOn expired")))

  (testing "events"
    (let [bidding-ends-on (+ (now-in-seconds) 60)]
      (is (= (-> (district-tasks/add-task "Event test" bidding-ends-on false {})
                 (contract-event-in-tx :district-tasks :LogAddTask {})
                 (event->test))
             {:args {:id 3
                     :title "Event test"
                     :is-active false
                     :bidding-ends-on bidding-ends-on}
              :event "LogAddTask"})
          "Add task")
      (is (= (-> (district-tasks/update-task 0 "New title" bidding-ends-on true {})
                 (contract-event-in-tx :district-tasks :LogUpdateTask {})
                 (event->test))
             {:args {:id 0
                     :title "New title"
                     :is-active true
                     :bidding-ends-on bidding-ends-on}
              :event "LogUpdateTask"})
          "Update task")
      (is (= (-> (district-tasks/add-bid 0 "Bid title" "http://foo.io" "Bid description" 12.34 {})
                 (contract-event-in-tx :district-tasks :LogAddBid {})
                 (event->test))
             {:args {:task-id 0
                     :bid-id 2
                     :title "Bid title"
                     :url "http://foo.io"
                     :description "Bid description"
                     :amount 12.34
                     :creator (first accounts)}
              :event "LogAddBid"})
          "Add bid")
      (is (= (-> (district-tasks/remove-bid 0 2 {})
                 (contract-event-in-tx :district-tasks :LogRemoveBid {})
                 (event->test))
             {:args {:task-id 0
                     :bid-id 2}
              :event "LogRemoveBid"})
          "Remove bid")
      (is (= (-> (district-tasks/add-voter 0 1 {})
                 (contract-event-in-tx :district-tasks :LogAddVoter {})
                 (event->test))
             {:args {:task-id 0
                     :bid-id 1
                     :voter (first accounts)}
              :event "LogAddVoter"})
          "Add voter"))))
