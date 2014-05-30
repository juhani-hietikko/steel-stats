(ns import.scrape-ff2
  (:require [net.cgrand.enlive-html :as html]))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn opening-post-in-topic [topic-url]
  (first (map html/text (html/select (fetch-url topic-url) [:div.post]))))
