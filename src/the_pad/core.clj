(ns the-pad.core
  (:use the-pad.render)
  (:use the-pad.awt.render)
  (:require [the-pad.cells.cell :as c])
  (:require [the-pad.primitives.segment :as s])
  (:require [the-pad.awt.window :as awt]))

(defn cap-fps! [fps t0]
  (let [t2 (System/currentTimeMillis)
        left (- (/ 1000 fps)
                (- t2 t0))]
    (if (> left 0)
      (Thread/sleep left))))

(defn animate [fps f & cells]
  {:pre [(> fps 0)]}
  (loop [t0 (System/currentTimeMillis)
         cells cells]
    (let [t1 (System/currentTimeMillis)
          delta (- t1 t0)
          cells-steped (map (fn [cell] (c/step cell nil delta))
                            cells)]
      (apply f delta (map first cells-steped))
      (cap-fps! fps t1)
      (recur t1 (map second cells-steped)))))

(defn line-cell [mouse-state]
  (c/->PureStateCell {:mouse-state mouse-state}
                     (fn [{:keys [mouse-state starting-point]} _ _]
                       (if (:button-1 @mouse-state)
                         (let [starting-point (if starting-point
                                                starting-point
                                                (:position @mouse-state))
                               endpoint (:position @mouse-state)]
                           [(s/->FixedLinear starting-point endpoint)
                            {:mouse-state mouse-state
                             :starting-point starting-point}])
                         [false {:mouse-state mouse-state}]))))

(defn -main
  [& args]
  (let [w (awt/->window "foo" 640 480 true)
        mouse-line (line-cell (:mouse-state w))]
    (animate 60
             (fn [delta line]
               (if line
                 (awt/draw! w [(render line AWT)])
                 (awt/draw! w [])))
             mouse-line)))
