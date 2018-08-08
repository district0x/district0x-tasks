(ns district0x-tasks.runner
  (:require [cljs.nodejs :as nodejs]
            [district.graphql-utils :as graphql-utils]
            [district.server.graphql :as graphql]
            [district.server.graphql.utils :as utils]
            [doo.runner :refer-macros [doo-tests]]
            [district0x-tasks.contracts.tasks-test]))

(nodejs/enable-util-print!)

(set! (.-error js/console) (fn [x] (.log js/console x)))

(doo-tests
 'district0x-tasks.contracts.tasks-test)
