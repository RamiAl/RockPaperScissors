# Rock Paper Scissors

### To get started 
Open the terminal in the target directory in the project and run the it with:
```
java -jar RockPaperScissors-0.0.1-SNAPSHOT.jar
```

### How to play (using Postman)
##### Create game:
```
http://localhost:8080/api/games
```
- Type of request: POST

The body of the request should contain the name of the player:
```
{
    "name": "<playerName>"
}
```
In return you will get id of the game you just created so your friend can ues it to join the game.

##### Join the game
```
http://localhost:8080/api/games/<gameId>/join
```
- Type of request: POST

The body of the request should also contain this json object:
```
{
    "name": "<playerName>"
}
```

##### Play
To play (make a move) this game, you and your friend:

```
http://localhost:8080/api/games/<gameId>/move
```
- Type of request: POST

The body of the request should contain the name of the player:
```
{
	"name" : "<playerName>",
	"move" : "<move>"
}
```
The move has to be "rock", "paper" or "scissors".

##### Result 
To see the result or the state of the game:
```
http://localhost:8080/api/games/<gameId>
```
- Type of request: GET


###### Have fun :)
