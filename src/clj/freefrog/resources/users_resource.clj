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

(defn put-user [user-id user-data]
  (p/put-user user-id user-data))

(defresource specific-users-resource [user-id]
  util/base-resource
  :allowed-methods [:put :get]
  :known-content-type? #(util/check-content-type % ["application/json"])
  :exists? (fn [_] (get-user user-id))
  :new? #(nil? (::user %))
  :put! #(put-user user-id (::user %))
  :handle-ok #(json/generate-string (::user %))
  :available-media-types ["application/json"])

(defresource general-users-resource []
  util/base-resource
  :known-content-type? #(util/check-content-type % ["application/json"])
  :allowed-methods [:post]
  :post! #(new-user %)
  :location #(util/build-entry-url (:request %) (::new-user-id %)))
