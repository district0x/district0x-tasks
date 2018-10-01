(ns district0x-tasks.server.core
  (:require
    [cljs.nodejs :as nodejs]
    [district.server.config :refer [config]]
    [district.server.logging]
    [district.server.middleware.logging :refer [logging-middlewares]]
    [district.server.web3-watcher]
    [district0x-tasks.server.db]
    [district0x-tasks.server.deployer]
    [district0x-tasks.server.generator]
    [district0x-tasks.server.syncer]
    [district0x-tasks.shared.smart-contracts]
    [district.server.graphql :as graphql]
    [district.server.graphql.utils :as utils]
    [district.graphql-utils :as graphql-utils]
    [district0x-tasks.shared.graphql-schema :refer [graphql-schema]]
    [district0x-tasks.server.graphql-resolvers :refer [resolvers-map]]
    [mount.core :as mount]
    [taoensso.timbre :refer-macros [info warn error]]))

(nodejs/enable-util-print!)

(defn -main [& _]
  (-> (mount/with-args
        {:config {:default {:web3 {:port 8545}}}
         :smart-contracts {:contracts-var #'district0x-tasks.shared.smart-contracts/smart-contracts}
         :graphql {:port 6500
                   :middlewares [logging-middlewares]
                   :schema (utils/build-schema graphql-schema
                                               resolvers-map
                                               {:kw->gql-name graphql-utils/kw->gql-name
                                                :gql-name->kw graphql-utils/gql-name->kw})
                   :field-resolver (utils/build-default-field-resolver graphql-utils/gql-name->kw)
                   :path "/graphql"
                   :graphiql true}
         :web3-watcher {:on-online (fn []
                                     (warn "Ethereum node went online again")
                                     (mount/stop #'district0x-tasks.server.db/district0x-tasks-db)
                                     (mount/start #'district0x-tasks.server.db/district0x-tasks-db
                                                  #'district0x-tasks.server.syncer/syncer))
                        :on-offline (fn []
                                      (warn "Ethereum node went offline")
                                      (mount/stop #'district0x-tasks.server.syncer/syncer))}
         :syncer {:ipfs-config {:host "http://127.0.0.1:5001" :endpoint "/api/v0"}}})
      (mount/except [#'district0x-tasks.server.deployer/deployer
                     #'district0x-tasks.server.generator/generator])
      (mount/start))
  (warn "System started" {:config @config}))

(set! *main-cli-fn* -main)
