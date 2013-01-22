(ns the-pad.util)

(defprotocol Monoid
  (mempty [this])
  (mappend [this a]))

(defn degree->radians [x]
  (* x (/ Math/PI 180)))