(ns ^:figwheel-no-load cfrqtool.app
  (:require [cfrqtool.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(core/init!)
