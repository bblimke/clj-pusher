(ns pusher.auth
 (:require
   [uk.co.holygoat.util.md5 :as md5])
 (:use
   [clojure.string :only [join]])
 (:import
   (javax.crypto Mac)
   (javax.crypto.spec SecretKeySpec)
   (java.math BigInteger)))

(defn ^{:dynamic true} *current-time-millis* []
  (long (System/currentTimeMillis)))

(defn- current-timestamp []
  (let [millis (*current-time-millis*)
        now (long (/ millis 1000))]
    now))

(defn- add-auth-params [key request]
  (assoc request
    :query (merge
             (request :query)
             {:auth_key key
              :auth_timestamp (current-timestamp)
              :auth_version "1.0"})))

(defn- ljust [xs x y] (apply str (concat (replicate (- x (.length #^String xs)) y) xs)))

(defn- byte-array-to-str [bytes]
  (let [big-integer (BigInteger. 1 bytes)
        hash (.toString big-integer 16)]
    (ljust hash 32 0)))

(defn- hmac
  "Calculate HMAC signature for given data."
  [#^String key #^String data]
  (let [hmac-sha256 "HmacSHA256"
        signing-key (SecretKeySpec. (.getBytes key) hmac-sha256)
        mac (doto (Mac/getInstance hmac-sha256) (.init signing-key))]
    (byte-array-to-str (.doFinal mac (.getBytes data)))))


(defn- parameter-string [params]
  (join "&"
        (map (fn [[key val]] (str (name key) "=" (str val)))
             (sort-by #(name (key %)) java.lang.String/CASE_INSENSITIVE_ORDER params))))

(defn- signature-string [request]
  (join "\n" [(request :method) (request :path) (parameter-string (request :query))]))

(defn- generate-signature [secret request]
  (hmac secret (signature-string request)))

(defn- add-signature [secret request]
  (assoc-in request [:query :auth_signature] (generate-signature secret request)))

(defn- add-body-md5-param [request]
  (assoc-in request [:query :body_md5] (md5/md5-sum (request :body))))

(defn authenticated-request [key secret request]
  (add-signature secret (add-auth-params key (add-body-md5-param request))))
