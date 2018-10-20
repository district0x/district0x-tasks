(ns district0x-tasks.server.generator
  (:require
    [mount.core :as mount :refer [defstate]]
    [cljs-web3.eth :as web3-eth]
    [cljs-web3.utils :refer [js->cljkk camel-case]]
    [district.cljs-utils :refer [rand-str]]
    [district.server.config :refer [config]]
    [district.server.smart-contracts :refer [contract-address contract-call instance]]
    [district.server.web3 :refer [web3]]
    [district0x-tasks.server.deployer]
    [district0x-tasks.server.contracts.district-tasks :as district-tasks]
    [contracts.mini-me-token :as mini-me-token]))

(defn generate-tokens-for-accounts [{:keys [accounts]}]
  (doseq [account accounts]
    (mini-me-token/generate-tokens account (inc (rand-int 1000)) {:gas 130000
                                                                  :from (last accounts)})))

(defn generate-tasks [{:keys [accounts last-block-timestamp]}]
  (let [next-week (+ last-block-timestamp (* 7 24 3600))
        very-soon (+ last-block-timestamp 360)]
    (district-tasks/add-task "Administrative" next-week false {:from (last accounts)})
    (district-tasks/add-task "Administrative" next-week true {:from (last accounts)})
    (district-tasks/add-bid 1 "ABC Company Ltd." "https://district0x.io" "Administrative bid 1.0." 100 {})
    (district-tasks/add-voter 1 0 {:from (nth accounts 0)})
    (district-tasks/add-voter 1 0 {:from (nth accounts 1)})
    (district-tasks/add-voter 1 0 {:from (nth accounts 2)})
    (district-tasks/add-bid 1 "ABC Company Ltd." "https://district0x.io" "Administrative bid 1.1 - imagine 2 bids have the same content." 100 {})
    (district-tasks/add-voter 1 1 {:from (nth accounts 2)})
    (district-tasks/add-voter 1 1 {:from (nth accounts 3)})
    (district-tasks/add-voter 1 1 {:from (nth accounts 4)})
    (district-tasks/add-bid 1 "Joker" "http://localhost:8080" "Administrative bid 1.2." 300 {})
    (district-tasks/add-bid 1 "John Smith" "" "Administrative bid 1.3<br /><b>foo</b>." 50 {})
    (district-tasks/add-task "Branding and Design" very-soon true {:from (last accounts)})
    (district-tasks/add-bid 2 "<b>bold</b>" "" "Branding and Design bid 2.0." 400 {})
    (district-tasks/add-task "Branding and Design" next-week true {:from (last accounts)})
    (district-tasks/add-bid 3 "bid title" "" "Branding and Design bid 3.0." 10 {})))

(defn start [opts]
  (let [opts (assoc opts :accounts (web3-eth/accounts @web3)
                         :last-block-timestamp (->> (web3-eth/block-number @web3)
                                                    (web3-eth/get-block @web3)
                                                    :timestamp))]
    (generate-tokens-for-accounts opts)
    (generate-tasks opts)))


(defstate ^{:on-reload :noop} generator :start (start (merge (:generator @config)
                                                             (:generator (mount/args)))))
