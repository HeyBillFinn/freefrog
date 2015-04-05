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

(ns freefrog.persistence
  (:import (freefrog DuplicateEntityException)))

(def anchor-circles (atom {}))

(defn reset-database []
  (reset! anchor-circles {}))

(defn new-governance-log 
  "Creates a new governance log for the circle using the given input log.
  Returns the unique ID of this newly created governance log."
  [])

(defn get-governance-log
  "Return the specified governance log for the circle. Throws
  EntityNotFoundException if the specified log or circle does not exist."
  [])

(defn put-governance-log
  "Store the governance log for the specified circle and ID. Throws
  EntityNotFoundException if the specified log or circle does not exist."
  [])

(defn get-all-governance-logs
  "Return a collection of all of the governance logs for the circle. Throws
  EntityNotFoundException if the circle does not exist."
  [_]
  [])

(defn new-anchor-circle
  "Creates a new anchor circle. Returns the unique ID of this newly created 
  anchor circle."
  [id circle principal]
  (when (not-empty (filter #(= id (:circle_id %)) (get @anchor-circles principal)))
    (throw (DuplicateEntityException. 
             (format "Anchor circle with ID %s already exists with user %s"
                     id principal))))
  (swap! anchor-circles update-in [principal] conj {:circle_id id 
                                                    :circle circle})
  id)

(defn get-anchor-circles-for-principal
  "Retrieves the anchor circles, filtered by the supplied principal."
  [principal]
  (get @anchor-circles principal))

