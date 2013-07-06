(ns the-pad.vector-space.vector-space
  (:require [the-pad.vector-space.additive-group :as a]))

(def VectorSpace ::vector-space)
(derive VectorSpace a/AdditiveGroup)

(defmulti scale (fn [v s] (type v)))

(defn divide [v s]
  (scale v (/ 1 s)))

(defn lerp [a b t]
  (a/+ a (scale (a/- b a) t)))

(derive java.lang.Integer VectorSpace)
(derive java.lang.Long VectorSpace)
(derive java.lang.Float VectorSpace)
(derive java.lang.Double VectorSpace)
(derive clojure.lang.Ratio VectorSpace)
(derive clojure.lang.BigInt VectorSpace)
(derive java.math.BigInteger VectorSpace)
(derive java.math.BigDecimal VectorSpace)
(derive clojure.lang.PersistentVector VectorSpace)

(defmethod scale java.lang.Integer [v s] (* v s))
(defmethod scale java.lang.Long [v s] (* v s))
(defmethod scale java.lang.Float [v s] (* v s))
(defmethod scale java.lang.Double [v s] (* v s))
(defmethod scale clojure.lang.Ratio [v s] (* v s))
(defmethod scale clojure.lang.BigInt [v s] (* v s))
(defmethod scale java.math.BigInteger [v s] (* v s))
(defmethod scale java.math.BigDecimal [v s] (* v s))
(defmethod scale clojure.lang.PersistentVector [v s]
  (vec (map #(scale % s) v)))

(def InnerSpace ::inner-space)
(derive InnerSpace VectorSpace)

(defmulti dot (fn [v u] {:pre [(if (and (vector? v) (vector? u))
                                  (= (count v) (count u))
                                  true)]}
                (type v)))

(defn magnitude-sq [v]
  (dot v v))

(defn magnitude [v]
  (Math/sqrt (magnitude-sq v)))

(defn normalize [v]
  (divide v (magnitude v)))

(defn project [v u]
  (scale u (/ (dot v u) (magnitude-sq u))))

(derive java.lang.Integer InnerSpace)
(derive java.lang.Long InnerSpace)
(derive java.lang.Float InnerSpace)
(derive java.lang.Double InnerSpace)
(derive clojure.lang.Ratio InnerSpace)
(derive clojure.lang.BigInt InnerSpace)
(derive java.math.BigInteger InnerSpace)
(derive java.math.BigDecimal InnerSpace)
(derive clojure.lang.PersistentVector InnerSpace)

(defmethod dot java.lang.Integer [v v'] (* v v'))
(defmethod dot java.lang.Long [v v'] (* v v'))
(defmethod dot java.lang.Float [v v'] (* v v'))
(defmethod dot java.lang.Double [v v'] (* v v'))
(defmethod dot clojure.lang.Ratio [v v'] (* v v'))
(defmethod dot clojure.lang.BigInt [v v'] (* v v'))
(defmethod dot java.math.BigInteger [v v'] (* v v'))
(defmethod dot java.math.BigDecimal [v v'] (* v v'))
(defmethod dot clojure.lang.PersistentVector [v v']
  (reduce a/+ (map dot v v')))
