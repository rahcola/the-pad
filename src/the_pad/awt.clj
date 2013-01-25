(ns the-pad.awt
  (:require [the-pad.screen :as s])
  (:require [the-pad.picture :as p])
  (:require [the-pad.util :as u])
  (:import [java.awt
            Frame Color
            Graphics2D GraphicsEnvironment
            Polygon Rectangle
            EventQueue RenderingHints
            Toolkit]
           [java.awt.image BufferedImage AffineTransformOp]
           [java.awt.geom Path2D$Double Ellipse2D$Double AffineTransform]
           [java.awt.event WindowAdapter ComponentAdapter]))

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

(extend-type the_pad.picture.Rectangle
  ARenderable
  (render [{:keys [width height]} graphics _]
    (.fill graphics
           (Rectangle. (- (/ width 2)) (- (/ height 2))
                       width height))))

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
    (.fill graphics
           (Ellipse2D$Double. (- (/ radius 2)) (- (/ radius 2))
                              radius radius))))

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
                0 0
                (.width bounds) (.height bounds))))

(defn ->frame [{:keys [title width height color]}]
  (doto (Frame. title)
    (.setBackground (Color. (:red color)
                            (:green color)
                            (:blue color)))
    (.setIgnoreRepaint true)
    (.setResizable true)
    (.setSize width height)
    (.setVisible true)))

(defn ->frame-buffer [frame]
  (let [gc (.getGraphicsConfiguration frame)
        w (.getWidth frame)
        h (.getHeight frame)]
    (.createCompatibleImage gc (* 2 w) (* 2 h))))

(defn window-listener [open]
  (proxy [WindowAdapter] []
    (windowOpened [_]
      (swap! open (constantly true)))
    (windowClosing [e]
      (swap! open (constantly false))
      (.dispose (.getWindow e)))))

(defn component-listener [buffer]
  (proxy [ComponentAdapter] []
    (componentResized [e]
      (let [frame (.getComponent e)
            fw (.getWidth frame)
            fh (.getHeight frame)]
        (swap! buffer
               (fn [buffer]
                 (if (and (= (* 2 fw) (.getWidth buffer))
                          (= (* 2 fh) (.getHeight buffer)))
                   buffer
                   (->frame-buffer frame))))))))

(defn awt-run-sync! [task]
  (EventQueue/invokeAndWait task))

(defn set-rendering-hints [graphics]
  (doto graphics
    (.setRenderingHint RenderingHints/KEY_RENDERING
                       RenderingHints/VALUE_RENDER_QUALITY)
    (.setRenderingHint RenderingHints/KEY_STROKE_CONTROL
                       RenderingHints/VALUE_STROKE_NORMALIZE)
    (.setRenderingHint RenderingHints/KEY_ALPHA_INTERPOLATION
                       RenderingHints/VALUE_ALPHA_INTERPOLATION_QUALITY)
    (.setRenderingHint RenderingHints/KEY_COLOR_RENDERING
                       RenderingHints/VALUE_COLOR_RENDER_QUALITY)
    (.setRenderingHint RenderingHints/KEY_DITHERING
                       RenderingHints/VALUE_DITHER_ENABLE)
    (.setRenderingHint RenderingHints/KEY_ANTIALIASING
                       RenderingHints/VALUE_ANTIALIAS_ON)))

(deftype Screen [frame buffer open]
  s/AScreen
  (open? [_] @open)
  (draw! [_ geometry]
    (awt-run-sync!
     (fn []
       (swap! frame
              (fn [frame]
                (if (.isDisplayable frame)
                  (let [frame-graphics (cast Graphics2D (.getGraphics frame))
                        buffer-graphics (.createGraphics @buffer)
                        bounds (.getBounds frame)
                        transform (AffineTransform/getScaleInstance 0.5 0.5)]
                    (set-rendering-hints frame-graphics)
                    (set-rendering-hints buffer-graphics)
                    (.setBackground buffer-graphics (.getBackground frame))
                    (.scale buffer-graphics 2 2)
                    (render geometry buffer-graphics bounds)
                    (.drawImage frame-graphics
                                @buffer
                                (AffineTransformOp.
                                 transform
                                 (AffineTransformOp/TYPE_BICUBIC))
                                0 0)
                    (.dispose frame-graphics)
                    (.dispose buffer-graphics)
                    (.sync (Toolkit/getDefaultToolkit))))
                frame))))))

(defn ->Screen [a]
  (let [frame (atom nil)
        buffer (atom nil)
        open (atom true)]
    (awt-run-sync! (fn []
                     (let [wl (window-listener open)
                           f (->frame a)
                           fb (->frame-buffer f)
                           cl (component-listener buffer)]
                       (.addWindowListener f wl)
                       (.addComponentListener f cl)
                       (swap! frame (constantly f))
                       (swap! buffer (constantly fb)))))
    (Screen. frame buffer open)))