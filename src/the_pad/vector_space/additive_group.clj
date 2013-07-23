(ns the-pad.vector-space.additive-group
  (:refer-clojure :rename {+ core+
                           +' core+'
                           - core-
                           -' core-'}))

(def AdditiveGroup ::additive-group)

(defmulti zero type)
(defmulti +' (fn ([v v'] {:pre [(if (and (vector? v) (vector? v'))
                                  (= (count v) (count v'))
                                  true)]}
                    (type v))))
(defmulti -' type)

(defn +
  ([v] v)
  ([v v'] (+' v v'))
  ([v v' & vs] (reduce + (+ v v') vs)))

(defn -
  ([v] (-' v))
  ([v v'] {:pre [(= (type v) (type v'))]}
     (+ v (- v')))
  ([v v' & vs]
     (reduce - (- v v') vs)))

(derive java.lang.Integer AdditiveGroup)
(derive java.lang.Long AdditiveGroup)
(derive java.lang.Float AdditiveGroup)
(derive java.lang.Double AdditiveGroup)
(derive clojure.lang.Ratio AdditiveGroup)
(derive clojure.lang.BigInt AdditiveGroup)
(derive java.math.BigInteger AdditiveGroup)
(derive java.math.BigDecimal AdditiveGroup)
(derive clojure.lang.PersistentVector AdditiveGroup)

(defmethod zero java.lang.Integer [_] (int 0))
(defmethod zero java.lang.Long [_] (long 0))
(defmethod zero java.lang.Float [_] (float 0))
(defmethod zero java.lang.Double [_] (double 0))
(defmethod zero clojure.lang.Ratio [_] (bigint 0))
(defmethod zero clojure.lang.BigInt [_] (bigint 0))
(defmethod zero java.math.BigInteger [_] java.math.BigInteger/ZERO)
(defmethod zero java.math.BigDecimal [_] java.math.BigDecimal/ZERO)
(defmethod zero clojure.lang.PersistentVector [v] (vec (map zero v)))

(defmethod +' java.lang.Integer [v v'] (core+ v v'))
(defmethod +' java.lang.Long [v v'] (core+ v v'))
(defmethod +' java.lang.Float [v v'] (core+ v v'))
(defmethod +' java.lang.Double [v v'] (core+ v v'))
(defmethod +' clojure.lang.Ratio [v v'] (core+ v v'))
(defmethod +' clojure.lang.BigInt [v v'] (core+ v v'))
(defmethod +' java.math.BigInteger [v v'] (core+ v v'))
(defmethod +' java.math.BigDecimal [v v'] (core+ v v'))
(defmethod +' clojure.lang.PersistentVector [v v'] (vec (map +' v v')))

(defmethod -' java.lang.Integer [v] (core- v))
(defmethod -' java.lang.Long [v] (core- v))
(defmethod -' java.lang.Float [v] (core- v))
(defmethod -' java.lang.Double [v] (core- v))
(defmethod -' clojure.lang.Ratio [v] (core- v))
(defmethod -' clojure.lang.BigInt [v] (core- v))
(defmethod -' java.math.BigInteger [v] (core- v))
(defmethod -' java.math.BigDecimal [v] (core- v))
(defmethod -' clojure.lang.PersistentVector [v] (vec (map -' v)))
