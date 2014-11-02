(ns freefrog.rest-spec
  (:require [speclj.core :refer :all]
            [clj-http.client :as http-client]
            [freefrog.rest :as r]))

(describe "governance rest api"
  (before-all (r/start-test-server))
  (after-all (r/stop-test-server))

  (it "should return status code 404 at the root"
    (should= 404 (:status (http-client/get "http://localhost:3000" {:throw-exceptions false}))))

  (context "when no circles have been created"
    (with response (http-client/get "http://localhost:3000/circle" {:throw-exceptions false}))
    (it "should return an empty array"
        (should= 200 (:status @response))
        (should= "[]" (:body @response)))))