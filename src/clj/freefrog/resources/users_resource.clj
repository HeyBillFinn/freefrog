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

(ns freefrog.resources.users_resource
  (:require [freefrog.resources.resource_util :as util]
            [liberator.representation :refer [ring-response]]
            [liberator.core :refer [resource defresource]]
            [clj-json.core :as json]
            [freefrog.users :as u]
            [freefrog.persistence :as p])
  (:import [org.apache.http HttpStatus]))

(defn new-user [ctx]
  {::new-user-id (p/new-user (u/create-user (:parsed-json-body ctx)))})

(defn get-user [user-id]
  [true {::user (p/get-user user-id)}])

(defresource specific-users-resource [user-id]
  util/base-resource
  :exists? (fn [_] (get-user user-id))
  )
  ;:allowed-methods [:put :get]
;  :exists? (fn [_] (get-governance-log circle-id log-id))
;  :new? #(nil? (::governance-log %))
;  :put! #(put-governance-log circle-id log-id %)
;  :handle-ok #(if (:is-open? (::governance-log %))
;                (ring-response {:status HttpStatus/SC_OK 
;                                :headers {"Open-Meeting" "true"}})
;                (json/generate-string (::governance-log %))))

(defresource general-users-resource []
  util/base-resource
  :known-content-type? #(util/check-content-type % ["application/json"])
  :allowed-methods [:post]
  :post! #(new-user %)
  :location #(util/build-entry-url (:request %) (::new-user-id %))
  )
  ;:post! (fn [_] (new-governance-log circle-id))
  ;:exists? (fn [_] (get-governance-logs circle-id))
  ;:handle-ok #(json/generate-string (::governance-logs %))
  ;:location #(util/build-entry-url (:request %) (::new-governance-log-id %)))
