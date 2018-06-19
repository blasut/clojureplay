(ns workout
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [clojure.pprint :as pprint]))

(s/valid? number? 44)

(s/def ::sets (s/and int? #(> % 0)))
(s/def ::reps (s/and int? #(> % 0)))
(s/def ::number (s/and int? #(> % 0)))
(s/def ::unit #{:kg :lb})
(s/def ::unit keyword?)
(s/def ::weight (s/keys :req [::unit ::number]))
(s/def ::name string?)

(let [exercise (s/keys :req-un [::sets
                                ::reps
                                ::name])
      pass (s/coll-of exercise)
      schema (s/coll-of pass)
      completed-exercise (s/keys :req-un [::sets ::reps ::weight])
      completed-pass (s/and pass (s/coll-of completed-exercise))]
  (s/explain exercise {:sets 5 :reps 5 :name "Squat"})
  (println (s/exercise completed-pass))
  (s/exercise completed-exercise))


(let [pass {:date "2018-19-6" :name "A" :week 1
            :exercises [{:name "Squat"
                         :expected [5 5 50]
                         :set-reps-weight [[1 5 20]
                                           [2 5 40]
                                           [3 5 30]
                                           [4 5 30]
                                           [5 5 30]]}]}
      input-pass {:date "2018-19-6" :name "A" :week 1
                  :exercises [{:name "Squat"
                               :expected "50*5*5"
                               :set-reps-weight "1/5-20\n2/5-40\n3/5-30\n4/5-30\n5/5-30\n"}]}
      parse-pass (fn [raw-pass]
                   (let [s-r-w (map (fn [e]
                                      (map #(str/split % #"/|-")
                                           (:set-reps-weight (update e :set-reps-weight #(str/split % #"\n")))))
                                    (:exercises raw-pass))]
                     s-r-w))]
  (pprint/pprint (:exercises input-pass))
  (pprint/pprint (parse-pass input-pass)))

(map #(str "Hello " % "!") ["Ford" "Arthur" "Tricia"])











