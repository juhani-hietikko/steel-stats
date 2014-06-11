(ns import.post-processing
  )

(defn- normalize-player-name [the-name]
  the-name)

(defn- parse-int [s]
  (if (empty? s)
    0
    (. Integer parseInt (clojure.string/trim s))))

(defn- split-tuple [tuple separator first-key second-key]
  (let [[fst snd] (map parse-int (clojure.string/split tuple separator))]
    {first-key fst second-key snd}))

(defn- split-fraction [fraction success-key attemps-key]
  (split-tuple fraction #"/" success-key attemps-key))

(defn- groom-item [target-map [k v]]
  (let [groomed (case k
                  :all-passes (split-fraction v :all-passes-succeeded :all-passes-attempted)
                  :hard-passes (split-fraction v :hard-passes-succeeded :hard-passes-attempted)
                  :other-duels-attack (split-fraction v :other-duels-attack-succeeded :other-duels-attack-attempted)
                  :aerial-duels-attack (split-fraction v :aerial-duels-attack-succeeded :aerial-duels-attack-attempted)
                  :other-duels-defence (split-fraction v :other-duels-defence-succeeded :other-duels-defence-attempted)
                  :aerial-duels-defence (split-fraction v :aerial-duels-defence-succeeded :aerial-duels-defence-attempted)
                  :goal-attempts-and-assists (split-tuple v #"\+" :goal-attempts :assists)
                  :player-name {}
                  {k (parse-int v)})]
    (merge target-map groomed)))

(defn- groom-player-match-stats [target-map [_ player-stats]]
  (let [player-name (normalize-player-name (:player-name player-stats))
        groomed-player-stats (reduce groom-item {} player-stats)]
    (conj target-map [player-name groomed-player-stats])))

(defn groom-match-stats [match-stats]
  (reduce groom-player-match-stats {} match-stats))
