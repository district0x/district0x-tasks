(ns district0x-tasks.server.contracts.district-tasks
  (:require [district.server.smart-contracts :refer [contract-call]]
            [bignumber.core :as bn]))

; tasks

(defn add-task [title bidding-ends-on active? opts]
  (contract-call :district-tasks :add-task title bidding-ends-on active? opts))

(defn count-tasks [opts]
  (-> (contract-call :district-tasks :count-tasks opts)
      (bn/number)))

(defn get-task [task-id opts]
  (let [[bidding-ends-on active?] (contract-call :district-tasks :tasks task-id opts)]
    {:bidding-ends-on (bn/number bidding-ends-on)
     :active? active?}))

(defn update-task-active [task-id active? opts]
  (contract-call :district-tasks :update-task-active
                 task-id
                 active?
                 opts))

(defn update-task-bidding-ends-on [task-id bidding-ends-on opts]
  (contract-call :district-tasks :update-task-bidding-ends-on
                 task-id
                 bidding-ends-on
                 opts))

(defn update-task-title [task-id title opts]
  (contract-call :district-tasks :update-task-title
                 task-id
                 title
                 opts))

; bids

(defn add-bid [task-id title description opts]
  (contract-call :district-tasks :add-bid
                 task-id
                 title
                 description
                 opts))

(defn update-bid-title [task-id bid-id title opts]
  (contract-call :district-tasks :update-bid-title
                 task-id
                 bid-id
                 title
                 opts))

(defn update-bid-description [task-id bid-id description opts]
  (contract-call :district-tasks :update-bid-description
                 task-id
                 bid-id
                 description
                 opts))

(defn count-bids [task-id opts]
  (-> (contract-call :district-tasks :count-bids task-id opts)
      (bn/number)))

(defn get-bid [task-id bid-id opts]
  (let [[creator voters] (contract-call :district-tasks :get-bids task-id bid-id opts)]
    {:creator creator
     :voters voters}))


(defn add-voter [task-id bid-id opts]
  (contract-call :district-tasks :add-voter
                 task-id
                 bid-id
                 opts))

; voters

(defn count-voters [task-id bid-id opts]
  (-> (contract-call :district-tasks :count-voters task-id bid-id opts)
      (bn/number)))

(defn get-voters [task-id bid-id opts]
  )

(defn voted? [task-id bid-id voter opts]
  (contract-call :district-tasks :is-voted task-id bid-id voter opts))
