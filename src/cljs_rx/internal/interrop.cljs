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
  {:next     (fn next [x] ((.-next js-observer) x))
   :error    (fn error [e] ((.-error js-observer) e))
   :complete (fn complete [] ((.-complete js-observer)))})

(defn js-observer [{:keys [next error complete]}]
  (->> {:next next :error error :complete complete}
       (remove (comp nil? second))
       (into {})
       (clj->js)))

(defn ari-0 [f]
  {:pre [(or (fn? f) (undefined? f))]}
  (if-not (undefined? f)
    (if (preserve-arity? f)
      (fn [& args] (apply f args))
      (fn [] (f)))
    f))

(defn ari-1 [f]
  {:pre [(or (fn? f) (undefined? f))]}
  (if-not (undefined? f)
    (if (preserve-arity? f)
      (fn [& args] (apply f args))
      (fn [a1] (f a1)))
    f))

(defn ari-2 [f]
  {:pre [(or (fn? f) (undefined? f))]}
  (if-not (undefined? f)
    (if (preserve-arity? f)
      (fn [& args] (apply f args))
      (fn [a1 a2] (f a1 a2)))
    f))
