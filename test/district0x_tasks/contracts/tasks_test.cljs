(ns district0x-tasks.contracts.tasks-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests use-fixtures async]]
            [cljs-web3.core :as web3]
            [cljs-web3.eth :as web3-eth]
            [cljs-web3.evm :as web3-evm]
            [bignumber.core :as bn]
            [district.server.web3 :refer [web3]]
            [district.server.smart-contracts :refer [contract-call]]
            [district0x-tasks.server.dev :as dev]
            [district0x-tasks.server.deployer :as deployer]
            ))

(dev/-main)

(def accounts (web3-eth/accounts @web3))

(deftest tasks-test
  (let [tasks-contract (deployer/deploy-tasks-contract! {:from (last accounts)})]
    (testing "Only Owner can add tasks, update active and bidding ends on"
      (is (thrown? js/Error
                   (contract-call :district-tasks :add-task "Title" true (+ (.getTime (js/Date.)) 86400)
                                  {:from (first accounts)}))
          "not an owner, should Error")
      (is (contract-call :district-tasks :add-task
                         "Title"
                         true
                         (+ (.getTime (js/Date.) 86400))
                         {:from (last accounts)})
          "the owner, should pass"))))
