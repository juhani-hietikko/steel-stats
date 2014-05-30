(ns import.scrape-ff2
  (:require [net.cgrand.enlive-html :as html]))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn opening-post-in-topic [topic-url]
  (first (html/select (fetch-url topic-url) [:div.post])))

(defn- parse-table [html-tables n map-fn]
  (map map-fn (html/select (nth html-tables n) [:tr])))

(defn- cell-text [cells n]
  (html/text (nth cells n)))

(defn- parse-row [html-row cols]
  (let [cells (html/select html-row [:td])
        map-seed {:player-name (cell-text cells 0)}]
    (loop [remaining-cols cols
           the-map map-seed
           i 1]
      (if (empty? remaining-cols)
        the-map
        (recur (rest remaining-cols) 
               (conj the-map {(first remaining-cols) (cell-text cells i)}) 
               (inc i))))))

(defn- parse-minutes-and-passes-row [html-row]
  (parse-row html-row [:minutes :all-passes :hard-passes]))

(defn- parse-attack-duels-row [html-row]
  (parse-row html-row [:other-duels-attack :aerial-duels-attack]))

(defn- parse-defence-duels-row [html-row]
  (parse-row html-row [:other-duels-defence :aerial-duels-defence]))

(defn- parse-misc-row [html-row]
  (parse-row html-row [:interceptions :dribbles :ball-holdings :goal-attempts-and-assists]))

(defn match-stats [html-post]
  (let [html-tables (html/select html-post [:table])
        minutes-and-passes (parse-table html-tables 0 parse-minutes-and-passes-row)
    attack-duels (parse-table html-tables 1 parse-attack-duels-row)
    defence-duels (parse-table html-tables 2 parse-defence-duels-row)
    misc (parse-table html-tables 3 parse-misc-row)]
    minutes-and-passes))
