(ns the-pad.line
  (:require [the-pad.primitives.segment :as s])
  (:require [the-pad.primitives.trail :as t]))

(defn h-rule [d]
  (s/fix [(- (/ d 2)) 0] (s/linear [d 0])))

(defn v-rule [d]
  (s/fix [0 (/ d 2)] (s/linear [0 (- d)])))
