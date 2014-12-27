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

(ns freefrog.users-rest-spec
  (:require [speclj.core :refer :all]
            [clj-json.core :as json]
            [freefrog.rest-spec-helpers :as helpers]
            [freefrog.users :as u]
            [freefrog.persistence :as p])
  (:import [javax.persistence EntityNotFoundException]
           [org.apache.http HttpStatus]))

(def sample-user {:username "bfinn"
                  :password "abcd1234"})

(defn throw-illegal-arg [& args]
  (throw (IllegalArgumentException. "Illegal arguments")))

(defn throw-user-not-found [& args]
  (throw (EntityNotFoundException. "User does not exist.")))

(describe "users rest api"
  (before-all (helpers/start-test-server))
  (after-all (helpers/stop-test-server))

  (context "getting the users endpoint"
    (with response (helpers/http-request :get "/users"))

    (helpers/it-responds-with-status HttpStatus/SC_METHOD_NOT_ALLOWED @response))

  (context "posting to the users endpoint"
    (context "with plain/text content"
      (with response (helpers/http-request :post "/users"
                                           {:content-type "text/plain"
                                            :body "New User!"}))

      (helpers/it-responds-with-status HttpStatus/SC_UNSUPPORTED_MEDIA_TYPE 
                                       @response))

    (context "with badly formed JSON"
      (with response (helpers/http-request :post "/users"
                                           {:content-type "application/json"
                                            :body "{\"username:\"}"}))

      (helpers/it-responds-with-status HttpStatus/SC_BAD_REQUEST @response)
      (helpers/it-responds-with-body-containing "Unexpected character" @response))

    (context "with missing parameters"
      (around [it]
        (with-redefs [u/create-user throw-illegal-arg]
          (it)))
      (with response (helpers/http-request :post "/users"
                                           {:content-type "application/json"
                                            :body (json/generate-string sample-user)}))

      (helpers/it-responds-with-status HttpStatus/SC_BAD_REQUEST @response)
      (helpers/it-responds-with-body-containing "Illegal arguments" @response))

    (context "with a username and password"
      (around [it]
        (with-redefs [p/new-user (fn [& args] 1234)]
          (it)))
      (with response (helpers/http-request :post "/users"
                                           {:content-type "application/json"
                                            :body (json/generate-string sample-user)}))

      (helpers/it-responds-with-status HttpStatus/SC_CREATED @response)
      (it "returns the location of the newly created resource"
        (should= (str helpers/host-url "/users/1234") 
                 (helpers/get-location @response)))))

  (context "with a non-existent user"
    (around [it]
      (with-redefs [p/get-user throw-user-not-found]
        (it)))
    (context "getting the user"
      (with response (helpers/http-request :get "/users/1234"))

      (helpers/it-responds-with-status HttpStatus/SC_NOT_FOUND @response)
      (helpers/it-responds-with-body "User does not exist." @response))

    (context "putting the user"
      (with response (helpers/http-request :put "/users/1234"
                                           {:content-type "application/json"
                                            :body (json/generate-string 
                                                    sample-user)}))

      (helpers/it-responds-with-status HttpStatus/SC_NOT_FOUND @response)
      (helpers/it-responds-with-body "User does not exist." @response)))

  (context "with an existing user"
    (around [it]
      (with-redefs [p/get-user (fn [& args] {:username "bfinn"})]
        (it)))
    (context "getting the user"
      (with response (helpers/http-request :get "/users/1234"))

      (helpers/it-responds-with-status HttpStatus/SC_OK @response)
      (helpers/it-responds-with-content-type "application/json" @response)
      (helpers/it-responds-with-body-containing "\"username\":\"bfinn\"" @response))

    (context "putting the user"
      (context "with plain/text content"
        (with response (helpers/http-request :put "/users/1234"
                                             {:content-type "text/plain"
                                              :body "New User!"}))

        (helpers/it-responds-with-status HttpStatus/SC_UNSUPPORTED_MEDIA_TYPE 
                                         @response))
      (context "with badly formed JSON"
        (with response (helpers/http-request :put "/users/1234"
                                             {:content-type "application/json"
                                              :body "{\"username:\"}"}))

        (helpers/it-responds-with-status HttpStatus/SC_BAD_REQUEST @response)
        (helpers/it-responds-with-body-containing "Unexpected character" @response))

      (context "with a username and password"
        (with response (helpers/http-request :put "/users/1234"
                                           {:content-type "application/json"
                                            :body (json/generate-string 
                                                    sample-user)}))

        ; waiting on https://github.com/slagyr/speclj/pull/114
        (xit "should call p/put-user")
        (helpers/it-responds-with-status HttpStatus/SC_NO_CONTENT @response)))))

(run-specs)
