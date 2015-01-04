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

(ns freefrog.models.schema
  (:use [lobos.core :only (defcommand migrate)])
  (:require [lobos.connectivity :refer [open-global]]
            [db.config :refer [db-config]]
            [lobos.migration :as lm]))

(defcommand pending-migrations []
  (binding [lobos.migration/*src-directory* "src/clj/"]
    (lm/pending-migrations (:db db-config) sname)))

(defn actualized?
  "checks if there aren't pending migrations"
  []
  (empty? (pending-migrations)))

(defn actualize []
  (when-not (actualized?)
    (do 
      (binding [lobos.migration/*src-directory* "src/clj/"] (migrate))
      (lobos.core/migrate))))

