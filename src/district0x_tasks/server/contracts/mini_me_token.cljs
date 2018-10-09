(ns contracts.mini-me-token
  (:require [district.server.smart-contracts :refer [contract-call]]
            [bignumber.core :as bn]))

(defn generate-tokens [owner amount opts]
  (contract-call :mini-me-token :generate-tokens owner amount opts))

(defn enable-transfer [enable? opts]
  (contract-call :mini-me-token :enable-transfers enable? opts))

(defn transfer-tokens [to amount opts]
  (contract-call :mini-me-token :transfer to amount opts))

(defn balance-of [owner opts]
  {:tokens-balance (-> (contract-call :mini-me-token :balance-of owner opts)
                       (bn/number))})
