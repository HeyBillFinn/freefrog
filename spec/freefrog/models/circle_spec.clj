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

(ns freefrog.models.circle-spec
  (:require [speclj.core :refer :all]
            [freefrog.models.circle :as c]
            [freefrog.governance :as gov]
            [freefrog.models.schema :as schema])
  (:import [javax.persistence EntityNotFoundException]))

(schema/actualize)

(describe "Circle persistence"
  (tags :circle-persistence)
  (it "stores a circle"
    (should-not-throw (c/new-circle {:name "Test Circle"})))

  (context "with a created circle"
    (with circle-id (c/new-circle {:name "Test Circle"}))
    (it "returns a valid ID"
      (should-contain #"\d+" (str @circle-id)))
    
    (context "having retrieved the created circle"
      (with circle (c/get-circle @circle-id))
      (it "retreives the persisted circle"
        (should= "Test Circle" (:name @circle))
        (should= @circle-id (:id @circle))))))
      
(run-specs)

