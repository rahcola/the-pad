(ns the-pad.primitives.trail
  (:require [the-pad.vector-space.additive-group :as a])
  (:require [the-pad.vector-space.affine-space :as p])
  (:require [the-pad.primitives.segment :as s]))

(def Trail ::trail)

(defn trail
  ([segments]
     (trail segments false))
  ([segments closed?]
     (with-meta {:segments (vec segments)
                 :closed? closed?}
       {:type Trail})))

(defn offsets [{:keys [segments]}]
  (map s/offset segments))

(defn offset [trail]
  (reduce a/+ (offsets trail)))

(defn vertices [p trail]
  (reductions p/point+vector p (offsets trail)))

(defn add-closing-segment [{:keys [closed? segments] :as t}]
  (if closed?
    (let [closing (s/linear (a/- (offset t)))]
      (trail (conj segments closing) false))
    t))

(defn fix [p trail]
  (map s/fix (vertices p trail) (:segments (add-closing-segment trail))))
