(ns core
  (:require
   [system :as system]))

(defn synth
  []
  (system/init))

(defn -main
  [& args]
  (println "AWS Athena Monitoring")
  (synth))
