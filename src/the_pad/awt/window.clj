(ns the-pad.awt.window
  (:import javax.swing.JFrame)
  (:import java.awt.Dimension)
  (:import java.awt.Canvas)
  (:import java.awt.Graphics2D)
  (:import java.awt.event.MouseMotionListener)
  (:import java.awt.event.MouseListener)
  (:import java.awt.event.MouseWheelListener)
  (:import java.awt.event.MouseEvent))

(defrecord MouseState [position button-1 button-2 button-3])

(defn ^{:private true}
  update-mouse-position! [state event]
  (let [source (.getSource event)
        w (.getWidth source)
        h (.getHeight source)]
    (.translatePoint event (- (/ w 2)) (- (/ h 2)))
    (swap! state assoc :position [(.getX event) (- (.getY event))])))

(defn ^{:private true}
  update-button! [state event pressed?]
  (if-let [button (get {MouseEvent/BUTTON1 :button-1
                        MouseEvent/BUTTON2 :button-2
                        MouseEvent/BUTTON3 :button-3}
                       (.getButton event)
                       false)]
    (swap! state assoc button pressed?)))

(defn ^{:private true}
  ->mouse-listener [state]
  (reify
    MouseListener
    (mouseClicked [this event])
    (mouseEntered [this event])
    (mouseExited [this event])
    (mousePressed [this event]
      (update-button! state event true))
    (mouseReleased [this event]
      (update-button! state event false))
    MouseMotionListener
    (mouseDragged [this event]
      (update-mouse-position! state event)
      (update-button! state event true))
    (mouseMoved [this event]
      (update-mouse-position! state event))
    MouseWheelListener
    (mouseWheelMoved [this event])))

(defn ->window [name width height exit-on-close?]
  (let [f (new JFrame name)
        panel (.getContentPane f)
        canvas (new Canvas)
        mouse-state (atom (map->MouseState {:position [0 0]
                                            :button-1 false
                                            :button-2 false}))
        mouse-listener (->mouse-listener mouse-state)]
    (when exit-on-close?
      (.setDefaultCloseOperation f JFrame/EXIT_ON_CLOSE))
    (.setPreferredSize panel (new Dimension width height))
    (.setLayout panel nil)
    (.setBounds canvas 0 0 width height)
    (.addMouseListener canvas mouse-listener)
    (.addMouseMotionListener canvas mouse-listener)
    (.addMouseWheelListener canvas mouse-listener)
    (.add panel canvas)
    (.setIgnoreRepaint canvas true)
    (.pack f)
    (.setResizable f false)
    (.setVisible f true)
    (.createBufferStrategy canvas 2)
    {:frame f
     :width width
     :height height
     :strategy (.getBufferStrategy canvas)
     :mouse-state mouse-state}))

(defn draw! [window shapes]
  (let [w (:width window)
        h (:height window)
        g (cast Graphics2D (.getDrawGraphics (:strategy window)))]
    (.setColor g java.awt.Color/WHITE)
    (.fillRect g 0 0 w h)
    (.translate g (/ w 2) (/ h 2))
    (.scale g 1 -1)
    (.setColor g java.awt.Color/BLACK)
    (doseq [shape shapes]
      (.draw g shape)
      (.fill g shape))
    (.dispose g)
    (.show (:strategy window))))
