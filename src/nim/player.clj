(ns nim.player
  (:use [nim.core] :reload))

;; Automated player implements logic to make move in the game of Nim.

(defprotocol TurnTakerStrategy
  "Specifies an algorithm for calculating how many tokens to take for a turn in the game."
  (calculate-tokens-to-take [strategy game] "Returns the number of tokens recommended that the current player should take."))

;; Always takes just 1 token.
(defrecord StrategyTake1 []
  TurnTakerStrategy
  (calculate-tokens-to-take [this game] 1))

;; Takes as many tokens as possible.
(defrecord StrategyTakeMax []
  TurnTakerStrategy
  (calculate-tokens-to-take [this game]
                            (let [remaining-tokens (get-remaining-tokens game)
                                  max-tokens-allowed (get-max-tokens-to-take game)]
                              (if (>= remaining-tokens max-tokens-allowed)
                                max-tokens-allowed
                                remaining-tokens))))

;; TODO: Implement a strategy that does look-ahead to choose the best move.
;;   (defrecord StrategyLookAhead [number-of-moves] ...)

;; Sets a default strategy.
(def turn-taker-strategy (StrategyTakeMax.))

(defn auto-take-turn [game]
  "Returns a game after taking a turn for the current player."
  (take-turn (calculate-tokens-to-take turn-taker-strategy game) game))
