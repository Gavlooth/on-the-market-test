(ns graphical-editor.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

;;;specs for command input

;; Check if string can be coersed to integer
(defn ->integer? [a-string]
 (re-matches #"[0-9]*" a-string))

 ;; coerse to integer
(def ->int #(Integer/parseInt %))


;; Color should be RGB

(def color? #{"R" "G" "B" "r" "g" "b"})

;; Specs for command input arguments

(s/def ::I  (s/and  #(= 2 (count %)) (s/tuple ->integer? ->integer?)))

;; Image size restrictions
(s/def ::in-range (s/and #(>= % 1) #(<= % 250)))

(s/def ::C  #(= (count %) 0))

(s/def ::L  (s/and #(= (count %) 3) (s/tuple ->integer?  ->integer?  color?)))

(s/def ::V  (s/and #(= (count %) 4) (s/tuple ->integer? ->integer?  ->integer?  color?)))

(s/def ::H  (s/and #(= (count %) 4) (s/tuple ->integer? ->integer?  ->integer?  color?)))

(s/def ::F  (s/and #(= (count %) 3) (s/tuple ->integer?  ->integer?  color?)))

(s/def ::S  #(>= (count %) 3))

(s/def ::X  #(>= (count %) 3))


;;DRY function for argument coersion
(defn- coerce-helper [x]
  (let [head (pop x) tail (peek x)]
   (vec
    (concat (map  ->int head)
            (str/upper-case tail)))))

;; coerse command line arguments to the appropriate input types
(defmulti coerce-arguments (fn [x more] (keyword x)))

(defmethod coerce-arguments :I [_ more]
  (if (s/valid? ::I more)
   (let [[X Y] (mapv ->int more)]
    (if (and (s/valid? ::in-range Y) (s/valid? ::in-range X))
     [:I [X Y]]
     [:invalid-arguments]))
   [:invalid-arguments]))

(defmethod coerce-arguments :L [x more]
 (if (s/valid? ::L more)
    [:L  (coerce-helper more)]
    [:invalid-arguments]))

(defmethod coerce-arguments :V [x   more]
 (if (s/valid? ::V more)
   [:V  (coerce-helper more)]
   [:invalid-arguments]))

(defmethod coerce-arguments :H [x more]
 (if (s/valid? ::H more)
  [ :H  (coerce-helper more)]
  [:invalid-arguments]))

(defmethod coerce-arguments :F [x more]
 (if (s/valid? ::F more)
  [:F  (coerce-helper more)]
  [:invalid-arguments]))

(defmethod coerce-arguments :C [_ _]
  [:C])

(defmethod coerce-arguments :S [_ _]
  [:S])

(defmethod coerce-arguments :default [_ _]
 [:unknown-command])



