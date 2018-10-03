(ns district0x-tasks.server.graphql-resolvers
  (:require [bignumber.core :as bn]
            [cljs-time.core :as t]
            [cljs-web3.core :as web3-core]
            [cljs-web3.eth :as web3-eth]
            [cljs-web3.async.eth :as web3-eth-async]
            [cljs.core.match :refer-macros [match]]
            [cljs.nodejs :as nodejs]
            [clojure.string :as str]
            [district.graphql-utils :as graphql-utils]
            [district.server.config :refer [config]]
            ;[district.server.db :as db]
            [district0x-tasks.server.db :as db]
            [district.server.smart-contracts :as smart-contracts]
            [district.server.web3 :as web3]
            [honeysql.core :as sql]
            [honeysql.helpers :as sqlh]
            [print.foo :refer [look] :include-macros true]
            [taoensso.timbre :as log]
            ;[district.shared.error-handling :refer [try-catch-throw]]
            ))

(defn active-tasks [_ _]
  (db/get-active-tasks))

(defn task->bids [_ task]
  (db/get-bids task))

(defn bid->votes-count [_ bid]
  (db/sum-voters->tokens bid))

(def resolvers-map
  {:Query {:active-tasks active-tasks
           :bids task->bids}
   :Task {:task/bids task->bids}
   :Bid {:bid/votes-count bid->votes-count}})
