(ns pusher
 (:require
   [clj-http.client :as http]
   [pusher.auth :as auth])
 (:use
   [clojure.data.json :only [json-str]]))

(def ^{:dynamic true} *pusher-app-id* nil)
(def ^{:dynamic true} *pusher-key* nil)
(def ^{:dynamic true} *pusher-secret* nil)
(def ^{:dynamic true} *pusher-channel* nil)

(def pusher-api-host "http://api.pusherapp.com")

(defmacro with-pusher-auth [[app-id key secret] & body]
  `(binding [*pusher-app-id* ~app-id *pusher-key* ~key *pusher-secret* ~secret]
     ~@body))

(defmacro with-pusher-channel [channel & body]
  `(binding [*pusher-channel* ~channel]
     ~@body))

(defn- channel-events-path []
  (str "/apps/" *pusher-app-id* "/channels/" *pusher-channel* "/events"))

(defn- uri [path]
  (str pusher-api-host path))

(defstruct request :method :path :query :body)

(defn trigger [event data]
  (let [request (struct request "POST" (channel-events-path) {:name event} (json-str data))]
    (http/post (uri (request :path))
               {:body (request :body)
                :query-params (:query (auth/authenticated-request *pusher-key* *pusher-secret* request))
                :headers {"Content-Type" "application/json"}})))
