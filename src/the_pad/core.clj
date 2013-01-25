(ns the-pad.core
  (:require [the-pad.screen :as s])
  (:require [the-pad.util :as u])
  (:require [the-pad.picture :as p])
  (:require [the-pad.awt :as awt])
  (:require [the-pad.opengl :as gl]))

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
        (s/draw! screen picture)
        (if (s/open? screen)
          (recur (- (System/currentTimeMillis) t0)))))))

(defn -main
  [& args]
  (let [screen (gl/->Screen {:title "Window"
                             :width 400
                             :height 400
                             :color {:red 255 :green 255 :blue 255}
                             :full-screen false})
        cw (p/color (p/->Color 0.1 0.1 0.1)
                    (p/->Rectangle 0.25 0.25))
        ccw (p/color (p/->Color 0.9 0.9 0.9)
                     (p/->Rectangle 0.25 0.25))]
    (animate screen
             (fn [t]
               (let [angle (* (/ t 1000) 45)]
                 (u/mappend
                  (p/color (p/->Color 0.5 0.5 0.5)
                           p/blank)
                  (u/mappend
                   (p/translate 0.1 0 (p/rotate angle cw))
                   (p/translate -0.1 0 (p/rotate (- angle) ccw)))))))))