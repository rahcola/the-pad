(ns the-pad.primitives.segment
  (:require [the-pad.vector-space.additive-group :as a])
  (:require [the-pad.vector-space.vector-space :as v])
  (:require [the-pad.vector-space.affine-space :as p]))

(def Segment ::segment)
(def Linear ::linear)
(def Cubic ::cubic)
(def FixedSegment ::fixed-segment)
(def FixedLinear ::fixed-linear)
(def FixedCubic ::fixed-cubic)

(defn linear [v]
  (with-meta {:v v}
    {:type Linear}))

(derive Linear Segment)

(defn cubic [c1 c2 v]
  (with-meta {:c1 c1
              :c2 c2
              :v v}
    {:type Cubic}))

(derive Cubic Segment)

(defn fixed-linear [from to]
  (with-meta {:from from
              :to to}
    {:type FixedLinear}))

(derive FixedLinear FixedSegment)

(defn fixed-cubic [from c1 c2 to]
  (with-meta {:from from
              :c1 c1
              :c2 c2
              :to to}
    {:type FixedCubic}))

(derive FixedCubic FixedSegment)

(defn straight [v]
  (linear v))

(defn bezier-3 [v1 v2 v3]
  (cubic v1 v2 v3))

(defn offset [segment]
  (:v segment))

(defmulti split (fn [segment t] (type segment)))

(defmethod split Linear [{:keys [v]} t]
  (let [split-point (v/lerp (a/zero v) v t)]
    [(linear split-point) (linear (a/- v split-point))]))

(defmethod split Cubic [{:keys [c1 c2 v]} t]
  (let [split-point (v/lerp c1 c2 t)
        lc1 (v/lerp (a/zero c1) c1 t)
        rc2 (v/lerp c2 v t)
        lc2 (v/lerp lc1 split-point t)
        rc1 (v/lerp split-point rc2 t)
        e (v/lerp lc2 rc1 t)]
    [(cubic lc1 lc2 e) (cubic (a/- rc1 e) (a/- rc2 e) (a/- v e))]))

(defmulti length type)

(defmethod length Linear [{:keys [v]}]
  (v/magnitude v))

(defmethod length Cubic [{:keys [c1 c2 v]}]
  (reduce + (map v/magnitude [c1 (a/- c2 c1) (a/- v c2)])))

(defmulti arc-length (fn [segment accuracy] (type segment)))

(defmethod arc-length Linear [segment _]
  (length segment))

(defmethod arc-length Cubic [{:keys [c1 c2 v] :as segment} accuracy]
  (let [length (length segment)
        short-length (v/magnitude v)]
    (if (< (- length short-length) accuracy)
      (/ (+ length short-length) 2)
      (let [[l r] (split segment 0.5)]
        (+ (arc-length l accuracy) (arc-length r accuracy))))))

(defmulti reverse type)

(defmethod reverse Linear [{:keys [v]}]
  (linear (a/- v)))

(defmethod reverse Cubic [{:keys [c1 c2 v]}]
  (cubic (p/- c2 v) (p/- c1 v) (a/- v)))

(defmulti fix (fn [p segment]
                (type segment)))

(defmethod fix Linear [p {:keys [v]}]
  (fixed-linear p (p/point+vector p v)))

(defmethod fix Cubic [p {:keys [c1 c2 v]}]
  (fixed-cubic p
               (p/point+vector p c1)
               (p/point+vector p c2)
               (p/point+vector p v)))
