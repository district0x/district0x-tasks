(ns district0x-tasks.ui.events
  (:require [re-frame.core :as re-frame]
            [district.ui.web3-tx.events :as tx-events]
            [district.ui.smart-contracts.queries :as contract-queries]
            [district.ui.web3-accounts.queries :as account-queries]
            [district.ui.logging.events :as logging]
            [district.ui.notification.events :as notification-events]))

(re-frame/reg-event-fx
  ::add-bid
  (fn [{:keys [db]} [_ bid]]
    (println (pr-str bid))
    {}))

(re-frame/reg-event-fx
  ::add-voter
  (fn [{:keys [db]} [_ bid]]
    {:dispatch [::tx-events/send-tx {:instance (contract-queries/instance db :district-tasks)
                                     :fn :add-voter
                                     :args [(:task/id bid) (:bid/id bid)]
                                     :tx-opts {:from (account-queries/active-account db)}
                                     :on-tx-success-n [[::logging/success [::add-voter]]
                                                       [::notification-events/show "Vote sent."]]
                                     :on-tx-error-n [[::logging/error [::add-voter]]
                                                     [::notification-events/show "Error during voting :("]]
                                     :on-tx-hash-error-n [[::logging/error [::add-voter]]
                                                          [::notification-events/show "Vote not sent to the network. You have to sign your vote by your key!"]]}]}))
