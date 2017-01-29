(defproject clgame "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.lwjgl.lwjgl/lwjgl "2.9.3"]
                 [slick-util/slick-util  "1.0.0"]
                 [com.rpl/specter "0.9.2"]
                 [org.clojure/math.combinatorics "0.1.3"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/tools.namespace "0.2.11"]]
  :jvm-opts [~(str "-Djava.library.path=native/linux:" (System/getProperty "java.library.path"))]
  :main ^:skip-aot clgame.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
