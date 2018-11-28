(ns district0x-tasks.ui.components.font-icons)

(defn icon-paths [n]
  (->> (range 1 (inc n))
       (map #(vector (keyword (str "span.path" %))))))

(defn district0x-logo []
  [:img.logo {:src "icons/logo.svg"}])

(defn icon-mechanics []
  (into [:span.icon-mechanics]
        (icon-paths 43)))
