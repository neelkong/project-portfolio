# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer:

My answer was much less general and I realized there were some certain cases my solution could not actually handle like the provided implementation could. I should trust the process of the algorithm and just focus on the top level, general case.

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer:

Like when tessellating hexagons, during your random generation you need to ensure walls of a room or hallway do not collide with or into another room or hallway. The hexagon is the room or hallway and the tesselation is the placement of them or their walls.

**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer:

I would first work on a helper method that randomly generated rooms or hallways.

**What distinguishes a hallway from a room? How are they similar?**

Answer:

Both a hallway and a room both have walls. However, a hallway should be narrower than a room.
