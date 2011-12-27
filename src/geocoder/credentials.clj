(ns geocoder.credentials
  (:use [clojure.java.io :only (file)]))

(def ^:dynamic *credentials* {})

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

(defn lein-init-credentials
  "Load the geocoder credentials from the Leiningen init.clj file."
  [& [filename]]
  (let [file (file (or filename (lein-init-file)))]
    (when (lein-init-file? file)
      (in-ns 'user)
      (load-file (str file))
      (if-let [credentials (resolve 'user/geocoder-credentials)]
        @credentials))))

(defn lein-project-credentials
  "Load the geocoder credentials from the Leiningen project.clj file."
  [& [filename]]
  (let [file (file (or filename (lein-project-file)))]
    (if (lein-project-file? file)
      (->> (read-string (slurp (str file)))
           (drop-while #(not (keyword? %1)))
           (apply hash-map)
           :geocoder-credentials))))

(defn load-credentials
  "Load the geocoder credentials. If credentials is a map, it is
  returned as it is. Otherwise credentials is interpreted as a
  filename to a Leiningen's init.clj or a project.clj file."
  [& [credentials]]
  (or (if (map? credentials) credentials)
      (lein-init-credentials credentials)
      (lein-project-credentials credentials)))

(defmacro with-credentials
  "Evaluate body with *credentials* bound to the result
  of (load-credentials credentials)."
  [credentials & body]
  `(binding [*credentials* (load-credentials ~credentials)]
     ~@body))

(alter-var-root #'*credentials* (constantly (load-credentials)))
