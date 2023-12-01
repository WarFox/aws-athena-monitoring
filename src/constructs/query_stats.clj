(ns constructs.query-stats
  (:import
   [software.amazon.awscdk Aws RemovalPolicy])
  (:require
   [integrant.core :as ig]
   [clojure.string :as str]
   [cdk.s3 :as s3]
   [cdk.glue :as glue]))

(def database-name "athena_monitoring")

(defn canonical-bucket-name
  [name]
  (str/join "-" [name Aws/ACCOUNT_ID Aws/REGION]))

(defn config
  "Integrant config for athena monitor"
  [stack]
  {::bucket    stack
   ::database  {:stack  stack
                :bucket (ig/ref ::bucket)}
   ::table     {:stack    stack
                :database (ig/ref ::database)}})

(defmethod ig/init-key ::bucket
  [_ stack]
  (s3/bucket stack "athena-monitor"
             {:bucketName (canonical-bucket-name "athena-monitor")
              :removalPolicy RemovalPolicy/DESTROY}))

(defmethod ig/init-key ::database
  [_ {:keys [stack bucket]}]
  (glue/database stack "glue-database"
                 {:catalog-id Aws/ACCOUNT_ID
                  :database-input (glue/database-input
                                   {:description  "Athena Monitor database"
                                    :name         database-name
                                    :location-uri (str "s3://" (.getBucketName bucket))})}))

(defmethod ig/init-key ::table
  [_ {:keys [stack database]}]
  (-> (glue/table stack "query-stats-table"
                  {:catalog-id (.getCatalogId database)
                   :database-name database-name
                   :table-input (glue/table-input
                                 {:description "Athena query stats, refreshed every hour with data from CloudTrail and CloudWatch"
                                  :name "query_stats"
                                  :owner "Deepu"})})
      (.addDependency database)))

(defn construct
  [stack]
  (println "Athena Query Stats")
  (ig/init (config stack)))
