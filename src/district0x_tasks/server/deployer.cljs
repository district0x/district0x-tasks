(ns district0x-tasks.server.deployer
  (:require
    [cljs-web3.core :as web3]
    [cljs-web3.eth :as web3-eth]
    [district.cljs-utils :refer [rand-str]]
    [district.server.config :refer [config]]
    [district.server.smart-contracts :refer [contract-event-in-tx contract-address deploy-smart-contract! write-smart-contracts!]]
    [district.server.web3 :refer [web3]]
    [district0x-tasks.server.contract.dank-token :as dank-token]
    [district0x-tasks.server.contract.ds-auth :as ds-auth]
    [district0x-tasks.server.contract.ds-guard :as ds-guard]
    [district0x-tasks.server.contract.eternal-db :as eternal-db]
    [district0x-tasks.server.contract.meme-auction-factory :as meme-auction-factory]
    [district0x-tasks.server.contract.mutable-forwarder :as mutable-forwarder]
    [district0x-tasks.server.contract.registry :as registry]
    [mount.core :as mount :refer [defstate]]))

(declare deploy)
(defstate ^{:on-reload :noop} deployer
  :start (deploy (merge (:deployer @config)
                        (:deployer (mount/args)))))


(defn deploy [{:keys [:write?]
               :as deploy-opts}]
  (let [accounts (web3-eth/accounts @web3)
        deploy-opts (merge {:from (last accounts)}
                           deploy-opts)]

       (when write?
             (write-smart-contracts!))))
