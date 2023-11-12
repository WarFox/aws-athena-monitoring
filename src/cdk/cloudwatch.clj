(ns cdk.cloudwatch
  (:import
   [software.amazon.awscdk.services.cloudwatch Metric$Builder Dashboard$Builder GraphWidget$Builder]))

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
