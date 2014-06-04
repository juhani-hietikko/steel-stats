(ns import.mongodb-importer
  (:require [monger.core :as mg]
            [monger.collection :as mc]))

(defn save-to-mongo [stats]
  (let [conn (mg/connect)
        db   (mg/get-db conn "steel-test")]
    (mc/insert-and-return db "matches" {:name "5.3 2014 Unkari - Suomi"})))
