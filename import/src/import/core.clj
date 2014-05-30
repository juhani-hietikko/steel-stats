(ns import.core
  (:gen-class)
  (:require [import.scrape-ff2 :as ff2]))

(defn -main
  [& args]
  (println (ff2/match-stats (ff2/opening-post-in-topic "http://futisforum2.org/index.php?topic=165257.0"))))
