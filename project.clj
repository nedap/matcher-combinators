;; Please don't bump the library version by hand - use ci.release-workflow instead.
(defproject com.nedap.staffing-solutions/matcher-combinators "1.1.0-alpha1"
  :description "Library for creating matcher combinator to compare nested data structures"
  :url "https://github.com/nubank/matcher-combinators"
  :license {:name "Apache License, Version 2.0"}

  :signing {:gpg-key "releases-staffingsolutions@nedap.com"}

  :repositories {"releases" {:url      "https://nedap.jfrog.io/nedap/staffing-solutions/"
                             :username :env/artifactory_user
                             :password :env/artifactory_pass}}
  
  :repository-auth {#"https://nedap.jfrog\.io/nedap/staffing-solutions/"
                    {:username :env/artifactory_user
                     :password :env/artifactory_pass}}

  :cljfmt {:indents {facts    [[:block 1]]
                     fact     [[:block 1]]
                     fdef     [[:block 1]]
                     provided [[:inner 0]]
                     tabular  [[:inner 0]]}}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/spec.alpha "0.2.176"]
                 [org.clojure/math.combinatorics "0.1.5"]
                 [midje "1.9.8" :exclusions [org.clojure/clojure]]]

  :test-paths ["test/clj"]
  :source-paths ["src/cljc" "src/cljs" "src/clj"]

  :profiles {:dev {:plugins [[lein-midje "3.2.1"]
                             [lein-cljfmt "0.5.7"]
                             [lein-cljsbuild "1.1.7"]
                             [lein-kibit "0.1.6"]
                             [lein-ancient "0.6.15"]
                             [lein-doo "0.1.11"]]
                   :dependencies [[org.clojure/test.check "0.10.0-alpha3"]
                                  [org.clojure/clojurescript "1.10.520"]]}
             :ci   {:pedantic?    :abort
                    :jvm-opts     ["-Dclojure.main.report=stderr"]
                    :global-vars  {*assert* true} ;; `ci.release-workflow` relies on runtime assertions
                    :dependencies [[com.nedap.staffing-solutions/ci.release-workflow "1.3.0-alpha3"]]}
              :1.8 {:dependencies [[org.clojure/clojure "1.8.0"]]}}

  :aliases {"lint"     ["do" "cljfmt" "check," "kibit"]
            "lint-fix" ["do" "cljfmt" "fix," "kibit" "--replace"]
            "test-clj" ["all" "do" ["test"] ["check"]]
            "test-phantom" ["doo" "phantom" "test"]
            "test-advanced" ["doo" "phantom" "advanced-test"]
            "test-node-watch" ["doo" "node" "node-test"]
            "test-node" ["doo" "node" "node-test" "once"]}
  ;; Below, :process-shim false is workaround for <https://github.com/bensu/doo/pull/141>
  :cljsbuild {:builds [{:id "test"
                        :source-paths ["src/cljc" "src/cljs" "test/cljc" "test/cljs"]
                        :compiler {:output-to "target/out/test.js"
                                   :output-dir "target/out"
                                   :main matcher-combinators.doo-runner
                                   :optimizations :none
                                   :process-shim false}}
                       {:id "advanced-test"
                        :source-paths ["src/cljc" "src/cljs" "test/cljc" "test/cljs"]
                        :compiler {:output-to "target/advanced_out/test.js"
                                   :output-dir "target/advanced_out"
                                   :main matcher-combinator.doo-runner
                                   :optimizations :advanced
                                   :process-shim false}}
                       ;; Node.js requires :target :nodejs, hence the separate
                       ;; build configuration.
                       {:id "node-test"
                        :source-paths ["src/cljc" "src/cljs" "test/cljc" "test/cljs"]
                        :compiler {:output-to "target/node_out/test.js"
                                   :output-dir "target/node_out"
                                   :main matcher-combinators.doo-runner
                                   :optimizations :none
                                   :target :nodejs
                                   :process-shim false}}]})
