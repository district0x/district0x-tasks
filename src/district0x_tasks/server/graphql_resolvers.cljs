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
            [district.server.db :as db]
            [district.server.smart-contracts :as smart-contracts]
            [district.server.web3 :as web3]
            [honeysql.core :as sql]
            [honeysql.helpers :as sqlh]
            [print.foo :refer [look] :include-macros true]
            [taoensso.timbre :as log]
            ;[district.shared.error-handling :refer [try-catch-throw]]
            ))

;(def tasks-columns
;  [[:task/id :unsigned :integer not-nil]
;   [:task/title :varchar not-nil]
;   [:task/active? :boolean not-nil]
;   [:task/bidding-ends-on :unsigned :integer not-nil]
;   [:task/created-at :unsigned :integer not-nil]])
;
;(def bids-columns
;  [[:task/id :unsigned :integer not-nil]
;   [:bid/id :unsigned :integer not-nil]
;   [:bid/creator address not-nil]
;   [:bid/title :varchar not-nil]
;   [:bid/url :varchar]
;   [:bid/description :text not-nil]
;   [:bid/amount :unsigned :float not-nil]
;   [:bid/created-at :unsigned :integer not-nil]])
;
;(def voters-columns
;  [[:task/id :unsigned :integer not-nil]
;   [:bid/id :unsigned :integer not-nil]
;   [:voter/address address not-nil]])
;
;(def voters->tokens-columns
;  [[:voter/address address not-nil]
;   [:voter/tokens-amount :unsigned :integer not-nil]])
;
(def resolvers-map
  {}
  ;{:Task {:task/id
  ;        :task/title
  ;        :task/active?
  ;        :task/bidding-ends-on
  ;        :task/created-at}
  ; :Bid {:task/id
  ;       :bid/id
  ;       :bid/creator
  ;       :bid/title
  ;       :bid/url
  ;       :bid/description
  ;       :bid/amount
  ;       :bid/created-at}
  ; :_Task {:get
  ;        :all
  ;        :active
  ;        :bidding-ends-on}
  ; :_Bid {:task-bid-id
  ;       :all-by-task
  ;       }
  ; }
  )
