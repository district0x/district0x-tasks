(ns district0x-tasks.ui.events
  (:require [re-frame.core :as re-frame]
            [district.ui.web3-tx.events :as tx-events]
            [district.ui.smart-contracts.queries :as contract-queries]
            [district.ui.web3-accounts.queries :as account-queries]
            [district.ui.notification.events :as notification-events]))

(re-frame/reg-event-fx
  ::add-bid
  (fn [db [_ bid]]
    (println (pr-str bid))
    {}))

(re-frame/reg-event-fx
  ::add-voter
  (fn [db [_ bid]]
    (println (pr-str (account-queries/active-account db)))
    (println (pr-str (account-queries/accounts db)))
    {}
    #_{:dispatch [::tx-events/send-tx {:instance (contract-queries/instance db :district-tasks)
                                     :fn :add-voter
                                     :args [(:task/id bid) (:bid/id bid)]
                                     :tx-opts {:from (account-queries/active-account db)}}]}))

;(dispatch [::tx-events/send-tx {:instance MintableToken
;                               :fn :mint
;                               :args [(first accounts) (web3/to-wei 1 :ether)]
;                               :tx-opts {:from (first accounts) :gas 4500000}
;                               :on-tx-hash [::tx-hash]
;                               :on-tx-hash-n [[::tx-hash]]
;                               :on-tx-hash-error [::tx-hash-error]
;                               :on-tx-hash-error-n [[::tx-hash-error]]
;                               :on-tx-success [::tx-success]
;                               :on-tx-success-n [[::tx-success]]
;                               :on-tx-error [::tx-error]
;                               :on-tx-error-n [[::tx-error]]}])


;{:dispatch [::tx-events/send-tx {:instance (contract-queries/instance db :DANK)
;                                 :fn :approve-and-call
;                                 :args [(contract-queries/contract-address db :meme-factory)
;                                        deposit
;                                        extra-data]
;                                 :tx-opts {:from active-account
;                                           :gas 6000000}
;                                 :tx-id {:meme/create-meme tx-id}
;                                 :on-tx-success-n [[::logging/success [::create-meme]]
;                                                   [::notification-events/show (gstring/format "Meme created with meta hash %s" Hash)]]
;                                 :on-tx-hash-error [::logging/error [::create-meme]]
;                                 :on-tx-error [::logging/error [::create-meme]]}]})))
