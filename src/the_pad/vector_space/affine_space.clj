(ns the-pad.vector-space.affine-space
  (:refer-clojure :rename {- core-})
  (:require [the-pad.vector-space.additive-group :as a])
  (:require [the-pad.vector-space.vector-space :as v]))

(def AffineSpace ::affine-space)

(defmulti - (fn [p p'] {:pre [(if (and (vector? p) (vector? p'))
                                (= (count p) (count p'))
                                true)]}
              (type p)))

(defmulti point+vector (fn [p v] (type p)))

(defn point-vector [p v]
  (point+vector p (a/- v)))

(defn distance-sq [p p']
  (v/magnitude-sq (- p p')))

(defn distance [p p']
  (v/magnitude (- p p')))

(defn alerp [p p' t]
  (point+vector p (v/scale (- p' p) t)))

(derive java.lang.Integer AffineSpace)
(derive java.lang.Long AffineSpace)
(derive java.lang.Float AffineSpace)
(derive java.lang.Double AffineSpace)
(derive clojure.lang.Ratio AffineSpace)
(derive clojure.lang.BigInt AffineSpace)
(derive java.math.BigInteger AffineSpace)
(derive java.math.BigDecimal AffineSpace)
(derive clojure.lang.PersistentVector AffineSpace)

(defmethod - java.lang.Integer [p p'] (core- p p'))
(defmethod - java.lang.Long [p p'] (core- p p'))
(defmethod - java.lang.Float [p p'] (core- p p'))
(defmethod - java.lang.Double [p p'] (core- p p'))
(defmethod - clojure.lang.Ratio [p p'] (core- p p'))
(defmethod - clojure.lang.BigInt [p p'] (core- p p'))
(defmethod - java.math.BigInteger [p p'] (core- p p'))
(defmethod - java.math.BigDecimal [p p'] (core- p p'))
(defmethod - clojure.lang.PersistentVector [p p']
  (vec (map - p p')))

(defmethod point+vector java.lang.Integer [p v] (+ p v))
(defmethod point+vector java.lang.Long [p v] (+ p v))
(defmethod point+vector java.lang.Float [p v] (+ p v))
(defmethod point+vector java.lang.Double [p v] (+ p v))
(defmethod point+vector clojure.lang.Ratio [p v] (+ p v))
(defmethod point+vector clojure.lang.BigInt [p v] (+ p v))
(defmethod point+vector java.math.BigInteger [p v] (+ p v))
(defmethod point+vector java.math.BigDecimal [p v] (+ p v))
(defmethod point+vector clojure.lang.PersistentVector [p v]
  (vec (map point+vector p v)))
