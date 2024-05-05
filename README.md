## Developed a [<ins>Collect board game</ins>](https://boardgamegeek.com/boardgame/260926/collecto) using Client/server design pattern in Java using:

OOP programming paradigm: Class & Object, Abstraction, Encapsulation, Inheritance, Polymorphism, interface, enum,\
Locks & Synchronization for Concurrency management, Sockets & Networking, Exception handling and jUnit testing.

### How to initiate the server?
To initiate the server, navigate to the 'server' directory and run the CollectoServerTUI.java file in your terminal. 

When prompted, input a port number (e.g., 81) and press enter to proceed.

### How to start the game from the client side?

To begin the game from the client side, first, navigate to the 'client' directory and execute the 'CollectoClientTUI.java' file in your terminal. Then, input the server address along with the port number. If the server is hosted locally, you can use '127.0.0.1' to connect.

Next, perform a handshake with the server by entering 'HELLO~[some description]'. This establishes communication.

After the handshake, log in to the system by entering 'LOGIN~[your name]'. Once logged in, enter 'QUEUE' to wait for the game to start.

Once there are at least two players in the queue, a game session will commence.

To move the balls, you'll use the 'MOVE~[number]' command, where [number] corresponds to the row or column containing the balls you want to move. 

You can use 'HINT' to receive a hint for making next move.

Wire protocols such as HELLO, LOGIN, QUEUE, and MOVE are use to establish communication and interact with the game server.



