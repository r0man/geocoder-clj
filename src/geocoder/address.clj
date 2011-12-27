(ns geocoder.address)

(defrecord Address [country city location street-name street-number postal-code region provider])

(defn make-address
  "Make a new address."
  [& {:keys [country city location street-name street-number postal-code region provider]}]
  (Address. country city location street-name street-number postal-code region provider))
