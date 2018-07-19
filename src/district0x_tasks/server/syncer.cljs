(ns district0x-tasks.server.syncer
  (:require
    [bignumber.core :as bn]
    [camel-snake-kebab.core :as cs :include-macros true]
    [cljs-web3.core :as web3]
    [cljs-web3.eth :as web3-eth]
    [district.server.config :refer [config]]
    [district.server.smart-contracts :refer [replay-past-events]]
    [district.server.web3 :refer [web3]]
    [district.web3-utils :as web3-utils]
    [district0x-tasks.server.db :as db]
    [district0x-tasks.server.deployer]
    [district0x-tasks.server.generator]
    [mount.core :as mount :refer [defstate]]
    [taoensso.timbre :as log]
    [cljs-ipfs-api.files :as ifiles]
    [print.foo :refer [look] :include-macros true]))

(declare start)
(declare stop)
(defstate ^{:on-reload :noop} syncer
          :start (start (merge (:syncer @config)
                               (:syncer (mount/args))))
          :stop (stop syncer))

(def info-text "smart-contract event")
(def error-text "smart-contract event error")

(defn start [opts]
      )

(defn stop [syncer]
      )

