(ns import.scrape-ff2
  (:require [net.cgrand.enlive-html :as html]))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn opening-post-in-topic [topic-url]
  (first (html/select (fetch-url topic-url) [:div.post])))

(defn- cell-text [cells n]
  (html/text (nth cells n)))

(defn- parse-minutes-and-passes-row [html-row]
  (let [cells (html/select html-row [:td])]
    {:player-name (cell-text cells 0)
     :minutes (cell-text cells 1)
     :all-passes (cell-text cells 2)
     :hard-passes (cell-text cells 3)}))

(defn minutes-and-passes [html-post]
  (let [html-table (first (html/select html-post [:table]))]
    (map parse-minutes-and-passes-row (html/select html-table [:tr]))))
