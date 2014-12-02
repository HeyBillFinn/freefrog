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
  (:require [liberator.representation :refer [ring-response]])
  (:import java.net.URL))

(defn put-or-post? [ctx]
  (#{:put :post} (get-in ctx [:request :request-method])))

(defn check-content-type [ctx content-types]
  (if (put-or-post? ctx)
    (or (some #{(get-in ctx [:request :headers "content-type"])} content-types)
        [false {:message "Unsupported Content-Type"}])
    true))

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
    (ring-response {:status 400 :body (:failed ctx)})))

(defn handle-exception [ctx]
  (let [exception (:exception ctx)]
    (when (= (type exception)
             javax.persistence.EntityNotFoundException)
      (ring-response {:status 404 :body (.getMessage exception)}))))
