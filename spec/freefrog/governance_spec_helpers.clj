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

(ns freefrog.governance-spec-helpers
  (:require [freefrog.governance :as g]
            [speclj.core :refer :all]))

(def sample-anchor (g/anchor-circle "Sample"))
(def role-name "Programmer")

(def sample-purpose "Building awesome software")
(def sample-anchor-with-role (g/add-role sample-anchor role-name sample-purpose))

(defn merge-params [fixed-params variable-params]
  (if variable-params
    (merge fixed-params variable-params)
    fixed-params))

(defn should-not-update-missing-or-empty-roles [fn type-str & params]
  (println type-str params)
  (describe (format "%s problems" type-str)
    (it "doesn't work with a role that doesn't exist"
      (should-throw IllegalArgumentException (str "Role not found: "
                                                  role-name)
        (apply fn (merge-params [sample-anchor role-name] params))))

    (it "doesn't work with an empty role name"
      (should-throw IllegalArgumentException "Name may not be empty"
        (apply fn (merge-params [sample-anchor-with-role nil] params)))
      (should-throw IllegalArgumentException "Name may not be empty"
        (apply fn (merge-params [sample-anchor-with-role ""] params))))))
