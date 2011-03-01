(ns nim.test.ctl
  (:use [nim.ctl] :reload)
  (:use [clojure.test]))

(deftest test-start-game
  "Expects opening message."
  (is (= (start-game) "There are 9 tokens.")))
