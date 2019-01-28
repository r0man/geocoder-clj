(ns geocoder.google
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [geocoder.util :refer [fetch-json format-location]]
            [inflections.core :refer [underscore]]))

;; Results

(s/def ::types (s/coll-of string?))
(s/def ::global-code string?)
(s/def ::compound-code string?)
(s/def ::plus-code (s/keys :req-un [::compound-code ::global-code]))
(s/def ::place-id string?)
(s/def ::partial-match boolean?)
(s/def ::lng number?)
(s/def ::lat number?)
(s/def ::southwest (s/keys :req-un [::lat ::lng]))
(s/def ::northeast (s/keys :req-un [::lat ::lng]))
(s/def ::viewport (s/keys :req-un [::northeast ::southwest]))
(s/def ::location-type string?)
(s/def ::location (s/keys :req-un [::lat ::lng]))

(s/def ::geometry
  (s/keys :req-un [::location ::location-type ::viewport]))

(s/def ::formatted-address string?)
(s/def ::short-name string?)
(s/def ::long-name string?)

(s/def ::address-components
  (s/coll-of (s/keys :req-un [::long-name ::short-name ::types])))

(s/def ::result
  (s/keys :req-un
          [::address-components
           ::formatted-address]
          :opt-un
          [::geometry
           ::place-id
           ::types
           ::partial-match
           ::plus-code]))

(s/def ::results
  (s/coll-of ::result :gen-max 3))

;; Geocoder

(s/def ::sensor boolean?)
(s/def ::language string?)
(s/def ::key any?)
(s/def ::query-params (s/keys :req-un [::key ::language ::sensor]))
(s/def ::url string?)
(s/def ::request-method keyword?)

(s/def ::geocoder
  (s/keys :req-un [::query-params ::request-method ::url]))

(defn- extract-type
  "Extract the address type from response."
  [response type]
  (let [type (underscore (name type))]
    (->> (:address-components response)
         (filter #(contains? (set (:types %)) type) )
         (first))))

(defn long-name
  "Extract the long name of the address type from response."
  [response type]
  (:long-name (extract-type response type)))

(defn short-name
  "Extract the short name of the address type from response."
  [response type]
  (:short-name (extract-type response type)))

(defn city
  "Returns the city of `address`."
  [address]
  (:long-name (extract-type address :locality)))

(defn country
  "Returns the country of `address`."
  [address]
  {:name (long-name address :country)
   :iso-3166-1-alpha-2 (str/lower-case (short-name address :country))})

(defn location
  "Returns the geographical location of `address`."
  [address]
  (:location (:geometry address)))

(defn street-name
  "Returns the street name of `address`."
  [address]
  (long-name address :route))

(defn street-number
  "Returns the street number of `address`."
  [address]
  (long-name address :street-number))

(defn postal-code
  "Returns the postal code of `address`."
  [address]
  (long-name address :postal-code))

(defn region
  "Returns the region of `address`."
  [address]
  (long-name address :administrative-area-level-1))

(defn- request [request]
  (let [{:keys [error-message status] :as response} (fetch-json request)]
    (if (contains? #{"OK" "ZERO_RESULTS"} status)
      response
      (throw (ex-info (str "Geocode request failed: " error-message) response)))))

(defn geocode-address
  "Geocode an address."
  [geocoder address & {:as opts}]
  (when-not (str/blank? address)
    (-> (update-in geocoder [:query-params] #(merge %1 opts))
        (assoc-in [:query-params :address] address)
        (request)
        :results)))

(s/fdef geocode-address
  :args (s/cat :geocoder ::geocoder :address string?)
  :ret ::results)

(defn geocode-location
  "Geocode a geographical location."
  [geocoder location & {:as opts}]
  (-> (update-in geocoder [:query-params] #(merge %1 opts))
      (assoc-in [:query-params :latlng] (format-location location))
      (request)
      :results))

(s/fdef geocode-location
  :args (s/cat :geocoder ::geocoder :location ::location)
  :ret ::results)

(defn geocoder
  "Returns a new Google Maps geocoder."
  [& [{:keys [api-key language sensor?]}]]
  {:request-method :get
   :url "https://maps.google.com/maps/api/geocode/json"
   :query-params
   {:key api-key
    :language (or language "en")
    :sensor (or sensor? false)}})
