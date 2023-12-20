(ns black-jack.core
  (:require [clojure.string :refer [join]]))

(def estado-pontos-jogador (atom 0))
(def estado-pontos-dealer (atom 0))
(def state-winning-streak (atom 0))

(defn count-wins []
  (reset! estado-pontos-jogador 0)
  (reset! estado-pontos-dealer 0)
  (swap! state-winning-streak inc))

(defn transform-string [card]
   (join " " (mapv (fn [carta] (str (:value carta) " " (:naipe carta))) card)))

(defn deck-of-cards []
  (let [decks ["Copas" "Ouros" "Paus" "Espadas"]
        random-position-deck (int (* (rand) 4))
        radom-value-card (+ 1 (rand-int 10))
        ]
    [{:value radom-value-card :naipe (nth decks random-position-deck)}]
    ))

(defn count-points [value-cards & [reset?]]
  (when reset?
    (reset! estado-pontos-jogador 0))
  (let [valor-atual (-> value-cards first :value)
        estado-atual (swap! estado-pontos-jogador + valor-atual)]

    estado-atual))

(defn count-points-dealer [value-cards & [reset?]]
  (when reset?
    (reset! estado-pontos-dealer 0))
  (let [valor-atual (-> value-cards first :value)
        estado-atual (swap! estado-pontos-dealer + valor-atual)]

    estado-atual))

(defn check-value-deck-jogador [value]
  (if (> value 21)
    (do
      (println "Você perdeu !")
      (println "Pontuação final Jogador :" @estado-pontos-jogador)
      (Thread/sleep 1000)
      (System/exit 0))))

(defn check-value-deck-dealer [value]
  (if (> value 21)
    (do
      (println "O Dealer perdeu !")
      (println "Pontuação final Dealer:" value)
      (println)
      (println "Vitórias consecutivas:" (count-wins))
      )))

(defn compare-values []
  (cond (> @estado-pontos-dealer @estado-pontos-jogador)
        (do
          (println)
          (println "Pontuação Dealer:" @estado-pontos-dealer )
          (println "O Dealer ganhou !")
          (Thread/sleep 1000)
          (System/exit 0)
          )

        (< @estado-pontos-dealer @estado-pontos-jogador)
        (do
          (println)
          (println "Pontuação Jogador:" @estado-pontos-jogador )
          (println "O jogador ganhou !")
          (println "Vitórias consecutivas:" (count-wins))
          (println)
          )
        :else
        (reset! estado-pontos-jogador 0)
        (reset! estado-pontos-dealer 0)
        (println "Temos um empate !")))

(defn -main []
  (loop [pontos-atuais @estado-pontos-jogador]
    (println "1 - Para pedir uma nova carta ")
    (println "2 - Para Dealer jogar ")
    (println "3 - Para comparar valores")

    (let [card (Integer/parseInt (read-line))]
        (cond (= card 1)
          (do
            (let [new-card (deck-of-cards)]
              (println "---------- PLAYER----------")
              (println "Carta: " (transform-string new-card))
              (check-value-deck-jogador (count-points new-card))
              (println "Pontuação final jogador: " @estado-pontos-jogador)
              )
              (println)
              (println)
              (recur pontos-atuais))
          (= card 2)
          (do
            (let [new-card-dealer (deck-of-cards)]
              (println "---------- DEALER JOGANDO----------")
              (println "Carta: " (transform-string new-card-dealer))
              (check-value-deck-dealer (count-points-dealer new-card-dealer))
              (println "Pontuação atual Dealer: " @estado-pontos-dealer)
              )
              (println)
              (println)
              (recur 0))
          (= card 3)
          (do
            (compare-values)

            (recur 0))
          :else
              (println "Você desistiu! O Dealer venceu.")
              ))))

;Guardar a quantida de vitória consecutivas