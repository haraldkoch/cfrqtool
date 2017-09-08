(ns cfrqtool.routes.dns
  (:require [cfrqtool.db.core :as configdb]
            [cfrqtool.layout :as layout]
            [cfrqtool.routes.home :refer [response-handler]]
            [clj-dns.core :as dns]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]))

(defn reverse [ip]
  (str (dns/ip-to-reverse-str ip)))

(response-handler addhost [{:keys [params] session :session}]
  (log/info "addhost: " params))

(defroutes dns-routes
  (POST "/dns/addhost" request (addhost request)))
