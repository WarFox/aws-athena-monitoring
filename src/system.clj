(ns system
  (:require
   [integrant.core :as ig]
   [stacks.athena-monitor :as athena-monitor])
  (:import
   [software.amazon.awscdk App]))

(defn config
  [app]
  {::athena-monitor {:app app}
   ::synth          {:app app
                     :stacks [(ig/ref ::athena-monitor)]}})

(defmethod ig/init-key ::app
  [_ _]
  (App.))

(defmethod ig/init-key ::synth
  [_ {:keys [app]}]
  (.synth app))

(defmethod ig/init-key ::athena-monitor
  [_ {:keys [app]}]
  (athena-monitor/stack app))

(defn init
  "Initialise system"
  []
  (ig/init (config (App.))))
