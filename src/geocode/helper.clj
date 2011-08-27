(ns geocode.helper)

(def *api-key* nil)

(defn format-location
  "Format the location as query parameter."
  [location] (str (:latitude location) "," (:longitude location)))

(defmacro with-api-key
  "Binds *api-key* to the given key."
  [key & body]
  `(binding [*api-key* ~key]
     ~@body))

