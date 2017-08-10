(ns cfrqtool.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [cfrqtool.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[cfrqtool started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[cfrqtool has shut down successfully]=-"))
   :middleware wrap-dev})
