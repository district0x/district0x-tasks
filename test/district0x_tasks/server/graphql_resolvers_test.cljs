(ns district0x-tasks.server.graphql-resolvers-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests use-fixtures async]]
            [district0x-tasks.server.dev :as dev]
            [district0x-tasks.server.deployer :as deployer]
            [district0x-tasks.server.graphql-resolvers :as resolver]
            [district.server.graphql :as graphql]
            [district0x-tasks.server.db :as db]
            [district0x-tasks.server.contracts.district-tasks :as district-tasks]
            [district.server.web3 :refer [web3]]
            [cljs-web3.eth :as web3-eth]))

(dev/-main)

(def accounts (web3-eth/accounts @web3))

(deployer/deploy {:from (first accounts)})
(dev/resync)

(defn now-in-seconds []
  (-> (.getTime (js/Date.))
      (quot 1000)
      (inc)))

(district-tasks/add-task "Not active 0" (+ (now-in-seconds) 600) false {})
(district-tasks/add-task "Task 1" (+ (now-in-seconds) 600) true {})
(district-tasks/add-task "Task 2" (+ (now-in-seconds) 600) true {})
(district-tasks/add-task "Task 3" (+ (now-in-seconds) 600) true {})
;(district-tasks/add-bid 1 "Bid 1.0" "http://example.com/" "Bid description" 0.01 {})
;(district-tasks/add-bid 1 "Bid 1.1" "http://example.com/" "Bid description" 0.08 {})
;(district-tasks/add-bid 2 "Bid 2.1" "http://example.com/" "Bid description" 1.32 {})
;(district-tasks/add-bid 3 "Bid 3.0" "http://example.com/" "Bid description" 278 {})
;(district-tasks/add-bid 3 "Bid 3.1" "http://example.com/" "Bid description" 254 {})
;(district-tasks/add-bid 3 "Bid 3.2" "http://example.com/" "Bid description" 289.35 {})
;(district-tasks/add-voter 1 0 {:from (nth accounts 0)})
;(district-tasks/add-voter 1 0 {:from (nth accounts 1)})
;(district-tasks/add-voter 1 1 {:from (nth accounts 0)})
;(district-tasks/add-voter 3 2 {:from (nth accounts 0)})
;(district-tasks/add-voter 3 2 {:from (nth accounts 1)})
;(district-tasks/add-voter 3 2 {:from (nth accounts 2)})
;(district-tasks/add-voter 3 2 {:from (nth accounts 3)})


;(testing "graphql"
;  )

#_(println (pr-str (graphql/run-query
                   {:queries [[:active-tasks
                               [:task/id :task/title
                                [:task/bids [:bid/id]]]]]})))

;(println (pr-str (graphql/run-query "{activeTasks {task_id, task_title}}")))
