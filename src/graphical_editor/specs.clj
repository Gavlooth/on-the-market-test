(ns graphical-editor.specs
  (:require [clojure.spec.alpha :as s]))



;;;specs for command input

;; Check if string can be coersed to integer
(defn ->integer? [a-string]
  (re-matches #"[0-9]*" a-string))

;; Color should be RGB
(def color? #{"R" "G" "B" "r" "g" "b"})
 

(s/def ::Color)

(s/def ::I  (s/and  #(= 2) #(s/* ->integer?)))


(s/def ::C  #(= (count %) 0))
;; (s/form)

(s/def ::L  (s/and #(= (count %) 3) (s/tuple ->integer?  ->integer?  color?)))


(s/def ::V  (s/and #(= (count %) 4) (s/tuple ->integer? ->integer?  ->integer?  color?)))


(s/def ::H  (s/and #(= (count %) 3) (s/tuple ->integer? ->integer?  ->integer?  color?)))


(s/def ::F  (s/and #(= (count %) 3) (s/tuple ->integer?  ->integer?  color?)))


(s/def ::S  #(>= (count %) 3))


(s/def ::X  #(>= (count %) 3))

