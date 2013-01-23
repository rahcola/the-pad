(ns the-pad.core
  (:require [the-pad.awt :as awt]
            [the-pad.picture :as p]
            [the-pad.util :as u]))

(defn -main
  "I don't do a whole lot."
  [& args]
  (let [screen (awt/->Screen "Window" 400 400)
        l (p/color (p/->Color 100 100 100)
                   (p/->Polygon [[-50 -50] [50 -50] [50 50] [-50 50]]))]
    (loop [angle 0]
      (awt/draw! screen
                 (u/mappend p/blank
                            (p/translate 200 200 (p/rotate angle l))))
      (if (awt/open? screen)
        (do (Thread/sleep 40)
            (recur (if (>= angle 360)
                     0
                     (+ angle 6))))))))