(ns geocoder.test.credentials
  (:import java.io.File)
  (:use [clojure.string :only (join)]
        clojure.test
        geocoder.credentials))

(def credentials {:bing "bing" :yahoo "yahoo"})

(defn example-init-credentials []
  (let [filename "/tmp/init.clj"]
    (spit filename `(def ~'geocoder-credentials ~credentials))
    filename))

(defn example-project-credentials []
  (let [filename "/tmp/project.clj"]
    (spit filename `(~'defproject ~'myproject "1.0" :geocoder-credentials ~credentials))
    filename))

(deftest test-lein-init-file
  (let [file (lein-init-file)]
    (is (instance? File file))
    (is (= (join File/separator [(System/getenv "HOME") ".lein" "init.clj"])
           (str file)))))

(deftest test-lein-init-file?
  (is (not (lein-init-file? nil)))
  (is (not (lein-init-file? "")))
  (is (lein-init-file? (example-init-credentials))))

(deftest test-lein-project-file
  (let [file (lein-project-file)]
    (is (instance? File file))
    (is (= (join File/separator [(System/getProperty "user.dir") "project.clj"])
           (str file)))))

(deftest test-lein-project-file?
  (is (not (lein-project-file? nil)))
  (is (not (lein-project-file? "")))
  (is (lein-project-file? (example-project-credentials))))

(deftest test-load-credentials
  (testing "map credentials"
    (is (= credentials (load-credentials credentials))))
  (testing "init.clj credentials"
    (is (= credentials (load-credentials (example-init-credentials)))))
  (testing "project.clj credentials"
    (is (= credentials (load-credentials (example-project-credentials))))))

(deftest test-with-credentials
  (testing "map credentials"
    (with-credentials credentials
      (is (= credentials *credentials*))))
  (testing "init.clj credentials"
    (with-credentials (example-init-credentials)
      (is (= credentials *credentials*))))
  (testing "project.clj credentials"
    (with-credentials (example-project-credentials)
      (is (= credentials *credentials*)))))
