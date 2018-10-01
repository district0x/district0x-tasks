(ns district0x-tasks.ui.home.page
  (:require
   [district.ui.component.page :refer [page]]
   [district.ui.graphql.subs :as gql]
   [district0x-tasks.ui.components.app-layout :refer [layout]]
   [re-frame.core :refer [subscribe dispatch]]
   [reagent.core :as r]
   [react-infinite]
   [print.foo :refer [look] :include-macros true]
   [district0x-tasks.ui.utils :as utils]))

(def auction-node-graph [:meme-auction/address
                         :meme-auction/start-price
                         :meme-auction/end-price
                         :meme-auction/duration
                         :meme-auction/description
                         [:meme-auction/seller [:user/address]]
                         [:meme-auction/meme-token
                          [:meme-token/number
                           [:meme-token/meme
                            [:meme/title
                             :meme/image-hash
                             :meme/total-minted]]]]])

(def new-on-marketplace-query
  [:search-meme-auctions
   {:order-by :meme-auctions.order-by/started-on
    :first 6}
   [[:items auction-node-graph]]])

(def rare-finds-query
  [:search-meme-auctions
   {:order-by :meme-auctions.order-by/meme-total-minted
    :order-dir :asc
    :first 6}
   [[:items auction-node-graph]]])

(def random-picks-query
  [:search-meme-auctions
   {:order-by :meme-auctions.order-by/random
    :first 6}
   [[:items auction-node-graph]]])

(defmethod page :route/home []
  (let [search-atom (r/atom {:term ""})
        ;new-on-market (subscribe [::gql/query {:queries [new-on-marketplace-query]}])
        ;rare-finds (subscribe [::gql/query {:queries [rare-finds-query]}])
        ;random-picks (subscribe [::gql/query {:queries [random-picks-query]}])
        ]
    (fn []
      [layout
       {:meta {:title "district0x-tasks"
               :description "Description"}
        :search-atom search-atom}
       #_[:div.home
        [:p.inspired "Inspired by the work of Simon de la Rouviere and his Curation Markets design, the third district to be deployed to dthe district0x."]
        [:section.meme-highlights
         [:div.new-on-marketplace
          [:div.icon]
          [:div.header
           [:div.middle
            [:h2.title "New On Marketplace"]
            [:h3.title "Lorem ipsum ..."]]]
          [auctions-list (-> @new-on-market :search-meme-auctions :items)]
          [:a.more {:href (utils/path-with-query (utils/path :route.marketplace/index)
                                                 {:order-by "started-on"
                                                  :order-dir "desc"})} "See More"]]

         [:div.rare-finds
          [:div.icon]
          [:div.header
           [:div.middle
            [:h2.title "Rare Finds"]
            [:h3.title "Lorem ipsum ..."]]]
          [auctions-list (-> @rare-finds :search-meme-auctions :items)]
          [:a.more {:href (utils/path-with-query (utils/path :route.marketplace/index)
                                                 {:order-by "meme-total-minted"
                                                  :order-dir "asc"})}
           "See More"]]

         [:div.random-pics
          [:div.icon]
          [:div.header
           [:div.middle
            [:h2.title "Random Picks"]
            [:h3.title "Lorem ipsum ..."]]]
          [auctions-list (-> @random-picks :search-meme-auctions :items)]
          [:a.more {:href (utils/path-with-query (utils/path :route.marketplace/index)
                                                 {:order-by "random"})}
           "See More"]]]]])))


