(ns cljs-rx.ajax
  (:refer-clojure :exclude [get])
  (:require [cljs.core :as core]
            [cljs-rx.core :as rx]
            [cljsjs.rxjs :as rxjs]))

(def ^:private ajax (.-ajax rxjs/Observable))

(def ^:private opts-key-conversions
  {:url                 "url"
   :body                "body"
   :user                "user"
   :async?              "async"
   :method              "method"
   :headers             "headers"
   :timeout             "timeout"
   :password            "password"
   :has-content?        "hasContent"
   :cross-domain?       "crossDomain"
   :with-credentials?   "withCredentials"
   :create-xhr          "createXHR"
   :progress-subscriber "progressSubscriber"
   :response-type       "responseType"})

(defn- ->ajax-opts [opts]
  (->> (select-keys opts (keys opts-key-conversions))
       (map (fn [[k v]] [(core/get opts-key-conversions k) v]))
       (into {})
       (clj->js)))

(defn- ->clj-response [ajax-response]
  {:status        (.-status ajax-response)
   :response      (js->clj (.-response ajax-response))
   :response-text (.-responseText ajax-response)
   :response-type (.-responseType ajax-response)})


; === operators ===

(defn request [request-opts]
  (-> (ajax (->ajax-opts request-opts))
      (rx/map ->clj-response)))

(defn get
  ([url headers]
   (rx/map ((.-get ajax) url (clj->js headers)) ->clj-response))
  ([url]
   (get url {})))

(defn post
  ([url body headers]
   (rx/map ((.-post ajax) url (clj->js body) (clj->js headers)) ->clj-response))
  ([url body]
   (post url body {})))

(defn put
  ([url body headers]
   (rx/map ((.-put ajax) url (clj->js body) (clj->js headers)) ->clj-response))
  ([url body]
   (put url body {})))

(defn patch
  ([url body headers]
   (rx/map ((.-patch ajax) url (clj->js body) (clj->js headers)) ->clj-response))
  ([url body]
   (patch url body {})))

(defn delete
  ([url headers]
   (request {:url url :headers headers :method "DELETE"}))
  ([url]
   (delete url {})))
