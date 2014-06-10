(ns import.ff2-match-search
  (:require [net.cgrand.enlive-html :as html]
            [import.util :as util]))

(defn pick-match-links-from [url]
  (let [links-in-opening-post (html/select (util/opening-post-in-topic url) [:a])
        links-to-drop #{"http://futisforum2.org/index.php?topic=16119.0"
                        "http://futisforum2.org/index.php?topic=41553.0"
                        "http://futisforum2.org/index.php?topic=157398.0"}]
    (->> (map #(get-in % [:attrs :href]) 
              links-in-opening-post)
         (filter (complement links-to-drop)))))
