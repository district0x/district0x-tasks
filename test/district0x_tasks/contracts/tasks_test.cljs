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
  (let [tasks-contract (deployer/deploy-tasks-contract! {:from (last accounts)
                                                         :arguments ["Task title"]})]
    (testing "Only Owner can manage they tasks, set them active, set end date for bidding"
      (is (thrown? js/Error
                   (contract-call :tasks :set-active false {:from (first accounts)}))
          "not an owner, should Error")
      (is (contract-call :tasks :set-active false {:from (last accounts)})
          "the owner try"))))


;Creator of contract should be able to add tasks, set them as active or not, set end date for bidding
;All string data for Tasks and Bid should be fired as solidity event, not saved into contract state
;On blockchain we can represent tasks and bid just as number IDs: 1, 2, 3...
;Users can submit bid for a tasks
;Voters vote on a particular bid. Contract just saves the fact that this address voted for this bid. Not saving amount or anything.
;Voting and bidding should not be possible after bidding end date of a task


;#object[Error Error: VM Exception while processing transaction: revert]
