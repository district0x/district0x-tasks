(ns district0x-tasks.runner
  (:require [cljs.nodejs :as nodejs]
            [district.graphql-utils :as graphql-utils]
            [district.server.graphql :as graphql]
            [district.server.graphql.utils :as utils]
            [doo.runner :refer-macros [doo-tests]]
            [district0x-tasks.contracts.tasks-test]))

; lein solc

; avoid run twice!
; npm i -D chockidar
; require chockidar
; code watch resources/public/contracts/build
; run deploy contract
; run tests (touch)

; lein doo node server-tests
; write 1 test




; http://grishaev.me/en/lein
; https://www.gnu.org/software/make/

; final conclusion is: it shouldn't be in cljs
; it should be in shell
; inotify-tools
; incron - to raczej do systemu, a nie do projektu

(nodejs/enable-util-print!)

(set! (.-error js/console) (fn [x] (.log js/console x)))

(defn deploy-contracts []
  (println "DEPLOY CONTRACTS!!!!!!"))

(deploy-contracts)

(doo-tests
 'district0x-tasks.contracts.tasks-test)
