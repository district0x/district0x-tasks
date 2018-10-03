(ns district0x-tasks.runner
  (:require [cljs.nodejs :as nodejs]
            [doo.runner :refer-macros [doo-tests]]
            [district0x-tasks.contracts.tasks-test]
            [district0x-tasks.server.db-test]
            [district0x-tasks.server.graphql-resolvers-test]))

(nodejs/enable-util-print!)

(set! (.-error js/console) (fn [x] (.log js/console x)))

(doo-tests
 'district0x-tasks.contracts.tasks-test
 'district0x-tasks.server.db-test
 'district0x-tasks.server.graphql-resolvers-test
 )
