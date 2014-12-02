;
; Copyright © 2014 Courage Labs
;
; This file is part of Freefrog.
;
; Freefrog is free software: you can redistribute it and/or modify
; it under the terms of the GNU Affero General Public License as published by
; the Free Software Foundation, either version 3 of the License, or
; (at your option) any later version.
;
; Freefrog is distributed in the hope that it will be useful,
; but WITHOUT ANY WARRANTY; without even the implied warranty of
; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
; GNU Affero General Public License for more details.
;
; You should have received a copy of the GNU Affero General Public License
; along with this program.  If not, see <http://www.gnu.org/licenses/>.
;

(ns freefrog.rest-spec-helpers
  (:require [speclj.core :refer :all]
            [clj-http.client :as http-client]
            [freefrog.rest :as r])
  (:use [ring.adapter.jetty]))

(def test-server (ref nil))

(defn start-test-server []
  (when-not @test-server
    (dosync
      (ref-set test-server (run-jetty #'r/handler {:port 3000 :join? false}))))
  (.start @test-server))

(defn stop-test-server []
  (.stop @test-server))

(def host-url "http://localhost:3000")

(def http-request-fns
  {:get http-client/get 
   :put http-client/put 
   :post http-client/post})

(defn http-request 
  ([method uri]
   (http-request method uri nil))
  ([method uri options]
   (apply (get http-request-fns method) 
          [(str host-url uri) 
           (merge {:throw-exceptions false
                   :content-type "text/plain"
                   :body ""} options)])))

(defn get-location [response]
  (get (:headers response) "Location"))

(defmacro it-responds-with-status [expected-status response]
  `(it "should return the right response code"
    (should= ~expected-status (:status ~response))))

(defmacro it-responds-with-body [expected-body response]
  `(it "should contain the appropriate body"
    (should= ~expected-body (:body ~response))))

(defmacro it-responds-with-body-containing [expected-body response]
  `(it "should contain the appropriate body"
    (should-contain ~expected-body (:body ~response))))

