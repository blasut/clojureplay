(defproject pcl "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-beta2"]
                 [org.clojure/test.check "0.10.0-alpha2"]
                 [ring "1.6.2"]
                 [ring/ring-spec "0.0.3"]]
  :main ^:skip-aot pcl.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
