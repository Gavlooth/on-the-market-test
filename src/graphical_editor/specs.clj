(ns graphical-editor.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

;;;specs for command input

;; Check if string can be coersed to integer
(defn ->integer? [a-string]
 (re-matches #"[0-9]*" a-string))



(s/def ::skata ->integer?)
(s/conform ::skata "q22")

;; Color should be RGB

(def color? #{"R" "G" "B" "r" "g" "b"})

(s/def ::I  (s/and  #(= 2 (count %)) (s/tuple ->integer? ->integer?)))


(s/def ::C  #(= (count %) 0))
;; (s/form)

(s/def ::L  (s/and #(= (count %) 3) (s/tuple ->integer?  ->integer?  color?)))


(s/def ::V  (s/and #(= (count %) 4) (s/tuple ->integer? ->integer?  ->integer?  color?)))


(s/def ::H  (s/and #(= (count %) 3) (s/tuple ->integer? ->integer?  ->integer?  color?)))


(s/def ::F  (s/and #(= (count %) 3) (s/tuple ->integer?  ->integer?  color?)))



(s/def ::S  #(>= (count %) 3))

(s/def ::X  #(>= (count %) 3))



(defn- coerce-helper [x]
  (let [head (pop x) tail (peek x)]
   (vec
    (concat (map #(Integer/parseInt %) head)
            (str/upper-case tail)))))

(defmulti coerce-arguments (fn [x more] (keyword x)))

(defmethod coerce-arguments :I [_ more]
  (if (s/valid? ::I more)
   [:I (mapv #(Integer/parseInt %) more)]
   [:invalid-args]))


(defmethod coerce-arguments :C [x more]
  [:C])


(defmethod coerce-arguments :L [x more]
 (if (s/valid? ::L more)
    [:L  (coerce-helper more)]
    [:invalid-args]))

(defmethod coerce-arguments :V [x   more]
 (if (s/valid? ::V more)
   [:V  (coerce-helper more)]
   [:invalid-args]))


(defmethod coerce-arguments :H [x more]
 (if (s/valid? ::H more)
   [ :H  (coerce-helper more)]
  [:invalid-args]))

(defmethod coerce-arguments :F [x more]
 (if (s/valid? ::F more)
  [:F  (coerce-helper more)]
  [:invalid-args]))

(defmethod coerce-arguments :S [x more]
  [:S])


