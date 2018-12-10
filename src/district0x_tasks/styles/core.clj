(ns district0x-tasks.styles.core
  (:require [garden.def :refer [defstyles]]
            [district0x-tasks.styles.base.media :as media]
            [garden.units :refer [rem px em]]))

(def primary-color "#2c398f")
(def secondary-color "#47608e")
(def third-color "#23fdd8")

(defstyles notification
           [:.notification {:box-sizing "border-box"
                            :position "fixed"
                            :width "100%"
                            :padding "30px"
                            :text-align "center"
                            :background-color "#FFFDE7"
                            :z-index 1
                            :font-size "18px"}
            (media/max-tablet {:display "none"})
            [:.success {:background-color "#C5E1A5"}]]
           [:.notification-metamask {:position "static"
                                     :background-color "#E1F5FE"}])

(println (media/max-tablet [:& {:display "none"}]))

(defstyles top
           [:.top {:margin-bottom "66px"
                   :height "80px"
                   :padding-left "20px"
                   :padding-right "50px"
                   :display "flex"
                   :justify-content "space-between"
                   :align-items "center"
                   :-webkit-box-shadow "0px 0px 120px 0px rgba(73, 79, 104, 0.11)"
                   :-moz-box-shadow "0px 0px 120px 0px rgba(73, 79, 104, 0.11)"
                   :box-shadow "0px 0px 120px 0px rgba(73, 79, 104, 0.11)"}
            (media/max-tablet {:display "none"})
            [:.top-right {:display "flex"
                          :align-items "center"
                          :font-size "16px"
                          :font-family "'filson-soft'"
                          :color primary-color}
             [:.accounts
              [:.icon-select-address {:position "relative"}
               [:&:after {:position "absolute"
                          :content "'\\e911'"
                          :transform "rotate(90deg)"
                          :top 0
                          :right 0
                          :color "#69c9e0"
                          :font-size "13px"
                          :line-height "36px"
                          :border-radius "28px"
                          :background-color primary-color
                          :padding "0px 10px"
                          :pointer-events "none"}]]]
             [:select {:height "36px"
                       :width "172px"
                       :margin-left "14px"
                       :padding "0px 16px"
                       :padding-right "60px"
                       :border (str "1px solid " secondary-color)
                       :background-color "white"
                       :border-radius "28px"
                       :-webkit-appearance "none"
                       :-moz-appearance "none"
                       :appearance "none"
                       :-webkit-box-shadow " 0px 4px 0px 0px #E8EAF2"
                       :-moz-box-shadow " 0px 4px 0px 0px #E8EAF2"
                       :box-shadow " 0px 4px 0px 0px #E8EAF2"
                       :font-size "14px"
                       :color "#c7cdd9"
                       :display "block"}]
             ["select::-ms-expand" {:display "none"}]]])

(defstyles top-mobile
           [:.top-mobile {:height "43px"}
            (media/min-tablet {:display "none"})
            [:.logo {:margin-top "22px"
                     :margin-left "-30px"}]
            [:.menu {:position :absolute
                     :top 0
                     :right 0
                     :width (px 50)
                     :height (px 50)
                     :cursor :pointer
                     :background-color primary-color}
             [:.icon-menu
              {:pointer-events "none"
               :font-size (px 29)
               :position :absolute
               :top (px 7)
               :right (px 7)
               :line-height "100%"
               :color third-color}]]])

(defstyles menu
           [:.app-menu {:margin-right "160px"
                        :border-radius "0 10px 10px 0"
                        :-webkit-box-shadow "0px 0px 120px 0px rgba(73, 79, 104, 0.11)"
                        :-moz-box-shadow "0px 0px 120px 0px rgba(73, 79, 104, 0.11)"
                        :box-shadow "0px 0px 120px 0px rgba(73, 79, 104, 0.11)"
                        :font-family "proxima-nova"
                        :background-color "white"}
            [:ul {:margin "0"
                  :padding "0"
                  :width "250px"}
             [:li {:list-style-type "none"}]
             ["li:not(:first-child)" {:border-top " 1px solid #fbfbfb"}]
             ["li:not(:last-child)" {:border-bottom " 1px solid #f0f2f6"}]]
            [:a {:display "flex"
                 :align-items "center"
                 :padding "18px 20px"
                 :font-size "16px"
                 :text-decoration "none"
                 :color secondary-color}
             [:span [:&:before {:display "block"
                                :width "45px"
                                :font-size "22px"
                                :font-weight "bold"
                                :color "#88e4e6"}]]]
            (media/max-tablet {:position "absolute"
                               :top (px 80)
                               :left 0})]
           (media/max-tablet [:.menu-closed {:display "none"}]))

(defstyles app-page
           [:.app-page {:width "100%"
                        :position "relative"
                        :flex-grow "1"
                        :padding "90px 80px 70px 140px"
                        :border-radius "10px"
                        :-webkit-box-shadow "0px 0px 120px 0px rgba(73, 79, 104, 0.11)"
                        :-moz-box-shadow "0px 0px 120px 0px rgba(73, 79, 104, 0.11)"
                        :box-shadow "0px 0px 120px 0px rgba(73, 79, 104, 0.11)"}
            ["[class^=\"icon-\"], [class*=\" icon-\"]" {:position "absolute"
                                                        :top "0"
                                                        :left "95px"
                                                        :font-size "350px"}]
            [:.page-top {:min-height "300px"
                         :padding-left "140px"
                         :color secondary-color}
             [:h1 {:margin "0"
                   :font-size "36px"
                   :font-family "filson-soft"
                   :font-weight "300"
                   :color "#232e80"}]
             [:.description {:margin-top "46px"
                             :margin-bottom "0px"
                             :line-height "32px"
                             :font-size "22px"
                             :font-family "proxima-soft"}]
             [:.bidding {:margin-top "46px"
                         :margin-bottom "48px"
                         :font-size "16px"
                         :font-family "proxima-soft"
                         :font-weight "600"}]]])

(defstyles bids
           [:.bids {:color secondary-color}
            [:h2 {:margin-top "0px"
                  :margin-bottom "16px"
                  :font-family "filson-soft"
                  :font-weight "normal"}]
            [:.bid {:line-height "27px"
                    :font-family "proxima-soft"}
             [:a {:font-weight "bold"
                  :color secondary-color
                  :text-decoration-color "#d1d7e3"}]
             [:.info {:display "flex"
                      :justify-content "space-between"}]
             [:.description {:margin-top "0px"
                             :margin-bottom "36px"
                             :white-space "pre-wrap"}]]
            [".bid:not(:first-of-type)" {:margin-top "140px"}]])

(defstyles bids-form
           [:.bids-form {:margin-top "50px"
                         :font-family "proxima-soft"
                         :color secondary-color}
            [:h2 {:margin-bottom "40px"
                  :font-size "23px"
                  :font-family "filson-soft"
                  :font-weight "normal"}]
            [:.button {:padding "14px 48px"
                       :font-size "18px"}]
            [:.form-container {:display "flex"
                               :justify-content "space-between"}]
            [:.left {:width "40%"
                     :min-width "180px"
                     :max-width "410px"
                     :margin-bottom "6pz"
                     :display "flex"
                     :flex-direction "column"
                     :justify-content "space-between"}]
            [:input {:width "100%"
                     :border-width "0 0 1px 0"
                     :border-color "#E0DFE6"
                     :font-family "proxima-soft"
                     :font-size "16px"
                     :line-height "30px"}]
            [".form-container > .input-group" {:flex-grow 2
                                               :margin-left "35px"}]
            [:textarea {:box-sizing "border-box"
                        :width "100%"
                        :height "165px"
                        :padding "22px"
                        :border "none"
                        :border-radius "10px"
                        :background-color "#F6F5FA"
                        :font-size "14px"
                        :font-family "proxima-nova"}]
            [:.amount {:position "relative"}
             [:&:after {:position "absolute"
                        :top "8px"
                        :right "0"
                        :content "'ETH'"}]
             [:input {:width "calc(100% - 40px)"
                      :padding-right "40px"}]]
            [:.unit {:display "inline"}]
            [:.button {:float "right"}]])

(defstyles votes
           [:.votes-line {:display "flex"}
            [:hr {:margin 0
                  :height "9px"
                  :border "none"
                  :background-color "#394596"}
             [:&:first-child {:background-color "#04ffcc"}]
             [:&:last-child {:flex-grow 1}]]]
           [:.votes-text {:margin-top "10px"
                          :margin-bottom "0px"
                          :text-align "center"}]
           [:.vote {:display "flex"
                    :justify-content "flex-end"}])
(defstyles footer
           [:.footer {:display "grid"
                      :font-family "filson-soft"
                      :color secondary-color}
            [:.description {:grid-area "description"
                            :font-size "18px"
                            :line-height "32px"}]
            [:.district0x-network {:grid-area "district0x"
                                   :font-size "11px"}
             [:a {:color secondary-color
                  :text-decoration-color "#d1d7e3"}]]
            [:.icons {:grid-area "icons"
                      :display "grid"
                      :grid-template-columns "auto auto auto auto"
                      :justify-items "end"}
             [:.button {:display "inline-block"
                        :width "42px"
                        :height "42px"
                        :line-height "42px"
                        :padding "0px"
                        :border-radius "50%"
                        :font-size "16px"
                        :text-align "center"
                        :vertical-align "middle"
                        :text-decoration "none"}
              [:&.icon-github {:font-size "18px"}]]]]
           (media/min-tablet
             [:.footer {:margin "64px 190px 0px 410px"
                        :grid-template-columns "auto auto 208px"
                        :grid-gap "35px 10%"
                        :grid-template-areas "'logo . .' 'description links icons' 'district0x links .'"}
              [:ul {:grid-area "links"
                    :margin 0
                    :padding 0}
               [:li {:list-style-type "none"}]
               [:a {:font-size "13px"
                    :line-height "32px"
                    :font-family "proxima-soft"
                    :color secondary-color
                    :text-decoration "none"}]]])
           (media/max-tablet
             [:.footer {:margin-top "80px"
                        :grid-row-gap "40px"
                        :grid-template-areas "'logo' 'description' 'icons' 'district0x'"
                        :justify-items "center"}
              [:ul {:display "none"}]]))

(defstyles main
           [:body {:margin 0
                   ;:min-width "1250px"
                   :margin-bottom "70px"
                   :font-family "sans-serif"}
            (media/max-tablet {:margin "0 30px"})]
           [:.app-content {:display "flex"
                           :justify-content "space-between"}
            ;(media/max-tablet {:margin-top "50px"})
            (media/min-tablet {:margin-right "190px"})]
           [:.button {:border "none"
                      :border-radius "28px"
                      :font-size "13px"
                      :padding "10px 32px"
                      :font-family "filson-soft"
                      :background-color primary-color
                      :color "#7cf8fa"
                      :cursor "pointer"
                      :-webkit-box-shadow "0px 4px 0px 0px #E8EAF2"
                      :-moz-box-shadow "0px 4px 0px 0px #E8EAF2"
                      :box-shadow "0px 4px 0px 0px #E8EAF2"}]
           [:img.logo {:width "200px"
                       :max-height "100%"}]
           (media/max-tablet [:img.logo {:height "35px"}])
           notification
           top
           top-mobile
           menu
           app-page
           bids
           bids-form
           votes
           footer)