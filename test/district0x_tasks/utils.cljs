(ns district0x-tasks.utils
  (:require [cljs.test :refer-macros [deftest is testing run-tests use-fixtures async]]
            [district0x-tasks.server.deployer :as deployer]
            [district0x-tasks.server.dev :as dev]
            [district.server.web3 :refer [web3]]
            [cljs-web3.eth :as web3-eth]
            [cljs-web3.evm :as web3-evm]))

(dev/-main)

(def accounts (web3-eth/accounts @web3))

(defn last-block-timestamp []
  (->> (web3-eth/block-number @web3)
       (web3-eth/get-block @web3)
       :timestamp))

(defn feature-in-seconds []
  (+ (last-block-timestamp) 600))

(defn inc-eth-time!
  ([seconds]
   (web3-evm/increase-time! @web3 [seconds])
   (web3-evm/mine! @web3))
  ([] (inc-eth-time! 600)))

(defn prepare-contracts [tests]
  (deployer/deploy {:from (first accounts)})
  (dev/resync)
  (tests))
