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

(ns freefrog.resources.resource_util
  (:require [clj-json.core :as json]
            [clojure.java.io :as io]
            [liberator.representation :refer [ring-response]])
  (:import (java.net URL)
           (org.apache.http HttpStatus)))

(defn put-or-post? [ctx]
  (#{:put :post} (get-in ctx [:request :request-method])))

(defn check-content-type [ctx content-types]
  (if (put-or-post? ctx)
    (or (some #{(get-in ctx [:request :headers "content-type"])} content-types)
        [false {:message "Unsupported Content-Type"}])
    true))

(defn body-as-string [ctx]
  (if-let [body (get-in ctx [:request :body])]
    (condp instance? body
      java.lang.String body
      (slurp (io/reader body)))))

(defn malformed-json? [ctx]
  (when (and (put-or-post? ctx) 
             (= "application/json" (get-in ctx [:request :content-type])))
    (try
      (if-let [body (body-as-string ctx)]
        (let [data (json/parse-string body)]
          [false {:parsed-json-body data}])
        [true {:message "No body"}])
      (catch Exception e
        [true {:message (format "IOException: %s" (.getMessage e))}]))))

(defn build-entry-url
  ([request]
   (URL. (format "%s://%s:%s%s"
                 (name (:scheme request))
                 (:server-name request)
                 (:server-port request)
                 (:uri request))))
  ([request id]
   (URL. (format "%s/%s" (build-entry-url request) (str id)))))

(defn validate-context [ctx]
  (when (:failed ctx)
    (ring-response {:status HttpStatus/SC_BAD_REQUEST :body (:failed ctx)})))

(defn handle-exception [ctx]
  (let [exception (:exception ctx)]
    (condp = (type exception)
      javax.persistence.EntityNotFoundException (ring-response 
                                                  {:status HttpStatus/SC_NOT_FOUND 
                                                   :body (.getMessage exception)})
      java.lang.IllegalArgumentException (ring-response 
                                           {:status HttpStatus/SC_BAD_REQUEST 
                                            :body (.getMessage exception)})
      (ring-response {:status HttpStatus/SC_INTERNAL_SERVER_ERROR 
                      :body (.getMessage exception)}))))

(def base-resource
  {:known-content-type? #(check-content-type % ["text/plain"])
   :handle-created validate-context
   :malformed? malformed-json?
   :handle-no-content validate-context
   :handle-exception handle-exception})
