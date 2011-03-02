(ns nim.test.player
  (:use [nim.core] :reload)
  (:use [nim.player] :reload)
  (:use [clojure.test]))

(deftest test-StrategyTakeMax-with-many-tokens
  "Expects maximum allowed tokens to be taken."
  (let [game (reset-game)
        strategy (nim.player.StrategyTakeMax.)]
    (is (== (calculate-tokens-to-take strategy game) (get-max-tokens-to-take game)))))

(deftest test-StrategyTakeMax-not-enough-tokens
  "Expects all remaining tokens to be taken."
  (let [game0 (reset-game)
        game1 (set-remaining-tokens (- (get-max-tokens-to-take game0) 1) game0)
        strategy (nim.player.StrategyTakeMax.)]
    (is (== (calculate-tokens-to-take strategy game1) (get-remaining-tokens game1)))))

(deftest test-StrategyTake1
  "Expects always one token to be taken."
  (let [game (reset-game)
        strategy (nim.player.StrategyTake1.)]
    (is (== (calculate-tokens-to-take strategy game) 1))))

