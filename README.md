#Pusher Client for Clojure

This library provides access to [Pusher API](http://www.pusherapp.com) from Clojure.

##Building

Make sure you have [Leiningen](http://github.com/technomancy/leiningen) installed.

    lein deps && lein jar

##Installation

Library can be installed as a dependency from [Clojars](http://clojars.org/clj-pusher)

##Example

    (:use 'pusher)

    (with-pusher-auth ["my-pusher-app-id" "my-pusher-key" "my-pusher-secret"]
      (with-pusher-channel "test_channel"
        (with-pusher-cluster "ap3"
          (trigger "my_event" {:data "helloworld"}))))

or with credentials set permanently

    (alter-var-root (var *pusher-app-id*)
      (constantly "my-pusher-app-id"))
    (alter-var-root (var *pusher-key*)
      (constantly "my-pusher-key"))
    (alter-var-root (var *pusher-secret*)
      (constantly "my-pusher-secret"))
    (alter-var-root (var *pusher-channel*)
      (constantly "test_channel"))
    (alter-var-root (var *pusher-cluster*)
      (constantly "ap3"))
      
    (trigger "my_event" {:data "helloworld"})

##Copyright

  Copyright (c) 2010 Bartosz Blimke. See LICENSE for details.
