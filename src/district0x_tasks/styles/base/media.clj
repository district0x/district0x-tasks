(ns district0x-tasks.styles.base.media
  (:require [garden.def :refer [defstyles]]
            [garden.stylesheet :refer [at-media]]
            [garden.units :refer [px]]))

(def breakpoints {:mobile 320
                  :tablet 768
                  :computer 992
                  :large 1200
                  :wide 1920})

(defn media [{:keys [min-width max-width]} css]
  (let [min-width (some-> (get breakpoints min-width)
                          (inc))
        max-width (get breakpoints max-width)
        media-query (cond-> {:screen true}
                            min-width (assoc :min-width (px min-width))
                            max-width (assoc :max-width (px max-width)))]
    (at-media media-query [:& css])))

(defn min-tablet [css]
  (media {:min-width :tablet} css))

(defn max-tablet [css]
  (media {:max-width :tablet} css))