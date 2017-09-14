(ns cfrqtool.handlers
  (:require [cfrqtool.db :as db]
            [re-frame.core :as rf]))

(rf/reg-event-db
  :initialize-db
  (fn [_ _]
    db/default-db))

(rf/reg-event-db
  :set-active-page
  (fn [db [_ page]]
    (assoc db :page page)))

(rf/reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

(reg-event-db
  :initialize
  (fn [db _]
    (dispatch [:fetch-blacklist])))

(reg-event-db
  :bad-response
  (fn [db [_ error]]
    (assoc db :error error)))
