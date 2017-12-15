(ns cfrqtool.status
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
  :set-status
  (fn [db [_ status]]
    (assoc db :status status)))

(rf/reg-event-db
  :clear-status
  (fn [db _]
    (dissoc db :status)))

(rf/reg-event-db
  :set-error
  (fn [db [_ error]]
    (assoc db :error error)))

(rf/reg-event-db
  :clear-error
  (fn [db _]
    (dissoc db :error)))

(rf/reg-event-db
  :clear-loading
  (fn [db _]
    (dissoc db :loading? :error :should-be-loading?)))

(rf/reg-event-db
  :set-loading-for-real-this-time
  (fn [{:keys [should-be-loading?] :as db} _]
    (if should-be-loading?
      (assoc db :loading? true)
      db)))

(rf/reg-event-fx
  :set-loading
  (fn [{db :db} _]
    {:dispatch-later [{:ms 100 :dispatch [:set-loading-for-real-this-time]}]
     :db             (-> db
                         (assoc :should-be-loading? true)
                         (assoc :loading? true)
                         (dissoc :error))}))

(rf/reg-sub
  :status
  (fn [db _]
    (:status db)))

(rf/reg-sub
  :error
  (fn [db _]
    (:error db)))

(rf/reg-sub
  :loading?
  (fn [db _]
    (:loading? db)))

(defn error-modal []
  (when-let [error @(rf/subscribe [:error])]
    [:div.modal-wrapper
     [:div.modal-backdrop
      {:on-click (fn [event]
                   (do
                     (rf/dispatch [:clear-error])
                     (.preventDefault event)
                     (.stopPropagation event)))}]
     [:div.modal-child {:style {:width "70%"}}
      [:div.modal-content.panel-danger
       [:div.modal-header.panel-heading
        [:button.close
         {:type                    "button" :title "Cancel"
          :on-click                #(rf/dispatch [:clear-error])
          :dangerouslySetInnerHTML {:__html "&times;"}}]
        [:h4.modal-title "An anomaly has been detected. Please remain calm."]]
       [:div.modal-body
        [:div [:b error]]]
       [:div.modal-footer
        [:button.btn.btn-default
         {:type     "button" :title "Ok"
          :on-click #(rf/dispatch [:clear-error])}
         "Ok"]]]]]))

(defn status-message []
  (let [status (rf/subscribe [:status])]
    (when-let [status-text @status]
      [:div.row
       [:div.col-sm-11
        [:div.alert.alert-success status-text]]
       [:div-col-sm-1
        [:button.btn.btn-success
         {:type                    "button"
          :title                   "clear"
          :on-click                #(rf/dispatch [:clear-status])
          :dangerouslySetInnerHTML {:__html "&times;"}}]]])))

(defn loading-throbber
  []
  (let [loading? (rf/subscribe [:loading?])]
    (when @loading?
      [:div#throbber
       [:div.spinner
        [:div.bounce1]
        [:div.bounce2]
        [:div.bounce3]]])))
