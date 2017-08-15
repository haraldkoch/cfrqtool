(ns cfrqtool.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [cfrqtool.layout :refer [error-page]]
            [cfrqtool.routes.home :refer [home-routes]]
            [cfrqtool.routes.dns :refer [dns-routes]]
            [compojure.route :as route]
            [cfrqtool.env :refer [defaults]]
            [mount.core :as mount]
            [cfrqtool.middleware :as middleware]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    (-> (routes #'home-routes #'dns-routes)
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
