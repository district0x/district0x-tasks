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


(def tasks-placeholder "feedfeedfeedfeedfeedfeedfeedfeedfeedfeed")

(defn deploy-district-voting! [default-opts]
  (deploy-smart-contract! :district-voting (merge default-opts {:gas 2200000})))

(defn deploy-tasks-contract! [default-opts]
  (deploy-smart-contract! :tasks (merge default-opts {:gas 2200000
                                                      ; :arguments [] argument to contract constructor
                                                      ;:placeholder-replacements {tasks-placeholder :tasks}
                                                      })))

(defn deploy [{:keys [:write?]
               :as deploy-opts}]
  (let [accounts (web3-eth/accounts @web3)
        deploy-opts (merge {:from (last accounts)}
                           deploy-opts)]

    (deploy-district-voting! deploy-opts)
    (deploy-tasks-contract! deploy-opts)

    (when write?
      (write-smart-contracts!))))

(defstate ^{:on-reload :noop} deployer
  :start (deploy (merge (:deployer @config)
                        (:deployer (mount/args)))))


(defn foo []
  (println "TASKS-FOO"
           (pr-str
             (contract-call :tasks :foo))))
