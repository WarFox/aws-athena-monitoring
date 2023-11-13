(ns cdk.cloudwatch
  (:import
   [java.util Collections]
   [software.amazon.awscdk.services.cloudwatch Metric$Builder MathExpression$Builder Dashboard$Builder GraphWidget$Builder]))

(defn metric
  [{:keys [namespace metricName metric-name dimensions]}]
  (-> (Metric$Builder/create)
      (.namespace namespace)
      (.metricName (or metric-name metricName))
      (.dimensionsMap dimensions)
      (.build)))

(defn graph-widget
  [{:keys [title left right view]}]
  (-> (GraphWidget$Builder/create)
      (.title title)
      (.left left)
      (.right right)
      (.view view)
      (.build)))

(defn math-expression
  [{:keys [expression label]}]
  (-> (MathExpression$Builder/create)
      (.expression expression)
      (.label label)
      (.build)))

(defn dashboard
  [stack id {:keys [dashboard-name widgets]}]
  (-> (Dashboard$Builder/create stack id)
      (.dashboardName dashboard-name)
      (.widgets (Collections/singletonList widgets))
      (.build)))
