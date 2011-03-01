(ns nim.ctl
  (:use [nim.core] :reload)
  (:use [nim.player] :reload))

(def persisted-game (reset-game))

(defn rules []
  (str "This game is called Nim. "
       "There are "
       (get-remaining-tokens persisted-game)
       " tokens currently on the board. "
       "You may take [1.."
       (get-max-tokens-to-take persisted-game)
       "] tokens at each turn. "
       "The player who takes the last token wins the game. "
       \newline
       "Use (start-game) to initialize the board. "
       \newline
       "Use (take-tokens number-of-tokens) to take a turn or "
       "use (machine-takes-turn) to let the computer calculate the move. "))

(defn turn-notification [game]
  (let [player-name (get-player-name game)]
    (if (< (get-remaining-tokens game) 1)
      (str player-name " won the game!")
      (str "It is " player-name "'s turn."))))

(defn start-game []
  "Initializes game, so that user can start playing."
  (def persisted-game (reset-game))
  (str (rules)
       \newline \newline
       (turn-notification persisted-game)))

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
                         " tokens remaining. "
                         (turn-notification persisted-game))))))

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
         " tokens remaining. "
         (turn-notification persisted-game))))

