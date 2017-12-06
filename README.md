# cljs-rx

Complete RxJS 5 bindings for ClojureScript

[![Clojars Project](http://clojars.org/milankinen/cljs-rx/latest-version.svg)](https://clojars.org/milankinen/cljs-rx)

## Example usage

```clj 
(ns example.app
  (:require [cljs-rx.core :as rx]
            [cljs-rx.ajax :as ajax]))

(-> (rx/interval 5000)
    (rx/switch-map (fn [_] (ajax/request {:url "/status" :method "GET" :response-type "json"}))
    (rx/map #(js->clj (:response %) :keywordize-keys true))
    (rx/filter identity)
    (rx/subscribe! {:next     #(js/console.log %)
                    :error    #(js/console.error %)
                    :complete #(js/console.log "complete")}))
```

## Available namespaces

#### `cljs-rx.core`

Core functions and factories

#### `cljs-rx.ajax`

Observable AJAX functions

#### `cljs-rx.scheduler`

RxJS schedulers  

#### `cljs-rx.subject`

Subjects and related functions


## Differences to JS version

* `rx/from` accepts ClojureScript sequences and `IWatchable`s (= Atoms)
* Every object satisfying either `Fn` or `IFn` can be used with operators in 
  addition to normal JS functions, e.g. `(rx/filter obs #{:lol :bal})`  
* Operator functions (e.g. predicates and transform functions) receive only the
  mapped/tested values. If you need to access event indexes as well, use `:all-args`
  meta flag, e.g.
```clj
(-> ...
    (rx/map (fn [x i] (+ x i)))  ; ATTENTION! i == undefined
    ...)
    
(-> ...
    (rx/map ^:all-args (fn [x i] (+ x i)))  ; now works
    ...)
```

## License

* Bindings: MIT 
* RxJS: Apache 2.0
