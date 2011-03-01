(ns nim.player
  (:use [nim.core] :reload))

;Automated player implements logic to make move in the game of Nim.
(defn auto-calculate-turn [game]
  "Returns a game after taking a turn for the current player."
  (take-turn 1 game) ;FIXME: Implement lookahead and smart choices.
  )
