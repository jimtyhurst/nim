(ns nim.core)

;; State-less logic for playing the game of Nim.
;; All of the essential state is passed in a Game object.

;; Default values for configuring a game.
(def default-number-of-tokens 9)
(def default-max-number-of-tokens-to-take 3)
(def default-players {:player1 "Player 1", :player2 "Player 2"})
(def default-turn-taking-sequence '(:player1 :player2))

(defn get-next-item-position [item all-items item-counter]
  "Returns the zero-based item-counter when item is the first element in all-items; -1 when there are no more items to match."
  (cond (nil? all-items) -1
        (empty? all-items) -1
        (= item (first all-items)) item-counter
        :else (recur item (next all-items) (+ 1 item-counter))))

(defn get-next-item [current-item all-items]
  "Returns the item after current-item in the list, wrapping around to the beginning if necessary."
  (let [next-item-index (get-next-item-position current-item all-items 0)]
    (cond (< next-item-index 0) ;;not found
          nil
          (>= next-item-index (- (count all-items) 1)) ;;found last element, so wrap-around
          (if (or (nil? all-items) (empty? all-items))
            nil
            (first all-items))
          :else ;;found not at end of list, so okay to grab next element
          (nth all-items (+ 1 next-item-index)))))

(defprotocol GameState
  "Specifies accessors for game board configuration parameters."
  (set-players [this players] "Returns a game with the given map of player to player name.")
  (set-turn-taking-sequence [this turn-taking-sequence] "Returns a game with the given seq of players.")
  (get-max-tokens-to-take [this] "Returns the maximum number of tokens that may be taken on one turn.")
  (set-max-tokens-to-take [this number-of-tokens] "Returns a game that allows number-of-tokens to be taken on one turn.")
  (get-remaining-tokens [this] "Returns the number of tokens remaining to be played in this game.")
  (set-remaining-tokens [this number-of-tokens] "Returns a game that has number-of-tokens remaining to be played.")
  (get-next-player [this] "Returns the player whose turn it is to play in this game.")
  (get-next-player-name [this] "Returns the string name of the player whose turn it is to play in this game.")
  (set-next-player [this player] "Returns a game with player set to take the next turn.")
  (increment-next-turn-taker [this] "Returns a game with the next-player advanced to the next player in the seq to take a turn.")
  (completed? [this] "Returns true when game as reached the end state.")
  (valid-player? [this player] "Returns true if player is in the list of known players.")
  (valid-game? [this] "Returns true if game has a valid configuration.")
  (valid-move? [this tokens] "Returns true if number of tokens can be taken from the game.")
  (take-turn [this tokens] "Returns a game after taking a turn with the requested number of tokens."))

(defrecord Game [players turn-taking-sequence max-tokens-to-take remaining-tokens next-player]
  GameState
  (set-players [this new-players] (Game. new-players turn-taking-sequence max-tokens-to-take remaining-tokens next-player))
  (set-turn-taking-sequence [this new-turn-taking-sequence] (Game. players new-turn-taking-sequence max-tokens-to-take remaining-tokens next-player))
  (get-max-tokens-to-take [this] max-tokens-to-take)
  (set-max-tokens-to-take [this new-max-tokens-to-take] (Game. players turn-taking-sequence new-max-tokens-to-take remaining-tokens next-player))
  (get-remaining-tokens [this] remaining-tokens)
  (set-remaining-tokens [this new-remaining-tokens] (Game. players turn-taking-sequence max-tokens-to-take new-remaining-tokens next-player))
  (get-next-player [this] next-player)
  (set-next-player [this new-next-player] (Game. players turn-taking-sequence max-tokens-to-take remaining-tokens new-next-player))
  (get-next-player-name [this] (players next-player))
  (increment-next-turn-taker [this] (set-next-player this (get-next-item next-player turn-taking-sequence)))
  (completed? [this] (== 0 remaining-tokens))
  (valid-player? [this player] (some (hash-set player) turn-taking-sequence))
  (valid-game? [this] (and (< 0 max-tokens-to-take)
                           (<= 0 remaining-tokens)
                           (valid-player? this next-player)))
  (valid-move? [this tokens] (and (> tokens 0)
                                  (<= tokens remaining-tokens)
                                  (<= tokens max-tokens-to-take)))
  (take-turn [this tokens] (if (and (valid-game? this) (valid-move? this tokens))
                             (let [game (set-remaining-tokens this (- (get-remaining-tokens this) tokens))]
                               (if (completed? game)
                                 game
                                 (increment-next-turn-taker game)))
                             nil)))

(defn reset-game
  "Returns an initialized game board, ready to begin playing."
  ([]
     (reset-game default-players default-turn-taking-sequence default-max-number-of-tokens-to-take default-number-of-tokens (first default-turn-taking-sequence)))
  ([players turn-taking-sequence max-tokens-to-take remaining-tokens next-player]
     (Game. players turn-taking-sequence max-tokens-to-take remaining-tokens next-player)))
