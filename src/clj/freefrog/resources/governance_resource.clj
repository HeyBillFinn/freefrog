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

(ns freefrog.resources.governance_resource
  (:require [freefrog.resources.resource_util :as util]
            [liberator.representation :refer [ring-response]]
            [liberator.core :refer [resource defresource]]
            [clj-json.core :as json]
            [freefrog.governance-logs :as gl]
            [freefrog.persistence :as p])
  (:import [org.apache.http HttpStatus]))

(defn new-governance-log [circle-id]
  {::new-governance-log-id (p/new-governance-log 
                             circle-id 
                             (gl/create-governance-log))})

(defn get-governance-log [circle-id log-id]
  [true {::governance-log (p/get-governance-log circle-id log-id)}])

(defn put-governance-log [circle-id gov-id context]
  (let [gov-log (p/get-governance-log circle-id gov-id)]
    (when (:is-open? gov-log)
      (p/put-governance-log circle-id gov-id 
                            (assoc gov-log
                                   :is-open? false)))))

(defn get-governance-logs [circle-id]
  [true {::governance-logs (p/get-all-governance-logs circle-id)}])

(defn put-governance-log-agenda [circle-id gov-id context]
  (let [gov-log (p/get-governance-log circle-id gov-id)]
    (if (:is-open? gov-log)
      (p/put-governance-log circle-id gov-id 
                            (assoc gov-log
                                   :agenda (:body context)))
      {:failed "Agenda is closed."})))

(defresource governance-agenda-resource [circle-id log-id]
  util/base-resource 
  :allowed-methods [:get :put]
  :exists? (fn [_] (get-governance-log circle-id log-id))
  :new? #(nil? (:agenda (::governance-log %)))
  :put! #(put-governance-log-agenda circle-id log-id %)
  :handle-ok #(if (:is-open? (::governance-log %))
                (ring-response {:status HttpStatus/SC_OK 
                                :headers {"Content-Type" "text/plain"}
                                :body (str (:agenda (::governance-log %)))})
                (ring-response {:status HttpStatus/SC_BAD_REQUEST 
                                :body "Agenda is closed."}))
  :location #(util/build-entry-url (:request %)))

(defresource specific-governance-resource [circle-id log-id]
  util/base-resource 
  :allowed-methods [:put :get]
  :known-content-type? #(util/check-content-type % ["text/plain"])
  :exists? (fn [_] (get-governance-log circle-id log-id))
  :new? #(nil? (::governance-log %))
  :put! #(put-governance-log circle-id log-id %)
  :handle-ok #(if (:is-open? (::governance-log %))
                (ring-response {:status HttpStatus/SC_OK 
                                :headers {"Open-Meeting" "true"}})
                (json/generate-string (::governance-log %))))

(defresource general-governance-resource [circle-id]
  util/base-resource 
  :allowed-methods [:get :post]
  :known-content-type? #(util/check-content-type % ["text/plain"])
  :post! (fn [_] (new-governance-log circle-id))
  :exists? (fn [_] (get-governance-logs circle-id))
  :handle-ok #(json/generate-string (::governance-logs %))
  :location #(util/build-entry-url (:request %) (::new-governance-log-id %)))
