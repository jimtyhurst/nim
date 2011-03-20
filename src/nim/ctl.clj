(ns nim.ctl
  (:use [nim.core] :reload)
  (:use [nim.player] :reload))

;; Command-line interface to the Nim game.
;; Usage:
;;   (start-game)
;;   (take-tokens number-of-tokens)
;;   (machine-takes-turn)

;; Global holds the current state of the game between turns.
(def *persisted-game* (reset-game))

(defn rules []
  (str "This game is called Nim. "
       "There are "
       (get-remaining-tokens *persisted-game*)
       " tokens currently on the board. "
       "You may take [1.."
       (get-max-tokens-to-take *persisted-game*)
       "] tokens at each turn. "
       "The player who takes the last token wins the game. "
       \newline
       "Use (start-game) to initialize the board. "
       \newline
       "Use (take-tokens number-of-tokens) to take a turn or "
       "use (machine-takes-turn) to let the computer calculate the move. "))

(defn- turn-notification
  "Returns displayable string of whose turn it is to play next."
  [game]
  (let [player-name (get-next-player-name game)]
    (if (completed? game)
      (str player-name " won the game!")
      (str "It is " player-name "'s turn."))))

(defn start-game
  "Initializes game, so that user can start playing, returning a displayable string of instructions to continue playing the game."
  []
  (let [game (reset-game)]
    (def *persisted-game* game)
    (str (rules)
         \newline \newline
         (turn-notification game))))

(defn take-tokens
  "Removes the number-of-tokens from the game board, returning a displayable string description of the game state."
  [number-of-tokens]
  (let [played-game (take-turn *persisted-game* number-of-tokens)]
    (cond (nil? played-game) (str "Illegal move")
          :else (do (def *persisted-game* played-game)
                    (str "You removed "
                         number-of-tokens
                         " tokens."
                         " There are "
                         (get-remaining-tokens played-game)
                         " tokens remaining. "
                         (turn-notification played-game))))))

(defn machine-takes-turn
  "Calculates a move and removes tokens from the game board, returning a displayable string description of the game state."
  []
  (let [previous-tokens (get-remaining-tokens *persisted-game*)
        played-game (auto-take-turn *persisted-game*)]
    (def *persisted-game* played-game)
    (str "I took "
         (- previous-tokens (get-remaining-tokens played-game))
         " tokens."
         " There are "
         (get-remaining-tokens played-game)
         " tokens remaining. "
         (turn-notification played-game))))

(defn help []
  (str "(start-game) ;Initializes board to default preferences. "
       \newline
       "(take-tokens number-of-tokens) ;Player takes a turn by taking number-of-tokens. "
       \newline
       "(machine-takes-turn) ;The application makes a choice for the current turn."))

