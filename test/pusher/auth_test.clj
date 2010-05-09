(ns pusher.auth-test
  (:use [pusher.auth] :reload-all)
  (:use [clojure.test])
  (:use org.rathore.amit.conjure.core))


(def request {:method "POST" 
              :path "/some/path" 
              :query {"name" "my_event"} 
              :body "{\"some\":\"data\"}"})


(deftest test-authenticated-request
  (stubbing [current-time-millis (long 1324324324000)]
  (is (= 
    ((authenticated-request "key" "secret" request) :query)
    
    {:auth_signature "4f52920dc59426560a907925e81f1adf8b6ae7226b85b74d2edc3343690f0762"
     :auth_version "1.0"
     :auth_timestamp (long 1324324324)
     :auth_key "key"
     :body_md5 "7b3d404f5cde4a0b9b8fb4789a0098cb" 
     "name" "my_event"}))))
