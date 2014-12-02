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

(ns freefrog.rest
  (:require [liberator.dev]
            [freefrog.resources.governance_resource :as gr]
            [compojure.route :as route]
            [compojure.core :refer [defroutes ANY GET]]))

(defroutes app
  (ANY "/circles/:circle-id/governance" [circle-id] 
       (gr/general-governance-resource circle-id))
  (ANY "/circles/:circle-id/governance/:log-id" [circle-id log-id] 
       (gr/specific-governance-resource circle-id log-id))
  (ANY "/circles/:circle-id/governance/:log-id/agenda" [circle-id log-id]
       (gr/governance-agenda-resource circle-id log-id))
  ;;TODO
  ;(ANY "/circles/:circle-id/governance/:log-id/current" [circle-id log-id]
       ;(governance-current-resource circle-id log-id))
  (route/not-found "<h1>:-(</hi>"))

(def handler 
  (-> app 
    (liberator.dev/wrap-trace :header :ui)))

