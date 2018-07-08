(defproject graphical-editor "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 #_[mount "0.1.12"]
                 [org.clojure/spec.alpha "0.2.168"]
                 #_[com.taoensso/timbre "4.10.0"]
                 #_[org.apache.commons/commons-lang3 "3.7"]]




  :main ^:skip-aot graphical-editor.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all :uberjar-name "gheditor.jar"}

             :dev {;:resource-paths ["test/dev-resources"]
                   :dependencies   [ [org.clojure/tools.namespace "0.2.11"]]


                    :injections []

                   :plugins        [[lein-cljfmt "0.5.6"]
                                    [lein-kibit "0.1.5"]]
                   :source-paths ["dev"]
                   :repl-options {:init-ns user
                                  :init (set! *warn-on-reflection* true)}}})
