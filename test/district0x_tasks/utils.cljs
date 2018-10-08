(ns district0x-tasks.utils
  (:require [cljs.test :refer-macros [deftest is testing run-tests use-fixtures async]]
            [district0x-tasks.server.deployer :as deployer]
            [district0x-tasks.server.dev :as dev]
            [district.server.web3 :refer [web3]]
            [cljs-web3.eth :as web3-eth]))

(dev/-main)

(def accounts (web3-eth/accounts @web3))

(defn prepare-contracts [tests]
  (deployer/deploy {:from (first accounts)})
  (dev/resync)
  (tests))
