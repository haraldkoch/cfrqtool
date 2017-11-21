(ns cfrqtool.pages.blacklist
  (:require [ajax.core :refer [GET POST]]
            [cfrqtool.misc :refer [render-table]]
            [re-frame.core :as rf]))

(defn input-field [param data-atom]
  [:input.form-control
   {:type        :text :value (get @data-atom param)
    :placeholder (name param)
    :on-change   #(swap! data-atom assoc param (.-value (.-target %)))}])

(defn add-entry-form [_ _]
  (fn [show-entry-form form-data]
    (if @show-entry-form
      [:div.row
       [:div.col-sm-12
        [:div.input-group
         [input-field :ipaddr form-data]
         [input-field :type form-data]
         [input-field :date form-data]
         [input-field :description form-data]
         [:button.btn.btn-primary
          {:on-click #(do (rf/dispatch [:hide-form])
                          (rf/dispatch [:add-blacklist-entry! @form-data]))}
          "add entry"]]]])))

(defn blacklist-page []
  (let [show-entry-form (rf/subscribe [:show-entry-form?])
        form-data (atom {:ipaddr nil :type nil :date nil :description nil})
        blacklist (rf/subscribe [:blacklist])
        error (rf/subscribe [:error])
        blacklist-loaded? (rf/subscribe [:blacklist-loaded?])]
    (fn []
      [:div
       (if @error
         [:div.row
          [:div.col.sm-12
           [:h2 "Error"]
           [:div (str @error)]]])
       [add-entry-form show-entry-form form-data]
       [:div.row
        [:div.col-sm-2
         [:button.btn.btn-primary
          {:on-click #(if @show-entry-form
                        (rf/dispatch [:hide-form])
                        (rf/dispatch [:show-form]))}
          (if @show-entry-form "hide" "new entry")]]
        [:div.col-sm-10
         (if-not @blacklist-loaded?
           [:div "Loading blacklist entries..."]
           [render-table @blacklist [:ipaddr :type :date :description]])]
        ]])))

(rf/reg-sub
  :show-entry-form?
  (fn [db _]
    (:show-entry-form? db)))

(rf/reg-sub
  :blacklist
  (fn [db _]
    (:blacklist db)))

(rf/reg-sub
  :blacklist-loaded?
  (fn [db _]
    (:blacklist-loaded? db)))

(rf/reg-event-db
  :show-form
  (fn [db _]
    (assoc db :show-entry-form? true)))

(rf/reg-event-db
  :hide-form
  (fn [db _]
    (assoc db :show-entry-form? nil)))

(rf/reg-event-fx
  :fetch-blacklist
  (fn [{:keys [db]} _]
    {:http {:method        GET
            :url           "/blacklist/entries"
            :success-event [:process-blacklist-response]
            :error-event   [:bad-response]}
     :db   (assoc db :blacklist-loaded? false)}))

(rf/reg-event-db
  :process-blacklist-response
  (fn
    [db [_ response]]
    (-> db
        (assoc :blacklist-loaded? true)
        (assoc :blacklist (js->clj response)))))


(rf/reg-event-fx
  :add-blacklist-entry!
  (fn
    [{:keys [db]} [_ form-data]]
    {:http {:method        POST
            :url           "/blacklist/entries"
            :success-event [:process-add-response]
            :error-event   [:bad-response]
            :ajax-map      {:params form-data}}
     :db   (assoc db :loading? true)}))

(rf/reg-event-db
  :process-add-response
  (fn
    [db [_ response]]
    (-> db
        (assoc :loading? false)
        (assoc :data (js->clj response)))))