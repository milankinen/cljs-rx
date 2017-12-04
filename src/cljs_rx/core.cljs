(ns cljs-rx.core
  (:refer-clojure :exclude [map filter empty range concat count delay distinct first last every?
                            empty? group-by max min merge reduce])
  (:require [cljs.core :as core]
            [cljsjs.rxjs :as rxjs]
            [cljs-rx.internal.interrop :refer [fn-1 fn-2 fn-3 cljs-observer js-observer js-iterator]]))

(defn ->ish [ish]
  (if (or (sequential? ish)
          (set? ish))
    (js-iterator (seq ish))
    ish))

; === sources ===

(defn create [on-subscribe]
  (letfn [(wrapped-subs [observer]
            (on-subscribe (cljs-observer observer)))]
    ((.-create rxjs/Observable) wrapped-subs)))

(defn of [& xs]
  (apply (.-of rxjs/Observable) xs))

(defn defer [obs-factory-fn]
  ((.-defer rxjs/Observable) (fn-2 obs-factory-fn)))

(defn empty
  ([scheduler] ((.-empty rxjs/Observable) scheduler))
  ([] (empty js/undefined)))

(defn from
  ([ish scheduler]
   ((.-from rxjs/Observable) (->ish ish) scheduler))
  ([ish]
   ((.-from rxjs/Observable) (->ish ish))))

(defn from-event
  ([event-target-like event-name opts selector]
   ((.-fromEvent rxjs/Observable) event-target-like event-name (some-> opts (clj->js)) selector))
  ([event-target-like event-name opts]
   ((.-fromEvent rxjs/Observable) event-target-like event-name (some-> opts (clj->js))))
  ([event-target-like event-name]
   ((.-fromEvent rxjs/Observable) event-target-like event-name)))

(defn from-event-pattern
  ([add-handler remove-handler selector]
   ((.-fromEventPattern rxjs/Observable) (fn-1 add-handler) (fn-3 remove-handler) selector))
  ([add-handler remove-handler]
   ((.-fromEventPattern rxjs/Observable) (fn-1 add-handler) (fn-3 remove-handler)))
  ([add-handler]
   ((.-fromEventPattern rxjs/Observable) (fn-1 add-handler))))

(defn from-promise
  ([js-promise scheduler]
   ((.-fromPromise rxjs/Observable) js-promise scheduler))
  ([js-promise]
   ((.-fromPromise rxjs/Observable) js-promise)))

(defn interval
  ([period scheduler]
   ((.-interval rxjs/Observable) period scheduler))
  ([period]
   ((.-interval rxjs/Observable) period)))

(defn never []
  ((.-never rxjs/Observable)))

(defn range
  ([start count scheduler]
   ((.-range rxjs/Observable) start count scheduler))
  ([start count]
   ((.-range rxjs/Observable) start count))
  ([start]
   ((.-range rxjs/Observable) start))
  ([]
   ((.-range rxjs/Observable))))

(defn throw*
  ([error scheduler]
   ((.-throw rxjs/Observable) error scheduler))
  ([error]
   ((.-throw rxjs/Observable) error)))

(defn timer
  ([initial-delay period scheduler]
   ((.-timer rxjs/Observable) initial-delay period scheduler))
  ([initial-delay period]
   ((.-timer rxjs/Observable) initial-delay period))
  ([initial-delay]
   ((.-timer rxjs/Observable) initial-delay)))

(defn zip [& observables]
  (apply (.-zip rxjs/Observable) observables))


; === operators ===

(defn audit [obs duration-selector]
  (.audit obs duration-selector))

(defn audit-time
  ([obs duration scheduler]
   (.auditTime obs duration scheduler))
  ([obs duration]
   (.auditTime obs duration)))

(defn buffer [obs closing-notifier]
  (.buffer obs closing-notifier))

(defn buffer-count
  ([obs buffer-size start-buffer-every]
   (.bufferCount obs buffer-size start-buffer-every))
  ([obs buffer-size]
   (.bufferCount obs buffer-size)))

(defn buffer-time
  ([obs buffer-time-span buffer-creation-interval max-buffer-size scheduler]
   (.bufferTime obs buffer-time-span buffer-creation-interval max-buffer-size scheduler))
  ([obs buffer-time-span buffer-creation-interval max-buffer-size]
   (.bufferTime obs buffer-time-span buffer-creation-interval max-buffer-size))
  ([obs buffer-time-span buffer-creation-interval]
   (.bufferTime obs buffer-time-span buffer-creation-interval))
  ([obs buffer-time-span]
   (.bufferTime obs buffer-time-span)))

(defn buffer-toggle [obs openings closing-selector]
  (.bufferToggle obs openings (fn-1 closing-selector)))

(defn buffer-when [obs closing-selector]
  (.bufferWhen obs (fn-1 closing-selector)))

(defn catch* [obs selector]
  (.catch obs (fn-1 selector)))

(defn combine-all [obs]
  (.combineAll obs))

(defn combine-latest [& observables]
  (if (core/empty? observables)
    (of [])
    ((apply (.-combineLatest rxjs/operators) (rest observables)) (core/first observables))))

(defn concat [& observables]
  (if (core/empty? observables)
    (empty)
    ((apply (.-concat rxjs/operators) (rest observables)) (core/first observables))))

(defn concat-all [obs]
  (.concatAll obs))

(defn concat-map
  ([obs project result-selector]
   (.concatMap obs (fn-1 project) (fn-1 result-selector)))
  ([obs project]
   (.concatMap obs (fn-1 project))))

(defn concat-map-to
  ([obs inner-observable result-selector]
   (.concatMapTo obs inner-observable (fn-1 result-selector)))
  ([obs inner-observable]
   (.concatMapTo obs inner-observable)))

(defn count
  ([obs predicate]
   (.count obs (fn-1 predicate)))
  ([obs]
   (.count obs)))

(defn debounce [obs duration-selector]
  (.debounce obs (fn-1 duration-selector)))

(defn debounce-time
  ([obs due-time scheduler]
   (.debounceTime obs due-time scheduler))
  ([obs due-time]
   (.debounceTime obs due-time)))

(defn default-if-empty
  ([obs default-value]
   (.defaultIfEmpty obs default-value))
  ([obs]
   (.defaultIfEmpty obs)))

(defn delay
  ([obs time-or-date scheduler]
   (.delay obs time-or-date scheduler))
  ([obs time-or-date]
   (.delay obs time-or-date)))

(defn delay-when
  ([obs duration-selector subscription-delay]
   (.delayWhen obs (fn-1 duration-selector) subscription-delay))
  ([obs duration-selector]
   (.delayWhen obs (fn-1 duration-selector))))

(defn dematerialize [obs]
  (.dematerialize obs))

(defn distinct
  ([obs key-selector flushes]
   (.distinct obs (fn-1 key-selector) flushes))
  ([obs key-selector]
   (.distinct obs (fn-1 key-selector)))
  ([obs]
   (.distinct obs)))

(defn distinct-until-changed
  ([obs comparator]
   (.distinctUntilChanged obs (fn-2 comparator)))
  ([obs]
   (.distinctUntilChanged obs #(= %1 %2))))

(defn do* [obs observer]
  (.do obs (js-observer observer)))

(defn element-at
  ([obs index default-value]
   (.elementAt obs index default-value))
  ([obs index]
   (.elementAt obs index js/undefined)))

(defn every? [obs predicate]
  (.every obs (fn-1 predicate)))

(defn exhaust [obs]
  (.exhaust obs))

(defn exhaust-map
  ([obs project result-selector]
   (.exhaustMap obs (fn-1 project) (fn-1 result-selector)))
  ([obs project]
   (.exhaustMap obs (fn-1 project))))

(defn expand
  ([obs project concurrent scheduler]
   (.expand obs (fn-1 project) concurrent scheduler))
  ([obs project concurrent]
   (.expand obs (fn-1 project) concurrent))
  ([obs project]
   (.expand obs (fn-1 project))))

(defn filter [obs predicate]
  (.filter obs (fn-1 predicate)))

(defn find [obs predicate]
  (.find obs (fn-1 predicate)))

(defn find-index [obs predicate]
  (.findIndex obs (fn-1 predicate)))

(defn first
  ([obs predicate result-selector default-value]
   (.first obs (fn-1 predicate) (fn-1 result-selector) default-value))
  ([obs predicate result-selector]
   (.first obs (fn-1 predicate) (fn-1 result-selector)))
  ([obs predicate]
   (.first obs (fn-1 predicate)))
  ([obs]
   (.first obs)))

(defn for-each [obs on-next]
  (.forEach obs (fn-1 on-next)))

(defn group-by
  ([obs key-selector result-selector duration-selector]
   (.groupBy obs (fn-1 key-selector) (fn-1 result-selector) (fn-1 duration-selector)))
  ([obs key-selector result-selector]
   (.groupBy obs (fn-1 key-selector) (fn-1 result-selector)))
  ([obs key-selector]
   (.groupBy obs (fn-1 key-selector))))

(defn ignore-elements [obs]
  (.ignoreElements obs))

(defn empty? [obs]
  (.isEmpty obs))

(defn last
  ([obs predicate]
   (.last obs (fn-1 predicate)))
  ([obs]
   (.last obs)))

(defn map [obs f]
  (.map obs (fn-1 f)))

(defn map-to [obs value]
  (.mapTo obs value))

(defn materialize [obs]
  (.materialize obs))

(defn max
  ([obs comparer]
   (.max obs (fn-3 comparer)))
  ([obs]
   (.max obs)))

(defn merge [& observables]
  (if (core/empty? observables)
    (empty)
    ((apply (.-merge rxjs/operators) (core/rest observables)) (core/first observables))))

(defn merge-all
  ([obs concurrent]
   (.mergeAll obs concurrent))
  ([obs]
   (.mergeAll obs)))

(defn merge-map
  ([obs project result-selector concurrent]
   (.mergeMap obs (fn-1 project) (fn-1 result-selector) concurrent))
  ([obs project result-selector]
   (.mergeMap obs (fn-1 project)))
  ([obs project]
   (.mergeMap obs (fn-1 project))))

(defn merge-map-to
  ([obs inner-observable result-selector concurrent]
   (.mergeMapTo obs inner-observable (fn-1 result-selector) concurrent))
  ([obs inner-observable result-selector]
   (.mergeMapTo obs inner-observable (fn-1 result-selector)))
  ([obs inner-observable]
   (.mergeMapTo obs inner-observable)))

(defn merge-scan
  ([obs accumulator seed concurrent]
   (.mergeScan obs (fn-3 accumulator) seed concurrent))
  ([obs accumulator seed]
   (.mergeScan obs (fn-3 accumulator) seed)))

(defn min
  ([obs comparer]
   (.min obs (fn-3 comparer)))
  ([obs]
   (.min obs)))

(defn multicast
  ([obs subject-or-subject-factory selector]
   (.multicast subject-or-subject-factory (fn-1 selector)))
  ([obs subject-or-subject-factory]
   (.multicast subject-or-subject-factory)))

(defn observe-on [obs scheduler delay]
  (.observeOn obs scheduler delay))

(defn pairwise [obs]
  (.pairwise obs))

(defn partition [obs predicate]
  (.partition obs (fn-1 predicate)))

(defn pluck [obs & props]
  (((apply (.-pluck rxjs/operators) props) obs)))

(defn publish [obs]
  (.publish obs))

(defn publish-behaviour [obs value]
  (.publishBehavior obs value))

(defn publish-last [obs]
  (.publishLast obs))

(defn publish-replay
  ([obs buffer-size window-size scheduler]
   (.publishReplay obs buffer-size window-size scheduler))
  ([obs buffer-size window-size]
   (.publishReplay obs buffer-size window-size))
  ([obs buffer-size]
   (.publishReplay obs buffer-size))
  ([obs]
   (.publishReplay obs)))

(defn race [& observables]
  (if (core/empty? observables)
    (empty)
    (((apply (.-race rxjs/operators) (core/rest observables)) (core/first observables)))))

(defn reduce
  ([obs accumulator seed]
   (.reduce obs (fn-3 accumulator) seed))
  ([obs accumulator]
   (.reduce obs (fn-3 accumulator))))

(defn repeat [obs count]
  (.repeat obs count))

(defn repeat-when [obs notifier]
  (.repeatWhen obs (fn-1 notifier)))

(defn subscribe [obs observer]
  (.subscribe obs (js-observer observer)))

