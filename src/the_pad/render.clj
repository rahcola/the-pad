(ns the-pad.render)

(defmulti render (fn [object backend] [(type object) backend]))
