(ns the-pad.awt
  (:require [the-pad.picture :as p])
  (:require [the-pad.util :as u])
  (:import [java.awt Frame Color GraphicsEnvironment Polygon]
           [java.awt.geom Path2D$Double Ellipse2D$Double]
           [java.awt.event WindowListener]))

(defprotocol ARenderable
  (render [this graphics]))

(extend-type the_pad.picture.Pictures
  ARenderable
  (render [{:keys [objects]} graphics]
    (doseq [o objects]
      (render o graphics))))

(extend-type the_pad.picture.Polygon
  ARenderable
  (render [{:keys [path]} graphics]
    (let [awt-poly (Polygon.)]
      (doseq [[x y] path]
        (.addPoint awt-poly (int x) (int y)))
      (.fill graphics awt-poly))))

(extend-type the_pad.picture.Line
  ARenderable
  (render [{:keys [path]} graphics]
    (let [awt-path (Path2D$Double.)
          [[x y] & rest] path]
      (.moveTo awt-path (double x) (double y))
      (doseq [[x y] rest]
        (.lineTo awt-path (double x) (double y)))
      (.draw graphics awt-path))))

(extend-type the_pad.picture.Circle
  ARenderable
  (render [{:keys [radius]} graphics]
    (.fill graphics (Ellipse2D$Double. 0 0 radius radius))))

(extend-type the_pad.picture.Color
  ARenderable
  (render [{:keys [red green blue]} graphics]
    (.setColor graphics (Color. red green blue))))

(extend-type the_pad.picture.Rotate
  ARenderable
  (render [{:keys [angle]} graphics]
    (.rotate graphics (u/degree->radians angle))))

(defn ->frame [title width height]
  (doto (-> (GraphicsEnvironment/getLocalGraphicsEnvironment)
            (.getDefaultScreenDevice)
            (.getDefaultConfiguration)
            (Frame.))
    (.setIgnoreRepaint true)
    (.setTitle title)
    (.setSize width height)
    (.setResizable false)
    (.setVisible true)
    (.createBufferStrategy 2)))

(defn dispose-on-close-listener [frame]
  (reify WindowListener
    (windowActivated [this _])
    (windowClosed [this _])
    (windowClosing [this e]
      (swap! frame (fn [frame]
                     (.dispose frame)
                     false)))
    (windowDeactivated [this _])
    (windowDeiconified [this _])
    (windowIconified [this _])
    (windowOpened [this _])))

(defn add-window-listener [frame listener]
  (doto frame (.addWindowListener listener)))

(defprotocol AScreen
  (draw! [screen geometry]))

(deftype Screen [frame]
  AScreen
  (draw! [_ geometry]
    (swap! frame (fn [frame]
                   (if frame
                     (let [b (.getBufferStrategy frame)
                           graphics (.getDrawGraphics b)
                           bounds (.getBounds frame)]
                       (render geometry graphics)
                       (.show b)
                       (.dispose graphics)
                       frame))))))

(defn ->Screen [title width height]
  (let [frame (atom (->frame title width height))]
    (swap! frame add-window-listener (dispose-on-close-listener frame))
    (Screen. frame)))