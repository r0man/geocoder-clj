(defproject geocoder-clj "0.2.4-SNAPSHOT"
  :description "A Clojure library for various geocoding services."
  :url "https://github.com/r0man/geocoder-clj"
  :min-lein-version "2.0.0"
  :lein-release {:deploy-via :clojars}
  :author "Roman Scherer"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[clj-http "0.7.7"]
                 [environ "0.4.0"]
                 [geo-clj "0.3.6"]
                 [inflections "0.9.2"]
                 [org.clojure/clojure "1.5.1"]
                 [org.dspace.dependencies/dspace-geoip "1.2.3"]])
