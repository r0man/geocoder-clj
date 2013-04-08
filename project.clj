(defproject geocoder-clj "0.1.2-SNAPSHOT"
  :description "A Clojure library for various geocoding services."
  :url "https://github.com/r0man/geocoder-clj"
  :min-lein-version "2.0.0"
  :author "Roman Scherer"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[clj-http "0.7.0"]
                 [inflections "0.8.0"]
                 [org.clojure/clojure "1.5.1"]
                 [org.dspace.dependencies/dspace-geoip "1.2.3"]])
