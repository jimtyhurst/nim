(ns nim.test.core
  (:use [nim.core] :reload)
  (:use [clojure.test]))

(deftest test-reset-default
  "Expects game with default preferences and state."
  (let [game (reset-game)]
    (is (== (get-max-tokens-to-take game) default-max-number-of-tokens-to-take))
    (is (== (get-remaining-tokens game) default-number-of-tokens))
    (is (= (get-next-player game) default-starting-player))))

(deftest test-reset-specified
  "Expects game with given preferences and state."
  (let [expected-tokens 7
        expected-max-tokens 3
        expected-next-player :player1
        game (reset-game expected-tokens expected-max-tokens expected-next-player)]
    (is (== (get-max-tokens-to-take game) expected-max-tokens))
    (is (== (get-remaining-tokens game) expected-tokens))
    (is (= (get-next-player game) expected-next-player))))

(deftest test-valid-player
  (is (valid-player? :player1))
  (is (valid-player? :player2))
  (is (not (valid-player? :unknown-player))))

(deftest test-valid-game
  "Expects that default configuration is valid."
  (let [game (reset-game)]
    (is (valid-game? game))))

(deftest test-set-remaining-tokens
  "Expects fully-specified game with revised remaining-tokens."
  (let [expected-tokens (- default-number-of-tokens 1)
        game (set-remaining-tokens expected-tokens (reset-game))]
    (is (== (get-remaining-tokens game) expected-tokens))))

(deftest test-choose-next-turn-taker
  "Expects the next player in sequence."
  (is (= (choose-next-turn-taker :player1) :player2))
  (is (= (choose-next-turn-taker :player2) :player1)))

(deftest test-set-next-player
  "Expects :player1 to relinquish to :player2"
  (let [game (set-next-player :player2 (reset-game))]
    (is (= (get-next-player game) :player2))
    (is (= (get-next-player (set-next-player :player1 game)) :player1))))

(deftest test-take-turn-unwon-game
  "Expects :player1 to relinquish to :player2."
  (let [game0 (reset-game)
        game1 (take-turn 1 game0)]
    (is (= (get-next-player game0) default-starting-player))
    (is (= (get-next-player game1) (choose-next-turn-taker default-starting-player)))))

(deftest test-take-turn-wins-game
  "Expects :player2 as winner will remain as next-player."
  (let [expected-remaining-tokens 1
        expected-winner (choose-next-turn-taker default-starting-player)
        game0 (reset-game)
        game1 (set-next-player expected-winner (set-remaining-tokens expected-remaining-tokens game0))]
    (is (= (get-next-player game0) default-starting-player))
    (is (= (get-remaining-tokens game0) default-number-of-tokens))
    (is (= (get-next-player game1) expected-winner))
    (is (= (get-remaining-tokens game1) expected-remaining-tokens))
    (is (= (get-next-player (take-turn expected-remaining-tokens game1)) expected-winner))))

(deftest test-take-turn-with-valid-tokens
  "Expects valid move"
  (let [expected-next-player (choose-next-turn-taker default-starting-player)
        tokens-taken 3
        game (take-turn tokens-taken (reset-game))]
    (is (not (nil? game)))
    (is (== (get-remaining-tokens game) (- default-number-of-tokens tokens-taken)))
    (is (= (get-next-player game) expected-next-player))))
