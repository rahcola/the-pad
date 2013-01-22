(defproject the-pad "0.1.0-SNAPSHOT"
  :description "the pad"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.lwjgl.lwjgl/lwjgl "2.8.5"]]
  :jvm-opts ["-Djava.library.path=native/linux"]
  :main the-pad.core
  :profiles {:dev {:dependencies [[midje "1.4.0"]]}})