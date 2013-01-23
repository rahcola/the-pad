(ns the-pad.picture
  (:require [the-pad.util :as u]))

(defrecord Pictures [objects])

(defrecord Polygon [path])

(defrecord Rectangle [width height])

(defrecord Line [path])

(defrecord Circle [radius])

(defrecord Color [red green blue])

(defrecord Rotate [angle])

(defrecord Translate [x y])

(defrecord Blank [])

(def Picture ::root)
(def h (-> (make-hierarchy)
           (derive Pictures Picture)
           (derive Polygon Picture)
           (derive Rectangle Picture)
           (derive Line Picture)
           (derive Circle Picture)
           (derive Color Picture)
           (derive Rotate Picture)
           (derive Translate Picture)
           (derive Blank Picture)))

(defmulti pictures (fn [a b] [(type a) (type b)])
  :hierarchy #'h)

(defmethod pictures [Pictures Pictures] [a b]
  (Pictures. (vec (concat (:objects a) (:objects b)))))

(defmethod pictures [Pictures Picture] [a b]
  (Pictures. (conj (:objects a) b)))

(defmethod pictures [Picture Pictures] [a b]
  (Pictures. (vec (cons a (:objects b)))))

(defmethod pictures [Picture Picture] [a b]
  (Pictures. [a b]))

(def monoid-impl
  {:mempty (fn [_] (Pictures. []))
   :mappend (fn [a b] (pictures a b))})

(extend Pictures u/Monoid monoid-impl)
(extend Polygon u/Monoid monoid-impl)
(extend Rectangle u/Monoid monoid-impl)
(extend Line u/Monoid monoid-impl)
(extend Circle u/Monoid monoid-impl)
(extend Color u/Monoid monoid-impl)
(extend Rotate u/Monoid monoid-impl)
(extend Translate u/Monoid monoid-impl)
(extend Blank u/Monoid monoid-impl)

(defn color [c picture]
  (u/mappend c picture))

(defn rotate [angle picture]
  (u/mappend (Rotate. angle) picture))

(defn translate [x y picture]
  (u/mappend (Translate. x y) picture))

(def blank (Blank.))