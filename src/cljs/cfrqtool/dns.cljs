(ns cfrqtool.dns
  (:require [re-frame.core :as rf]))

(defn dns-page []
  (fn []
    [:div.container
     [:div.row
      [:div.col-sm-12
       [:p "DNS resource editor."]]]]))