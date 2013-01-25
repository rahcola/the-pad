(ns the-pad.screen)

(defprotocol AScreen
  (open? [screen])
  (draw! [screen geometry]))