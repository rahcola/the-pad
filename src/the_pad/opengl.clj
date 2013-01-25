(ns the-pad.opengl
  (:require [the-pad.screen :as s])
  (:require [the-pad.picture :as p])
  (:import [org.lwjgl.opengl Display DisplayMode GL11]))

(defn gl-clear []
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT
                        GL11/GL_DEPTH_BUFFER_BIT)))

(defn gl-clear-color [red green blue]
  (GL11/glClearColor red green blue 1))

(defn gl-color [red green blue]
  (GL11/glColor3f (float red) (float green) (float blue)))

(defn gl-quad [vertices]
  (GL11/glBegin GL11/GL_QUADS)
  (doseq [[x y] vertices]
    (GL11/glVertex2f x y))
  (GL11/glEnd))

(defn gl-rotate [angle]
  (GL11/glRotated angle 0 0 -1))

(defn gl-translate [x y]
  (GL11/glTranslated x y 0))

(defprotocol ARenderable
  (render [object]))

(extend-type the_pad.picture.Blank
  ARenderable
  (render [_]
    (gl-clear)))

(extend-type the_pad.picture.Rectangle
  ARenderable
  (render [{:keys [width height]}]
    (gl-quad [[(- (/ width 2)) (/ height 2)] [(/ width 2) (/ height 2)]
              [(/ width 2) (- (/ height 2))] [(- (/ width 2)) (- (/ height 2))]])))

(extend-type the_pad.picture.Color
  ARenderable
  (render [{:keys [red green blue]}]
    (gl-clear-color red green blue)
    (gl-color red green blue)))

(extend-type the_pad.picture.Translate
  ARenderable
  (render [{:keys [x y picture]}]
    (GL11/glPushMatrix)
    (gl-translate x y)
    (render picture)
    (GL11/glPopMatrix)))

(extend-type the_pad.picture.Rotate
  ARenderable
  (render [{:keys [angle picture]}]
    (GL11/glPushMatrix)
    (gl-rotate angle)
    (render picture)
    (GL11/glPopMatrix)))

(extend-type the_pad.picture.Pictures
  ARenderable
  (render [{:keys [pictures]}]
    (doseq [o pictures]
      (render o))))

(deftype Screen [open]
  s/AScreen
  (open? [_] @open)
  (draw! [_ geometry]
    (swap! open
           (fn [_]
             (let [close-requested? (Display/isCloseRequested)]
               (if close-requested?
                 (do (Display/destroy) false)
                 (do (GL11/glViewport 0 0 (Display/getWidth) (Display/getHeight))
                     (render geometry)
                     (Display/update)
                     (GL11/glLoadIdentity)
                     true)))))))

(defn ->Screen [{:keys [title width height color]}]
  (Display/setDisplayMode (DisplayMode. width height))
  (Display/setVSyncEnabled true)
  (Display/setResizable true)
  (Display/setTitle title)
  (Display/setInitialBackground (:red color)
                                (:green color)
                                (:blue color))
  (Display/create)
  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (GL11/glLoadIdentity)
  (GL11/glMatrixMode GL11/GL_MODELVIEW)
  (Screen. (atom true)))