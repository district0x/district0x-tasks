(ns district0x-tasks.contracts.tasks-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests use-fixtures async]]
            [cljs-web3.core :as web3]
            [cljs-web3.eth :as web3-eth]
            [cljs-web3.evm :as web3-evm]
            [district.server.web3 :refer [web3]]
            ))

(deftest tasks-test
  (testing "foo"
    (is (= 1 0)
        "bar")))
