(ns cljs-rx.subject
  (:require [cljsjs.rxjs :as rxjs]))

; === classes ===

(def Subject rxjs/Subject)

(def AsyncSubject rxjs/AsyncSubject)

(def ReplaySubject rxjs/ReplaySubject)

(def BehaviorSubject rxjs/BehaviorSubject)

; === factories ===

(defn subject []
  (Subject.))

(defn behaviour-subject [value]
  (BehaviorSubject. value))

(defn replay-subject [buffer-size]
  (ReplaySubject. buffer-size))

(defn async-subject []
  (AsyncSubject.))

; === operators ===

(defn next! [subject value]
  {:pre [(instance? Subject subject)]}
  (.next subject value))

(defn error! [subject err]
  {:pre [(instance? Subject subject)]}
  (.error subject err))

(defn complete! [subject]
  {:pre [(instance? Subject subject)]}
  (.complete subject))

(defn ->observable [subject]
  {:pre [(instance? Subject subject)]}
  (.asObservable subject))

(defn stopped? [subject]
  {:pre [(instance? Subject subject)]}
  (true? (.-isStopped subject)))

(defn value [behaviour-subject]
  {:pre [(instance? BehaviorSubject behaviour-subject)]}
  (.getValue behaviour-subject))
