(ns the-pad.awt
  (:require [the-pad.picture :as p])
  (:require [the-pad.util :as u])
  (:import [java.awt Frame Color GraphicsEnvironment Polygon]
           [java.awt.geom Path2D$Double Ellipse2D$Double]
           [java.awt.event WindowListener]))

(defprotocol ARenderable
  (render [this graphics bounds]))

(extend-type the_pad.picture.Pictures
  ARenderable
  (render [{:keys [objects]} graphics bounds]
    (doseq [o objects]
      (render o graphics bounds))))

(extend-type the_pad.picture.Polygon
  ARenderable
  (render [{:keys [path]} graphics _]
    (let [awt-poly (Polygon.)]
      (doseq [[x y] path]
        (.addPoint awt-poly (int x) (int y)))
      (.fill graphics awt-poly))))

(extend-type the_pad.picture.Line
  ARenderable
  (render [{:keys [path]} graphics _]
    (let [awt-path (Path2D$Double.)
          [[x y] & rest] path]
      (.moveTo awt-path (double x) (double y))
      (doseq [[x y] rest]
        (.lineTo awt-path (double x) (double y)))
      (.draw graphics awt-path))))

(extend-type the_pad.picture.Circle
  ARenderable
  (render [{:keys [radius]} graphics _]
    (.fill graphics (Ellipse2D$Double. 0 0 radius radius))))

(extend-type the_pad.picture.Color
  ARenderable
  (render [{:keys [red green blue]} graphics _]
    (.setColor graphics (Color. red green blue))))

(extend-type the_pad.picture.Rotate
  ARenderable
  (render [{:keys [angle]} graphics _]
    (.rotate graphics (u/degree->radians angle))))

(extend-type the_pad.picture.Translate
  ARenderable
  (render [{:keys [x y]} graphics _]
    (.translate graphics x y)))

(extend-type the_pad.picture.Blank
  ARenderable
  (render [_ graphics bounds]
    (.clearRect graphics
                (.x bounds) (.y bounds)
                (.width bounds) (.height bounds))))

(defn ->frame [title width height]
  (doto (-> (GraphicsEnvironment/getLocalGraphicsEnvironment)
            (.getDefaultScreenDevice)
            (.getDefaultConfiguration)
            (Frame.))
    (.setIgnoreRepaint true)
    (.setTitle title)
    (.setSize width height)
    (.setResizable true)
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
  (open? [screen])
  (draw! [screen geometry]))

(deftype Screen [frame]
  AScreen
  (open? [_]
    (not (nil? @frame)))
  (draw! [_ geometry]
    (swap! frame (fn [frame]
                   (if frame
                     (let [b (.getBufferStrategy frame)
                           graphics (.getDrawGraphics b)
                           bounds (.getBounds frame)]
                       (render geometry graphics bounds)
                       (.show b)
                       (.dispose graphics)
                       frame))))))

(defn ->Screen [title width height]
  (let [frame (atom (->frame title width height))]
    (swap! frame add-window-listener (dispose-on-close-listener frame))
    (Screen. frame)))