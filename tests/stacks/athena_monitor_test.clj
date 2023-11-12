(ns stacks.athena-monitor-test
  (:import
   [software.amazon.awscdk App]
   [software.amazon.awscdk.assertions Template])
  (:require
   [clojure.test :as t]
   [stacks.athena-monitor :as athena-monitor]))

(t/deftest stack-test
  (t/testing "Athena monitoring stack has 1 CloudWatch dashboard"

    (let [stack    (athena-monitor/stack (App.))
          template (Template/fromStack stack)]

      (t/is (nil?
             (.resourceCountIs template "AWS::CloudWatch::Dashboard" 1)))

      (t/is (nil?
             (.hasResourceProperties template
                                     "AWS::CloudWatch::Dashboard"
                                     {"DashboardName" "athena-dashboard"}))))))
