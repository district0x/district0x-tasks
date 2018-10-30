(ns district0x-tasks.ui.events
  (:require [re-frame.core :as re-frame]
            [district.ui.web3-tx.events :as tx-events]
            [district.ui.smart-contracts.queries :as contract-queries]
            [district.ui.web3-accounts.queries :as account-queries]
            [district.ui.logging.events :as logging]
            [district.ui.notification.events :as notification-events]))

(re-frame/reg-event-fx
  ::add-bid
  (fn [{:keys [db]} [id bid]]
    {:dispatch [::tx-events/send-tx {:instance (contract-queries/instance db :district-tasks)
                                     :fn :add-bid
                                     :args [(:task/id bid) (:bid/title bid) (or (:bid/url bid) "") (:bid/description bid) (* 100 (float (:bid/amount bid)))]
                                     :tx-opts {:from (account-queries/active-account db)}
                                     :on-tx-success-n [[::logging/success [id]]
                                                       [::notification-events/show {:type :success :message "Bid sent."}]]
                                     :on-tx-error-n [[::logging/error [id]]
                                                     [::notification-events/show "Unknown error during add bid."]]
                                     :on-tx-hash-error [::logging/error [id]]}]}))

(re-frame/reg-event-fx
  ::add-voter
  (fn [{:keys [db]} [id bid voted?]]
    (if (false? voted?)
      {:dispatch [::tx-events/send-tx {:instance (contract-queries/instance db :district-tasks)
                                       :fn :add-voter
                                       :args [(:task/id bid) (:bid/id bid)]
                                       :tx-opts {:from (account-queries/active-account db)
                                                 :gas 90000}
                                       :on-tx-success-n [[::logging/success [id]]
                                                         [::notification-events/show {:type :success :message "Vote sent."}]]
                                       :on-tx-error-n [[::logging/error [id]]
                                                       [::notification-events/show "Unknown error during voting."]]
                                       :on-tx-hash-error [::logging/error [id]]}]}
      {:dispatch [::notification-events/show "You already voted for this bid."]})))

(re-frame/reg-event-fx
  ::voted?->add-voter
  (fn [{:keys [db]} [id bid]]
    {:web3/call {:web3 (get-in db [:district.ui.web3 :web3])
                 :fns [{:instance (contract-queries/instance db :district-tasks)
                        :fn :is-voted
                        :args [(:task/id bid) (:bid/id bid) (account-queries/active-account db)]
                        :on-success [::add-voter bid]
                        :on-error [::logging/error [id]]}]}}))
