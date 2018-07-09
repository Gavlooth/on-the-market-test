(ns graphical-editor.core
  (:require #_[clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [ graphical-editor.specs :as specs])
  (:gen-class))

;;;; Main program and functions

(def state&
 "Holds the image in current sesssion"
  (atom nil))

;; Use this to help whenever there is illicit association on
;; nonexisting pixels. This is generaly better (more efficient)
;;than having dynamic  spec restricting the operation inside image boundarys

;;; Image operations

(defmacro catch-out-of-bounds
 "wraps body in a try catch exeption that check wheneve
  there is an out of bounds  access"
  [body]
  `(try ~body
        (catch IndexOutOfBoundsException e#
          (println "You exeded image dimentions. Please try again"))))

(defn initizalize-image!
 "Creates an MxN vector of vectors representing an image"
  [[dim-X dim-Y]]
 (let [generator-fn (comp vec repeat)]
   (reset! state& (generator-fn dim-X (generator-fn dim-Y 0)))))

(defn clear-image!
 "Reset every pixel to 0"
  []
 (let  [clear-fn (fn [x] (mapv (fn [y] (mapv (constantly 0) y)) x))]
  (when  @state&
    (swap! state& clear-fn))))

(defn color-pixel
 "Set! a pixel to one of R G B representing a color"
  [[dim-X dim-Y color]]
 (catch-out-of-bounds (swap! state& assoc-in  [dim-X dim-Y] color)))

;; Use transients to operate on vectors for efficiency
(defn- replace-subvector
  "Changes every element to a subvector to value"
  [a-vector start end value]
  (loop [i start the-vector (transient a-vector)]
    (if (<= i end)
      (recur (inc i) (assoc! the-vector i value))
      (persistent! the-vector))))

(defn draw-horizontal-segment!
 "Draw a vertical segment of colour C in column Y between rows X1 and X2.
  Inclusive"
  [[X1 X2 Y  color]]
  (catch-out-of-bounds
    (let [start (min X1 X2) end (max X1 X2)]
      (swap! state&  update Y replace-subvector start end color))))

(defn draw-vertical-segment!
 "Draw a horizontal segment of colour C in row X between columns Y1 and Y2.
  Inclusive."
  [[X Y1 Y2 color]]
  (catch-out-of-bounds
   (let [start (min Y1 Y2) end (max Y1 Y2)]
      (loop [i start state (transient @state&)]
        (if (<= i end)
          (let [current-column (get state i)
                updated-column (assoc current-column X color)]
            (recur (inc i) (assoc! state i updated-column)))
          (reset! state& (persistent! state)))))))

;;Find region R incrementaly to avoid stack overflow
;;This implementation uses transients in a loop
;;and is efficient but not layzy
;;Can be easyly refactor, to use lazy-seq instead

(defn region-R [[X Y C]]
 (let [image (deref state&)
       pixel-color (fn [[x y]]  (get (get image x) y "nil"))
       color (pixel-color [X Y])
       neighbours (fn [[x y]] [[(inc x) y] [(dec x) y] [x (inc y)] [x (dec y)]])
       same-color-neighbours (fn [the-neighbours] (filter #(= color (pixel-color %)) the-neighbours))]
  (loop [region-R (transient #{[X Y]})
         candidates (neighbours [X Y])]
    (let [similar-neighbour-pixels (same-color-neighbours candidates)
          updated-region-R (reduce conj! region-R similar-neighbour-pixels)
          new-candidates (remove updated-region-R (mapcat neighbours similar-neighbour-pixels))]
      (if (seq new-candidates)
        (recur updated-region-R new-candidates)
        (persistent! updated-region-R))))))


(defn fill-region-R!
  "Fill the region R with the colour C"
  [[X Y C]]
  (catch-out-of-bounds
   (let [the-region-R  (region-R [X Y])
         transient-state (transient @state&)]
      (loop [image transient-state pixels the-region-R]
        (if-let  [[x y]  (first pixels)]
         (recur  (assoc! image x (assoc (get image x) y C)) (rest pixels))
         (reset! state& (persistent! image)))))))

(defn show-image!
 "Show the contents of the current image"
  []
  (println "=>")
  (doseq [x @state&]
   (do
    (doseq [y x] (print y))
    (flush)
    (println))))

;;; Main program

(defn program-loop
 "Main program loop. Enter X to terminate"
  []
  (print "please insert a command" \newline "> ")
  (flush)
  (let [[in & more] (str/split (str/trim (read-line)) #" ")]
    (if-not (= "X" in)
     (let [[cmd args] (specs/coerce-arguments in (vec more))]
      (condp = cmd
        :I  (initizalize-image! args)
        :C  (clear-image!)
        :L  (color-pixel args)
        :V  (draw-vertical-segment! args)
        :F  (fill-region-R! args)
        :H  (draw-horizontal-segment! args)
        :S  (show-image!)
        :unknown-command  (println "This option doesn't exist. Please insert a valid command")
        :invalid-arguments (println "invalid command arguments, please provide valid arguments for that command"))
      (recur))
     (do
       (println \newline "Session terminated. Goodbuy")
       (reset! state& nil)))))



(defn -main [& args] (program-loop))


