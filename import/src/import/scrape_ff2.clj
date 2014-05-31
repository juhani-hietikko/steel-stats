(ns import.scrape-ff2
  (:require [net.cgrand.enlive-html :as html]))

(defn- parse-table [html-tables n map-fn]
  (let [rows (map map-fn (html/select (nth html-tables n) [:tr]))]
    (reduce (fn [rows-map row] 
              (conj rows-map {(:player-name row) row})) 
            {} 
            rows)))

(defn- parse-row [html-row cols]
  (let [cells (html/select html-row [:td])
        filtered-cells (if (= 9 (count cells))
                         ; A hack to handle the variation of the passes table 
                         ; where there are extra columns for both halves.
                         [(nth cells 1) (nth cells 2) (nth cells 6)]
                         (rest cells))
        map-seed {:player-name (html/text (first cells))}]
    (loop [remaining-cols cols
           remaining-cells filtered-cells
           the-map map-seed]
      (if (or (empty? remaining-cols) (empty? remaining-cells))
        the-map
        (recur (rest remaining-cols)
               (rest remaining-cells)
               (conj the-map {(first remaining-cols) (html/text (first remaining-cells))}))))))

(defn- parse-minutes-and-passes-row [html-row]
  (parse-row html-row [:minutes :all-passes :hard-passes]))

(defn- parse-attack-duels-row [html-row]
  (parse-row html-row [:other-duels-attack :aerial-duels-attack]))

(defn- parse-defence-duels-row [html-row]
  (parse-row html-row [:other-duels-defence :aerial-duels-defence]))

(defn- parse-misc-row [html-row]
  (parse-row html-row [:interceptions :dribbles :ball-holdings :goal-attempts-and-assists]))

(defn match-stats-from-post [html-post]
  (let [html-tables (html/select html-post [:table])
        minutes-and-passes (parse-table html-tables 0 parse-minutes-and-passes-row)
        attack-duels (parse-table html-tables 1 parse-attack-duels-row)
        defence-duels (parse-table html-tables 2 parse-defence-duels-row)
        misc (parse-table html-tables 3 parse-misc-row)]
    (merge-with (fn [row-map-a row-map-b]
                  (merge row-map-a row-map-b)) 
                minutes-and-passes attack-duels defence-duels misc)))

(defn- fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn- opening-post-in-topic [topic-url]
  (first (html/select (fetch-url topic-url) [:div.post])))

(defn match-stats-from-topic [topic-url]
  (match-stats-from-post (opening-post-in-topic topic-url)))
