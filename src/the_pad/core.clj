(ns the-pad.core
  (:require [the-pad.awt :as awt]
            [the-pad.picture :as p]
            [the-pad.util :as u]))

(defn -main
  "I don't do a whole lot."
  [& args]
  (let [screen (awt/->Screen "Window" 400 400)]
    (awt/draw! screen (p/color (p/->Color 100 100 100)
                               (p/->Polygon [[10 10]   [110 10]
                                             [110 110] [10 110]])))))