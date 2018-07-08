(ns user
 (:require [clojure.tools.namespace.repl :as tn]))


(defn reset [] (tn/refresh-all))
