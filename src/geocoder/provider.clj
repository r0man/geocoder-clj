(ns geocoder.provider
  (:require [cheshire.core :refer [parse-string]]
            [clj-http.client :as client]
            [inflections.core :refer [hyphenize]]))

(defprotocol IAddress
  (city [provider address]
    "Returns the city of the address.")
  (country [provider address]
    "Returns the country of the address.")
  (location [provider address]
    "Returns the location of the address.")
  (street-name [provider address]
    "Returns the street name of the address.")
  (street-number [provider address]
    "Returns the street number of the address.")
  (postal-code [provider address]
    "Returns the postal code of the address.")
  (region [provider address]
    "Returns the region of the address."))

(defprotocol IGeocodeAddress
  (geocode-address [provider address options]
    "Geocode the address."))

(defprotocol IGeocodeLocation
  (geocode-location [provider location options]
    "Geocode the location."))

(defprotocol IGeocodeInternetAddress
  (geocode-ip-address [provider ip-address options]
    "Geocode the internet address."))

(defn decode
  "Decode the geocoder response."
  [response provider]
  (map
   #(with-meta
      {:city (city provider %1)
       :country (country provider %1)
       :location (location provider %1)
       :street-name (street-name provider %1)
       :street-number (street-number provider %1)
       :postal-code (postal-code provider %1)
       :region (region provider %1)
       :provider (:name provider)}
      (if (map? %1) %1))
   (remove nil? (if (sequential? response) response [response]))))

(defn fetch-json
  "Send the request, parse the JSON body and return the response as a
  hyphenized map."
  [request]
  (let [read-json #(parse-string %1 true)]
    (->> (client/request request)
         :body read-json hyphenize)))

(defn supports-address-geocoding?
  "Returns true if provider supports address geocoding, otherwise false."
  [provider] (satisfies? IGeocodeAddress provider))

(defn supports-ip-address-geocoding?
  "Returns true if provider supports internet address geocoding, otherwise false."
  [provider] (satisfies? IGeocodeInternetAddress provider))

(defn supports-location-geocoding?
  "Returns true if provider supports location geocoding, otherwise false."
  [provider] (satisfies? IGeocodeLocation provider))
