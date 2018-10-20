(ns district0x-tasks.ui.components.app-layout
  (:require
    [district.ui.component.active-account :refer [active-account]]
    [district.ui.component.active-account-balance :refer [active-account-balance]]
    [district.ui.component.form.input :as inputs :refer [text-input*]]
    [district.ui.component.font-icons :as icons]
    [re-frame.core :refer [subscribe dispatch]]
    [district.ui.graphql.subs :as gql]
    [district0x-tasks.ui.utils :as utils]
    [cljs-time.core :as t]
    [cljs-time.coerce :as tc]
    [reagent.core :as r]
    [re-frame.core :as re-frame]
    [district.ui.router.subs :as router-subs]
    [district.format :as format]
    [reagent.format :as r-format]))

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
    :title "d0xINFRA"}
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
        task (->> @(re-frame/subscribe [::gql/query
                                        {:queries
                                         [[:active-tasks
                                           [:task/id :task/title :task/is-active :task/bidding-ends-on
                                            [:task/bids [:bid/id :bid/creator :bid/title :bid/url :bid/description :bid/amount :bid/votes-sum]]]]]}])
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
      [:p "Lorem ipsum"]
      (if ?interval
        [:p "Bidding and voting will be closed in " (:d ?interval) " days " (:h ?interval) " hours."]
        [:p "Bidding and voting is closed."])]

     (into [:div.bids
            [:h2 "Bids"]]
           (for [bid bids]
             [:div.bid
              [:a {:href (not-empty (:bid/url bid))} (:bid/title bid)]
              [:p (:bid/description bid)]
              [:p (:bid/amount bid)]
              [:div.votes-line
               [:hr {:width "30%"}]
               [:hr]]
              [:p.votes-text (format/format-token (:bid/votes-sum bid) {:token "DNT"}) " (" (format-percentage (/ (:bid/votes-sum bid) bids-sum)) ")"]
              [:button.vote "Vote"]]))

     [:div.bids-form
      [:h2 "Submit a Bid"]
      [:form
       [:input {:name "name"
                :placeholder "Name"}]
       [:input {:name "url"
                :placeholder "Website URL"}]
       [:input {:name "eth"
                :placeholder "Bid"}]
       [:textarea {:name "description"
                   :placeholder "Description"
                   :rows 10
                   :cols 40}]
       [:button "Submit"]]]]))

(defn footer []
  [:div.footer
   [icons/district0x-logo-with-slogan]
   [:p "A network of decentralised markets and communities. Create, operate, and govern. Powered by Ethereum, Aragon and IPFS."]
   [:p "Part of the district0x Network"]
   [:ul
    [:li "Blog"]
    [:li "Team"]
    [:li "Transparency"]
    [:li "FAQ"]]
   [:button.icon-reddit]
   [:button.icon-twitter]
   [:button.icon-medium]
   [:button.icon-github]])

(defn layout []
  [:div.app-container
   [:div.top
    [icons/district0x-logo-with-slogan]
    [:div.top-right
     [:span "1,236,346 DNT"]
     ; address fo user
     [:div.icon-select-address
      [:select
       [:option "0x123123123123123123123123123"]
       [:option "2"]
       [:option "3"]]]]]
   [:div.app-content
    [menu]
    [page]]
   [footer]])
