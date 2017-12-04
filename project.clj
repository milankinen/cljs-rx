(defproject milankinen/cljs-rx "5.5.2.0"
  :description "RxJS bindings for ClojureScript"
  :url "https://github.com/milankinen/cljs-rx"
  :license {:name "MIT"
            :url  "https://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.9.0-beta4"]
                 [org.clojure/clojurescript "1.9.946"]]

  :plugins [[lein-figwheel "0.5.14"]
            [lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]]

  :resource-paths ["resources"]
  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.4"]
                                  [figwheel-sidecar "0.5.14"]
                                  [com.cemerick/piggieback "0.2.2"]
                                  [org.clojure/data.json "0.2.6"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}
  :cljsbuild
  {:builds {:min
            {:source-paths ["src"]
             :compiler     {:output-to     "target/cljsbuild/public/js/app.js"
                            :output-dir    "target/cljsbuild/public/js"
                            :source-map    "target/cljsbuild/public/js/app.js.map"
                            :optimizations :advanced
                            :pretty-print  false}}
            :app
            {:source-paths ["src"]
             :compiler     {:asset-path    "/js/out"
                            :output-to     "target/cljsbuild/public/js/app.js"
                            :output-dir    "target/cljsbuild/public/js/out"
                            :source-map    true
                            :optimizations :none
                            :pretty-print  true}}}})
