(ns import.ff2-match-search
  (:require [net.cgrand.enlive-html :as html]
            [import.util :as util]))

(defn pick-match-links-from [url]
  (let [links-in-opening-post (html/select (util/opening-post-in-topic url) [:a])]
    (println (map html/text links-in-opening-post))))
