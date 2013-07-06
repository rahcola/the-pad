(ns the-pad.core
  (:require [the-pad.line :as l])
  (:require [the-pad.primitives.segment :as s])
  (:require [the-pad.primitives.trail :as t])
  (:require [the-pad.render :as r])
  (:require [the-pad.awt :as awt])
  (:import javax.swing.JFrame)
  (:import java.awt.Dimension)
  (:import java.awt.Canvas)
  (:import java.awt.Graphics2D))

(defn ->window [name width height exit-on-close?]
  (let [f (new JFrame name)
        panel (.getContentPane f)
        canvas (new Canvas)]
    (when exit-on-close?
      (.setDefaultCloseOperation f JFrame/EXIT_ON_CLOSE))
    (.setPreferredSize panel (new Dimension width height))
    (.setLayout panel nil)
    (.setBounds canvas 0 0 width height)
    (.add panel canvas)
    (.setIgnoreRepaint canvas true)
    (.pack f)
    (.setResizable f false)
    (.setVisible f true)
    (.createBufferStrategy canvas 2)
    {:frame f
     :width width
     :height height
     :strategy (.getBufferStrategy canvas)}))

(defn draw! [window shapes]
  (let [w (:width window)
        h (:height window)
        g (cast Graphics2D (.getDrawGraphics (:strategy window)))]
    (.translate g (/ w 2) (/ h 2))
    (.scale g 1 -1)
    (.setColor g (java.awt.Color/BLACK))
    (doseq [shape shapes]
      (.draw g shape)
      (.fill g shape))
    (.dispose g)
    (.show (:strategy window))))

(defn -main
  [& args]
  (let [w (->window "foo" 800 600 true)]
    (draw! w [(r/render (l/h-rule 800) awt/AWT)
              (r/render (l/v-rule 600) awt/AWT)
              (r/render (t/trail [(s/linear [10 10])
                                  (s/cubic [-10 50] [60 50] [90 0])]
                                 true) awt/AWT)])))
