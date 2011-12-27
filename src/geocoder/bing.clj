(ns geocoder.bing
  (:use geocoder.config
        geocoder.location
        geocoder.provider))

(def ^{:dynamic true} *geocoder* nil)

(defrecord Geocoder [name key])

(defn make-geocoder
  "Make a Bing geocoder."
  [attributes]
  (if (:key attributes)
    (map->Geocoder (assoc attributes :name "Bing"))))

(defn- make-request
  "Make a Bing geocode request map."
  [provider & options]
  {:method :get :query-params {:key (:key provider)}})

(defn- fetch
  "Fetch and decode the Bing geocode response."
  [request provider]
  (decode
   (->> (fetch-json request)
        :resource-sets
        (mapcat :resources))
   provider))

(extend-type Geocoder
  IAddress
  (city [_ address]
    (:locality (:address address)))
  (country [_ address]
    {:name (:country-region (:address address))})
  (location [_ address]
    (apply make-location (:coordinates (:point address))))
  (street-name [_ address]
    (:address-line (:address address)))
  (street-number [_ address]
    nil)
  (postal-code [_ address]
    (:postal-code (:address address)))
  (region [_ address]
    (:state address)))

(extend-type Geocoder
  IGeocodeAddress
  (geocode-address [provider address options]
    (-> (make-request provider options)
        (assoc :url "http://dev.virtualearth.net/REST/v1/Locations")
        (assoc-in [:query-params :query] address)
        (fetch provider))))

(extend-type Geocoder
  IGeocodeLocation
  (geocode-location [provider location options]
    (-> (make-request provider options)
        (assoc :url (str "http://dev.virtualearth.net/REST/v1/Locations/" (to-str location)))
        (fetch provider))))

(alter-var-root #'*geocoder* (constantly (make-geocoder (:bing *config*))))
