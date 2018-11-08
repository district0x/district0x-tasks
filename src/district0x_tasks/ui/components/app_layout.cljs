(ns district0x-tasks.ui.components.app-layout
  (:require
    [district.ui.web3-accounts.subs :as accounts-subs]
    [district.ui.web3-account-balances.subs :as accounts-balances-subs]
    [district.ui.component.active-account :refer [active-account]]
    [district.ui.web3-accounts.events :as accounts-events]
    [district.ui.notification.subs :as notification-subs]
    [district.ui.component.form.input :as inputs]
    [district.ui.component.font-icons :as icons]
    [re-frame.core :refer [subscribe dispatch]]
    [district.ui.graphql.subs :as gql]
    [district0x-tasks.ui.utils :as utils]
    [cljs-time.core :as t]
    [reagent.core :as r]
    [re-frame.core :as re-frame]
    [district.ui.router.subs :as router-subs]
    [district0x-tasks.ui.events :as events]
    [district.format :as format]
    [reagent.format :as r-format]
    [cljs-web3.core :as web3]
    [bignumber.core :as bn])
  (:require-macros [reagent.ratom :refer [reaction]]))

(def pages
  [{:route :route.administrative/index
    :title "Administrative"
    :icon "icon-document"}
   {:route :route.branding-and-design/index
    :title "Branding and Design"
    :icon "icon-pencil-ruler"}
   {:route :route.community-and-marketing/index
    :title "Community and Marketing"
    :icon "icon-thumb-up"}
   {:route :route.d0xinfra/index
    :title "d0xINFRA"
    :icon "icon-circles"}
   {:route :route.district-registry/index
    :title "District Registry"
    :icon "icon-registry"}
   {:route :route.meme-factory/index
    :title "Meme Factory"
    :icon "icon-arrow-up"}
   {:route :route.next-district/index
    :title "Next District"
    :icon "icon-double-arrow-right"}
   {:route :route.about/index
    :title "About"
    :icon "icon-settings"}])

(defn format-percentage [p]
  (->> (* p 100)
       (r-format/format "%.2f%")))

(defn percentage-line [p]
  [:div.votes-line
   [:hr {:width p}]
   [:hr]])

(defn find-page [route]
  (filter #(= route (:route %)) pages))

(defn menu-item [{:keys [route title icon]}]
  [:li
   [:a
    {:class icon
     :href (utils/path route)}
    title]])

(defn menu []
  [:div.app-menu
   (into [:ul] (map #(menu-item %) pages))])

(defn page []
  (let [page-title (-> @(subscribe [::router-subs/active-page])
                       :name
                       (find-page)
                       (first)
                       :title)
        task-raw (re-frame/subscribe [::gql/query
                                      {:queries
                                       [[:active-tasks
                                         [:task/id :task/title :task/is-active :task/bidding-ends-on
                                          [:task/bids [:task/id :bid/id :bid/creator :bid/title :bid/url :bid/description :bid/amount :bid/votes-sum]]]]]}])
        form-data (r/atom {})]
    (fn []
      (let [task (->> @task-raw
                      :active-tasks
                      (filter #(= page-title (:task/title %)))
                      (first))
            bids (:task/bids task)
            bids-sum (reduce #(+ %1 (:bid/votes-sum %2)) 0 bids)
            ?interval (when (some-> (:task/bidding-ends-on task)
                                    (t/after? (t/now)))
                        (let [interval-raw (t/interval (t/now) (:task/bidding-ends-on task))]
                          {:d (t/in-days interval-raw)
                           :h (mod (t/in-hours interval-raw) 24)}))]
        [:div.app-page
         [icons/icon-mechanics]
         [:div.page-top
          [:h1 page-title]
          [:p.description "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin nibh augue, suscipit a, scelerisque sed, lacinia in, mi. Cras vel lorem. Etiam pellentesque aliquet tellus. Phasellus pharetra nulla ac diam. Quisque semper justo at risus. Donec venenatis, turpis vel hendrerit interdum, dui ligula ultricies purus, sed posuere libero dui id orci."]
          (if ?interval
            [:p.bidding "Bidding and voting will be closed in " (:d ?interval) " days " (:h ?interval) " hours."]
            [:p.bidding "Bidding and voting is closed."])]

         (into [:div.bids
                [:h2 "Bids"]]
               (for [bid bids
                     :let [p (-> (/ (:bid/votes-sum bid) bids-sum)
                                 (format-percentage))]]
                 [:div.bid
                  [:div.info
                   [:a {:href (not-empty (:bid/url bid))} (:bid/title bid)]
                   [:span.amount (:bid/amount bid) " ETH"]]
                  [:p.description (:bid/description bid)]
                  [percentage-line p]
                  [:p.votes-text (format/format-token (:bid/votes-sum bid) {:token "DNT"}) " (" p ")"]
                  [:div.vote
                   (when ?interval
                     [inputs/pending-button
                      {:class "button"
                       :on-click (fn [e]
                                   (.preventDefault e)
                                   (dispatch [::events/voted?->add-voter bid]))}
                      "Vote"])]]))

         [:div.bids-form
          [:h2 "Submit a Bid"]
          [:div.form-container
           [:div.left
            [inputs/text-input {:form-data form-data
                                :id :bid/title
                                :placeholder "Name"}]
            [inputs/text-input {:form-data form-data
                                :type :url
                                :id :bid/url
                                :placeholder "Website URL"}]
            [:div.amount [inputs/text-input {:form-data form-data
                                             :type :number
                                             :id :bid/amount
                                             :min 0
                                             :step 0.01
                                             :placeholder "Bid"}]]]
           [inputs/textarea-input {:form-data form-data
                                   :id :bid/description
                                   :placeholder "Description"
                                   :rows 10
                                   :cols 40}]]
          [inputs/pending-button
           {:class "button"
            :on-click (fn [e]
                        (.preventDefault e)
                        (if (<= 0 (:bid/amount @form-data))
                          (dispatch [::events/add-bid (merge {:task/id (:task/id task)} @form-data)])
                          (js/alert "Bid amount has to be non-negative number.")))}
           "Submit"]]]))))

;;; todo form, footer

(defn footer []
  [:div.footer
   [icons/district0x-logo]
   [:div.footer-container
    [:div
     [:p.description "A network of decentralised markets and communities. Create, operate, and govern. Powered by Ethereum, Aragon and IPFS."]
     [:p.district0x-network "Part of the " [:a {:href "http://district0x.io"} "district0x Network"]]]
    [:ul
     [:li [:a {:href "https://blog.district0x.io/"} "Blog"]]
     [:li [:a {:href "https://district0x.io/team/"} "Team"]]
     [:li [:a {:href "https://district0x.io/transparency/"} "Transparency"]]
     [:li [:a {:href "https://district0x.io/faq/"} "FAQ"]]]
    [:div.icons
     [:a.icon-reddit.button {:href "https://www.reddit.com/r/district0x"}]
     [:a.icon-twitter.button {:href "https://twitter.com/district0x"}]
     [:a.icon-medium.button {:href ""}]
     [:a.icon-github.button {:href "https://github.com/district0x"}]]]])

(defn notification-metamask []
  (let [active-account (subscribe [::accounts-subs/active-account])]
    (fn []
      (when-not @active-account
        [:div.notification-metamask
         "You have to install / unlock " [:a {:href "https://metamask.io/"} "metamask"] " browser extension to vote."]))))

(defn notifications []
  (let [notification (re-frame/subscribe [::notification-subs/notification])]
    (fn []
      (when (:message @notification)
        [:div.notification
         {:class (:type @notification)}
         (:message @notification)]))))

(defn layout []
  (let [accounts (subscribe [::accounts-subs/accounts])
        active-account (subscribe [::accounts-subs/active-account])
        active-account-balance (subscribe [::accounts-balances-subs/active-account-balance :DNT])]
    (fn []
      [:div.app-container
       [notification-metamask]
       [notifications]
       [:div.top
        [icons/district0x-logo]
        [:div.top-right
         [:span (format/format-token (bn/number @active-account-balance) {:token "DNT"})]
         [:div.accounts
          [:div.icon-select-address
           (into [:select
                  {:on-click #(dispatch [::events/notification-no-accounts])
                   :value (str @active-account)
                   :on-change (fn [event]
                                (dispatch [::accounts-events/set-active-account event.target.value]))}]
                 (for [account @accounts]
                   [:option account]))]]]]
       [:div.app-content
        [menu]
        [page]]
       [footer]])))
