(ns import.core
  (:gen-class)
  (:require [import.ff2-scrape :as ff2]
            [import.ff2-match-search :as matches]
            [import.post-processing :as pp]
            [import.mongodb-importer :as mg-import]))

(defn -main
  [& args]
  (let [match-topic-urls (matches/pick-match-links-from 
                           "http://futisforum2.org/index.php?topic=126157.0")
        stats-per-match (map ff2/match-stats-from-topic match-topic-urls)
        processed-stats-per-match (map pp/groom-match-stats stats-per-match)]
    (println (first processed-stats-per-match))))
  ;(mg-import/save-to-mongo {})
  ;(println (ff2/match-stats-from-topic "http://futisforum2.org/index.php?topic=159803.0" 0)))
