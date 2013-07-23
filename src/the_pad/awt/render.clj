(ns the-pad.awt.render
  (:require [the-pad.render :as r])
  (:require [the-pad.primitives.segment :as s])
  (:import [the_pad.primitives.segment
            Linear
            FixedLinear
            Cubic
            FixedCubic])
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

(defmethod r/render [Linear AWT]
  [segment _]
  (r/render (s/fix segment [0 0]) AWT))

(defmethod r/render [FixedLinear AWT]
  [{:keys [p s]} _]
  (awt-line p s))

(defmethod r/render [Cubic AWT]
  [segment _]
  (r/render (s/fix segment [0 0]) AWT))

(defmethod r/render [FixedCubic AWT]
  [{:keys [p c1 c2 s]} _]
  (awt-cubic p c1 c2 s))
