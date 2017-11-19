(ns playground.etl-pipelines
  (:require [clojure.java.io :as io]
            [cheshire.core :as json]))

;; https://tech.grammarly.com/blog/building-etl-pipelines-with-clojure

(comment
  (letfn [(rand-obj []
            (case (rand-int 3)
              0 {:type "number" :number (rand-int 1000)}
              1 {:type "string" :string (apply str (repeatedly 30 #(char (+ 33 (rand-int 90)))))}
              2 {:type "empty"}))]
    (with-open [f (io/writer "/tmp/dummy.json")]
      (binding [*out* f]
        (dotimes [_ 100000]
          (println (json/encode (rand-obj))))))))

;; fake db
(def db (atom 0))

(defn save-into-database [batch]
  (swap! db + (count batch)))

(def file-name "/tmp/dummy.json")

(defn parse-json-file-lazy [file]
  (map #(json/decode % true)
       ;; line-seq reads into a lazy seq, woop
       (line-seq (io/reader file))))

(take 10 (parse-json-file-lazy file-name))

(defn valid-entry? [log-entry]
  (not= (:type log-entry) "empty"))

(defn transform-entry-if-relevant [log-entry]
  (cond (= (:type log-entry) "number")
        (let [number (:number log-entry)]
          (when (> number 900)
            (assoc log-entry :number (Math/log number))))

        (= (:type log-entry) "string")
        (let [string (:string log-entry)]
          (when (re-find #"a" string)
            (update log-entry :string str "-improved")))))

(->> (parse-json-file-lazy file-name)
     (filter valid-entry?)
     (keep transform-entry-if-relevant)
     (take 10))

(defn process [files]
  (->> files
       (mapcat parse-json-file-lazy) ;; mapcat because on file produces many entries
       (filter valid-entry?)
       (keep transform-entry-if-relevant)
       (partition-all 1000) ;; form batches for "saving" into db
       (map save-into-database)
       doall)) ;; nice little "trick" to force eagerness

(comment
  (time (process [file-name]))

  (time (process (repeat 8 file-name))))







