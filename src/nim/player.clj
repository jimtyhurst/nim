(ns nim.player
  (:use [nim.core] :reload))

;Automated player implements logic to make move in the game of Nim.
(defn take-turn [game]
  "Returns a game after taking a turn for the current player."
  (take-tokens 1 game) ;FIXME: Implement lookahead and smart choices.
  )
