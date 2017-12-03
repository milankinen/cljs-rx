(ns cljs-rx.core
  (:refer-clojure :exclude [map filter empty range concat count delay distinct
                            first last every? empty? group-by max min merge find])
  (:require [cljs.core :as core]
            [cljsjs.rxjs :as rxjs]
            [cljs-rx.internal.interrop :refer [ari-1 ari-0 ari-2 cljs-observer js-observer js-iterator]]))

; === sources ===

(defn create [on-subscribe]
  (letfn [(wrapped-subs [observer]
            (on-subscribe (cljs-observer observer)))]
    ((.-create rxjs/Observable) wrapped-subs)))

(defn of [& xs]
  (apply (.-of rxjs/Observable) xs))

(defn defer [obs-factory-fn]
  ((.-defer rxjs/Observable) (ari-0 obs-factory-fn)))

(defn empty
  ([scheduler] ((.-empty rxjs/Observable) scheduler))
  ([] (empty js/undefined)))

(defn from
  ([ish scheduler]
   (let [ish' (if (or (sequential? ish)
                      (set? ish))
                (js-iterator ish)
                ish)]
     ((.-from rxjs/Observable) ish' scheduler)))
  ([ish]
   (from ish js/undefined)))

(defn from-event
  ([event-target-like event-name opts selector]
   (let [opts' (or (some-> opts (clj->js)) opts)]
     ((.-fromEvent rxjs/Observable) event-target-like event-name opts' selector)))
  ([event-target-like event-name opts]
   (from-event event-target-like event-name opts js/undefined))
  ([event-target-like event-name]
   (from-event event-target-like event-name js/undefined)))

(defn from-event-pattern
  ([add-handler remove-handler selector]
   ((.-fromEventPattern rxjs/Observable) (ari-1 add-handler) (ari-2 remove-handler) selector))
  ([add-handler remove-handler]
   (from-event-pattern add-handler remove-handler js/undefined))
  ([add-handler]
   (from-event-pattern add-handler js/undefined)))

(defn from-promise
  ([js-promise scheduler]
   ((.-fromPromise rxjs/Observable) js-promise scheduler))
  ([js-promise]
   (from-promise js-promise js/undefined)))

(defn interval
  ([period scheduler]
   ((.-interval rxjs/Observable) period scheduler))
  ([period]
   (interval period js/undefined)))

(defn never []
  ((.-never rxjs/Observable)))

(defn range
  ([start count scheduler] ((.-range rxjs/Observable) start count scheduler))
  ([start count] (range start count js/undefined))
  ([start] (range start js/undefined))
  ([] (range js/undefined)))

(defn throw*
  ([error scheduler] ((.-throw rxjs/Observable) error scheduler))
  ([error] ((.-throw rxjs/Observable) error)))

(defn timer
  ([initial-delay period scheduler]
   ((.-timer rxjs/Observable) initial-delay period scheduler))
  ([initial-delay period]
   (timer initial-delay period js/undefined))
  ([initial-delay]
   (timer initial-delay js/undefined)))

(defn zip [& observables]
  (apply (.-zip rxjs/Observable) observables))

; TODO: implement
; (defn web-socket [])

; === operators ===

(defn audit [obs duration-selector]
  (.audit obs duration-selector))

(defn audit-time
  ([obs duration scheduler]
   (.auditTime obs duration scheduler))
  ([obs duration]
   (audit-time obs duration js/undefined)))

(defn buffer [obs closing-notifier]
  (.buffer obs closing-notifier))

(defn buffer-count
  ([obs buffer-size start-buffer-every]
   (.bufferCount obs buffer-size start-buffer-every))
  ([obs buffer-size]
   (buffer-count obs buffer-size js/undefined)))

(defn buffer-time
  ([obs buffer-time-span buffer-creation-interval max-buffer-size scheduler]
   (.bufferTime obs buffer-time-span buffer-creation-interval max-buffer-size scheduler))
  ([obs buffer-time-span buffer-creation-interval max-buffer-size]
   (buffer-time obs buffer-time-span buffer-creation-interval max-buffer-size js/undefined))
  ([obs buffer-time-span buffer-creation-interval]
   (buffer-time obs buffer-time-span buffer-creation-interval js/undefined))
  ([obs buffer-time-span]
   (buffer-time obs buffer-time-span js/undefined)))

(defn buffer-toggle [obs openings closing-selector]
  (.bufferToggle obs openings (ari-1 closing-selector)))

(defn buffer-when [obs closing-selector]
  (.bufferWhen obs (ari-1 closing-selector)))

(defn catch* [obs selector]
  (.catch obs (ari-1 selector)))

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
   (.concatMap obs (ari-1 project) (ari-1 result-selector)))
  ([obs project]
   (concat-map obs project js/undefined)))

(defn concat-map-to
  ([obs inner-observable result-selector]
   (.concatMapTo obs inner-observable (ari-1 result-selector)))
  ([obs inner-observable]
   (concat-map-to obs inner-observable js/undefined)))

(defn count
  ([obs predicate] (.count obs (ari-1 predicate)))
  ([obs] (count obs js/undefined)))

(defn debounce [obs duration-selector]
  (.debounce obs (ari-1 duration-selector)))

(defn debounce-time
  ([obs due-time scheduler]
   (.debounceTime obs due-time scheduler))
  ([obs due-time]
   (debounce-time obs due-time js/undefined)))

(defn default-if-empty
  ([obs default-value] (.defaultIfEmpty obs default-value))
  ([obs] (.defaultIfEmpty obs)))

(defn delay
  ([obs time-or-date scheduler]
   (.delay obs time-or-date scheduler))
  ([obs time-or-date]
   (delay obs time-or-date js/undefined)))

(defn delay-when
  ([obs duration-selector subscription-delay]
   (.delayWhen obs (ari-1 duration-selector) subscription-delay))
  ([obs duration-selector]
   (delay-when obs duration-selector js/undefined)))

(defn dematerialize [obs]
  (.dematerialize obs))

(defn distinct
  ([obs key-selector flushes]
   (.distinct obs (ari-1 key-selector) flushes))
  ([obs key-selector]
   (distinct obs key-selector js/undefined))
  ([obs]
   (distinct obs js/undefined)))

(defn distinct-until-changed
  ([obs comparator]
   (.distinctUntilChanged obs (ari-2 comparator)))
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
  (.every obs (ari-1 predicate)))

(defn exhaust [obs]
  (.exhaust obs))

(defn exhaust-map
  ([obs project result-selector]
   (.exhaustMap obs (ari-1 project) (ari-1 result-selector)))
  ([obs project]
   (exhaust-map obs project js/undefined)))

(defn expand
  ([obs project concurrent scheduler]
   (.expand obs (ari-1 project) concurrent scheduler))
  ([obs project concurrent]
   (expand obs project concurrent js/undefined))
  ([obs project]
   (expand obs project js/undefined)))

(defn filter [obs predicate]
  (.filter obs (ari-1 predicate)))

(defn find [obs predicate]
  (.find obs (ari-1 predicate)))

(defn find-index [obs predicate]
  (.findIndex obs (ari-1 predicate)))

(defn first
  ([obs predicate result-selector default-value]
   (.first obs (ari-1 predicate) (ari-1 result-selector) default-value))
  ([obs predicate result-selector]
   (first obs predicate result-selector js/undefined))
  ([obs predicate]
   (first obs predicate js/undefined))
  ([obs]
   (.first obs)))

(defn for-each [obs on-next]
  (.forEach obs (ari-1 on-next)))

(defn group-by
  ([obs key-selector result-selector duration-selector]
   (.groupBy obs (ari-1 key-selector) (ari-1 result-selector) (ari-1 duration-selector)))
  ([obs key-selector result-selector]
   (group-by obs key-selector result-selector js/undefined))
  ([obs key-selector]
   (group-by obs key-selector)))

(defn ignore-elements [obs]
  (.ignoreElements obs))

(defn empty? [obs]
  (.isEmpty obs))

(defn last
  ([obs predicate]
   (.last obs (ari-1 predicate)))
  ([obs]
   (.last obs)))

(defn map [obs f]
  (.map obs (ari-1 f)))

(defn map-to [obs value]
  (.mapTo obs value))

(defn materialize [obs]
  (.materialize obs))

(defn max
  ([obs comparer]
   (.max obs (ari-2 comparer)))
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
   (.mergeMap obs (ari-1 project) (ari-1 result-selector) concurrent))
  ([obs project result-selector]
   (merge-map obs project result-selector js/undefined))
  ([obs project]
   (merge-map obs project js/undefined)))

(defn merge-map-to
  ([obs inner-observable result-selector concurrent]
   (.mergeMapTo obs inner-observable (ari-1 result-selector) concurrent))
  ([obs inner-observable result-selector]
   (merge-map-to obs inner-observable result-selector js/undefined))
  ([obs inner-observable]
   (merge-map-to obs inner-observable js/undefined)))

(defn merge-scan
  ([obs accumulator seed concurrent]
   (.mergeScan obs (ari-2 accumulator) seed concurrent))
  ([obs accumulator seed]
   (merge-scan obs accumulator seed js/undefined)))

(defn min
  ([obs comparer]
   (.min obs (ari-2 comparer)))
  ([obs]
   (.min obs)))

(defn multicast
  ([obs subject-or-subject-factory selector]
   (.multicast subject-or-subject-factory (ari-1 selector)))
  ([obs subject-or-subject-factory]
   (multicast obs subject-or-subject-factory js/undefined)))

(defn subscribe [obs {:keys [next error complete]
                      :or   {next     (constantly nil)
                             error    (constantly nil)
                             complete (constantly nil)}}]
  (.subscribe obs (ari-1 next) (ari-1 error) (ari-1 complete)))

