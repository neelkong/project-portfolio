# valueIterationAgents.py
# -----------------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


# valueIterationAgents.py
# -----------------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


import mdp, util

from learningAgents import ValueEstimationAgent
import collections

class ValueIterationAgent(ValueEstimationAgent):
    """
        * Please read learningAgents.py before reading this.*

        A ValueIterationAgent takes a Markov decision process
        (see mdp.py) on initialization and runs value iteration
        for a given number of iterations using the supplied
        discount factor.
    """
    def __init__(self, mdp, discount = 0.9, iterations = 100):
        """
          Your value iteration agent should take an mdp on
          construction, run the indicated number of iterations
          and then act according to the resulting policy.

          Some useful mdp methods you will use:
              mdp.getStates()
              mdp.getPossibleActions(state)
              mdp.getTransitionStatesAndProbs(state, action)
              mdp.getReward(state, action, nextState)
              mdp.isTerminal(state)
        """
        self.mdp = mdp
        self.discount = discount
        self.iterations = iterations
        self.values = util.Counter() # A Counter is a dict with default 0
        self.runValueIteration()

    def runValueIteration(self):
        # Write value iteration code here
        "* YOUR CODE HERE *"
        s = self.mdp.getStates()
        count = 0
        toAdd = []
        while count < self.iterations:
            for state in s:
                qValues = []
                actions = self.mdp.getPossibleActions(state)
                for a in actions:
                    qV = self.computeQValueFromValues(state, a)
                    qValues.append(qV)
                m = max(qValues, default=0)
                toAdd.append((state, m))
            for x in toAdd:
                self.values[x[0]] = x[1]
            count += 1

    def getValue(self, state):
        """
          Return the value of the state (computed in _init_).
        """
        return self.values[state]


    def computeQValueFromValues(self, state, action):
        """
          Compute the Q-value of action in state from the
          value function stored in self.values.
        """
        "* YOUR CODE HERE *"
        values = []
        sum = 0
        nextStates = self.mdp.getTransitionStatesAndProbs(state, action)
        for branch in nextStates:
            v = branch[1] * (self.mdp.getReward(state, action, branch[0]) + self.discount * self.values[branch[0]])
            sum += v
        return sum

    def computeActionFromValues(self, state):
        """
          The policy is the best action in the given state
          according to the values currently stored in self.values.

          You may break ties any way you see fit.  Note that if
          there are no legal actions, which is the case at the
          terminal state, you should return None.
        """
        "* YOUR CODE HERE *"
        if (self.mdp.isTerminal(state)):
            return None
        moves = self.mdp.getPossibleActions(state)
        values = []
        for m in moves:
            v = self.computeQValueFromValues(state, m)
            values.append(v)
        maxValue = max(values)
        index = values.index(maxValue)
        return moves[index]

    def getPolicy(self, state):
        return self.computeActionFromValues(state)

    def getAction(self, state):
        "Returns the policy at the state (no exploration)."
        return self.computeActionFromValues(state)

    def getQValue(self, state, action):
        return self.computeQValueFromValues(state, action)

class AsynchronousValueIterationAgent(ValueIterationAgent):
    """
        * Please read learningAgents.py before reading this.*

        An AsynchronousValueIterationAgent takes a Markov decision process
        (see mdp.py) on initialization and runs cyclic value iteration
        for a given number of iterations using the supplied
        discount factor.
    """
    def __init__(self, mdp, discount = 0.9, iterations = 1000):
        """
          Your cyclic value iteration agent should take an mdp on
          construction, run the indicated number of iterations,
          and then act according to the resulting policy. Each iteration
          updates the value of only one state, which cycles through
          the states list. If the chosen state is terminal, nothing
          happens in that iteration.

          Some useful mdp methods you will use:
              mdp.getStates()
              mdp.getPossibleActions(state)
              mdp.getTransitionStatesAndProbs(state, action)
              mdp.getReward(state)
              mdp.isTerminal(state)
        """
        ValueIterationAgent.__init__(self, mdp, discount, iterations)

    def runValueIteration(self):
        counter = 0
        states = self.mdp.getStates()
        while counter < self.iterations:
            curr_state = states[counter % len(states)]
            if( not self.mdp.isTerminal(curr_state)):
                qValues = []
                actions = self.mdp.getPossibleActions(curr_state)
                for a in actions:
                    qV = self.computeQValueFromValues(curr_state, a)
                    qValues.append(qV)
                self.values[curr_state] = max(qValues)
            counter += 1

class PrioritizedSweepingValueIterationAgent(AsynchronousValueIterationAgent):
    """
        * Please read learningAgents.py before reading this.*

        A PrioritizedSweepingValueIterationAgent takes a Markov decision process
        (see mdp.py) on initialization and runs prioritized sweeping value iteration
        for a given number of iterations using the supplied parameters.
    """
    def __init__(self, mdp, discount = 0.9, iterations = 100, theta = 1e-5):
        """
          Your prioritized sweeping value iteration agent should take an mdp on
          construction, run the indicated number of iterations,
          and then act according to the resulting policy.
        """
        self.theta = theta
        ValueIterationAgent.__init__(self, mdp, discount, iterations)

    def runValueIteration(self):
        "* YOUR CODE HERE *"
        queue = util.PriorityQueue()
        predecessors = set()
        #store tuples of (curr_state, pred)
        s = self.mdp.getStates()
        for state in s:
            if( not self.mdp.isTerminal(state)):
                actions = self.mdp.getPossibleActions(state)
                for a in actions:
                    transitions = self.mdp.getTransitionStatesAndProbs(state, a)
                    #returns (nextState, prob)
                    for transition in transitions:
                        predecessors.add((transition[0], state))
        for state in s:
            if(not self.mdp.isTerminal(state)):
                curr = self.values[state]
                max_action = self.computeActionFromValues(state)
                m = self.computeQValueFromValues(state, max_action)
                diff = abs(curr - m) * -1
                queue.push(state, diff)
        for iteration in range(self.iterations):
            if queue.isEmpty():
                return
            state = queue.pop()
            if not self.mdp.isTerminal(state):
                max_action = self.computeActionFromValues(state)
                v = self.computeQValueFromValues(state, max_action)
                self.values[state] = v
            for predPair in predecessors:
                if(predPair[0] == state):
                    #Find max Qvalue
                    p = predPair[1]
                    max_action = self.computeActionFromValues(p)
                    if(not self.mdp.isTerminal(p)):
                        m = self.computeQValueFromValues(p, max_action)
                        difference = abs(self.values[p] - m)
                        if(difference > self.theta):
                            queue.update(p, difference * -1)