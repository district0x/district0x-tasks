(ns district0x-tasks.server.db
  (:require
    [district.server.config :refer [config]]
    [district.server.db :as db]
    [district.server.db.column-types :refer [address not-nil default-nil default-zero default-false sha3-hash primary-key]]
    [district.server.db.honeysql-extensions]
    [honeysql.core :as sql]
    [honeysql.helpers :refer [merge-where merge-order-by merge-left-join defhelper]]
    [mount.core :as mount :refer [defstate]]
    [taoensso.timbre :as logging :refer-macros [info warn error]]
    [medley.core :as medley]))

(declare start)
(declare stop)
(defstate ^{:on-reload :noop} district0x-tasks-db
          :start (start (merge (:district0x-tasks/db @config)
                               (:district0x-tasks/db (mount/args))))
          :stop (stop))

(def ipfs-hash (sql/call :char (sql/inline 46)))

(def registry-entries-columns
  [[:reg-entry/address address primary-key not-nil]
   [:reg-entry/version :unsigned :integer not-nil]
   [:reg-entry/creator address not-nil]
   [:reg-entry/deposit :unsigned :BIG :INT not-nil]
   [:reg-entry/created-on :unsigned :integer not-nil]
   [:reg-entry/challenge-period-end :unsigned :integer not-nil]
   [:challenge/challenger address default-nil]
   [:challenge/created-on :unsigned :integer default-nil]
   [:challenge/voting-token address default-nil]
   [:challenge/reward-pool :unsigned :BIG :INT default-nil]
   [:challenge/meta-hash ipfs-hash default-nil]
   [:challenge/comment :varchar default-nil]
   [:challenge/commit-period-end :unsigned :integer default-nil]
   [:challenge/reveal-period-end :unsigned :integer default-nil]
   [:challenge/votes-for :BIG :INT default-nil]
   [:challenge/votes-against :BIG :INT default-nil]
   [:challenge/claimed-reward-on :unsigned :integer default-nil]])

(def registry-entry-column-names (map first registry-entries-columns))

(defn- index-name [col-name]
       (keyword (namespace col-name) (str (name col-name) "-index")))

(defn start [opts]
      (db/run! {:create-table [:reg-entries]
                :with-columns [registry-entries-columns]})

      )


(defn stop []
      (db/run! {:drop-table [:reg-entries]})
      )

(defn create-insert-fn [table-name column-names & [{:keys [:insert-or-replace?]}]]
      (fn [item]
          (let [item (select-keys item column-names)]
               (db/run! {(if insert-or-replace? :insert-or-replace-into :insert-into) table-name
                         :columns (keys item)
                         :values [(vals item)]}))))

(defn create-update-fn [table-name column-names id-keys]
      (fn [item]
          (let [item (select-keys item column-names)
                id-keys (if (sequential? id-keys) id-keys [id-keys])]
               (db/run! {:update table-name
                         :set item
                         :where (concat
                                  [:and]
                                  (for [id-key id-keys]
                                       [:= id-key (get item id-key)]))}))))

(defn create-get-fn [table-name id-keys]
      (let [id-keys (if (sequential? id-keys) id-keys [id-keys])]
           (fn [item fields]
               (cond-> (db/get {:select (if (sequential? fields) fields [fields])
                                :from [table-name]
                                :where (concat
                                         [:and]
                                         (for [id-key id-keys]
                                              [:= id-key (get item id-key)]))})
                       (keyword? fields) fields))))

(def insert-registry-entry! (create-insert-fn :reg-entries registry-entry-column-names))
(def update-registry-entry! (create-update-fn :reg-entries registry-entry-column-names :reg-entry/address))
(def get-registry-entry (create-get-fn :reg-entries :reg-entry/address))
