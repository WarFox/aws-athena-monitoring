(ns cdk.s3
  (:import
   [software.amazon.awscdk.services.s3 Bucket$Builder]))

(defn bucket
  [stack id {:keys [bucket-name removal-policy]}]
  (-> (Bucket$Builder/create stack id)
      (.bucketName bucket-name)
      (.removalPolicy removal-policy)
      (.build)))
