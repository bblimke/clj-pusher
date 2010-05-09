(ns pusher
 (:require
   [com.twinql.clojure.http :as http]
   [pusher.auth :as auth])
 (:use
   [clojure.contrib.json.write :only [json-str]])
 (:import
   (java.net URI URLEncoder)
   (org.apache.http.entity StringEntity)))

(defonce *pusher-app-id* nil)
(defonce *pusher-key* nil)
(defonce *pusher-secret* nil)
(defonce *pusher-channel* nil)

(defonce pusher-api-host "http://api.pusherapp.com")

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
    (http/post (new URI (uri (request :path)))
      :body (StringEntity. (request :body))
      :as :string
      :query ((auth/authenticated-request *pusher-key* *pusher-secret* request) :query)
      :headers {"Content-Type" "application/json"})))




