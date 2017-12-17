(ns cfrqtool.pages.blacklist
  (:require [ajax.core :as ajax]
            [cfrqtool.misc :as misc]
            [reagent.core :as r]
            [re-frame.core :as rf]))

(defn input-field [param data-atom]
  [:input.form-control
   {:type        :text
    :value       (get @data-atom param)
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

(defn blacklist-row [_]
  (let [edit (r/atom false)]
    (fn [entry]
      (if @edit
        (let [form-data (r/atom (merge entry {:date (.getTime (js/Date.))}))]
          [:tr
           [:td [input-field :ipaddr form-data]]
           [:td [input-field :type form-data]]
           [:td (misc/fmt-time (:date entry))]
           [:td [input-field :description form-data]]
           [:td
            [:button.btn.btn-primary
             {:on-click #(do
                           (rf/dispatch [:update-blacklist-entry! @form-data])
                           (reset! edit (not @edit)))}
             "Save"]
            [:button.btn.btn-secondary
             {:on-click #(reset! edit (not @edit))}
             "Cancel"]]])
        [:tr
         [:td (:ipaddr entry)]
         [:td (:type entry)]
         [:td (misc/fmt-time (:date entry))]
         [:td (:description entry)]
         [:td [:button.btn.btn-primary
               {:on-click #(reset! edit (not @edit))}
               "edit"]]]))))

(defn blacklist-table [items]
  [:table.table.table-striped.table-condensed
   [:thead [:tr [:th "IP"] [:th "Type"] [:th "Date"] [:th "Description"] [:th]]]
   (into [:tbody]
         (for [entry items]
           ^{:key (:id entry)}
           [blacklist-row entry]))])

(defn blacklist-page []
  (let [show-entry-form (rf/subscribe [:show-entry-form?])
        form-data (r/atom {:ipaddr nil :type nil :date nil :description nil})
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
          (if @show-entry-form "hide" "new entry")]]]
       [:div.row
        [:div.col-sm-12
         (if-not @blacklist-loaded?
           [:div "Loading blacklist entries..."]
           [blacklist-table @blacklist])]
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
  (fn [{db :db} _]
    {:http-xhrio {:method          :get
                  :uri             "/blacklist/entries"
                  :format          (ajax/transit-request-format)
                  :response-format (ajax/transit-response-format)
                  :on-success      [:process-blacklist-response]
                  :on-failure      [:http-error]}
     :db         (assoc db :blacklist-loaded? false)}))

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
    [{db :db} [_ form-data]]
    {:http-xhrio {:method          :post
                  :uri             "/blacklist/entries"
                  :params          form-data
                  :format          (ajax/transit-request-format)
                  :response-format (ajax/transit-response-format)
                  :on-success      [:process-add-response]
                  :on-failure      [:http-error]}
     :db         (assoc db :loading? true)}))

(defn replace-by-id [sequence entry]
  (map #(if (= (:id %) (:id entry)) entry %) sequence))

(rf/reg-event-db
  :process-add-response
  (fn
    [db [_ response]]
    (-> db
        (assoc :loading? false)
        (update :blacklist conj response))))

(rf/reg-event-fx
  :update-blacklist-entry!
  (fn
    [{db :db} [_ form-data]]
    {:http-xhrio {:method          :put
                  :uri             (str "/blacklist/entries/" (:id form-data))
                  :params          form-data
                  :format          (ajax/transit-request-format)
                  :response-format (ajax/transit-response-format)
                  :on-success      [:process-update-response]
                  :on-failure      [:http-error]}
     :db         (assoc db :loading? true)}))

(rf/reg-event-db
  :process-update-response
  (fn
    [db [_ response]]
    (-> db
        (assoc :loading? false)
        (update :blacklist replace-by-id response))))