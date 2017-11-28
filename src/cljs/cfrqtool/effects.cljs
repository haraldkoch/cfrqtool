(ns cfrqtool.effects
  (:require [re-frame.core :as rf :refer [dispatch reg-fx reg-event-fx]]))

(reg-fx
  :http
  (fn [{:keys [method
               url
               success-event
               error-event
               ignore-response-body
               ajax-map]
        :or {error-event [:ajax-error]
             ajax-map {}}}]
    #_(dispatch [:set-loading])
    (println "http event method " method " to " url)
    (method url (merge
                  {:handler (fn [response]
                              (when success-event
                                (dispatch (if ignore-response-body
                                            success-event
                                            (conj success-event response))))
                              #_(dispatch [:unset-loading]))
                   :error-handler (fn [error]
                                    (dispatch (conj error-event error))
                                    #_(dispatch [:unset-loading]))}
                  ajax-map))))
