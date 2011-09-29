(ns geocoder.location)

(def earth-radius-km 6371.009)

(defrecord Location [latitude longitude])

(defn make-location [latitude longitude]
  (Location.
   (Double/parseDouble (str latitude))
   (Double/parseDouble (str longitude))))

(defprotocol ILocation
  (latitude [location]
    "Returns the latitude of location.")
  (longitude [location]
    "Returns the longitude of location.")
  (to-str [location]
    "Returns the string representation of location."))

(extend-type clojure.lang.IPersistentMap
  ILocation
  (latitude [this]
    (:latitude this))
  (longitude [this]
    (:longitude this))
  (to-str [this]
    (str (latitude this) "," (longitude this))))

(defn distance-in-km
  "Returns the distance between location-1 and location-2 in km."
  [location-1 location-2 & [radius]]
  (let [[lat1-r long1-r lat2-r long2-r]
        (map #(Math/toRadians %)
             [(latitude location-1) (longitude location-1)
              (latitude location-2) (longitude location-2)])]
    (* (or radius earth-radius-km)
       (Math/acos (+ (* (Math/sin lat1-r) (Math/sin lat2-r))
                     (* (Math/cos lat1-r) (Math/cos lat2-r) (Math/cos (- long1-r long2-r)))) ))) )
