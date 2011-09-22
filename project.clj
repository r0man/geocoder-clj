(defproject geocoder-clj "0.0.2-SNAPSHOT"
  :description "A Clojure library for various geocoding services."
  :dependencies [[clj-http "0.2.0"]
                 [inflections "0.6.0-SNAPSHOT"]
                 [org.clojure/clojure "1.3.0-RC0"]
                 [org.clojure/data.json "0.1.1"]]
  :multi-deps {"1.2" [[clj-http "0.2.0"]
                      [inflections "0.6.0-SNAPSHOT"]
                      [org.clojure/clojure "1.2.1"]
                      [org.clojure/data.json "0.1.1"]]})