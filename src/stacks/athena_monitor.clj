(ns stacks.athena-monitor
  (:import
   [software.amazon.awscdk Stack]
   [software.amazon.awscdk.services.cloudwatch Dashboard$Builder])
  (:require
   [integrant.core :as ig]))

(defn config
  "Integrant config for athena-monitor"
  [stack]
  {::dashboard {:stack stack}})

(defmethod ig/init-key ::dashboard
  [_ {:keys [stack]}]
  (-> (Dashboard$Builder/create stack "dashboard")
      (.dashboardName "athena-dashboard")
      (.build)))

(defn stack
  [parent]
  (let [stack (Stack. parent "athena-monitor")]

    (ig/init (config stack))
      ;; return stack
    stack))
