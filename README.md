﻿# Checkers-Neuroevolution

Implementation of Neuroevolution algorithm for training of an agent to play Checkers.
At the first generation, the agents start with a fixed topology Neural Network with random weights. The one which had the highest score is chosen to create the next generation of agents which will have a copy Neural Network of the best previous generation agent but with a random mutation. 

A Neural Network is used to look at the board state and give a evaluation of the board. To decide a move the agent uses the Alpha-beta pruning to see which will be the state of the board in next few timesteps, given the evaluation of the boards, it chooses the best move.

First the batch of agents plays againts a existant bot, which was tested to play like an advanced player due to using Alpha-beta to check the next 8 steps. When the agents are successfully able to win against the bot, they play against each other to further improve their mastery.

To give a score to the agent, the agent plays against the bot with different Alpha Beta depth values, from 1-8. The agent with the highest winnings is chosen to create the next generation.

![image](https://user-images.githubusercontent.com/5073663/55118646-ea976000-50e6-11e9-8a53-c6c73390923e.png)

#### Note
Initial vanilla checkers game based on [this repository.](https://github.com/AshishPrasad/Checkers)
