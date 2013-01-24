(ns the-pad.core
  (:require [the-pad.awt :as awt]
            [the-pad.picture :as p]
            [the-pad.util :as u]))

(defn limit [start fps]
  (let [delta (- (System/currentTimeMillis) start)
        time-per-frame (/ 1000 fps)
        sleep (- time-per-frame delta)]
    (if (> sleep 0)
      (Thread/sleep sleep))))

(defn animate [screen update]
  (let [t0 (System/currentTimeMillis)]
    (loop [t 0]
      (let [start (System/currentTimeMillis)
            picture (update t)]
        (awt/draw! screen picture)
        (if (awt/open? screen)
          (do (limit start 60)
              (recur (- (System/currentTimeMillis) t0))))))))

(defn -main
  "I don't do a whole lot."
  [& args]
  (let [screen (awt/->Screen {:title "Window"
                              :width 400
                              :height 400
                              :color {:red 255 :green 255 :blue 255}
                              :full-screen false})
        l (u/mappend (p/color (p/->Color 100 100 100)
                              (p/->Rectangle 100 100))
                     (p/color (p/->Color 0 0 0)
                              (p/->Line [[0 0] [0 -50]])))]
    (animate screen
             (fn [t]
               (let [angle (* t (/ 360 5000))]
                 (u/mappend p/blank
                            (p/translate 200 200
                                         (p/rotate angle l))))))))