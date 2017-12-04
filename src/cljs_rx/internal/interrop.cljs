(ns cljs-rx.internal.interrop
  (:require [cljsjs.rxjs :as rxjs]))

(def ^:private iterator-sym
  (.-iterator rxjs/Symbol))

(deftype Iterator [^:mutable xs]
  Object
  (next [_]
    (if-not (empty? xs)
      (let [x (first xs)]
        (set! xs (rest xs))
        #js {:value x :done false})
      #js {:value nil :done true})))

(defn js-iterator [xs]
  (let [ish (clj->js {})]
    (unchecked-set ish iterator-sym (fn [& _] (Iterator. xs)))
    ish))

(defn- preserve-arity? [f]
  (true? (:all-args (meta f))))

(defn cljs-observer [js-observer]
  {:next     (fn next [x] (.next js-observer x))
   :error    (fn error [e] (.error js-observer e))
   :complete (fn complete [] (.complete js-observer))})

(defn js-observer [{:keys [next error complete]}]
  (->> {:next next :error error :complete complete}
       (remove (comp nil? second))
       (into {})
       (clj->js)))

(defn fn-n [f]
  {:pre [(fn? f)]}
  (if-not (goog/isFunction f)
    (fn [& args] (apply f args))
    f))

(defn fn-0 [f]
  {:pre [(fn? f)]}
  (if (preserve-arity? f)
    (fn-n f)
    (fn [] (f))))

(defn fn-1 [f]
  {:pre [(fn? f)]}
  (if (preserve-arity? f)
    (fn-n f)
    (fn [a1] (f a1))))

(defn fn-2 [f]
  {:pre [(fn? f)]}
  (if (preserve-arity? f)
    (fn-n f)
    (fn [a1 a2] (f a1 a2))))
