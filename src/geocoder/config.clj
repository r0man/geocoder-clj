(ns geocoder.config
  (:use [clojure.java.io :only (file)]))

(def ^:dynamic *config* {})

(defn lein-init-file
  "Returns the Leiningen init.clj file."
  [] (file (System/getenv "HOME") ".lein" "init.clj"))

(defn lein-init-file?
  "Returns true if filename is a Leiningen init.clj file, otherwise false."
  [filename]
  (if-let [file (file filename)]
    (and (.exists file) (= "init.clj"(.getName file)))))

(defn lein-project-file
  "Returns the Leiningen project.clj file."
  [] (file (System/getProperty "user.dir") "project.clj"))

(defn lein-project-file?
  "Returns true if filename is a Leiningen project.clj file, otherwise false."
  [filename]
  (if-let [file (file filename)]
    (and (.exists file) (= "project.clj"(.getName file)))))

(defn lein-init-config
  "Load the geocoder config from the Leiningen init.clj file."
  [& [filename]]
  (let [file (file (or filename (lein-init-file)))]
    (when (lein-init-file? file)
      (in-ns 'user)
      (load-file (str file))
      (if-let [config (resolve 'user/geocoder-config)]
        @config))))

(defn lein-project-config
  "Load the geocoder config from the Leiningen project.clj file."
  [& [filename]]
  (let [file (file (or filename (lein-project-file)))]
    (if (lein-project-file? file)
      (->> (read-string (slurp (str file)))
           (drop-while #(not (keyword? %1)))
           (apply hash-map)
           :geocoder-config))))

(defn load-config
  "Load the geocoder config. If config is a map, it is
  returned as it is. Otherwise config is interpreted as a
  filename to a Leiningen's init.clj or a project.clj file."
  [& [config]]
  (or (if (map? config) config)
      (lein-init-config config)
      (lein-project-config config)))

(defmacro with-config
  "Evaluate body with *config* bound to the result
  of (load-config config)."
  [config & body]
  `(binding [*config* (load-config ~config)]
     ~@body))

(alter-var-root #'*config* (constantly (load-config)))
