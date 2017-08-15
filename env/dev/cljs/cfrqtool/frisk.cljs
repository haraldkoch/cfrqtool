(ns syseng-support.frisk
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as r]
            [re-frame.core :refer [register-sub subscribe]]
            [datafrisk.core :as d]))

(register-sub
  :full-db
  (fn [db _] (reaction @db)))

(defn frisk-component []
  (let [db (subscribe [:full-db])]
    (fn []
      [d/DataFriskShell @db])))
