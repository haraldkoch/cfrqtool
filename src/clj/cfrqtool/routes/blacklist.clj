(ns cfrqtool.routes.blacklist
  (:require [cfrqtool.db.blacklist :as blacklist]
            [cfrqtool.layout :as layout]
            [cfrqtool.routes.home :refer [response-handler]]
            [cfrqtool.validation :refer [validate-blacklist]]
            [compojure.core :refer [defroutes GET POST PUT DELETE]]
            [ring.util.http-response :as response]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.network.ip :as ip]
            [clojure.tools.logging :as log])
  (:import (java.net InetAddress)
           (java.util Date)))

(defn- default-prefix-length [ip]
  (let [addr (ip/make-ip-address ip)
        version (ip/version addr)]
    (cond
      (= version 4) 32
      (= version 6) 128)))

(defn- to-ip-map
  ([ip] (to-ip-map ip (default-prefix-length ip)))
  ([ip prefix] {:ip ip :prefix prefix}))

(defn- parse-ipaddr [ipaddr]
  (apply to-ip-map (str/split ipaddr #"/")))

(defn create-entry! [params]
  (blacklist/create-entry! params)
  (str "blacklist entry " (:ip params) " created successfully"))

(defn do-create-entry! [{:keys [:params]}]
  ; FIXME parse :ipaddr into :ip and :prefix
  (println "params:" params)
  (if-let [errors (validate-blacklist params)]
    (response/internal-server-error {:error errors})
    (let
      [id
       (-> params
           (merge (parse-ipaddr (:ipaddr params)))
           (merge {:date (Date.)})
           (dissoc :ipaddr)
           (blacklist/create-entry!)
           (:generated_key))]
      (blacklist/get-entry {:id id}))))

(defn update-entry! [params]
  (println "params:" params)
  (-> params
      (merge (parse-ipaddr (:ipaddr params)))
      (merge {:date (Date.)})
      (dissoc :ipaddr)
      (blacklist/update-entry!))
  (blacklist/get-entry {:id (:id params)}))

(defn do-update-entry! [_ {:keys [:params]}]
  (if-let [errors (validate-blacklist params)]
    (response/internal-server-error {:error errors})
    (update-entry! params)))

(defn do-delete-entry! [id]
  (blacklist/delete-entry! id)
  (str "entry " id " deleted."))

(response-handler blacklist-fetch [] (blacklist/get-all-entries))
(response-handler blacklist-get [id] (blacklist/get-entry {:id id}))
(response-handler blacklist-find [ip] (blacklist/find-entry {:ip ip}))
(response-handler blacklist-create [request] (do-create-entry! request))
(response-handler blacklist-update [id request] (do-update-entry! id request))
(response-handler blacklist-delete [id] (do-delete-entry! {:id id}))

(defroutes blacklist-routes
  (GET "/blacklist/entries" [] (blacklist-fetch))
  (GET "/blacklist/entries/:id" [id] (blacklist-get id))
  (GET "/blacklist/entries/?ip=:ip" [ip] (blacklist-find ip))
  (POST "/blacklist/entries" request (blacklist-create request))
  (PUT "/blacklist/entries/:id" [id :as request] (blacklist-update id request))
  (DELETE "/blacklist/entries/:id" [id] (blacklist-delete id)))
