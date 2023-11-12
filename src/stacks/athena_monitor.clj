(ns stacks.athena-monitor
  (:import
   [java.util Collections]
   [software.amazon.awscdk Stack]
   [software.amazon.awscdk.services.cloudwatch GraphWidgetView Dashboard$Builder])
  (:require
   [cdk.cloudwatch :as cloudwatch]
   [integrant.core :as ig]))

(defn config
  "Integrant config for athena-monitor"
  [stack]
  {::execution-time-graph {}
   ::dashboard {:stack stack
                :widgets [(ig/ref ::execution-time-graph)]}})

(defmethod ig/init-key ::execution-time-graph
  [_ _]
  (cloudwatch/graph-widget
   {:title "ExecutionTime for Successful vs Failed"
    :left [(cloudwatch/metric {:namespace  "AWS/Athena"
                               :metricName "TotalExecutionTime"
                               :dimensions {"WorkGroup"  "primary"
                                            "QueryType"  "DML"
                                            "QueryState" "SUCCEEDED"}})

           (cloudwatch/metric {:namespace  "AWS/Athena"
                               :metricName "TotalExecutionTime"
                               :dimensions {"WorkGroup"  "primary"
                                            "QueryType"  "DML"
                                            "QueryState" "FAILED"}})]

    :view GraphWidgetView/TIME_SERIES}))

(defmethod ig/init-key ::dashboard
  [_ {:keys [stack widgets]}]
  (-> (Dashboard$Builder/create stack "dashboard")
      (.dashboardName "athena-dashboard")
      (.widgets (Collections/singletonList widgets))
      (.build)))

(defn stack
  [parent]
  (let [stack (Stack. parent "athena-monitor")]

    (ig/init (config stack))
      ;; return stack
    stack))
