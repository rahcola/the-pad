(ns the-pad.awt
  (:require [the-pad.render :as r])
  (:require [the-pad.primitives.segment :as s])
  (:require [the-pad.primitives.trail :as t])
  (:import [java.awt.geom
            CubicCurve2D$Double
            Line2D$Double
            Path2D$Double]))

(def AWT ::awt)

(defn awt-line [[x1 y1] [x2 y2]]
  (new Line2D$Double x1 y1 x2 y2))

(defn awt-cubic [[x1 y1] [cx1 cy1] [cx2 cy2] [x2 y2]]
  (new CubicCurve2D$Double
       x1 y1
       cx1 cy1
       cx2 cy2
       x2 y2))

(defmethod r/render [s/Linear AWT]
  [{:keys [v]} _]
  (awt-line [0 0] v))

(defmethod r/render [s/FixedLinear AWT]
  [{:keys [from to]} _]
  (awt-line from to))

(defmethod r/render [s/Cubic AWT]
  [{:keys [c1 c2 v]} _]
  (awt-cubic [0 0] c1 c2 v))

(defmethod r/render [s/FixedCubic AWT]
  [{:keys [from c1 c2 to]} _]
  (println "foo")
  (awt-cubic from c1 c2 to))

(defmethod r/render [t/Trail AWT]
  [trail _]
  (let [path (new Path2D$Double)]
    (doseq [segment (t/fix [0 0] trail)]
      (.append path (r/render segment AWT) true))
    path))
