(ns cljs-rx.core
  (:refer-clojure :exclude [map filter empty range concat count delay distinct first last every?
                            empty? group-by max min merge reduce find partition repeat take take-last
                            take-while to-array])
  (:require [cljs.core :as core]
            [cljsjs.rxjs :as rxjs]
            [cljs-rx.internal.interrop :refer [fn-0 fn-1 fn-2 fn-2 fn-n
                                               cljs-observer
                                               js-observer
                                               js-iterator]]))

(def ^:private Observable rxjs/Observable)

(def ^:private operators rxjs/operators)

(defn- -create [on-subscribe]
  (letfn [(wrapped-subs [observer]
            (on-subscribe (cljs-observer observer)))]
    ((.-create Observable) wrapped-subs)))

(defn- coll->ish [coll]
  (js-iterator (seq coll)))

(defn- watchable->ish [watchable]
  (-> (fn [{:keys [next]}]
        (letfn [(on-val [_ _ old new]
                  (when (not= old new)
                    (next new)))]
          (next @watchable)
          (add-watch watchable next on-val)
          (fn unwatch []
            (remove-watch watchable next))))
      (-create)))

(defn- ->ish [ish]
  (cond
    (coll? ish) (coll->ish ish)
    (satisfies? IWatchable ish) (watchable->ish ish)
    :else ish))

(defn- apply-with-project [op obs others]
  (let [project (core/last others)
        args    (if (fn? project)
                  (conj (vec (core/drop-last 1 others)) (fn-n project))
                  others)]
    ((apply op args) obs)))

; === factories ===

(defn create [on-subscribe]
  (-create on-subscribe))

(defn of [& xs]
  (apply (.-of Observable) xs))

(defn defer [obs-factory-fn]
  ((.-defer Observable) (fn-0 obs-factory-fn)))

(defn empty
  ([scheduler] ((.-empty Observable) scheduler))
  ([] (empty js/undefined)))

(defn from
  ([ish scheduler]
   ((.-from Observable) (->ish ish) scheduler))
  ([ish]
   ((.-from Observable) (->ish ish))))

(defn from-event
  ([event-target-like event-name opts selector]
   ((.-fromEvent Observable) event-target-like event-name (some-> opts (clj->js)) selector))
  ([event-target-like event-name opts]
   ((.-fromEvent Observable) event-target-like event-name (some-> opts (clj->js))))
  ([event-target-like event-name]
   ((.-fromEvent Observable) event-target-like event-name)))

(defn from-event-pattern
  ([add-handler remove-handler selector]
   ((.-fromEventPattern Observable) (fn-1 add-handler) (fn-2 remove-handler) selector))
  ([add-handler remove-handler]
   ((.-fromEventPattern Observable) (fn-1 add-handler) (fn-2 remove-handler)))
  ([add-handler]
   ((.-fromEventPattern Observable) (fn-1 add-handler))))

(defn from-promise
  ([js-promise scheduler]
   ((.-fromPromise Observable) js-promise scheduler))
  ([js-promise]
   ((.-fromPromise Observable) js-promise)))

(defn interval
  ([period scheduler]
   ((.-interval Observable) period scheduler))
  ([period]
   ((.-interval Observable) period)))

(defn never []
  ((.-never Observable)))

(defn range
  ([start count scheduler]
   ((.-range Observable) start count scheduler))
  ([start count]
   ((.-range Observable) start count))
  ([start]
   ((.-range Observable) start))
  ([]
   ((.-range Observable))))

(defn throw-error
  ([err scheduler]
   (.subscribeOn (-create (fn [{:keys [error complete]}] (error err))) scheduler))
  ([err]
   (-create (fn [{:keys [error]}] (error err)))))

(defn timer
  ([initial-delay period scheduler]
   ((.-timer Observable) initial-delay period scheduler))
  ([initial-delay period]
   ((.-timer Observable) initial-delay period))
  ([initial-delay]
   ((.-timer Observable) initial-delay)))

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
  (.bufferWhen obs (fn-0 closing-selector)))

(defn catch-error [obs selector]
  (((.-catchError operators) (fn-1 selector)) obs))

(defn combine-all
  ([obs project]
   (.combineAll obs (fn-n project)))
  ([obs]
   (.combineAll obs)))

(defn combine-latest [& observables]
  (if (core/empty? observables)
    (of [])
    (apply-with-project (.-combineLatest operators) (core/first observables) (rest observables))))

(defn concat [& observables]
  (if (core/empty? observables)
    (empty)
    ((apply (.-concat operators) (rest observables)) (core/first observables))))

(defn concat-all [obs]
  (.concatAll obs))

(defn concat-map
  ([obs project result-selector]
   (.concatMap obs (fn-1 project) (fn-2 result-selector)))
  ([obs project]
   (.concatMap obs (fn-1 project))))

(defn concat-map-to
  ([obs inner-observable result-selector]
   (.concatMapTo obs inner-observable (fn-2 result-selector)))
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

(defn element-at
  ([obs index default-value]
   (.elementAt obs index default-value))
  ([obs index]
   (.elementAt obs index)))

(defn every? [obs predicate]
  (.every obs (fn-1 predicate)))

(defn exhaust [obs]
  (.exhaust obs))

(defn exhaust-map
  ([obs project result-selector]
   (.exhaustMap obs (fn-1 project) (fn-2 result-selector)))
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
   (.first obs (fn-1 predicate) (fn-2 result-selector) default-value))
  ([obs predicate result-selector]
   (.first obs (fn-1 predicate) (fn-2 result-selector)))
  ([obs predicate]
   (.first obs (fn-1 predicate)))
  ([obs]
   (.first obs)))

(defn for-each! [obs on-next]
  (.forEach obs (fn-1 on-next)))

(defn group-by
  ([obs key-selector result-selector duration-selector]
   (.groupBy obs (fn-1 key-selector) (fn-2 result-selector) (fn-1 duration-selector)))
  ([obs key-selector result-selector]
   (.groupBy obs (fn-1 key-selector) (fn-2 result-selector)))
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
   (.max obs (fn-2 comparer)))
  ([obs]
   (.max obs)))

(defn merge [& observables]
  (if (core/empty? observables)
    (empty)
    ((apply (.-merge operators) (core/rest observables)) (core/first observables))))

(defn merge-all
  ([obs concurrent]
   (.mergeAll obs concurrent))
  ([obs]
   (.mergeAll obs)))

(defn merge-map
  ([obs project result-selector concurrent]
   (.mergeMap obs (fn-1 project) (fn-2 result-selector) concurrent))
  ([obs project result-selector]
   (.mergeMap obs (fn-1 project)))
  ([obs project]
   (.mergeMap obs (fn-1 project))))

(defn merge-map-to
  ([obs inner-observable result-selector concurrent]
   (.mergeMapTo obs inner-observable (fn-2 result-selector) concurrent))
  ([obs inner-observable result-selector]
   (.mergeMapTo obs inner-observable (fn-2 result-selector)))
  ([obs inner-observable]
   (.mergeMapTo obs inner-observable)))

(defn merge-scan
  ([obs accumulator seed concurrent]
   (.mergeScan obs (fn-2 accumulator) seed concurrent))
  ([obs accumulator seed]
   (.mergeScan obs (fn-2 accumulator) seed)))

(defn min
  ([obs comparer]
   (.min obs (fn-2 comparer)))
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
  (((apply (.-pluck operators) props) obs)))

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
    (((apply (.-race operators) (core/rest observables)) (core/first observables)))))

(defn reduce
  ([obs accumulator seed]
   (.reduce obs (fn-2 accumulator) seed))
  ([obs accumulator]
   (.reduce obs (fn-2 accumulator))))

(defn repeat [obs count]
  (.repeat obs count))

(defn repeat-when [obs notifier]
  (.repeatWhen obs (fn-1 notifier)))

(defn retry [obs count]
  (.retry obs count))

(defn retry-when [obs notifier]
  (.retryWhen obs (fn-1 notifier)))

(defn sample [obs notifier]
  (.sample obs notifier))

(defn sample-time
  ([obs period scheduler]
   (.sampleTime obs period scheduler))
  ([obs period]
   (.sampleTime obs period)))

(defn scan
  ([obs accumulator seed]
   (.scan obs (fn-2 accumulator) seed))
  ([obs accumulator]
   (.scan obs (fn-2 accumulator))))

(defn sequence-equal
  ([obs compare-to comparator]
   (.sequenceEqual obs compare-to (fn-2 comparator)))
  ([obs compare-to]
   (.sequenceEqual obs compare-to)))

(defn share [obs]
  (.share obs))

(defn single [obs predicate]
  (.single obs (fn-1 predicate)))

(defn skip [obs count]
  (.skip obs count))

(defn skip-until [obs notifier]
  (.skipUntil obs notifier))

(defn skip-while [obs predicate]
  (.skipWhile obs (fn-1 predicate)))

(defn start-with [obs & values]
  ((apply (.-startWith operators) values) obs))

(defn subscribe! [obs observer]
  (.subscribe obs (js-observer observer)))

(defn subscribe-on [obs scheduler]
  (.subscribeOn obs scheduler))

(defn switch [obs]
  ((.-switchAll operators) obs))

(defn switch-map
  ([obs project result-selector]
   (.switchMap obs (fn-1 project) (fn-2 result-selector)))
  ([obs project]
   (.switchMap obs (fn-1 project))))

(defn switch-map-to
  ([obs inner-observable result-selector]
   (.switchMapTo obs inner-observable (fn-2 result-selector)))
  ([obs inner-observable]
   (.switchMapTo obs inner-observable)))

(defn take [obs count]
  (.take obs count))

(defn take-last [obs count]
  (.takeLast obs count))

(defn take-until [obs notifier]
  (.takeUntil obs notifier))

(defn take-while [obs predicate]
  (.takeWhile obs (fn-1 predicate)))

(defn tap [obs observer-or-on-next]
  (let [observer (if (fn? observer-or-on-next)
                   (fn-1 observer-or-on-next)
                   (js-observer observer-or-on-next))]
    (((.-tap operators) observer) obs)))

(defn throttle [obs duration-selector]
  (.throttle obs (fn-1 duration-selector)))

(defn throttle-time
  ([obs duration scheduler]
   (.throttleTime obs duration scheduler))
  ([obs duration]
   (.throttleTime obs duration)))

(defn time-interval
  ([obs scheduler]
   (.timeInterval obs scheduler))
  ([obs]
   (.timeInterval obs)))

(defn timeout
  ([obs due scheduler]
   (.timeout obs due scheduler))
  ([obs due]
   (.timeout obs due)))

(defn timeout-with
  ([obs due with-observable scheduler]
   (.timeoutWith obs due with-observable scheduler))
  ([obs due with-observable]
   (.timeoutWith obs due with-observable)))

(defn timestamp
  ([obs scheduler]
   (.timestamp obs scheduler))
  ([obs]
   (.timestamp obs)))

(defn to-array! [obs]
  (.toArray obs))

(defn to-promise!
  ([obs promise-ctor]
   (.toPromise obs promise-ctor))
  ([obs]
   (.toPromise obs)))

(defn unsubscribe! [subscription]
  (.unsubscribe subscription))

(defn window [obs window-boundaries]
  (.window obs window-boundaries))

(defn window-count
  ([obs window-size start-window-every]
   (.windowCount obs window-size start-window-every))
  ([obs window-size]
   (.windowCount obs window-size)))

(defn window-toggle [obs openings closing-selector]
  (.windowToggle openings (fn-1 closing-selector)))

(defn window-when [obs closing-selector]
  (.windowWhen obs (fn-0 closing-selector)))

(defn with-latest-from [obs & others]
  (apply-with-project (.-withLatestFrom operators) obs others))

(defn zip [& observables]
  (apply (.-zipStatic operators) observables))

(defn zip-all
  ([obs project]
   (.zipAll obs (fn-n project)))
  ([obs]
   (.zipAll obs)))
