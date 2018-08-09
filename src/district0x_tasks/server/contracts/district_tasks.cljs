(ns district0x-tasks.server.contracts.district-tasks
  (:require [district.server.smart-contracts :refer [contract-call]]
            [bignumber.core :as bn]))

(defn add-task [title bidding-ends-on active? otps]
  (contract-call :district-tasks :add-task title bidding-ends-on active? otps))

(defn count-tasks [otps]
  (-> (contract-call :district-tasks :count-tasks otps)
      (bn/number)))

(defn add-bid [task-id otps]
  (contract-call :district-tasks :add-bid
                 task-id
                 otps))

(defn count-bids [task-id otps]
  (-> (contract-call :district-tasks :count-bids task-id otps)
      (bn/number)))

(defn add-voter [task-id bid-id otps]
  (contract-call :district-tasks :add-voter
                 task-id
                 bid-id
                 otps))

(defn count-voters [task-id bid-id otps]
  (-> (contract-call :district-tasks :count-voters task-id bid-id otps)
      (bn/number)))
