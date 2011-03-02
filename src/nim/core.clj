(ns nim.core)

                                        ;State-less logic for playing the game of Nim.
                                        ;All of the essential state is passed in the game map.

;Default values for configuring a game.
(def default-number-of-tokens 9)
(def default-max-number-of-tokens-to-take 3)
(def default-starting-player :player1)

                                        ;TODO: players should be part of the :board-preferences, so that they
                                        ;are part of the game state.
(def players '(:player1 :player2))
(def player-names {:player1 "Player 1", :player2 "Player 2"})

(defn get-player-name [game]
  (let [player ((game :game-state) :next-player)]
    (player-names player)))

(defn get-max-tokens-to-take [game]
  "Returns preference for max tokens that may be taken in one turn in given game."
  ((game :board-preferences) :max-tokens-to-take))

(defn get-remaining-tokens [game]
  "Returns number of remaining tokens in given game."
  ((game :game-state) :remaining-tokens))

(defn set-remaining-tokens [tokens game]
  "Returns game with the remaining-tokens set to given number of tokens."
  (assoc game :game-state (assoc (game :game-state) :remaining-tokens tokens)))

(defn get-next-player [game]
  "Returns the player whose turn it is to play in the game."
  ((game :game-state) :next-player))

(defn set-next-player [player game]
  "Returns game with the next-player set to given player."
  (assoc game :game-state (assoc (game :game-state) :next-player player)))

(defn choose-next-turn-taker [player]
  "Returns the next player in sequence who should be given a turn to play."
  ;FIXME: Generalize to read next player from players list, which
  ;would allow for more than two players.
  (if (= player :player1)
    :player2
    :player1))

(defn completed? [game]
  "Returns true when game has reached the end state."
  (== 0 (get-remaining-tokens game)))

;; (defn get-game-winner [game]
;;   "Returns symbol for player who won the game or nil when game is not complete yet."
;;   (if (completed? game)
;;     (get-next-player game)
;;     nil))

(defn valid-player? [player]
  "Returns true if player is in the list of known players."
  (some (hash-set player) players))

(defn valid-game? [game]
  "Returns true if game has a valid configuration."
  (and (< 0 (get-max-tokens-to-take game))
       (<= 0 (get-remaining-tokens game))
       (valid-player? (get-next-player game))))

(defn valid-move? [tokens game]
  "Returns true if number of tokens can be taken from the game."
  (and (> tokens 0)
       (<= tokens (get-remaining-tokens game))
       (<= tokens (get-max-tokens-to-take game))))

(defn reset-game
  "Returns an initialized game board, ready to begin playing."
  ([]
  (reset-game default-number-of-tokens default-max-number-of-tokens-to-take default-starting-player))
  ([number-of-tokens max-number-of-tokens-to-take starting-player]
  {:board-preferences
   {:max-tokens-to-take max-number-of-tokens-to-take}
   :game-state
   {:remaining-tokens number-of-tokens
    :next-player starting-player}}))

(defn relinquish-turn [game]
  "Returns game with next player incremented to the next player who should have a turn to play. Note: no change occurs once the game has been won."
  (if (completed? game)
    game
    (set-next-player (choose-next-turn-taker (get-next-player game)) game)))

(defn remove-tokens [tokens game]
  "Returns a game board after taking the requested number of tokens from the board."
  (if (valid-move? tokens game)
    (set-remaining-tokens (- (get-remaining-tokens game) tokens) game)
    nil))

(defn take-turn [tokens game]
  "Returns game board after taking a turn with the requested number of tokens."
  (if (valid-game? game)
    (relinquish-turn (remove-tokens tokens game))
    nil))
