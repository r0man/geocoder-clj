(defproject geocoder-clj "0.3.4-SNAPSHOT"
  :description "A Clojure library for various geocoding services."
  :url "https://github.com/r0man/geocoder-clj"
  :min-lein-version "2.0.0"
  :deploy-repositories [["releases" :clojars]]
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[cheshire "5.8.1"]
                 [clj-http "3.9.1"]
                 [com.maxmind.geoip2/geoip2 "2.12.0"]
                 [inflections "0.13.1"]
                 [org.clojure/clojure "1.10.0"]]
  :profiles {:dev {:dependencies [[clj-http "3.9.1"]
                                  [org.clojure/test.check "0.9.0"]
                                  [spec-provider "0.4.14"]]}}
  :plugins [[jonase/eastwood "0.3.5"]]
  :aliases {"ci" ["do" ["test"] ["lint"]]
            "lint" ["do"  ["eastwood"]]})
