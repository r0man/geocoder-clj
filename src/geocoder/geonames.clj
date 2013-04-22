(ns geocoder.geonames
  (:require [clojure.string :refer [lower-case]]
            [environ.core :refer [env]]
            [geo.core :refer [point point-x point-y]]
            [geocoder.util :refer [fetch-json]]))

(defn city
  [address]
  (:place-name address))

(defn country
  [address]
  {:iso-3166-1-alpha-2 (lower-case (:country-code address))})

(defn location [address]
  (point 4326 (:lng address) (:lat address)))

(defn street-name [address]
  (:address-line (:address address)))

(defn postal-code [address]
  (:postal-code address))

(defn region [address]
  (:admin-name1 address))

(defn- request
  "Make a Bing geocode request map."
  [& [opts]]
  {:request-method :get
   :query-params
   (-> (dissoc opts :api-key)
       (assoc :username (or (:api-key opts)
                            (env :geonames-api-key))))})

(defn- fetch
  "Fetch and decode the Geonames geocode response."
  [request]
  (-> (fetch-json request)
      :postal-codes))

(defn geocode-address [address & {:as opts}]
  (-> (request opts)
      (assoc :url "http://api.geonames.org/postalCodeSearchJSON")
      (assoc-in [:query-params :placename] address)
      (fetch)))

(defn geocode-location [location & {:as opts}]
  (-> (request opts)
      (assoc :url (str "http://api.geonames.org/findNearbyPostalCodesJSON"))
      (assoc-in [:query-params :lat] (point-y location))
      (assoc-in [:query-params :lng] (point-x location))
      (fetch)))
