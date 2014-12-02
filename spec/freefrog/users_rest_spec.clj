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

(defn illegal-arg-thrower [& args]
  (throw (IllegalArgumentException. "Illegal arguments")))

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
        (with-redefs [u/create-user illegal-arg-thrower]
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
                 (helpers/get-location @response))))))
;
;  (context "with a circle"
;    (around [it]
;      (with-redefs [p/get-all-governance-logs (fn [id] sample-gov-log)
;                    p/new-governance-log (fn [& args] 5678)]
;        (it)))
;
;    (context "requesting the governance endpoint"
;      (with response (helpers/http-request :get "/circles/1234/governance"))
;
;      (helpers/it-responds-with-status HttpStatus/SC_OK @response)
;      (helpers/it-responds-with-body (json/generate-string sample-gov-log)
;                                        @response))
;
;    (context "posting to the governance endpoint with application/json"
;      (with response (helpers/http-request :post 
;                                   "/circles/1234/governance" 
;                                   {:content-type "application/json"}))
;      (it "should not respond to application/json"
;        (should= HttpStatus/SC_UNSUPPORTED_MEDIA_TYPE (:status @response))))
;
;    (context "posting to the governance endpoint"
;      (with response (helpers/http-request :post "/circles/1234/governance"))
;
;      (helpers/it-responds-with-status HttpStatus/SC_CREATED @response)
;      (it "should return the location of the newly created governance log"
;        (should= (str helpers/host-url "/circles/1234/governance/5678") 
;                 (helpers/get-location @response))))
;
;    (context "with a non-existent governance endpoint"
;      (around [it]
;        (with-redefs [p/get-governance-log govt-meeting-not-found-thrower]
;          (it)))
;
;      (context "putting to the agenda endpoint"
;        (with response (helpers/http-request :put 
;                                     "/circles/1234/governance/5678/agenda"
;                                     {:body "New agenda"}))
;        (helpers/it-responds-with-status HttpStatus/SC_NOT_FOUND @response)
;        (helpers/it-responds-with-body "Governance meeting does not exist" 
;                                          @response)))
;
;    (context "with an existing governance endpoint"
;      (context "with an empty open agenda"
;        (around [it]
;          (with-redefs [p/get-governance-log (fn [& args] {:is-open? true :agenda nil})]
;            (it)))
;
;        (context "putting to the agenda endpoint with an unsupported media type"
;          (with response (helpers/http-request :put
;                                       "/circles/1234/governance/5678/agenda"
;                                       {:body "New agenda"
;                                        :content-type "application/json"}))
;          (helpers/it-responds-with-status 415 @response))
;
;        (context "putting to the agenda endpoint"
;          (with response (helpers/http-request :put
;                                       "/circles/1234/governance/5678/agenda"
;                                       {:body "New agenda"}))
;
;          (helpers/it-responds-with-status HttpStatus/SC_CREATED @response)
;          (it "should return the location of the newly created governance log"
;            (should= (str helpers/host-url "/circles/1234/governance/5678/agenda") 
;                     (helpers/get-location @response))))
;
;        (context "getting the agenda endpoint"
;          (with response (helpers/http-request :get "/circles/1234/governance/5678/agenda"))
;          (helpers/it-responds-with-status HttpStatus/SC_OK @response)
;          (helpers/it-responds-with-body "" @response)
;          (it "should return an empty agenda"
;            (should-contain "text/plain" (get-in @response
;                                                 [:headers "Content-Type"])))))
;
;      (context "with an existing open agenda"
;        (around [it]
;          (with-redefs [p/get-governance-log (fn [& args] {:is-open? true :agenda "Current agenda"})]
;            (it)))
;
;        (context "getting the governance resource"
;          (with response (helpers/http-request :get "/circles/1234/governance/5678"))
;          (helpers/it-responds-with-status HttpStatus/SC_OK @response)
;          (it "should return that an open meeting exists"
;            (should-contain "true" (get-in @response
;                                           [:headers "Open-Meeting"]))))
;
;        (context "putting to the governance resource"
;          (with response (helpers/http-request :put "/circles/1234/governance/5678"))
;          ;(xit "should persist a closed governance log")
;          (helpers/it-responds-with-status HttpStatus/SC_NO_CONTENT @response))
;
;        (context "putting to the agenda endpoint"
;          (with response (helpers/http-request :put 
;                                       "/circles/1234/governance/5678/agenda"
;                                       {:body "New agenda"}))
;
;          (helpers/it-responds-with-status HttpStatus/SC_NO_CONTENT @response))
;
;        (context "getting the agenda endpoint"
;          (with response (helpers/http-request :get "/circles/1234/governance/5678/agenda"))
;          (helpers/it-responds-with-status HttpStatus/SC_OK @response)
;          (helpers/it-responds-with-body "Current agenda" @response)
;          (it "should return the contents of the existing, open agenda"
;            (should-contain "text/plain" (get-in @response
;                                                 [:headers "Content-Type"])))))
;
;      (context "with an existing closed agenda"
;        (around [it]
;          (with-redefs [p/get-governance-log (fn [& args] {:is-open? false 
;                                                           :agenda "Current closed agenda"})]
;            (it)))
;
;        (context "getting the governance resource"
;          (with response (helpers/http-request :get "/circles/1234/governance/5678"))
;          (helpers/it-responds-with-status HttpStatus/SC_OK @response)
;          (helpers/it-responds-with-body-containing "{\"agenda\":\"Current closed agenda\"" 
;                                            @response))
;
;        (context "putting to the governance resource"
;          (with response (helpers/http-request :put "/circles/1234/governance/5678"))
;          (helpers/it-responds-with-status HttpStatus/SC_NO_CONTENT @response))
;
;        (context "putting to the agenda endpoint"
;          (with response (helpers/http-request :put
;                                       "/circles/1234/governance/5678/agenda"
;                                       {:body "New agenda"}))
;
;          (helpers/it-responds-with-status HttpStatus/SC_BAD_REQUEST @response)
;          (helpers/it-responds-with-body "Agenda is closed." @response))
;
;        (context "getting the agenda endpoint"
;          (with response (helpers/http-request :get "/circles/1234/governance/5678/agenda"))
;          (helpers/it-responds-with-status HttpStatus/SC_BAD_REQUEST @response)
;          (helpers/it-responds-with-body "Agenda is closed." @response))))))

(run-specs)
