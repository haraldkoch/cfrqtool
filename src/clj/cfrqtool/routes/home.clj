(ns cfrqtool.routes.home
  (:require [cfrqtool.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]))

(defn home-page []
  (layout/render "home.html"))

(defmacro response-handler [fn-name args & body]
  `(defn ~fn-name ~args
     (try
       (response/ok (do ~@body))
       (catch Exception e#
         (log/error "error handling request" e#)
         (response/internal-server-error {:error (.getMessage e#)})))))

(defroutes home-routes
  (GET "/" []
       (home-page))
  (GET "/docs" []
       (-> (response/ok (-> "docs/docs.md" io/resource slurp))
           (response/header "Content-Type" "text/plain; charset=utf-8"))))

