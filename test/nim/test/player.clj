(ns nim.test.player
  (:use [nim.core] :reload)
  (:use [nim.player] :reload)
  (:import (nim.player DecisionNode))
  (:use [clojure.test]))

(deftest test-StrategyTakeMax-with-many-tokens
  "Expects maximum allowed tokens to be taken."
  (let [game (reset-game)
        strategy (nim.player.StrategyTakeMax.)]
    (is (== (calculate-tokens-to-take strategy game) (get-max-tokens-to-take game)))))

(deftest test-StrategyTakeMax-not-enough-tokens
  "Expects all remaining tokens to be taken."
  (let [game0 (reset-game)
        game1 (set-remaining-tokens game0 (- (get-max-tokens-to-take game0) 1))
        strategy (nim.player.StrategyTakeMax.)]
    (is (== (calculate-tokens-to-take strategy game1) (get-remaining-tokens game1)))))

(deftest test-StrategyTake1
  "Expects always one token to be taken."
  (let [game (reset-game)
        strategy (nim.player.StrategyTake1.)]
    (is (== (calculate-tokens-to-take strategy game) 1))))

(deftest test-StrategyLookAhead-final-turn
  "Expects all remaining tokens to be taken."
  (let [game0 (reset-game)
        game1 (set-remaining-tokens game0 (- (get-max-tokens-to-take game0) 1))
        strategy (nim.player.StrategyLookAhead.)]
    (is (== (get-max-tokens-to-take-this-turn game1) (get-remaining-tokens game1)))
    (is (== (calculate-tokens-to-take strategy game1) (get-remaining-tokens game1)))))

(deftest test-StrategyLookAhead-1-of-5
  "Expects 1 of 5 remaining tokens to be taken, which guarantees a win."
  (let [game0 (reset-game)
        game1 (set-remaining-tokens game0 5)
        strategy (nim.player.StrategyLookAhead.)]
    (is (== (calculate-tokens-to-take strategy game1) 1))))

(deftest test-StrategyLookAhead-2-of-6
  "Expects 2 of 6 remaining tokens to be taken, which guarantees a win."
  (let [game0 (reset-game)
        game1 (set-remaining-tokens game0 6)
        strategy (nim.player.StrategyLookAhead.)]
    (is (== (calculate-tokens-to-take strategy game1) 2))))

(deftest test-StrategyLookAhead-3-of-7
  "Expects 3 of 7 remaining tokens to be taken, which guarantees a win."
  (let [game0 (reset-game)
        game1 (set-remaining-tokens game0 7)
        strategy (nim.player.StrategyLookAhead.)]
    (is (== (calculate-tokens-to-take strategy game1) 3))))

(deftest test-StrategyLookAhead-1-of-9
  "Expects 1 of 9 remaining tokens to be taken, which guarantees a win."
  (let [game0 (reset-game)
        game1 (set-remaining-tokens game0 9)
        strategy (nim.player.StrategyLookAhead.)]
    (is (== (calculate-tokens-to-take strategy game1) 1))))

(deftest test-build-decision-tree-final-turn
  "Expects 2 nodes."
  (let [game0 (reset-game)
        game1 (set-remaining-tokens game0 2)
        possible-choices (range 1 3)
        trees (build-decision-tree game1 possible-choices)]
    (is (== (count trees) (count possible-choices)))
    (is (== (:winning-descendants (second trees)) 1))
    (is (== (:losing-descendants (first trees)) 1))))

(deftest test-expand-decision-node-winning-move
  "Expects winning descendants to be incremented."
  (let [starting-player (first default-turn-taking-sequence)
        unexpanded-node (DecisionNode. starting-player (second default-turn-taking-sequence) 2 2 0 0)
        node (expand-decision-node starting-player default-max-number-of-tokens-to-take unexpanded-node)]
    (is (== (:winning-descendants node) 1))
    (is (is-final-move? node))))
