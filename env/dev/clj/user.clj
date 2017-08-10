(ns user
  (:require [mount.core :as mount]
            [cfrqtool.figwheel :refer [start-fw stop-fw cljs]]
            cfrqtool.core))

(defn start []
  (mount/start-without #'cfrqtool.core/repl-server))

(defn stop []
  (mount/stop-except #'cfrqtool.core/repl-server))

(defn restart []
  (stop)
  (start))


