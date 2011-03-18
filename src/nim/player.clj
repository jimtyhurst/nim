(ns nim.player
  (:use [nim.core] :reload))

;; Automated player implements logic to make move in the game of Nim.

(defprotocol TurnTakerStrategy
  "Specifies an algorithm for calculating how many tokens to take for a turn in the game. Assumes there is at least one token remaining to be taken."
  (calculate-tokens-to-take [strategy game] "Returns the number of tokens recommended that the current player should take."))

;; Always takes just 1 token.
(defrecord StrategyTake1 []
  TurnTakerStrategy
  (calculate-tokens-to-take [this game] 1))

;; Always takes as many tokens as possible.
(defrecord StrategyTakeMax []
  TurnTakerStrategy
  (calculate-tokens-to-take
   [this game]
   (get-max-tokens-to-take-this-turn game)))

(defprotocol IDecisionNode
  "Holds state for a possible move in a game."
  (set-winning-descendants [this n] "Returns a node with the int number of winning descendants updated.")
  (set-losing-descendants [this n] "Returns a node with the int number of losing descendants updated.")
  (is-final-move? [this] "Returns true if there are no more tokens remaining after this move.")
  (get-winning-percentage [this] "Returns ratio of winning descendants to total descendants."))

(defrecord DecisionNode [player opponent tokens-remaining tokens-taken winning-descendants losing-descendants]
  IDecisionNode
  (set-winning-descendants
   [this n]
   (DecisionNode. player opponent tokens-remaining tokens-taken n losing-descendants))
  (set-losing-descendants
   [this n]
   (DecisionNode. player opponent tokens-remaining tokens-taken winning-descendants n))
  (is-final-move? [this] (== 0 (- tokens-remaining tokens-taken)))
  (get-winning-percentage
   [this]
   (cond (and (== 0 winning-descendants) (== 0 losing-descendants))
         ;; cannot divide by zero
         0
         :else
         ;; calculate ratio of winners
         (/ (float winning-descendants) (float (+ winning-descendants losing-descendants))))))

(defn count-descendants
  "Sums total number of winning or losing descendants across all nodes."
  [descendant-accessor all-nodes]
  (reduce + (map (fn [node] (descendant-accessor node)) all-nodes)))

(def expand-decision-node)

;; Builds a node for each of the possible choices,
;; then recursively calls expand-decision-node.
(defn expand-possible-choices
  "Returns list of decision nodes, one for each possible choice."
  [starting-player max-tokens-to-take player opponent remaining-tokens possible-token-choices]
  (map
   (fn [n]
     (expand-decision-node
      starting-player
      max-tokens-to-take
      (DecisionNode. player opponent remaining-tokens n 0 0)))
   possible-token-choices))

;; Counts winning and losing descendants.
;; In case where this is not a leaf node, makes recursive call
;; to expand-possible-choices for all of the possible choices
;; that exist from this point in the game.
(defn expand-decision-node
  "Returns node that has been searched to determine numbers of winning descendants and losing descendants."
  [starting-player max-tokens-to-take node]
  (cond (is-final-move? node) ;; reached leaf node
        (if (= starting-player (:player node))
          (set-winning-descendants node 1)
          (set-losing-descendants node 1)
          )
        :else ;; build descendants and count their winners and losers
        (let [tokens-remaining (- (:tokens-remaining node) (:tokens-taken node))
              possible-choices (range 1 (+ 1 (min tokens-remaining max-tokens-to-take)))
              child-nodes (expand-possible-choices starting-player max-tokens-to-take (:opponent node) (:player node) tokens-remaining possible-choices)]
          (set-losing-descendants
           (set-winning-descendants node (count-descendants :winning-descendants child-nodes))
           (count-descendants :losing-descendants child-nodes)))))

;; Convenience function to get the recursion started.
(defn build-decision-tree
  "Returns list of possible moves, where each move is an exhaustive decision tree of all possible moves, starting from current game position"
  [game possible-token-choices]
  (expand-possible-choices
   (get-next-player game)
   (get-max-tokens-to-take game)
   (get-next-player game)
   (get-opponent game)
   (get-remaining-tokens game)
   possible-token-choices))

(defn- max-node
  "Returns the node that has the highest percentage of winning descendants."
  [node1 node2]
  (if (> (get-winning-percentage node1) (get-winning-percentage node2))
    node1
    node2))

(defn choose-best-move
  "Returns the node that has the highest percentage of winning descendants."
  [possible-moves]
  (cond
   (or (nil? possible-moves) (empty? possible-moves))
   nil
   :else
   (reduce max-node possible-moves)))

;; Looks ahead to all possible completions from the current
;; game position and chooses next move based on percentage
;; of winning descendants.
(defrecord StrategyLookAhead []
  TurnTakerStrategy
  (calculate-tokens-to-take
   [this game]
   (let [possible-token-choices (range 1 (+ 1 (get-max-tokens-to-take-this-turn game)))
         best-move (choose-best-move (build-decision-tree game possible-token-choices))]
     (if (nil? best-move)
       0
       (:tokens-taken best-move)))))

;; Sets a default strategy.
(def turn-taker-strategy (StrategyLookAhead.))

(defn auto-take-turn [game]
  "Returns a game after taking a turn for the current player."
  (take-turn game (calculate-tokens-to-take turn-taker-strategy game)))
