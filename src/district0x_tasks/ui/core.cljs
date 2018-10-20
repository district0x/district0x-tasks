(ns district0x-tasks.ui.core
  (:require
    [cljs.spec.alpha :as s]
    [district.ui.component.router :refer [router]]
    [district.ui.graphql]
    [district.ui.notification]
    [district.ui.now]
    [district.ui.reagent-render]
    [district.ui.router-google-analytics]
    [district.ui.router]
    [district.ui.server-config]
    [district.ui.smart-contracts]
    [district.ui.web3-account-balances]
    [district.ui.web3-accounts]
    [district.ui.web3-balances]
    [district.ui.web3-sync-now]
    [district.ui.web3-tx-id]
    [district.ui.web3-tx-log]
    [district.ui.web3-tx]
    [district.ui.web3]
    [district.ui.window-size]
    [district0x-tasks.shared.graphql-schema :refer [graphql-schema]]
    [district0x-tasks.shared.routes :refer [routes]]
    [district0x-tasks.shared.smart-contracts :refer [smart-contracts]]
    [district0x-tasks.ui.home.page]
    [mount.core :as mount]
    [print.foo :include-macros true]
    [re-frisk.core :refer [enable-re-frisk!]]
    [re-frame.core :as re-frame]))

(goog-define server-config-url "http://localhost:6200/config")

(def debug? ^boolean js/goog.DEBUG)

(defn dev-setup []
  (when debug?
    (enable-console-print!)
    (enable-re-frisk!)))

(defn ^:export init []
  (s/check-asserts debug?)
  (dev-setup)
  (-> (mount/with-args
        (merge {:web3 {:url "http://localhost:8549"}
                :smart-contracts {:contracts smart-contracts}
                :web3-balances {:contracts (select-keys smart-contracts [:mini-me-token])}
                :web3-tx-log {:open-on-tx-hash? true
                              :tx-costs-currencies [:USD]}
                :reagent-render {:id "app"
                                 :component-var #'router}
                :router {:routes routes
                         :default-route :route.administrative/index}
                :router-google-analytics {:enabled? (not debug?)}
                :graphql {:schema graphql-schema
                          :url "http://localhost:6500/graphql"}}
               {:server-config {:url server-config-url}}))
      (mount/start)))
