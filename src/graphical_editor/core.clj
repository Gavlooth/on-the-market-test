(ns graphical-editor.core
  (:require #_[clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [ graphical-editor.specs :as specs])
  (:gen-class))

(def state& (atom nil))

(defmacro exception-catcher [body]
  `(try ~body
        (catch IndexOutOfBoundsException e#
          (println "You exeded image dimentions. Please try again"))))



(defn initialize-image  [[dim-X dim-Y]]
 (let [generator-fn (comp vec repeat)]
   (reset! state& (generator-fn dim-X (generator-fn dim-Y 0)))))

(defn clear-image []
 (let  [clear-fn (fn [x] (mapv (fn [y] (mapv (constantly 0) y)) x))]
  (when  @state&
    (swap! state& clear-fn))))

(defn color-pixel [[dim-X dim-Y color]]
 (exception-catcher (swap! state& assoc-in  [dim-X dim-Y] color)))

(defn- replace-subvector [a-vector start end value]
  (loop [i start the-vector (transient a-vector)]
    (if (<= i end)
      (recur (inc i) (assoc! the-vector i value))
      (persistent! the-vector))))

(defn draw-horizontal-segment [[X1 X2 Y  color]]
 (exception-catcher
   (let [start (min X1 X2) end (max X1 X2)]
     (swap! state&  update Y replace-subvector start end color))))

(defn draw-vertical-segment [[X Y1 Y2 color]]
 (exception-catcher
  (let [start (min Y1 Y2) end (max Y1 Y2)]
     (loop [i start state (transient @state&)]
       (if (<= i end)
         (let [current-column (get state i)
               updated-column (assoc current-column X color)]
           (recur (inc i) (assoc! state i updated-column)))
         (reset! state& (persistent! state)))))))


(defn region-R
  ([x](region-R x [-1 -1]))
  ([[X Y  :as cords] previous
     (let [image (deref state&)
           pixel-fn (fn [[x y]]  (get (get image x) y))
           current-pixel  (pixel-fn cords)
           candidates  (remove #{previous} [[(inc X) Y] [(dec X) Y]
                                            [X (inc Y)]  [X (dec Y)]])
           similar-neighbour-pixels  (filter
                                       #(= current-pixel (pixel-fn %))
                                       candidates)]
       (lazy-cat  [cords] (mapcat #(region-R % cords)
                                  similar-neighbour-pixels)))]))



;; Here only the top level vector is turned to transient
;; hence then internal updated doesn use mutation and is hightly inefficient
;; It can be further improved by turning the colument vectors to transients

(defn fill-region-R [[X Y C]]
 (exception-catcher
  (let [the-region-R  (region-R [X Y])
        transient-state (transient @state&)]
     (loop [image transient-state pixels the-region-R]
       (if-let  [[x y]  (first pixels)]
        (recur  (assoc! image x (assoc (get image x) y C)) (rest pixels))
        (reset! state& (persistent! image)))))))



(defn show-image []
  (println "=>")
  (doseq [x @state&]
   (do
    (doseq [y x] (print y))
    (flush)
    (println))))


#_(do
    (initialize-image [10 20])
    (color-pixel [1 1 "R"])
    (color-pixel [3 4 "R"])
    (color-pixel [4 4 "R"])
    (color-pixel [3 3 "R"])
    (color-pixel [3 2 "R"])
    (color-pixel [2 2 "R"]))
#_(region-R [3 3])
#_(fill-region-R [3 3 "G"])

(defn program-loop []
  (print "please insert a command" \newline "> ")
  (flush)
  (let [[in & more] (str/split (str/trim (read-line)) #" ")]
    (if-not (= "X" in)
     (let [[cmd args] (specs/coerce-arguments in (vec more))]
      (condp = cmd
        :I  (initialize-image args)
        :C  (clear-image)
        :L  (color-pixel args)
        :V  (draw-vertical-segment args)
        :F  (fill-region-R args)
        :H  (draw-horizontal-segment args)
        :S  (show-image)
        :unknown-command  (println "This option doesn't exist. Please insert a valid command")
        :invalid-arguments (println "invalid command arguments, please provide valid arguments for that command"))
      (recur))
     (do
       (println \newline "Session terminated. Goodbuy")
       (reset! state& nil)))))



(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
