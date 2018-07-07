(ns user
 (:require [clojure.tools.namespace.repl :as tn]
           [graphical-editor.core :as ge]))


(defn reset [] (tn/refresh-all))
