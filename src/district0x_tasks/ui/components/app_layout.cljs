(ns district0x-tasks.ui.components.app-layout
  (:require
    [district.ui.component.active-account :refer [active-account]]
    [district.ui.component.active-account-balance :refer [active-account-balance]]
    [district.ui.component.form.input :as inputs :refer [text-input*]]
    [district.ui.component.font-icons :as icons]
    [re-frame.core :refer [subscribe dispatch]]
    [reagent.core :as r]))

(defn menu []
  [:div.app-menu
   [:ul
    [:li.icon-document "Administrative"]
    [:li.icon-pencil-ruler "Branding and Design"]
    [:li.icon-thumb-up "Community and" [:br] "Marketing"]
    [:li "d0xINFRA"]
    [:li.icon-registry "District Registry"]
    [:li.icon-arrow-up "Meme Factory"]
    [:li.icon-double-arrow-right "Next District"]
    [:li.icon-settings "About"]]])

(defn page []
  [:div.app-page
   [icons/icon-mechanics]
   [:div.page-top
    [:h1 "Administrative"]
    [:p "Lorem ipsum"]
    [:p "Bidding and voting will be closed in 12 days 13 hours"]]

   [:div.bids
    [:h2 "Bids"]
    [:div.bid
     [:a {:href ""} "Sourcerers Ltd."]
     [:p "Lorem ipsum"]
     [:p "145 ETH"]
     [:div.votes-line
      [:hr {:width "30%"}]
      [:hr]]
     [:p.votes-text "29,223,572 DNT (67,11%)"]
     [:button.vote "Vote"]]]

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
     [:button "Submit"]]]])

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
  (fn [{:keys [:meta :search-atom]} & children]
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
     [footer]]))
