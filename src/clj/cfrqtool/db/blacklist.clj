(ns cfrqtool.db.blacklist
  (:require
    [clj-time.jdbc]
    [clojure.java.jdbc :as jdbc]
    [conman.core :as conman]
    [cfrqtool.config :refer [env]]
    [mount.core :refer [defstate]])
  (:import [java.sql
            BatchUpdateException
            PreparedStatement]))

(defstate ^:dynamic *db*
           :start (conman/connect! {:jdbc-url (env :blacklistdb-url)})
           :stop (conman/disconnect! *db*))

(conman/bind-connection *db* "sql/blacklist_queries.sql")


(declare get-all-entries get-entry find-entry create-entry! update-entry! delete-entry!)