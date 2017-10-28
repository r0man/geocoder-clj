(defproject geocoder-clj "0.2.7-SNAPSHOT"
  :description "A Clojure library for various geocoding services."
  :url "https://github.com/r0man/geocoder-clj"
  :min-lein-version "2.0.0"
  :deploy-repositories [["releases" :clojars]]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[clj-http "0.9.2"]
                 [environ "0.5.0"]
                 [geo-clj "0.3.12"]
                 [inflections "0.9.9"]
                 [org.clojure/clojure "1.6.0"]
                 [org.dspace.dependencies/dspace-geoip "1.2.3"]])
