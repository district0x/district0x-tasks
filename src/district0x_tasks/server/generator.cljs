(ns district0x-tasks.server.generator
  (:require
    [bignumber.core :as bn]
    [cljs-ipfs-api.files :as ipfs-files]
    [cljs-web3.core :as web3]
    [cljs-web3.eth :as web3-eth]
    [cljs-web3.evm :as web3-evm]
    [cljs-web3.utils :refer [js->cljkk camel-case]]
    [district.cljs-utils :refer [rand-str]]
    [district.format :as format]
    [district.server.config :refer [config]]
    [district.server.smart-contracts :refer [contract-address contract-call instance]]
    [district.server.web3 :refer [web3]]
    [district0x-tasks.server.deployer]
    [taoensso.timbre :as log]
    [mount.core :as mount :refer [defstate]]
    [print.foo :refer [look] :include-macros true])
  (:require-macros [district0x-tasks.server.macros :refer [try-catch]]))

(def fs (js/require "fs"))

(declare start)
(defstate ^{:on-reload :noop} generator :start (start (merge (:generator @config)
                                                             (:generator (mount/args)))))


(defn start [opts]
      )
