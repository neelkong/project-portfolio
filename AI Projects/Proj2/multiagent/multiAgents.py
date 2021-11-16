# multiAgents.py
# --------------
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


from util import manhattanDistance
from game import Directions
import random, util

from game import Agent

class ReflexAgent(Agent):
    """
    A reflex agent chooses an action at each choice point by examining
    its alternatives via a state evaluation function.

    The code below is provided as a guide.  You are welcome to change
    it in any way you see fit, so long as you don't touch our method
    headers.
    """


    def getAction(self, gameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {NORTH, SOUTH, WEST, EAST, STOP}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices) # Pick randomly among the best

        "Add more of your code here if you want to"

        return legalMoves[chosenIndex]

    def evaluationFunction(self, currentGameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        food_distances = []
        newFood = successorGameState.getFood()
        ### Find distance to closest food
        for food in newFood.asList():
            food_distances.append(manhattanDistance(newPos, food))
        if ( len(food_distances) != 0):
            food_value = 1 / min(food_distances)
        newGhostStates = successorGameState.getGhostStates()
        ghost_distances = []
        for ghost in newGhostStates:
            ghost_distances.append(manhattanDistance(newPos, ghost.getPosition()))
        ghost_value = min(ghost_distances)
        newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]
        if ( len(food_distances) == 0):
            return 1000
        if (ghost_value > 0):
            return 10 * food_value - 1 / (ghost_value) + 1 / len(newFood.asList()) + successorGameState.getScore()
        else:
            return 0

def scoreEvaluationFunction(currentGameState):
    """
    This default evaluation function just returns the score of the state.
    The score is the same one displayed in the Pacman GUI.

    This evaluation function is meant for use with adversarial search agents
    (not reflex agents).
    """
    return currentGameState.getScore()

class MultiAgentSearchAgent(Agent):
    """
    This class provides some common elements to all of your
    multi-agent searchers.  Any methods defined here will be available
    to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

    You *do not* need to make any changes here, but you can if you want to
    add functionality to all your adversarial search agents.  Please do not
    remove anything, however.

    Note: this is an abstract class: one that should not be instantiated.  It's
    only partially specified, and designed to be extended.  Agent (game.py)
    is another abstract class.
    """

    def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)

class MinimaxAgent(MultiAgentSearchAgent):
    """
    Your minimax agent (question 2)
    """

    def getAction(self, gameState):
        """
        Returns the minimax action from the current gameState using self.depth
        and self.evaluationFunction.

        Here are some method calls that might be useful when implementing minimax.

        gameState.getLegalActions(agentIndex):
        Returns a list of legal actions for an agent
        agentIndex=0 means Pacman, ghosts are >= 1

        gameState.generateSuccessor(agentIndex, action):
        Returns the successor game state after an agent takes an action

        gameState.getNumAgents():
        Returns the total number of agents in the game

        gameState.isWin():
        Returns whether or not the game state is a winning state

        gameState.isLose():
        Returns whether or not the game state is a losing state
        """
        "*** YOUR CODE HERE ***"
        pacmanActions = gameState.getLegalActions(0)
        actionRewards = []
        for action in pacmanActions:
            nextState = gameState.generateSuccessor(0, action)
            actionRewards.append(self.minimaxSearch(self.depth, nextState, 1))
        #print(pacmanActions[actionRewards.index(max(actionRewards))])
        return pacmanActions[actionRewards.index(max(actionRewards))]
        
    def minimaxSearch(self, depthCounter, gameState, agentIndex):
        currAgentMoves = gameState.getLegalActions(agentIndex)
        if((depthCounter == 0 and agentIndex == 0) or gameState.isWin() or gameState.isLose() or len(currAgentMoves) == 0):
            return self.evaluationFunction(gameState)
        totalAgents = gameState.getNumAgents()
        actionValues = []
        if(agentIndex == totalAgents - 1):
            actionValues = [self.minimaxSearch(depthCounter - 1, (gameState.generateSuccessor(agentIndex, move)), 0) for move in currAgentMoves]
        else:
            actionValues = [self.minimaxSearch(depthCounter, (gameState.generateSuccessor(agentIndex, move)), agentIndex + 1) for move in currAgentMoves]
        if( agentIndex == 0):
            return max(actionValues)
        else:
            return min(actionValues)
            

class AlphaBetaAgent(MultiAgentSearchAgent):
    """
    Your minimax agent with alpha-beta pruning (question 3)
    """

    def getAction(self, gameState):
        """
        Returns the minimax action using self.depth and self.evaluationFunction
        """
        "*** YOUR CODE HERE ***"
        alpha = float("-inf")
        beta = float("inf")
        pacmanActions = gameState.getLegalActions(0)
        value = float("-inf")
        retAction = None
        for action in pacmanActions:
            oldValue = value
            nextState = gameState.generateSuccessor(0, action)
            value = max(value, self.ABSearch(self.depth, nextState, 1, alpha, beta))
            if(oldValue != value):
                retAction = action
            if(value > beta):
                return retAction
            alpha = max(alpha, value)
        print(retAction)
        return retAction
        
    def ABSearch(self, depthCounter, gameState, agentIndex, alpha, beta):
        currAgentMoves = gameState.getLegalActions(agentIndex)
        if((depthCounter == 0 and agentIndex == 0) or gameState.isWin() or gameState.isLose() or len(currAgentMoves) == 0):
           return self.evaluationFunction(gameState)
        totalAgents = gameState.getNumAgents()
        if(agentIndex == totalAgents - 1):
            index = 0
            decrement = 1
        else:
            index = agentIndex + 1
            decrement = 0
        if(agentIndex == 0):
            value = float("-inf")
            for move in currAgentMoves:
                value = max(value, self.ABSearch(depthCounter - decrement, (gameState.generateSuccessor(agentIndex, move)), index, alpha, beta))
                if(value > beta):
                    return value
                alpha = max(alpha, value)
            return value
        else:
            value = float("inf")
            for move in currAgentMoves:
                value = min(value, self.ABSearch(depthCounter - decrement, (gameState.generateSuccessor(agentIndex, move)), index, alpha, beta))
                if(value < alpha):
                    return value
                beta = min(value, beta)
            return value

class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def getAction(self, gameState):
        """
        Returns the expectimax action using self.depth and self.evaluationFunction

        All ghosts should be modeled as choosing uniformly at random from their
        legal moves.
        """
        "*** YOUR CODE HERE ***"
        pacmanActions = gameState.getLegalActions(0)
        actionRewards = []
        for action in pacmanActions:
            nextState = gameState.generateSuccessor(0, action)
            actionRewards.append(self.expectimaxSearch(self.depth, nextState, 1))
        print(pacmanActions[actionRewards.index(max(actionRewards))])
        return pacmanActions[actionRewards.index(max(actionRewards))]

    def expectimaxSearch(self, depthCounter, gameState, agentIndex):
        currAgentMoves = gameState.getLegalActions(agentIndex)
        if((depthCounter == 0 and agentIndex == 0) or gameState.isWin() or gameState.isLose() or len(currAgentMoves) == 0):
            return self.evaluationFunction(gameState)
        totalAgents = gameState.getNumAgents()
        actionValues = []
        if(agentIndex == totalAgents - 1):
            actionValues = [self.expectimaxSearch(depthCounter - 1, (gameState.generateSuccessor(agentIndex, move)), 0) for move in currAgentMoves]
        else:
            actionValues = [self.expectimaxSearch(depthCounter, (gameState.generateSuccessor(agentIndex, move)), agentIndex + 1) for move in currAgentMoves]
        if( agentIndex == 0):
            return max(actionValues)
        else:
            sum = 0
            for value in actionValues:
                sum += value
            return sum * (1/ (len(actionValues)))
def betterEvaluationFunction(currentGameState):
    """
    Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
    evaluation function (question 5).

    DESCRIPTION: <write something here so we know what you did>
    """
    "*** YOUR CODE HERE ***"
    food = currentGameState.getFood()
    pos = currentGameState.getPacmanPosition()
    ### Find distance to closest food
    ghostStates = currentGameState.getGhostStates()
    newScaredTimes = [ghostState.scaredTimer for ghostState in ghostStates]
    food_distances = []
    for foods in food.asList():
        food_distances.append(manhattanDistance(pos, foods))
    if ( len(food_distances) != 0):
        food_value = 1 / min(food_distances)
    ghost_distances = []
    for ghost in ghostStates:
        ghost_distances.append(manhattanDistance(pos, ghost.getPosition()))
    ghost_value = min(ghost_distances)
    if ( len(food_distances) == 0):
        return 1000
    if (ghost_value > 0):
        return 10 * food_value - 1 / (ghost_value) + 2 * (1 / len(food.asList())) + currentGameState.getScore()
    else:
        return 0


# Abbreviation
better = betterEvaluationFunction
