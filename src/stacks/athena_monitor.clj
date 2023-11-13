(ns stacks.athena-monitor
  (:import
   [software.amazon.awscdk Stack]
   [software.amazon.awscdk.services.cloudwatch GraphWidgetView])
  (:require
   [cdk.cloudwatch :as cw]
   [integrant.core :as ig]))

(defn config
  "Integrant config for athena-monitor"
  [stack]
  {::execution-time-graph {}
   ::avg-processed-bytes {}
   ::executiontime-vs-processed-bytes {}
   ::dashboard {:stack stack
                :widgets [(ig/ref ::execution-time-graph)
                          (ig/ref ::avg-processed-bytes)
                          (ig/ref ::executiontime-vs-processed-bytes)]}})

(defmethod ig/init-key ::execution-time-graph
  [_ _]
  (cw/graph-widget
   {:title "ExecutionTime for Successful vs Failed"
    :left [(cw/metric {:namespace  "AWS/Athena"
                       :metric-name "TotalExecutionTime"
                       :dimensions {"WorkGroup"  "primary"
                                    "QueryType"  "DML"
                                    "QueryState" "SUCCEEDED"}})

           (cw/metric {:namespace  "AWS/Athena"
                       :metric-name "TotalExecutionTime"
                       :dimensions {"WorkGroup"  "primary"
                                    "QueryType"  "DML"
                                    "QueryState" "FAILED"}})]

    :view GraphWidgetView/TIME_SERIES}))

(defmethod ig/init-key ::avg-processed-bytes
  [_ _]
  (cw/graph-widget
   {:title "Average ProcessedBytes by workgroup"
    :left [(cw/math-expression
            {:expression "SELECT AVG(ProcessedBytes) FROM SCHEMA(\"AWS/Athena\", QueryState,QueryType,WorkGroup) GROUP BY WorkGroup, QueryState"
             :label "Average ProcessedBytes"})]

    :view GraphWidgetView/TIME_SERIES}))

(defmethod ig/init-key ::executiontime-vs-processed-bytes
  [_ _]
  (cw/graph-widget
   {:title "ExecutionTime and ProcessedBytes for Successful vs Failed"
    :left [(cw/metric {:namespace  "AWS/Athena"
                       :metric-name "TotalExecutionTime"
                       :dimensions {"WorkGroup"  "primary"
                                    "QueryType"  "DML"
                                    "QueryState" "SUCCEEDED"}})

           (cw/metric {:namespace  "AWS/Athena"
                       :metric-name "TotalExecutionTime"
                       :dimensions {"WorkGroup"  "primary"
                                    "QueryType"  "DML"
                                    "QueryState" "FAILED"}})]

    :right [(cw/metric {:namespace  "AWS/Athena"
                        :metric-name "ProcessedBytes"
                        :dimensions {"WorkGroup"  "primary"
                                     "QueryType"  "DML"
                                     "QueryState" "SUCCEEDED"}})

            (cw/metric {:namespace  "AWS/Athena"
                        :metric-name "ProcessedBytes"
                        :dimensions {"WorkGroup"  "primary"
                                     "QueryType"  "DML"
                                     "QueryState" "FAILED"}})]

    :view GraphWidgetView/TIME_SERIES}))

(defmethod ig/init-key ::dashboard
  [_ {:keys [stack widgets]}]
  (cw/dashboard stack "dashboard"
                {:dashboard-name "athena-dashboard"
                 :widgets  widgets}))

(defn stack
  [parent]
  (let [stack (Stack. parent "athena-monitor")]

    (ig/init (config stack))
      ;; return stack
    stack))
