(ns the-pad.cells.cell)

(defprotocol Cell
  (step [cell input delta]))

(deftype StatefullCell [state f!]
  Cell
  (step [this input delta]
    [(f! state input delta) this]))

(deftype PureStateCell [state f]
  Cell
  (step [_ input delta]
    (let [[output state'] (f state input delta)]
      [output (PureStateCell. state' f)])))
