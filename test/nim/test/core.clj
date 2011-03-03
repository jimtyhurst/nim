(ns nim.test.core
  (:use [nim.core] :reload)
  (:use [clojure.test]))

(defn- default-starting-player []
  (first default-turn-taking-sequence))

(deftest test-get-next-item-position-nil-list
  "Expects -1 for nil collection."
  (let [item-to-find :player1
        searchable-list nil
        expected-index -1]
    (is (== (get-next-item-position item-to-find searchable-list 0) expected-index))))

(deftest test-get-next-item-position-empty-list
  "Expects -1 for empty collection."
  (let [item-to-find :player1
        searchable-list '()
        expected-index -1]
    (is (== (get-next-item-position item-to-find searchable-list 0) expected-index))))

(deftest test-get-next-item-position-1-element
  "Expects 0 for item in first position of collection."
  (let [item-to-find :player1
        searchable-list '(:player1)
        expected-index 0]
    (is (== (get-next-item-position item-to-find searchable-list 0) expected-index))))

(deftest test-get-next-item-position-first-element
  "Expects 0 for item in first position of collection."
  (let [item-to-find :player1
        searchable-list '(:player1 :player2 :player3)
        expected-index 0]
    (is (== (get-next-item-position item-to-find searchable-list 0) expected-index))))

(deftest test-get-next-item-position-last-element
  "Expects count - 1 for item in first position of collection."
  (let [item-to-find :player3
        searchable-list '(:player1 :player2 :player3)
        expected-index (- (count searchable-list) 1)]
    (is (== (get-next-item-position item-to-find searchable-list 0) expected-index))))

(deftest test-get-next-item-nil-list
  "Expects nil for nil collection."
  (let [item-to-find :player1
        searchable-list nil
        expected-next-item nil]
    (is (= (get-next-item item-to-find searchable-list) expected-next-item))))

(deftest test-get-next-item-empty-list
  "Expects nil for empty collection."
  (let [item-to-find :player1
        searchable-list '()
        expected-next-item nil]
    (is (= (get-next-item item-to-find searchable-list) expected-next-item))))

(deftest test-get-next-item-1-element
  "Expects first item for item in collection with only one element."
  (let [item-to-find :player1
        searchable-list '(:player1)
        expected-next-item :player1]
    (is (= (get-next-item item-to-find searchable-list) expected-next-item))))

(deftest test-get-next-item-first-element
  "Expects second item for current-item in first position of multi-element collection."
  (let [item-to-find :player1
        searchable-list '(:player1 :player2 :player3)
        expected-next-item :player2]
    (is (= (get-next-item item-to-find searchable-list) expected-next-item))))

(deftest test-get-next-item-position-last-element
  "Expects first item for item in last position of collection."
  (let [item-to-find :player3
        searchable-list '(:player1 :player2 :player3)
        expected-next-item :player1]
    (is (= (get-next-item item-to-find searchable-list) expected-next-item))))

(deftest test-get-next-item-default-list
  "Expects second item from default turn taking sequence."
  (let [item-to-find (first default-turn-taking-sequence)
        searchable-list default-turn-taking-sequence
        expected-next-item (second default-turn-taking-sequence)]
    (is (= (get-next-item item-to-find searchable-list) expected-next-item))))

(deftest test-valid-game
  "Expects that default configuration is valid."
  (is (valid-game? (reset-game))))

(deftest test-valid-move
  "Expects valid move when number of tokens is within range."
  (is (valid-move? (reset-game) default-max-number-of-tokens-to-take)))

(deftest test-reset-default
  "Expects game with default preferences and state."
  (let [game (reset-game)]
    (is (== (get-max-tokens-to-take game) default-max-number-of-tokens-to-take))
    (is (== (get-remaining-tokens game) default-number-of-tokens))
    (is (= (get-next-player game) (default-starting-player)))))

(deftest test-reset-specified
  "Expects game with given preferences and state."
  (let [expected-tokens 7
        expected-max-tokens 3
        expected-next-player (default-starting-player)
        game (reset-game default-players default-turn-taking-sequence expected-max-tokens expected-tokens expected-next-player)]
    (is (== (get-max-tokens-to-take game) expected-max-tokens))
    (is (== (get-remaining-tokens game) expected-tokens))
    (is (= (get-next-player game) expected-next-player))))

(deftest test-valid-player
  (let [game (reset-game)]
    (is (valid-player? game (nth default-turn-taking-sequence 0)))
    (is (valid-player? game (nth default-turn-taking-sequence 1)))
    (is (not (valid-player? game :unknown-player)))))

(deftest test-valid-game
  "Expects that default configuration is valid."
  (let [game (reset-game)]
    (is (valid-game? game))))

(deftest test-set-remaining-tokens
  "Expects fully-specified game with revised remaining-tokens."
  (let [expected-tokens (- default-number-of-tokens 1)
        game (set-remaining-tokens (reset-game) expected-tokens)]
    (is (== (get-remaining-tokens game) expected-tokens))))

(deftest test-get-next-player-name
  "Expects string name of first player in sequence."
  (let [game (reset-game)
        first-player (get-next-player game)
        expected-name (default-players (first default-turn-taking-sequence))]
    (is (= (get-next-player-name game) expected-name))))

(deftest test-choose-next-turn-taker
  "Expects the next player in sequence."
  (let [game0 (increment-next-turn-taker (reset-game))
        game1 (increment-next-turn-taker game0)]
    (is (= (get-next-player game0) (nth default-turn-taking-sequence 1)))
    (is (= (get-next-player game1) (nth default-turn-taking-sequence 0)))))

(deftest test-set-next-player
  "Expects :player1 to relinquish to :player2"
  (let [game (set-next-player (reset-game) :player2)]
    (is (= (get-next-player game) :player2))
    (is (= (get-next-player (set-next-player game :player1)) :player1))))

(deftest test-take-turn-unwon-game
  "Expects :player1 to relinquish to :player2."
  (let [game0 (reset-game)
        game1 (take-turn game0 1)]
    (is (= (get-next-player game0) (nth default-turn-taking-sequence 0)))
    (is (= (get-next-player game1) (nth default-turn-taking-sequence 1)))))

(deftest test-take-turn-wins-game
  "Expects :player2 as winner will remain as next-player."
  (let [expected-remaining-tokens 1
        expected-winner (nth default-turn-taking-sequence 1)
        game0 (reset-game)
        game1 (set-next-player (set-remaining-tokens game0 expected-remaining-tokens) expected-winner)]
    (is (= (get-next-player game0) (default-starting-player)))
    (is (= (get-remaining-tokens game0) default-number-of-tokens))
    (is (= (get-next-player game1) expected-winner))
    (is (= (get-remaining-tokens game1) expected-remaining-tokens))
    (is (= (get-next-player (take-turn game1 expected-remaining-tokens)) expected-winner))))

(deftest test-take-turn-with-valid-tokens
  "Expects valid move"
  (let [expected-next-player (nth default-turn-taking-sequence 1)
        tokens-taken default-max-number-of-tokens-to-take
        game (take-turn (reset-game) tokens-taken)]
    (is (not (nil? game)))
    (is (== (get-remaining-tokens game) (- default-number-of-tokens tokens-taken)))
    (is (= (get-next-player game) expected-next-player))))

(deftest test-game-set-players
  "Expects new Game with new list of players, but all of the rest of the state remains the same."
  (let [expected-new-players '(:p1 "Player 1" :p2 "Player 2" :p3 "Player 3")
        game0 (reset-game)
        game1 (set-players game0 expected-new-players)]
    (is (= (:players game1) expected-new-players))
    (is (== (:max-tokens-to-take game1) default-max-number-of-tokens-to-take))
    (is (== (:remaining-tokens game1) default-number-of-tokens))
    (is (= (:next-player game1) (default-starting-player)))))
