(ns stacks.athena-monitor
  (:import
   [software.amazon.awscdk Stack])
  (:require
   [constructs.query-stats :as query-stats]
   [constructs.athena-dashboard :as athena-dashboard]))

(defn stack
  [parent]
  (let [stack (Stack. parent "athena-monitor")]

    (athena-dashboard/construct stack)
    (query-stats/construct stack)
      ;; return stack
    stack))
