(ns cljs-rx.scheduler
  (:require [cljsjs.rxjs :as rxjs]
            [cljs-rx.internal.interrop :refer [fn-2]]))

(def ^:private schedulers
  (js->clj rxjs/Scheduler :keywordize-keys true))

; === classes ===

(def VirtualTimeScheduler rxjs/VirtualTimeScheduler)

(def TestScheduler rxjs/TestScheduler)

; === factories ===

(defn test-scheduler [deep-equals-assertion]
  (TestScheduler. (fn-2 deep-equals-assertion)))

; TODO: implement virtual time scheduler
#_(defn virtual-time-scheduler []
  (VirtualTimeScheduler. ))


; === globals ===

(def asap (:asap schedulers))

(def async (:async schedulers))

(def animation-frame (:animationFrame schedulers))

(def queue (:queue schedulers))
