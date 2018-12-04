(ns district0x-tasks.ui.components.styles
  (:require [stylefy.core :as stylefy]))

(def button {:border "none"                                 ; Firefox
             :border-radius "28px"
             :font-size "13px"
             :padding "10px 32px"
             :font-family "filson-soft"
             :background-color "#2c398f"
             :color "#7cf8fa"
             :cursor "pointer"
             :box-shadow "0px 4px 0px 0px #E8EAF2"})

(def votes-line {:display "flex"
                 ::stylefy/sub-styles {:hr {:margin 0
                                            :height "8px"
                                            :border "none"
                                            :background-color "#394596"
                                            ::stylefy/mode {:first-child {:background-color "#04ffcc"}
                                                            :last-child {:flex-grow 1}}}}})

(def top-bar {:display "flex"
              :justify-content "space-between"
              :align-items "center"
              :height "80px"
              :margin-bottom "66px"
              :padding-left "20px"
              :padding-right "50px"
              :box-shadow "0px 0px 120px 0px rgba(73,79,104,0.11)"
              ::stylefy/sub-styles {:right {:display "flex"
                                            :align-items "center"
                                            :font-size "16px"
                                            :font-family "filson-soft"
                                            :color "#2c398f"}}})

(def select-account {:position "relative"
                     ::stylefy/mode {:after {:position "absolute"
                                             :content "'\\e911'"
                                             :transform "rotate(90deg)"
                                             :top "0"
                                             :right "0"
                                             :color "#69c9e0"
                                             :font-size "13px"
                                             :line-height "36px"
                                             :border-radius "28px"
                                             :background-color "#2c398f"
                                             :padding "0px 10px"
                                             :pointer-events "none"}}
                     ::stylefy/sub-styles {:select {:height "36px"
                                                    :width "172px"
                                                    :margin-left "14px"
                                                    :padding "0px 16px"
                                                    :padding-right "60px"
                                                    :border "1px solid #47608e"
                                                    :background-color "white"
                                                    :border-radius "28px"
                                                    :appearance "none"
                                                    :box-shadow "0px 4px 0px 0px #E8EAF2"
                                                    :font-size "14px"
                                                    :color "#c7cdd9"
                                                    :display "block"
                                                    ::stylefy/mode {"::-ms-expand" {:display "none"}}}}})

(def menu {:height "492px"
           :width "300px"
           :margin-right "160px"
           :border-radius "0 10px 10px 0"
           :box-shadow "0px 0px 120px 0px rgba(73, 79, 104, 0.11)"
           :font-family "proxima-nova"
           ::stylefy/sub-styles {:list {:margin 0
                                        :padding 0
                                        :width "250px"}
                                 :item {:list-style-type "none"
                                        ::stylefy/mode {":not(:first-child)" {:border-top "1px solid #fbfbfb"}
                                                        ":not(:last-child)" {:border-bottom "1px solid #f0f2f6"}}}
                                 :item-link {:display "flex"
                                             :align-items "center"
                                             :padding "18px 20px"
                                             :font-size "16px"
                                             :text-decoration "none"
                                             :color "#47608e"}
                                 :icon {::stylefy/mode {:before {:display "block"
                                                                 :width "45px"
                                                                 :font-size "22px"
                                                                 :font-weight "bold"
                                                                 :color "#88e4e6"}}}}})