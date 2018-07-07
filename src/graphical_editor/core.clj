(ns graphical-editor.core
  (:require #_[clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [ graphical-editor.specs :as specs])
  (:gen-class))

(def state& (atom nil))

(defn initialize-image  [[dim-X dim-Y]]
 (let [generator-fn (comp vec repeat)]
  (when-not @state&
   (reset! state& (generator-fn dim-Y (generator-fn dim-Y 0))))))

(defn clear-image []
 (let  [clear-fn (fn [x] (mapv (fn [y] (mapv (constantly 0) y)) x))]
  (when  @state&
    (swap! state& clear-fn))))

(defn color-pixel [[dim-X dim-Y color]]
 (swap! state& assoc-in  [dim-X dim-Y] color))

(defn- replace-subvector [[a-vector start end value]]
  (loop [i start the-vector (transient a-vector)]
    (if (<= i end)
      (recur (inc i) (assoc! the-vector i value))
      (persistent! the-vector))))

(defn draw-vertical-segment [[X Y1 Y2 color]]
 (let [start (min Y1 Y2) end (max Y1 Y2)]
  (swap! update X replace-subvector start end color)))

(defn draw-horizontal-segment [[Y X1 X2 color]]
  (let [start (min X1 X2) end (max X1 X2)]
    (loop [i start state (transient @state&)]
      (if (<= i end)
        (let [current-column (get state i)
              updated-column (assoc current-column Y color)]
          (recur (inc i) (assoc! state updated-column)))
        (reset! (persistent! state) state&)))))




(defn region-R
  ([x](region-R x [-1 -1]))
  ([[X Y  :as cords] previous]
   (let [image (deref state&)
         pixel-fn (fn [[x y]]  (get (get image x) y))
         current-pixel  (pixel-fn #spy/t cords)
         candidates  (remove #{previous} [[(inc X) Y] [(dec X) Y] [X (inc Y)]  [X (dec Y)]])
         similar-neighbour-pixels  (filter #(= current-pixel (pixel-fn %)) candidates)]
     (lazy-cat  [cords] (mapcat #(region-R % cords) #spy/t similar-neighbour-pixels)))))


(defn show-image []
 (doseq [x @state&]
  (do 
   (doseq [y x] (print y))
   (println ""))))

#_(initialize-image [10 10])
#_(color-pixel [3 4 "R"])
#_(color-pixel [4 4 "R"])
#_(color-pixel [3 3 "R"])
#_(color-pixel [3 2 "R"])
#_(color-pixel [2 2 "R"])
#_(region-R [3 3])
 

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
        :H  (draw-horizontal-segment args)
        :S  (show-image)
        :default (println "invalid input arguments, please try again"))
      (recur))
     (println \newline "Session terminated. Goodbuy"))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
