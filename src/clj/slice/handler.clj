(ns slice.handler
  (:require [slice.dev :refer [browser-repl start-figwheel]]
            [hiccup.page :as hiccup]
            [clojure.edn :as edn]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [selmer.parser :refer [render-file]]
            [environ.core :refer [env]]
            [prone.middleware :refer [wrap-exceptions]]))

(defroutes routes
  (GET "/" [] (render-file "templates/index.html" {:dev (env :dev?)}))

  (GET "/state" [] (slurp "work/state.edn"))
  (POST "/state" {:keys [body params]}
        (let [new-state (-> body slurp edn/read-string)]
          (spit "work/document.html"
                (hiccup/html5
                 [:head
                  (hiccup/include-css "external.css")
                  (hiccup/include-css "document.css")
                  [:meta {:charset "UTF-8"}]]
                 [:body
                  (:html new-state)]))
          (spit "work/document.css" (:css new-state))
          (spit "work/state.edn" (dissoc new-state :html :css))
          "saved"))

  (resources "/")
  (not-found "Not Found"))

(def app
  (let [handler (wrap-defaults routes api-defaults)]
    (if (env :dev?) (wrap-exceptions handler) handler)))
