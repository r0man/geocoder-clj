(defproject geocoder-clj "0.2.6-SNAPSHOT"
  :description "A Clojure library for various geocoding services."
  :url "https://github.com/r0man/geocoder-clj"
  :min-lein-version "2.0.0"
  :deploy-repositories [["releases" :clojars]]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-http "1.0.1"]
                 [environ "1.0.0"]
                 [geo-clj "0.3.15"]
                 [inflections "0.9.11"]
                 [com.maxmind.geoip/geoip-api "1.2.14"]]
  :plugins [[lein-ancient "0.5.5"]])
