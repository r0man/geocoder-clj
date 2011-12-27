(ns geocoder.test.config
  (:import java.io.File)
  (:use [clojure.string :only (join)]
        clojure.test
        geocoder.config))

(def config {:bing "bing" :yahoo "yahoo"})

(defn example-init-config []
  (let [filename "/tmp/init.clj"]
    (spit filename `(def ~'geocoder-config ~config))
    filename))

(defn example-project-config []
  (let [filename "/tmp/project.clj"]
    (spit filename `(~'defproject ~'myproject "1.0" :geocoder-config ~config))
    filename))

(deftest test-lein-init-file
  (let [file (lein-init-file)]
    (is (instance? File file))
    (is (= (join File/separator [(System/getenv "HOME") ".lein" "init.clj"])
           (str file)))))

(deftest test-lein-init-file?
  (is (not (lein-init-file? nil)))
  (is (not (lein-init-file? "")))
  (is (lein-init-file? (example-init-config))))

(deftest test-lein-project-file
  (let [file (lein-project-file)]
    (is (instance? File file))
    (is (= (join File/separator [(System/getProperty "user.dir") "project.clj"])
           (str file)))))

(deftest test-lein-project-file?
  (is (not (lein-project-file? nil)))
  (is (not (lein-project-file? "")))
  (is (lein-project-file? (example-project-config))))

(deftest test-load-config
  (testing "map config"
    (is (= config (load-config config))))
  (testing "init.clj config"
    (is (= config (load-config (example-init-config)))))
  (testing "project.clj config"
    (is (= config (load-config (example-project-config))))))

(deftest test-with-config
  (testing "map config"
    (with-config config
      (is (= config *config*))))
  (testing "init.clj config"
    (with-config (example-init-config)
      (is (= config *config*))))
  (testing "project.clj config"
    (with-config (example-project-config)
      (is (= config *config*)))))
