(ns district0x-tasks.ui.components.app-layout
  (:require
    [district.ui.component.active-account :refer [active-account]]
    [district.ui.component.active-account-balance :refer [active-account-balance]]
    [district.ui.component.form.input :as inputs :refer [text-input*]]
    [re-frame.core :refer [subscribe dispatch]]
    [reagent.core :as r]))


(defn app-layout []
  (fn [{:keys [:meta :search-atom]} & children]
    ))
