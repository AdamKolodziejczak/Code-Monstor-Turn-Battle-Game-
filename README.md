# Final Project - Turn-Based Battle Game
> Course: CSCI2020U Software Development and Integration

## Project Information
> Group 9: Grant, Hunter, Kolodziejczak, Jeanes

[Project Demo Video Link](https://www.youtube.com/watch?v=eNw2cJE6IRs)

Our team has designed a turn-based player vs player matchmaking game, called Coding Monster Game.  We created 6 different 'monsters' based various
different programming languages: C++, C, Java, JavaScript, Rust, Python and Assembly.  Each of the different programming
languages has different attributes such as their type, health, stamina, defence, attack, accuracy and skills.  Similar
to this, we added damage types in which each monster falls under - such as Unstructured, Procedural, Object-Oriented, 
and Functional - based on some of the different structures of programming languages.
We have also implemented different skills that are unique to each of the individual monsters.

Through development, we had run into a Race Condition problem, it happened when two people tried to interact with the
battle class at the same time.  We solved this condition using synchronized blocks.

## Game Features
- Turn-Based Monster Battling (Similar to Pokemon).
- Seven monsters with four types, randomly selected team of three for each player.
- Each monster has statistics such as Health, Attack, Defence, Stamina and Accuracy.
- Play against another users, once out of the queue, with a chat log of moves.
- Players can choose to attack, heal, rest, etc.
- Different skills for each monster; each skill has different statistics (seen when hovering the move).
- Custom sprites for each of the different monsters.
- Sound effects for action.
- Different ending page depending on if the player wins or loses.
- Players can play again once game is done (put into queue).
- Can have more than one battle room open at a time.

## How To Run:
1. Ensure GlassFish configurations are properly set up using `GlassFish 7.0.111 or newer` with the `exploded` deployment
2. Launch the GlassFish server
3. the `Home` webpage will open, you can go to our `About` page, or `Queue` page
4. To start a battle, Join the Queue
   1. Once you are in the `Queue` page, you are waiting for a second person to join this page
   2. Once a second person joins, you will be automatically re-directed to the `Battle` page
5. When you join the battle, you will be randomly selected 3 monsters to be on your team 
6. You can then choose one of the actions (attack, heal, etc)
7. Once one of your monsters 'crashes' (wanted to make it appropriate for school), you will not be allowed to switch to them
8. Once all 3 monsters crash, the respective players will be re-directed to a `Winner` and `Loser` pages.
9. On these pages you will be allowed to click the `Play Again` button which will re-direct you to the `Queue` page

## Resources
- Index.html background image:[Link Here](https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.vecteezy.com%2Fphoto%2F30606321-2d-hero-battle-pvp-arena-background-casual-game-art-design-ai-generative&psig=AOvVaw2gwDo_RCoI7AdqN-OdiWSb&ust=1713467990842000&source=images&cd=vfe&opi=89978449&ved=0CBIQjRxqFwoTCLDk6Oj7yYUDFQAAAAAdAAAAABAE)
- Queue.html background gif: [Link Here](https://www.artstation.com/artwork/14JOmZ)
- Winner.html background gif: [Link Here](https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.pinterest.com%2Fpin%2Fjust-one-ballet-class-in-2023--1120340844781138085%2F&psig=AOvVaw2_sK5AI95jkZDNxi1KYFAe&ust=1713540143087000&source=images&cd=vfe&opi=89978449&ved=0CBIQjhxqFwoTCIjG1M2IzIUDFQAAAAAdAAAAABAE)
- Loser.html background image: [Link Here](https://www.google.com/url?sa=i&url=https%3A%2F%2Fstock.adobe.com%2Fsearch%3Fk%3D%2522you%2Blose%2522&psig=AOvVaw0xjWIazedpE7FpLK4gPoSi&ust=1713540467900000&source=images&cd=vfe&opi=89978449&ved=0CBIQjRxqFwoTCMDcpOiJzIUDFQAAAAAdAAAAABAE)
- About.html background image: [Link Here](https://dribbble.com/shots/2303888-Lava-Animation)
- Helped with randomly generating a unique room code in QueueServer.java: [Link Here](https://ioflood.com/blog/java-uuid/#:~:text=There%20are%20various%20methods%20to,each%20time%20it%20is%20ran.)

## Contribution:

| Member      | Percentage Contribution| Work Description  |
| ------------- | ------------- | -------------|
|  Adam Kolodziejczak  | 25%  | I worked on backend features such as the queue for two users in queue.js and battle.js, the death of monsters in Battle.java and Battle.js, and other details in Java files such as Monster.java. I worked on front features such as adding backgrounds, sizing all pages, creating the queue.html and about.html, and fixing overlapping features. Also, I recorded the voice of the video and added resources and detail to the readme. |
| Andrew Hunter  | 25%  | I worked on game synchronization and logic including setting up the system for messaging between the server and client. I worked on the battle page, getting the monster cards displaying and hiding buttons when it is not your turn. I created and added sound effects to the project. Finally I fixed bugs including a couple of race conditions. |
|  Tyson Grant  | 25%  | In the front-end, I created each of the index.html, winner.html, loser.html, and helped largely within the battle.html as well as the about.html with resizing and positioning of all the elements.  In the backend, I created the code in Monster.java and Battle.java for the individual sprite images to be displayed in the battle arena with their respective monsters, added the server-end to handle what was done when each of the monsters died and the game ended, and helped generate unique IDs for the random rooms.  I created the README.md with all of the information and steps. |
| David Jeanes  | 25%  | For this project, I mainly worked on the backend with helping to create some of the Java classes such as Battle.java, Monster.java, and Skill.java. I created each of the skills the monsters can use and implemented their functionality such as the calculations to determine damage, accuracy, stat boosts and the attack multipliers for different types. I also created the lists of skills for each monster and tweaked the values for their various stats. For the frontend, I created the sprites that are displayed for each monster in battle, hand-drawing each one using Aseprite. |

NOTE: Each group member attended the several meetings we had and did a bit of everything. Each member openly helped others during meetings by suggesting ideas, finding solutions to problems, and debugging together.
