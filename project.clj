(defproject geocoder-clj "0.3.2-SNAPSHOT"
  :description "A Clojure library for various geocoding services."
  :url "https://github.com/r0man/geocoder-clj"
  :min-lein-version "2.0.0"
  :deploy-repositories [["releases" :clojars]]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[clj-http "3.9.1"]
                 [com.maxmind.geoip2/geoip2 "2.12.0"]
                 [geo-clj "0.6.3"]
                 [inflections "0.13.1"]
                 [org.clojure/clojure "1.10.0"]]
  :plugins [[jonase/eastwood "0.3.5"]]
  :aliases {"ci" ["do" ["test"] ["lint"]]
            "lint" ["do"  ["eastwood"]]})
