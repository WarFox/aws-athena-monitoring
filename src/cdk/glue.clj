(ns cdk.glue
  (:import
   [software.amazon.awscdk.services.glue CfnDatabase$Builder CfnDatabase$DatabaseInputProperty CfnTable$Builder CfnTable$TableInputProperty]))

(defn database
  [stack id {:keys [catalog-id database-input]}]
  (-> (CfnDatabase$Builder/create stack id)
      (.catalogId catalog-id)
      (.databaseInput database-input)
      (.build)))

(defn database-input
  [{:keys [description name location-uri]}]
  (-> (CfnDatabase$DatabaseInputProperty/builder)
      (.description description)
      (.name name)
      (.locationUri location-uri)
      (.build)))

(defn table
  [stack id {:keys [catalog-id database-name table-input]}]
  (-> (CfnTable$Builder/create stack id)
      (.catalogId catalog-id)
      (.databaseName database-name)
      (.tableInput table-input)
      (.build)))

(defn table-input
  [{:keys [description name owner]}]
  (-> (CfnTable$TableInputProperty/builder)
      (.description description)
      (.name name)
      (.owner owner)
      (.build)))
