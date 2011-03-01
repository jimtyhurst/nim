(ns nim.ctl
  (:use [nim.core] :reload))

(def persisted-game (reset-game))

(defn start-game []
  "Initializes game, so that user can start playing."
  (def persisted-game (reset-game))
  (str "There are "
       (get-remaining-tokens persisted-game)
       " tokens."))

(defn take-tokens [number-of-tokens]
  "Removes the number-of-tokens from the game board."
  (let [played-game (take-turn number-of-tokens persisted-game)]
    (cond (nil? played-game) (str "Illegal move")
          :else (do (def persisted-game played-game)
                    (str "You removed "
                         number-of-tokens
                         " tokens. "
                         "There are "
                         (get-remaining-tokens persisted-game)
                         " tokens remaining.")))))
