(ns cfrqtool.dns
  (:require [ajax.core :refer [GET POST]]
            [re-frame.core :as rf]))

(defn dns-page []
  (fn []
    [:div.container
     [:div.row
      [:div.col-sm-12
       [:p "DNS resource editor."]]]
     [:div.row
      [:div.col-sm-4
       [:input.form-control
        {:type        :text
         :placeholder "hostname"
         :on-change   #(rf/dispatch [:dns-set-hostname (.-value (.-target %))] )}]]
      [:div.col-sm-6
       [:input.form-control
        {:type        :text
         :placeholder "IP address"
         :on-change   #(rf/dispatch [:dns-set-ip (.-value (.-target %))] )}]]
      [:div.col-sm-2
       [:button.btn.btn-sm {:class "button-class"
              :on-click  #(rf/dispatch [:dns-add-host])}
        "Add Host"]]]]))

(rf/register-handler
  :dns-set-hostname
  (fn [db [_ hostname]]
    (assoc db :hostname hostname)))

(rf/register-handler
  :dns-set-ip
  (fn [db [_ ip]]
    (assoc db :ip ip)))

(rf/register-handler
  :dns-add-host             ;; <-- the button dispatched this id
  (fn
    [db _]

    ;; kick off the GET, making sure to supply a callback for success and failure
    (POST
      "/dns/addhost"
      {:params        {:hostname (:hostname db) :ip (:ip db)}
       :handler       #(rf/dispatch [:process-response %1])   ;; further dispatch !!
       :error-handler #(rf/dispatch [:bad-response %1])})     ;; further dispatch !!

    ;; update a flag in `app-db` ... presumably to trigger UI changes
    (assoc db :loading? true)))    ;; pure handlers must return a db

(rf/register-handler               ;; when the GET succeeds 
  :process-response             ;; the GET callback dispatched this event  
  (fn
    [db [_ response]]           ;; extract the response from the dispatch event vector
    (-> db
        (assoc :loading? false) ;; take away that modal 
        (assoc :data (js->clj response)))))  ;; fairly lame processing