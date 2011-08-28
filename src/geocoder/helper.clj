(ns geocoder.helper
  (:require [clj-http.client :as client])
  (:use [clojure.data.json :only (read-json)]
        [inflections.core :only (hyphenize-keys)]))

(defn json-request [request]
  (-> (client/request request) :body read-json hyphenize-keys))
