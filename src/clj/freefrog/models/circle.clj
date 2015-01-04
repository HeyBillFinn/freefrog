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

(ns freefrog.models.circle
  (:require [env.config :refer [env-config]]
            [clojure.java.io :refer :all]
            [korma.core :refer :all]))

(defentity circles
  (transform (fn [{scope_identity (keyword "scope_identity()") :as v}]
               (if scope_identity
                 (assoc v :id scope_identity)
                 v)))
      ;; H2 doesn't play nicely with JDBC's result set. Instead of returning
      ;; the generated ID in a map of the form {:<column-name> <generated-id>}, 
      ;; it returns {:scope_identity() <generated-id>}
  (database (:korma-db env-config)))

(defn new-circle
  "Creates a new circle. Returns the unique ID of this newly created circle."
  [circle]
  (:id (insert circles (values circle))))

(defn get-circle
  "Return the specified circle. Throws EntityNotFoundException if the
  specified circle does not exist."
  [circle-id]
  (first (select circles
          (where {:id circle-id}))))

(defn put-circle
  "Store the governance log for the specified circle and ID. Throws
  EntityNotFoundException if the specified log or circle does not exist."
  [circle-id circle])

