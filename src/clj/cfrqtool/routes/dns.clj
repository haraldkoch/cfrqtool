(ns cfrqtool.routes.dns
  (:require [cfrqtool.layout :as layout]
            [cfrqtool.routes.home :refer [response-handler]]
             [compojure.core :refer [defroutes GET POST]]
             [ring.util.http-response :as response]
             [clojure.java.io :as io]
             [clojure.tools.logging :as log]))

(response-handler addhost [{:keys [params] session :session}]
  (log/info "addhost: " params))

(defroutes dns-routes
  (POST "/dns/addhost" request (addhost request)))
