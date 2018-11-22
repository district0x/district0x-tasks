(ns district0x-tasks.server.deployer
  (:require
    [cljs-web3.core :as web3]
    [cljs-web3.eth :as web3-eth]
    [district.cljs-utils :refer [rand-str]]
    [district.server.config :refer [config]]
    [district.server.smart-contracts :refer [contract-event-in-tx contract-address deploy-smart-contract! write-smart-contracts!]]
    [district.server.web3 :refer [web3]]
    [mount.core :as mount :refer [defstate]]
    [district.server.smart-contracts :refer [contract-call]]))

(defn deploy-tasks-contract! [default-opts]
  (deploy-smart-contract! :district-tasks (merge default-opts {:gas 1500000})))

(defn deploy-mini-me-token-contract! [default-opts]
  (deploy-smart-contract! :mini-me-token (merge default-opts {:gas 2000000})))

(defn deploy [{:keys [:write?]
               :as deploy-opts}]
  (let [accounts (web3-eth/accounts @web3)
        deploy-opts (merge {:from (last accounts)}
                           deploy-opts)]

    (deploy-tasks-contract! deploy-opts)
    (deploy-mini-me-token-contract! deploy-opts)

    (when write?
      (write-smart-contracts!))))

(defstate ^{:on-reload :noop} deployer
  :start (deploy (merge (:deployer @config)
                        (:deployer (mount/args)))))
