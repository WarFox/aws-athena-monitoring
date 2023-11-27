(ns constructs.athena-dashboard
  (:import
   [software.amazon.awscdk.services.cloudwatch GraphWidgetView])
  (:require
   [cdk.cloudwatch :as cw]
   [integrant.core :as ig]
   [clojure.string :as str]))

(defn execution-time-graph
  [{:keys [query-type]}]
  (cw/graph-widget
   {:title (format "ExecutionTime for %s queries" query-type)
    :left  [(cw/metric {:namespace   "AWS/Athena"
                        :metric-name "TotalExecutionTime"
                        :dimensions  {"WorkGroup"  "primary"
                                      "QueryType"  query-type
                                      "QueryState" "SUCCEEDED"}})

            (cw/metric {:namespace   "AWS/Athena"
                        :metric-name "TotalExecutionTime"
                        :dimensions  {"WorkGroup"  "primary"
                                      "QueryType"  query-type
                                      "QueryState" "FAILED"}})]
    :view GraphWidgetView/TIME_SERIES}))

(defn processed-bytes-time-graph
  [{:keys [query-type]}]
  (cw/graph-widget
   {:title (format "ProcessedBytes for %s queries" query-type)
    :left  [(cw/metric {:namespace   "AWS/Athena"
                        :metric-name "ProcessedBytes"
                        :dimensions  {"WorkGroup"  "primary"
                                      "QueryType"  query-type
                                      "QueryState" "SUCCEEDED"}})

            (cw/metric {:namespace   "AWS/Athena"
                        :metric-name "ProcessedBytes"
                        :dimensions  {"WorkGroup"  "primary"
                                      "QueryType"  query-type
                                      "QueryState" "FAILED"}})]
    :view  GraphWidgetView/TIME_SERIES}))

(defn executiontime-vs-processed-bytes
  [{:keys [query-type]}]
  (cw/graph-widget
   {:title "ExecutionTime and ProcessedBytes for DML queries"
    :left  [(cw/metric {:namespace   "AWS/Athena"
                        :metric-name "TotalExecutionTime"
                        :dimensions  {"WorkGroup"  "primary"
                                      "QueryType"  query-type
                                      "QueryState" "SUCCEEDED"}})

            (cw/metric {:namespace   "AWS/Athena"
                        :metric-name "TotalExecutionTime"
                        :dimensions  {"WorkGroup"  "primary"
                                      "QueryType"  query-type
                                      "QueryState" "FAILED"}})]

    :right [(cw/metric {:namespace   "AWS/Athena"
                        :metric-name "ProcessedBytes"
                        :dimensions  {"WorkGroup"  "primary"
                                      "QueryType"  query-type
                                      "QueryState" "SUCCEEDED"}})

            (cw/metric {:namespace   "AWS/Athena"
                        :metric-name "ProcessedBytes"
                        :dimensions  {"WorkGroup"  "primary"
                                      "QueryType"  query-type
                                      "QueryState" "FAILED"}})]

    :view GraphWidgetView/TIME_SERIES}))

(defn usage-graph
  []
  (cw/graph-widget
   {:title "API Usage"
    :left  [(cw/metric {:namespace   "AWS/Usage"
                        :metric-name "CallCount"
                        :dimensions  {"Type"     "API"
                                      "Resource" "GetQueryResults"
                                      "Service"  "Athena"
                                      "Class"    "None"}})

            (cw/metric {:namespace   "AWS/Usage"
                        :metric-name "ResourceCount"
                        :dimensions  {"Type"     "Resource"
                                      "Resource" "ActiveQueryCount"
                                      "Service"  "Athena"
                                      "Class"    "DML"}})

            (cw/metric {:namespace   "AWS/Usage"
                        :metric-name "ResourceCount"
                        :dimensions  {"Type"     "Resource"
                                      "Resource" "ActiveQueryCount"
                                      "Service"  "Athena"
                                      "Class"    "DDL"}})]

    :view GraphWidgetView/TIME_SERIES}))

(defn average-processed-bytes
  []
  (cw/graph-widget
   {:title "Average ProcessedBytes by workgroup"
    :left [(cw/math-expression {:expression (str/join " " ["SELECT AVG(ProcessedBytes)"
                                                           "FROM SCHEMA(\"AWS/Athena\",QueryState,QueryType,WorkGroup)"
                                                           "GROUP BY WorkGroup, QueryState"])
                                :label "Average ProcessedBytes"})]
    :view GraphWidgetView/TIME_SERIES}))

(defmethod ig/init-key ::dashboard
  [_ {:keys [stack widgets]}]
  (cw/dashboard stack "dashboard"
                {:dashboard-name "athena-dashboard"
                 :widgets        widgets}))

(defn config
  "Integrant config for athena-monitor"
  [stack]
  {::dashboard {:stack   stack
                :widgets [(processed-bytes-time-graph {:query-type "DML"})
                          (processed-bytes-time-graph {:query-type "DDL"})
                          (execution-time-graph {:query-type "DML"})
                          (execution-time-graph {:query-type "DDL"})
                          (usage-graph)
                          (average-processed-bytes)
                          (executiontime-vs-processed-bytes {:query-type "DML"})
                          (executiontime-vs-processed-bytes {:query-type "DDL"})]}})

(defn construct
  [stack]
  (println "Athena Dashboard")
  (ig/init (config stack)))
