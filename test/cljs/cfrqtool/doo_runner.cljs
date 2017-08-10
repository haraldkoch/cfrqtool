(ns cfrqtool.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [cfrqtool.core-test]))

(doo-tests 'cfrqtool.core-test)

