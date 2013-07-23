(ns the-pad.primitives.segment
  (:refer-clojure :rename {reverse core-reverse})
  (:require [the-pad.vector-space.additive-group :as a])
  (:require [the-pad.vector-space.vector-space :as v])
  (:require [the-pad.vector-space.affine-space :as p]))

(defprotocol Segment
  (offset [segment])
  (split [segment t])
  (length [segment])
  (arc-length [segment accuracy])
  (reverse [segment])
  (fix [segment p]))

(defrecord FixedLinear [p s])
(defrecord FixedCubic [p c1 c2 s])

(defrecord Linear [v]
  Segment
  (offset [_] v)
  (split [_ t]
    (let [split-point (v/lerp (a/zero v) v t)]
      [(Linear. split-point) (Linear. (a/- v split-point))]))
  (length [_]
    (v/magnitude v))
  (arc-length [this accuracy]
    (length this))
  (reverse [_]
    (Linear. (a/- v)))
  (fix [_ p]
    (FixedLinear. p (p/point+vector p v))))

(defrecord Cubic [c1 c2 v]
  Segment
  (offset [_] v)
  (split [_ t]
    (let [split-point (v/lerp c1 c2 t)
          lc1 (v/lerp (a/zero c1) c1 t)
          rc2 (v/lerp c2 v t)
          lc2 (v/lerp lc1 split-point t)
          rc1 (v/lerp split-point rc2 t)
          e (v/lerp lc2 rc1 t)]
      [(Cubic. lc1 lc2 e) (Cubic. (a/- rc1 e) (a/- rc2 e) (a/- v e))]))
  (length [_]
    (reduce + (map v/magnitude [c1 (a/- c2 c1) (a/- v c2)])))
  (arc-length [this accuracy]
    (let [length (length this)
          short-length (v/magnitude v)]
      (if (< (- length short-length) accuracy)
        (/ (+ length short-length) 2)
        (let [[l r] (split this 0.5)]
          (+ (arc-length l accuracy) (arc-length r accuracy))))))
  (reverse [_]
    (Cubic. (p/- c2 v) (p/- c1 v) (a/- v)))
  (fix [_ p]
    (FixedCubic. p
                 (p/point+vector p c1)
                 (p/point+vector p c2)
                 (p/point+vector p v))))
