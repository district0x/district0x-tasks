(ns district0x-tasks.contracts.tasks-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests use-fixtures async]]
            [cljs-web3.eth :as web3-eth]
            [bignumber.core :as bn]
            [district.server.web3 :refer [web3]]
            [district.server.smart-contracts :refer [contract-call]]
            [district0x-tasks.server.dev :as dev]
            [district0x-tasks.server.deployer :as deployer]
            [district0x-tasks.server.contracts.district-tasks :as district-tasks]))

(dev/-main)

(def accounts (web3-eth/accounts @web3))

; {:from (first accounts)} is default account
(deployer/deploy-tasks-contract! {})

(deftest tasks-test
  (testing "Only Owner can addTasks, updateActive and updateBiddingEndsOn"
    (is (thrown? js/Error
                 (district-tasks/add-task "Title" (+ (.getTime (js/Date.)) 10000) true {:from (last accounts)}))
        "not an owner, should Error")
    (is (district-tasks/add-task "Title" (+ (.getTime (js/Date.)) 10000) true {:from (first accounts)})
        "the owner, should pass")
    (is (= 1 (district-tasks/count-tasks {}))))

  (testing "Add Bids to created tasks[0]"
    (is (district-tasks/add-bid 0 {}))
    (is (= 1 (district-tasks/count-bids 0 {}))))

  (testing "Add Voters to created tasks[0]->bid[0]"
    (is (district-tasks/add-voter 0 0 {}))
    (is (thrown? js/Error (district-tasks/add-voter 0 0 {}))
        "this should fail, because voter already voted")
    (is (= 1 (district-tasks/count-voters 0 0 {})))))
