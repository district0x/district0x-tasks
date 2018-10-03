(ns district0x-tasks.server.dev
  (:require
    [camel-snake-kebab.core :as cs :include-macros true]
    [cljs-time.core :as t]
    [cljs-web3.core :as web3]
    [cljs.nodejs :as nodejs]
    [cljs.pprint :as pprint]
    [cljs-web3.eth :as web3-eth]
    [cljs-web3.evm :as web3-evm]
    [district.server.config :refer [config]]
    [district.server.db :refer [db]]
    [district.server.endpoints]
    [district.server.graphql :as graphql]
    [district.server.logging :refer [logging]]
    [district.server.middleware.logging :refer [logging-middlewares]]
    [district.server.smart-contracts]
    [district.server.web3 :refer [web3]]
    [district.server.web3-watcher]
    [goog.date.Date]
    [graphql-query.core :refer [graphql-query]]
    [district0x-tasks.server.db]
    [district0x-tasks.server.deployer]
    [district0x-tasks.server.generator]
    [district0x-tasks.server.syncer]
    [district.graphql-utils :as graphql-utils]
    [district0x-tasks.shared.graphql-schema :refer [graphql-schema]]
    [district0x-tasks.server.graphql-resolvers :refer [resolvers-map]]
    [district0x-tasks.shared.smart-contracts]
    [mount.core :as mount]
    [district.server.graphql.utils :as utils]
    [print.foo :include-macros true]
    [clojure.pprint :refer [print-table]]
    [district.server.db :as db]
    [clojure.string :as str]
    [bignumber.core :as bn]))

(nodejs/enable-util-print!)

(def graphql-module (nodejs/require "graphql"))
(def parse-graphql (aget graphql-module "parse"))
(def visit (aget graphql-module "visit"))

(defn on-jsload []
  #(graphql/restart {:schema (utils/build-schema graphql-schema
                                                 resolvers-map
                                                 {:kw->gql-name graphql-utils/kw->gql-name
                                                  :gql-name->kw graphql-utils/gql-name->kw})
                     :field-resolver (utils/build-default-field-resolver graphql-utils/gql-name->kw)}))

(defn deploy-to-mainnet []
  (mount/stop #'district.server.web3/web3
              #'district.server.smart-contracts/smart-contracts)
  (mount/start-with-args (merge
                           (mount/args)
                           {:web3 {:port 8545}
                            :deployer {:write? true
                                       :gas-price (web3/to-wei 4 :gwei)}})
                         #'district.server.web3/web3
                         #'district.server.smart-contracts/smart-contracts))

(defn redeploy []
  (mount/stop)
  (-> (mount/with-args
        (merge
          (mount/args)
          {:deployer {:write? true}}))
      (mount/start)
      (pprint/pprint)))

(defn resync []
  (mount/stop #'district0x-tasks.server.db/district0x-tasks-db
              #'district0x-tasks.server.syncer/syncer)
  (-> (mount/start #'district0x-tasks.server.db/district0x-tasks-db
                   #'district0x-tasks.server.syncer/syncer)
      (pprint/pprint)))

(defn -main [& _]
  (-> (mount/with-args
        {:config {:default {:logging {:level "info"
                                      :console? true}
                            :graphql {:port 6500
                                      :middlewares [logging-middlewares]
                                      :schema (utils/build-schema graphql-schema
                                                                  resolvers-map
                                                                  {:kw->gql-name graphql-utils/kw->gql-name
                                                                   :gql-name->kw graphql-utils/gql-name->kw})
                                      :field-resolver (utils/build-default-field-resolver graphql-utils/gql-name->kw)
                                      :path "/graphql"
                                      :graphiql true}
                            :web3 {:port 8549}
                            :generator {}
                            :deployer {}
                            :ipfs {:host "http://127.0.0.1:5001" :endpoint "/api/v0" :gateway "http://127.0.0.1:8080/ipfs"}}}
         :smart-contracts {:contracts-var #'district0x-tasks.shared.smart-contracts/smart-contracts
                           :print-gas-usage? true
                           :auto-mining? true}})
      (mount/except [#'district0x-tasks.server.deployer/deployer
                     #'district0x-tasks.server.generator/generator])
      (mount/start)
      (pprint/pprint)))

(set! *main-cli-fn* -main)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Some useful repl tools ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn increase-time [seconds]
  (web3-evm/increase-time! @web3 [seconds])
  (web3-evm/mine! @web3))

(defn select
  "Usage: (select [:*] :from [:memes])"
  [& [select-fields & r]]
  (-> (db/all (->> (partition 2 r)
                   (map vec)
                   (into {:select select-fields})))
      (print-table)))

(defn print-db
  "(print-db) prints all db tables to the repl
   (print-db :users) prints only users table"
  ([] (print-db nil))
  ([table]
   (let [all-tables (if table
                      [(name table)]
                      (->> (db/all {:select [:name] :from [:sqlite-master] :where [:= :type "table"]})
                           (map :name)))]
     (doseq [t all-tables]
       (println "#######" (str/upper-case t) "#######")
       (select [:*] :from [(keyword t)])
       (println "\n\n")))))
