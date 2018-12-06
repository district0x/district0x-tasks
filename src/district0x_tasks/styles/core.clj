(ns district0x-tasks.styles.core
  (:require [garden.def :refer [defstyles]]))

(defstyles notification
           [:.notification {:position "fixed"
                            :width "100%"
                            :padding "30px"
                            :text-align "center"
                            :background-color "#FFFDE7"
                            :z-index 1
                            :font-size "18px"}
            [:.success {:background-color "#C5E1A5"}]]
           [:.notification-metamask {:width "100%"
                                     :padding "10px"
                                     :text-align "center"
                                     :background-color "#E1F5FE"
                                     :z-index 1
                                     :font-size "18px"}])

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
            [:.top-right {:display "flex"
                          :align-items "center"
                          :font-size "16px"
                          :font-family "'filson-soft'"
                          :color "#2c398f"}
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
                         :background-color "#2c398f"
                         :padding "0px 10px"
                         :pointer-events "none"}]]
             [:select {:height "36px"
                       :width "172px"
                       :margin-left "14px"
                       :padding "0px 16px"
                       :padding-right "60px"
                       :border "1px solid #47608e"
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

(defstyles menu
           [:.app-menu {:height "492px"
                        :width "300px"
                        :margin-right "160px"
                        :border-radius "0 10px 10px 0"
                        :-webkit-box-shadow "0px 0px 120px 0px rgba(73, 79, 104, 0.11)"
                        :-moz-box-shadow "0px 0px 120px 0px rgba(73, 79, 104, 0.11)"
                        :box-shadow "0px 0px 120px 0px rgba(73, 79, 104, 0.11)"
                        :font-family "proxima-nova"}
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
                 :color "#47608e"}
             [:span [:&:before {:display "block"
                                :width "45px"
                                :font-size "22px"
                                :font-weight "bold"
                                :color "#88e4e6"}]]]])

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
                         :color "#47608e"}
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
           [:.bids {:color "#47608e"}
            [:h2 {:margin-top "0px"
                  :margin-bottom "16px"
                  :font-family "filson-soft"
                  :font-weight "normal"}]
            [:.bid {:line-height "27px"
                    :font-family "proxima-soft"}
             [:a {:font-weight "bold"
                  :color "#47608e"
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
                         :color "#47608e"}
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
           [:.footer {:margin "64px 190px 0px 410px"
                      :font-family "filson-soft"
                      :color "#47608e"}
            [:.footer-container {:margin-top "25px"
                                    :display "flex"
                                    :justify-content "space-between"}]
            [:.description {:margin-top 0
                            :font-size "18px"
                            :line-height "32px"}]
            [:.district0x-network {:font-size "11px"}
             [:a {:color "#47608e"
                  :text-decoration-color "#d1d7e3"}]]
            [:ul {:margin "0"
                  :padding "0 100px"}
             [:li {:list-style-type "none"}]
             [:a {:font-size "13px"
                  :line-height "32px"
                  :font-family "proxima-soft"
                  :color "#47608e"
                  :text-decoration "none"}]]
            [:.button {:display "inline-block"
                       :width "42px"
                       :line-height "42px"
                       :padding "0px"
                       :border-radius "50%"
                       :font-size "16px"
                       :text-align "center"
                       :vertical-align "middle"
                       :text-decoration "none"
                       :margin-left "10px"}]
            [:.icon-github.button {:font-size "18px"}]
            [:.icons {:flex-shrink 0}]])

(defstyles main
           [:body {:min-width "1250px"
                   :margin 0
                   :margin-bottom "70px"
                   :font-family "sans-serif"}]
           [:.app-content {:margin-right "190px"
                           :display "flex"
                           :justify-content "space-between"}]
           [:.button {:border "none"
                      :border-radius "28px"
                      :font-size "13px"
                      :padding "10px 32px"
                      :font-family "filson-soft"
                      :background-color "#2c398f"
                      :color "#7cf8fa"
                      :cursor "pointer"
                      :-webkit-box-shadow "0px 4px 0px 0px #E8EAF2"
                      :-moz-box-shadow "0px 4px 0px 0px #E8EAF2"
                      :box-shadow "0px 4px 0px 0px #E8EAF2"}]
           [:img.logo {:width "200px"}]
           notification
           top
           menu
           app-page
           bids
           bids-form
           votes
           footer)