(ns district.ui.component.font-icons)

(defn icon-paths [n]
  (->> (range 1 (inc n))
       (map #(vector (keyword (str "span.path" %))))))

(defn district0x-logo []
  ;[:img {:src "icons/logo.svg"}]
  (into [:span.icon-planet]
        (icon-paths 5)))

(defn district0x-logo-with-slogan []
  [:div.district0x-logo
   [district0x-logo]
   [:span.district0x "district0x"]
   [:span.title "Tasks"]])

(defn icon-mechanics []
  (into [:span.icon-mechanics]
        (icon-paths 43)))
