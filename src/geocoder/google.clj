(ns geocoder.google
  (:use [clojure.string :only (lower-case)]
        geocoder.address
        geocoder.location
        geocoder.provider))

(defn request [provider & options]
  {:method :get
   :url "http://maps.google.com/maps/api/geocode/json"
   :query-params {:sensor false :language "en"}})

(defn- extract
  "Extract the component from result."
  [result component]
  (first (filter #(contains? (set (:types %)) component) (:address-components result))))

(defrecord Result []
  IAddress
  (city [result]
    (:long-name (extract result "locality")))
  (country [result]
    {:name (:long-name (extract result "country"))
     :iso-3166-1-alpha-2 (lower-case (:short-name (extract result "country")))})
  (location [result]
    (make-location
     (:lat (:location (:geometry result)))
     (:lng (:location (:geometry result)))))
  (street-name [result]
    (:long-name (extract result "route")))
  (street-number [result]
    (:long-name (extract result "street_number")))
  (postal-code [result]
    (:long-name (extract result "postal_code")))
  (region [result]
    (:long-name (extract result "administrative_area_level_1"))))

(defrecord Provider []
  IProvider
  (geocode-request [provider address options]
    (-> (request provider options)
        (assoc-in [:query-params :address] address)))
  (results [provider response]
    (map #(merge (Result.) %) (:results response)))
  (reverse-geocode-request [provider location options]
    (-> (request provider options)
        (assoc-in [:query-params :latlng] (to-str location)))))
