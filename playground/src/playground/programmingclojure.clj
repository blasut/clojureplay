(ns playground.programmingclojure
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

;; clojure programming

(comment
  "Using quote to figure out how different reader macros expand"
  ''x
  '''''''''''''''''''x
  '@x
  '#(+ % %)
  "pretty neat"
  '`(a b ~c)
  (pprint/pprint '`(a b ~c))
  (pprint/pprint '`(a 1))
  '(a 1)
  (pprint/pprint '`(a ~@(1 2 3) c))

  )

;; a lot of stuff has a implicit do block (progn)
(macroexpand '(let [a 5] (println 5) (println (+ a 1))))
;; cant be seen here though?


(def chas {:name "Chas" :age 31 :location "Massachusetts"})

;; this is reallly nice
;; fucking awesome u can use keys,strs and syms
(let [{:keys [name age location]} chas]
  (format "%s is %s years old and lives in %s." name age location))

(def brian {"name" "Brian" "age" 31 "location" "British Columbia"})
(let [{:strs [name age location]} brian]
  (format "%s is %s years old and lives in %s." name age location))

(def christophe {'name "Christophe" 'age 33 'location "Rhône-Alpes"})
(let [{:syms [name age location]} christophe]
  (format "%s is %s years old and lives in %s." name age location))

;; mutually recursive functions
;; letfn creates several named functions at once
(letfn [(odd? [n]
          (even? (dec n)))
        (even? [n]
          (or (zero? n)
              (odd? (dec n))))] (odd? 11))

;; function literals:
(read-string "(fn [x y] (Math/pow x y))")
;; both of thes are the same
(read-string "#(Math/pow %1 %2)")

(defn embedded-repl
  "A naive repl from clojure programming book. Enter :quit to exit"
  []
  (print (str (ns-name *ns*) ">>> "))
  (flush)
  (let [expr (read)
        value (eval expr)]
    (when (not= :quit value)
      (println value)
      (recur))))

(comment
  (embedded-repl)
  )

;; fun

;; compps
(def negated-sum-str (comp str - +))

(negated-sum-str 10 12 3.4)

(def camel->keyword (comp keyword
                          str/join
                          (partial interpose \-)
                          (partial map str/lower-case)
                          #(str/split % #"(?<=[a-z])(?=[A-Z])")))

(camel->keyword "CamelCase")
(camel->keyword "lowerCamelCase")

;; comp and ->> is the same thing. or has the same behaviour
;; comp and partial = point free programming

;; THE LOGGER

(defn print-logger
  [writer]
  #(binding [*out* writer]
     (println %)))

(def *out*-logger (print-logger *out*))
(*out*-logger "hello")

(def writer (java.io.StringWriter.))
(def retained-logger (print-logger writer))

(retained-logger "hello")

(str writer)

(defn file-logger
  [file]
  #(with-open [f (io/writer file :append true)]
     ((print-logger f) %)))

(def log->file (file-logger "messages.log"))

(log->file "hello")

(defn multi-logger
  [& logger-fns]
  #(doseq [f logger-fns]
     (f %)))

(def log (multi-logger
          (print-logger *out*)
          (file-logger "messages.log")))

(log "hello again")

(defn timestamped-logger [logger]
  #(logger (format "[%1$tY-%1$tm-%1$te %1$tH:%1$tM:%1$tS] %2$s" (java.util.Date.) %)))

(def log-timestamped (timestamped-logger
                      (multi-logger
                       (print-logger *out*)
                       (file-logger "messages.log"))))

(log-timestamped "good by now")

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Att page 140
(def orders
  [{:product "Clock", :customer "Wile Coyote", :qty 6, :total 300}
   {:product "Dynamite", :customer "Wile Coyote", :qty 20, :total 5000}
   {:product "Shotgun", :customer "Elmer Fudd", :qty 2, :total 800}
   {:product "Shells", :customer "Elmer Fudd", :qty 4, :total 100}
   {:product "Hole", :customer "Wile Coyote", :qty 1, :total 1000}
   {:product "Anvil", :customer "Elmer Fudd", :qty 2, :total 300}
   {:product "Anvil", :customer "Elmer Fudd", :qty 2, :total 300}
   {:product "Anvil", :customer "Wile Coyote", :qty 6, :total 900}])

(defn reduce-by
  [key-fn f init coll]
  (reduce (fn [summaries x]
            (let [k (key-fn x)]
              (assoc summaries k (f (summaries k init) x))))
          {} coll))

(reduce-by :customer #(+ %1 (:total %2)) 0 orders)

(reduce-by :product #(conj %1 (:customer %2)) #{} orders)

(defn reduce-by-in
  [keys-fn f init coll]
  (reduce (fn [summaries x]
            (let [ks (keys-fn x)]
              (assoc-in summaries ks
                        (f (get-in summaries ks init) x))))
          {} coll))

;; juxt seems cool. takes ((juxt a b c) x) => [(a x) (b x) (c x)]
(reduce-by-in (juxt :customer :product)
              #(+ %1 (:total %2)) 0 orders)

(def flat-breakup
  {["Wile Coyote" "Anvil"] 900,
   ["Elmer Fudd" "Anvil"] 300,
   ["Wile Coyote" "Hole"] 1000,
   ["Elmer Fudd" "Shells"] 100,
   ["Elmer Fudd" "Shotgun"] 800,
   ["Wile Coyote" "Dynamite"] 5000,
   ["Wile Coyote" "Clock"]
   300})

(reduce #(apply assoc-in %1 %2) {} flat-breakup)

;; Need to pracitce reduce

;; page 151

(defn naive-into
  [coll source]
  (reduce conj coll source))

(= (into #{} (range 500))
   (naive-into #{} (range 500)))

(defn faster-into
  [coll source]
  (persistent!  (reduce conj! (transient coll) source)))

(= (into #{} (range 500))
   (naive-into #{} (range 500))
   (faster-into #{} (range 500)))


;; 155

;; meta data metadata
(def e ^{:created (System/currentTimeMillis)} [1 2 3])

(meta e)

;; page 156



