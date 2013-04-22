(ns geocoder.bing
  (:require [environ.core :refer [env]]
            [geo.core :refer [point]]
            [geocoder.util :refer [fetch-json format-point]]))

(defn city
  "Returns the city of `address`."
  [address]
  (:locality (:address address)))

(defn country
  "Returns the country of `address`."
  [address]
  {:name (:country-region (:address address))})

(defn location
  "Returns the geographical location of `address`."
  [address]
  (let [[y x] (:coordinates (:point address))]
    (point 4326 x y)))

(defn street-name
  "Returns the street name of `address`."
  [address]
  (:address-line (:address address)))

(defn postal-code
  "Returns the postal code of `address`."
  [address]
  (:postal-code (:address address)))

(defn region
  "Returns the region of `address`."
  [address]
  (:state address))

(defn- request
  "Make a Bing geocode request map."
  [& [opts]]
  {:request-method :get
   :url "http://dev.virtualearth.net/REST/v1/Locations"
   :query-params
   (-> (dissoc opts :api-key)
       (assoc :key (or (:api-key opts)
                       (env :bing-api-key))))})

(defn- fetch
  "Fetch and decode the Bing geocode response."
  [request]
  (->> (fetch-json request)
       :resource-sets
       (mapcat :resources)))

(defn geocode-address
  "Geocode an address."
  [address & {:as opts}]
  (-> (request opts)
      (assoc-in [:query-params :query] address)
      (fetch)))

(defn geocode-location
  "Geocode a geographical location."
  [location & {:as opts}]
  (-> (request opts)
      (update-in [:url] #(format "%s/%s" %1 (format-point location)))
      (fetch)))
