(ns cfrqtool.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[cfrqtool started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[cfrqtool has shut down successfully]=-"))
   :middleware identity})
