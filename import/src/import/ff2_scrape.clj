(ns import.ff2-scrape
  (:require [net.cgrand.enlive-html :as html]
            [import.util :as util]))

(defn- parse-table [html-tables n map-fn]
  (let [rows (map map-fn (html/select (nth html-tables n) [:tr]))]
    (reduce (fn [rows-map row]
              (let [player (:player-name row)]
                (if (empty? player)
                  rows-map
                  (conj rows-map {player row})))) 
            {} rows)))

(defn- parse-row [html-row cols offset]
  (let [cells (html/select html-row [:td])
        filtered-cells (if (= 9 (count cells))
                         ; A hack to handle the variation of the passes table 
                         ; where there are extra columns for each half.
                         [(nth cells 1) (nth cells 2) (nth cells 6)]
                         (rest cells))
        cells-with-offset (drop (* offset (count cols)) filtered-cells)
        map-seed {:player-name (html/text (first cells))}]
    (loop [remaining-cols cols
           remaining-cells cells-with-offset
           the-map map-seed]
      (if (or (empty? remaining-cols) (empty? remaining-cells))
        the-map
        (recur (rest remaining-cols)
               (rest remaining-cells)
               (conj the-map {(first remaining-cols) (html/text (first remaining-cells))}))))))

(defn- row-parser [cols offset]
  (fn [html-row]
    (parse-row html-row cols offset)))

(defn match-stats-from-post [html-post match-index]
  (let [html-tables (html/select html-post [:table])
        minutes-and-passes (parse-table html-tables 0 (row-parser [:minutes 
                                                                   :all-passes 
                                                                   :hard-passes] match-index))
        attack-duels (parse-table html-tables 1 (row-parser [:other-duels-attack 
                                                             :aerial-duels-attack] match-index))
        defence-duels (parse-table html-tables 2 (row-parser [:other-duels-defence 
                                                              :aerial-duels-defence] match-index))
        misc (parse-table html-tables 3 (row-parser [:interceptions 
                                                     :dribbles 
                                                     :ball-holdings 
                                                     :goal-attempts-and-assists] match-index))]
    (merge-with (fn [row-map-a row-map-b]
                  (merge row-map-a row-map-b)) 
                minutes-and-passes attack-duels defence-duels misc)))

(defn match-stats-from-topic 
  ([topic-url]
   (match-stats-from-topic topic-url 0))
  ([topic-url match-index]
   (match-stats-from-post (util/opening-post-in-topic topic-url) match-index)))
