(ns uk.co.holygoat.util.md5
  (:refer-clojure)
  (:import
     (java.security 
       NoSuchAlgorithmException
       MessageDigest)
     (java.math BigInteger)))

;; Supposedly efficient padding routine.
(defmacro pad [pad-to pad-char #^String s]
  `(let [ss# ~s
         len# (int (.length ss#))
         diff# (int (- len# ~pad-to))
         #^String padstring# ~(apply str (repeat pad-to pad-char))]
     
     (if (< diff# 0)
       ;; Needs padding.
       (.concat (.substring padstring# (+ ~pad-to diff#))
                 ss#)
       ;; Either 0 (happy) or not (oh dear). In the failure case there's
       ;; nothing we can do...
       ss#)))


;; Might want to replace this with Apache Commons Codec DigestUtil:
;; http://commons.apache.org/codec/api-release/org/apache/commons/codec/digest/DigestUtils.html
;; String md5 = DigestUtils.md5Hex(str);
(defn md5-sum
  "Compute the hex MD5 sum of a string."
  [#^String str]
  (let [alg (doto (MessageDigest/getInstance "MD5")
              (.reset)
              (.update (.getBytes str)))]
    (try
      (pad 32 \0
        (.toString (new BigInteger 1 (.digest alg)) 16))
      (catch NoSuchAlgorithmException e
        (throw (new RuntimeException e))))))
