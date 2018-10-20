(ns district0x-tasks.ui.home.page
  (:require
   [district.ui.component.page :refer [page]]
   [district.ui.graphql.subs :as gql]
   [district0x-tasks.ui.components.app-layout :refer [layout]]
   [re-frame.core :refer [subscribe dispatch]]
   [reagent.core :as r]
   [react-infinite]
   [district0x-tasks.ui.utils :as utils]))

(defmethod page :default []
  (fn []
    [layout]))


