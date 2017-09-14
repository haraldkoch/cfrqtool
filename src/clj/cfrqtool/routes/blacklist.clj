(ns cfrqtool.routes.blacklist
  (:require [cfrqtool.db.blacklist :as blacklist]
            [cfrqtool.layout :as layout]
            [cfrqtool.routes.home :refer [response-handler]]
            [cfrqtool.validation :refer [validate-blacklist]]
            [compojure.core :refer [defroutes GET POST PUT DELETE]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]))

(defn create-entry! [params]
  (blacklist/create-entry! params)
  (str "blacklist entry " (:ip params) " created successfully"))

(defn do-create-entry! [{:keys [:params]}]
  (if-let [errors (validate-blacklist params)]
    (response/internal-server-error {:error errors})
    (create-entry! params)))


(defn update-entry! [params]
  (blacklist/update-entry! params)
  (str "blacklist entry " (:ip params) " updated"))

(defn do-update-entry! [_ {:keys [:params]}]
  (if-let [errors (validate-blacklist params)]
    (response/internal-server-error {:error errors})
    (update-entry! params)))


(response-handler blacklist-fetch [] (blacklist/get-all-entries))
(response-handler blacklist-get [id] (blacklist/get-entry {:id id}))
(response-handler blacklist-find [ip] (blacklist/find-entry {:ip ip}))
(response-handler blacklist-create [request] (do-create-entry! request))
(response-handler blacklist-update [id request] (do-update-entry! id request))
(response-handler blacklist-delete [id] (blacklist/delete-entry! {:id id}))

(defroutes blacklist-routes
  (GET "/blacklist/entries" [] (blacklist-fetch))
  (GET "/blacklist/entries/:id" [id] (blacklist-get id))
  (GET "/blacklist/entries/?ip=:ip" [ip] (blacklist-find ip))
  (POST "/blacklist/entries" request (blacklist-create request))
  (PUT "/blacklist/entries/:id" [id :as request] (blacklist-update id request))
  (DELETE "/blacklist/entries/:id" [id] (blacklist-delete id)))
