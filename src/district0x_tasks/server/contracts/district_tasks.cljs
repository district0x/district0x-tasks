(ns district0x-tasks.server.contracts.district-tasks
  (:require [district.server.smart-contracts :refer [contract-call]]
            [bignumber.core :as bn]))

;;; tasks

(defn add-task [title bidding-ends-on active? opts]
  "bidding-end-in is Unix Timestamp in seconds"
  (contract-call :district-tasks :add-task title bidding-ends-on active? opts))

(defn update-task [task-id title bidding-ends-on active? opts]
  (contract-call :district-tasks :update-task task-id title bidding-ends-on active? opts))

(defn get-task [task-id opts]
  (let [[bidding-ends-on active?] (contract-call :district-tasks :tasks task-id opts)]
    {:bidding-ends-on (bn/number bidding-ends-on)
     :active? active?}))

(defn count-tasks [opts]
  (-> (contract-call :district-tasks :count-tasks opts)
      (bn/number)))

;;; bids

(defn add-bid [task-id title description opts]
  (contract-call :district-tasks :add-bid
                 task-id
                 title
                 description
                 opts))

(defn update-bid [task-id bid-id title description opts]
  (contract-call :district-tasks :update-bid
                 task-id
                 bid-id
                 title
                 description
                 opts))

(defn count-bids [task-id opts]
  (-> (contract-call :district-tasks :count-bids task-id opts)
      (bn/number)))

(defn get-bid [task-id bid-id opts]
  {:creator (contract-call :district-tasks :get-bid task-id bid-id opts)})

;;; voters

(defn add-voter [task-id bid-id opts]
  (contract-call :district-tasks :add-voter
                 task-id
                 bid-id
                 opts))

(defn count-voters [task-id bid-id opts]
  (-> (contract-call :district-tasks :count-voters task-id bid-id opts)
      (bn/number)))

(defn get-voters [task-id bid-id opts]
  (contract-call :district-tasks :get-voters task-id bid-id opts))

(defn voted? [task-id bid-id voter opts]
  (contract-call :district-tasks :is-voted task-id bid-id voter opts))

;;; events

(defn log-task->cljs [event]
  (-> (update-in event [:args :id] bn/number)
      (update-in [:args :bidding-ends-on] bn/number)))

(defn log-bid->cljs [event]
  (-> (update-in event [:args :task-id] bn/number)
      (update-in [:args :bid-id] bn/number)))

(defn log-add-voter->cljs [event]
  (-> (update-in event [:args :task-id] bn/number)
      (update-in [:args :bid-id] bn/number)))

(defn event->cljs [event]
  (let [convert (case (:event event)
                  "LogAddTask" log-task->cljs
                  "LogUpdateTask" log-task->cljs
                  "LogAddBid" log-bid->cljs
                  "LogUpdateBid" log-bid->cljs
                  "LogAddVoter" log-add-voter->cljs)]
    (convert event)))
