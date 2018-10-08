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

(def tasks-columns
  [[:task/id :unsigned :integer not-nil]
   [:task/title :varchar not-nil]
   [:task/active? :boolean not-nil]
   [:task/bidding-ends-on :unsigned :integer not-nil]
   [:task/created-at :unsigned :integer not-nil]])

(def tasks-column-names (map first tasks-columns))

(def bids-columns
  [[:task/id :unsigned :integer not-nil]
   [:bid/id :unsigned :integer not-nil]
   [:bid/creator address not-nil]
   [:bid/title :varchar not-nil]
   [:bid/url :varchar]
   [:bid/description :text not-nil]
   [:bid/amount :unsigned :float not-nil]
   [:bid/votes-sum :unsigned :integer default-zero]
   [:bid/created-at :unsigned :integer not-nil]])

(def bids-column-names (map first bids-columns))

(def voters-columns
  [[:task/id :unsigned :integer not-nil]
   [:bid/id :unsigned :integer not-nil]
   [:voter/address address not-nil]])

(def voters-column-names (map first voters-columns))

(def voters->tokens-columns
  [[:voter/address address not-nil]
   [:voter/tokens-amount :unsigned :integer not-nil]])

(def voters->tokens-column-names (map first voters->tokens-columns))

(defn start [opts]
  (db/run! {:create-table [:tasks]
            :with-columns [tasks-columns]})
  (db/run! {:create-table [:bids]
            :with-columns [bids-columns]})
  (db/run! {:create-table [:voters]
            :with-columns [voters-columns]})
  (db/run! {:create-table [:voters->tokens]
            :with-columns [voters->tokens-columns]})
  (-> (sql/raw "CREATE UNIQUE INDEX tasks_index ON tasks (task_SLASH_id)")
      (db/run!))
  (-> (sql/raw "CREATE UNIQUE INDEX bids_index ON bids (task_SLASH_id, bid_SLASH_id)")
      (db/run!))
  (-> (sql/raw "CREATE UNIQUE INDEX voters_index ON voters (task_SLASH_id, bid_SLASH_id, voter_SLASH_address)")
      (db/run!))
  (-> (sql/raw "CREATE UNIQUE INDEX voters_tokens_index ON voters__GT_tokens (voter_SLASH_address)")
      (db/run!))
  ::started)

(defn stop []
  (db/run! {:drop-table [:tasks]})
  (db/run! {:drop-table [:bids]})
  (db/run! {:drop-table [:voters]})
  (db/run! {:drop-table [:voters->tokens]}))

(defstate ^{:on-reload :noop} district0x-tasks-db
  :start (start (merge (:district0x-tasks/db @config)
                       (:district0x-tasks/db (mount/args))))
  :stop (stop))

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

(def insert-task! (create-insert-fn :tasks tasks-column-names))
(def update-task! (create-update-fn :tasks tasks-column-names :task/id))
(defn task-item->edn [task-item]
  (update task-item :task/active? #(= 1 %)))
(def get-task (comp task-item->edn
                    (create-get-fn :tasks :task/id)))
(defn get-active-tasks []
  (db/all {:select [:*]
           :from [:tasks]
           :where [:and [:= :task/active? true]]}))

(def insert-bid! (create-insert-fn :bids bids-column-names))
(def update-bid! (create-update-fn :bids bids-column-names [:task/id :bid/id]))
(def get-bid (create-get-fn :bids [:task/id :bid/id]))
(defn get-bids [task]
  (let [task-id (:task/id task)]
    (db/all {:select [:*]
             :from [:bids]
             :where [:= :task/id task-id]})))
(defn remove-bid! [bid]
  (let [task-id (:task/id bid)
        bid-id (:bid/id bid)]
    (db/run! {:delete-from :bids
              :where [:and [:= :task/id task-id] [:= :bid/id bid-id]]})))

(def insert-voter! (create-insert-fn :voters voters-column-names))
(defn get-voters [bid]
  (let [task-id (:task/id bid)
        bid-id (:bid/id bid)]
    (db/all {:select [:*]
             :from [:voters]
             :where [:and [:= :task/id task-id] [:= :bid/id bid-id]]})))

(def insert-voter->tokens! (create-insert-fn :voters->tokens voters->tokens-column-names))
(def update-voter->tokens! (create-update-fn :voters->tokens voters->tokens-column-names :voter/address))
(def get-voter->tokens (create-get-fn :voters->tokens :voter/address))
(defn upsert-voter->tokens! [voter->token]
  (if (not-empty (get-voter->tokens voter->token [:*]))
    (update-voter->tokens! voter->token)
    (insert-voter->tokens! voter->token)))
(defn sum-voters->tokens [bid]
  (let [task-id (:task/id bid)
        bid-id (:bid/id bid)]
    (-> (db/get {:select [[(sql/call :sum :v->t.voter/tokens-amount) :sum]]
                 :from [[:voters :v] [:voters->tokens :v->t]]
                 :where [:and [:= :v.task/id task-id] [:= :v.bid/id bid-id]
                         [:= :v.voter/address :v->t.voter/address]]})
        :sum
        (or 0))))
