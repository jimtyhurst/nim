(ns nim.ctl
  (:use [nim.core] :reload)
  (:use [nim.player] :reload))

(def persisted-game (reset-game))

(defn start-game []
  "Initializes game, so that user can start playing."
  (def persisted-game (reset-game))
  (str "There are "
       (get-remaining-tokens persisted-game)
       " tokens."
       " You may take [1.."
       (get-max-tokens-to-take persisted-game)
       "] tokens at each turn."))

(defn take-tokens [number-of-tokens]
  "Removes the number-of-tokens from the game board."
  (let [played-game (take-turn number-of-tokens persisted-game)]
    (cond (nil? played-game) (str "Illegal move")
          :else (do (def persisted-game played-game)
                    (str "You removed "
                         number-of-tokens
                         " tokens."
                         " There are "
                         (get-remaining-tokens persisted-game)
                         " tokens remaining.")))))

(defn machine-takes-turn []
  "Calculates a move and removes tokens from the game board."
  (let [previous-tokens (get-remaining-tokens persisted-game)
        played-game (auto-calculate-turn persisted-game)]
    (def persisted-game played-game)
    (str "I took "
         (- previous-tokens (get-remaining-tokens persisted-game))
         " tokens."
         " There are "
         (get-remaining-tokens persisted-game)
         " tokens remaining.")))

