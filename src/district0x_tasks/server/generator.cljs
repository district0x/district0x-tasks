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
    [print.foo :refer [look] :include-macros true]))

(def fs (js/require "fs"))

(defstate ^{:on-reload :noop} generator :start (start (merge (:generator @config)
                                                             (:generator (mount/args)))))

(defn generate-tasks [{:keys [:accounts :memes/use-accounts :memes/items-per-account :memes/scenarios]}]
  (println "Generate-tasks println")
  #_(let [[max-total-supply max-auction-duration deposit commit-period-duration reveal-period-duration]
        (->> (eternal-db/get-uint-values :meme-registry-db [:max-total-supply :max-auction-duration :deposit :commit-period-duration
                                                            :reveal-period-duration])
             (map bn/number))
        scenarios (get-scenarios (look {:accounts accounts
                                        :use-accounts use-accounts
                                        :items-per-account items-per-account
                                        :scenarios scenarios}))]
    ))


(defn start [opts]
  (let [opts (assoc opts :accounts (web3-eth/accounts @web3))]
    (generate-tasks opts)))
