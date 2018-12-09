(ns district0x-tasks.styles.base.media
  (:require [garden.def :refer [defstyles]]
            [garden.stylesheet :refer [at-media]]
            [garden.units :refer [px]]))

(def breakpoints {:mobile 320
                  :tablet 768
                  :computer 992
                  :large 1200
                  :wide 1920})

(defn- media [{:keys [min-width max-width]} css]
  (let [media-query (cond-> {:screen true}
                            min-width (assoc :min-width (str min-width "px"))
                            max-width (assoc :max-width (str max-width "px")))]
    (at-media media-query [:& css])))

(defn min-tablet [css]
  (media {:min-width (inc (:tablet breakpoints))} css))

(defn max-tablet [css]
  (media {:max-width (:tablet breakpoints)} css))