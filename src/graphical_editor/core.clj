(ns graphical-editor.core
  (:require #_[clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str])
  (:gen-class))


(def state& (atom nil))

#_(def validate-arguments)


#_(defn program-loop []
   (let [[cmd & more] (read-line)]
     (condp = [cmd (count more)]
       ["I" 3])))





(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
