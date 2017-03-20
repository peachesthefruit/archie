(ns archie.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.json :as json]
            [clj-http.client :as client]
            [compojure.route :as route]))

(def auth_token
  "xoxp-17641790740-18419529792-151170615858-ff4431cac73e0a09b781db8fcd6fc429"
  )

(defn get_channel_info [channelID]
  (get-in (client/get "https://slack.com/api/channels.info" {:as :json, :query-params {:token auth_token, :channel channelID}}) [:body :channel :name])
  )

(defn get_channel_info [channelID]
  (get-in (client/get "https://slack.com/api/users.info" {:as :json, :query-params {:token auth_token, :channel channelID}}) [:body :channel :name])
  )

(defn handle_challenge [body]
  (get body :challenge)
  )

(defn handle_event [body]
  (println (get_channel_info (get-in body [:event :channel])))
  (println (get_user_info (get-in body [:event :user])))
  (println (get-in body [:event :text]))
  )

(defroutes app-routes
  (GET "/" [] "Hello World")
  (POST "/slack" req
        (let [body (get req :body)]
          (println body)
          (case (get body :type)
            "url_verification" (handle_challenge body)
            "event_callback" (handle_event body)
            )
          ))
  (route/not-found "Route Not Found"))

(def app
  (->
    (handler/site app-routes)
    (json/wrap-json-body {:keywords? true})
    json/wrap-json-response
    ))
