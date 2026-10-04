=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 1200 Game Project README
PennKey: kundama1
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. 2D Arrays: I used 2D arrays to keep track of all the bricks in the game board. Each Brick object is stored in the 2D array, and I can easily access any brick by its row and column index. This is an appropriate use of 2D arrays because the game board is naturally represented as a grid, and 2D arrays provide a convenient way to manage this grid structure.
  I was given the advice that I can't just remove an element from an array. So, I change the value to null. Since there are no other references to the brick, garbage collection deletes the brick object for me.

  2. Collections: I used a linkedlist to store the powerups since they can be added and removed at any time. The linkedlist allows for efficient insertion and deletion of powerups as they are collected by the player or expire after a certain time. This is an appropriate use of collections because the number of powerups can vary throughout the game, and a linkedlist provides the flexibility needed to manage this dynamic list. Also, only the starting element is removed at a time, which the linkedlist gives easy access to.

  3. File I/O: I used file I/O to save and load the game state. When the player saves the game, I write the current state of the game to a file. When the player loads a game, I read this information from the file and restore the game state accordingly. This is an appropriate use of file I/O because it allows players to save their progress and continue playing at a later time, which enhances the user experience.

  4. Inheritance and Subtyping: I made an exploding brick subclass for bricks that when hit, explode and destroy all their neighbors. This is the perfect use of this concept since exploding bricks are a type of brick, but they have additional behavior that regular bricks do not have. By using inheritance, I can reuse the common functionality of regular bricks while adding the unique behavior of exploding bricks without having to duplicate code.

===============================
=: File Structure Screenshot :=
===============================
- Include a screenshot of your project's file structure. This should include
  all of the files in your project, and the folders they are in. You can
  upload this screenshot in your homework submission to gradescope, named 
  "file_structure.png".

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.
I have a ball class for the ball that bounces around, a paddle class for the movable paddle which the player controls, a brick class for the bricks that the ball can hit and destroy, and a powerup class for the powerups that can be collected by the player. I also have a game class that manages the overall game state, including the game loop, collision detection, and user input.

- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?
I had one issue with collision detection. Because the hit object function uses willintersect, the ball bounced before it actually collided with the bricks, so the ball would bounce off the bricks and the bricks wouldn't be destroyed. I solved this by slightly enlargening the hitbox of the bricks like how they do in actual video games.

- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?
I think there is a good separation of functionality in terms of the different classes. I think the gameplay loop is a bit long, so I would try to break that into different modules if I could find a way to do that. I think private state is encapsulated well.


========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used 
  while implementing your game.
https://play-lh.googleusercontent.com/UmfWCkYcVm1a0OMZjpP-d4Q_YQ7SpxhwJpSN9OBmyJi5qRYZks4OvsaX0a95oNEAJfiT
That image for reference.