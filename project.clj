(defproject geocoder-clj "0.0.3-SNAPSHOT"
  :description "A Clojure library for various geocoding services."
  :dependencies [[clj-http "0.2.0"]
                 [inflections "0.6.0"]
                 [org.clojure/clojure "1.3.0"]
                 [org.clojure/data.json "0.1.1"]
                 [org.dspace.dependencies/dspace-geoip "1.2.3"]]
  :multi-deps {"1.2" [[clj-http "0.2.0"]
                      [inflections "0.6.0"]
                      [org.clojure/clojure "1.2.1"]
                      [org.clojure/data.json "0.1.1"]]})