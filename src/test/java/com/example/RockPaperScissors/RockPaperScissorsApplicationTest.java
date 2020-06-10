package com.example.RockPaperScissors;

import com.example.RockPaperScissors.entities.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class RockPaperScissorsApplicationTest {

    //IMPORTANT!
    //Go to Game class and set id to "22" otherwise the tests will not pass

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @AfterEach
    public void deleteAllGames() {
        this.restTemplate.delete("http://localhost:" + port + "/api/games");
    }

    @Test
    public void createGameWithPlayerName() {
        Player player = new Player("Rami");
        ResponseEntity<String> res = createGameReq(player);
        assertThat(res.getBody()).contains("Game created with id: 22");
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void createGameWithNoPlayerName() {
        Player player = new Player();
        ResponseEntity<String> res = createGameReq(player);
        assertThat(res.getBody()).contains("Name property is required");
        assertThat(res.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void joinGameWithCorrectNameAndGameId() {
        Player playerOne = new Player("Rami");
        Player playerTwo = new Player("Ted");

        ResponseEntity<String> res = createAndJoinGame(playerOne, playerTwo, "22");
        assertThat(res.getBody()).contains("Welcome to the game Ted");
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void joinGameWithIncorrectId() {
        Player playerOne = new Player("Rami");
        Player playerTwo = new Player("Ted");

        ResponseEntity<String> res = createAndJoinGame(playerOne, playerTwo, "3333");
        assertThat(res.getBody()).contains("There is no game with this id");
        assertThat(res.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void joinGameWithNoName() {
        Player playerOne = new Player("Rami");
        Player playerTwo = new Player();

        ResponseEntity<String> res = createAndJoinGame(playerOne, playerTwo, "22");
        assertThat(res.getBody()).contains("Name property is required");
        assertThat(res.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void joinGameWithTakenName() {
        Player playerOne = new Player("Rami");
        Player playerTwo = new Player("Rami");

        ResponseEntity<String> res = createAndJoinGame(playerOne, playerTwo, "22");
        assertThat(res.getBody()).contains(playerTwo.getName() + " is already taken");
        assertThat(res.getStatusCodeValue()).isEqualTo(406);
    }

    public ResponseEntity<String> createAndJoinGame(Player playerOne, Player playerTwo, String gameId){
        createGameReq(playerOne);
        return joinGameReq(playerTwo, gameId);
    }

    @Test
    public void makeCorrectMove (){
        Player playerOne = new Player("Rami");
        Player playerTwo = new Player("Ted");

        createAndJoinGame(playerOne, playerTwo, "22");

        playerOne.setMove("paper");
        playerTwo.setMove("rock");

        ResponseEntity<String> playerOneRes = makeMoveReq(playerOne, "22");
        assertThat(playerOneRes.getBody()).contains("Go see your result " + playerOne.getName());
        assertThat(playerOneRes.getStatusCodeValue()).isEqualTo(200);

        ResponseEntity<String> playerTwoRes = makeMoveReq(playerTwo, "22");
        assertThat(playerTwoRes.getBody()).contains("Go see your result " + playerTwo.getName());
        assertThat(playerTwoRes.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void makeMoveBeforeOtherPlayerJoin (){
        Player playerOne = new Player("Rami");
        createGameReq(playerOne);
        playerOne.setMove("rock");
        makeMoveReq(playerOne, "22");

        ResponseEntity<String> res = this.restTemplate.postForEntity("http://localhost:" + port +
                        "/api/games/22/move", playerOne, String.class);

        assertThat(res.getBody()).contains("The other player has not joined the game yet");
        assertThat(res.getStatusCodeValue()).isEqualTo(406);
    }

    @Test
    public void makeIncorrectMove (){
        Player playerOne = new Player("Rami");
        Player playerTwo = new Player("Ted");

        createAndJoinGame(playerOne, playerTwo, "22");

        playerOne.setMove("paper");
        playerTwo.setMove("Hello");

        ResponseEntity<String> playerOneRes = makeMoveReq(playerOne, "22");
        assertThat(playerOneRes.getBody()).contains("Go see your result " + playerOne.getName());
        assertThat(playerOneRes.getStatusCodeValue()).isEqualTo(200);

        ResponseEntity<String> playerTwoRes = makeMoveReq(playerTwo, "22");
        assertThat(playerTwoRes.getBody()).contains("incorrect move");
        assertThat(playerTwoRes.getStatusCodeValue()).isEqualTo(406);
    }

    @Test
    public void makeMoveWithInvalidName (){
        Player playerOne = new Player("Rami");
        Player playerTwo = new Player("Ted");

        createAndJoinGame(playerOne, playerTwo, "22");

        playerOne.setMove("paper");
        playerOne.setName("Gustav");

        playerTwo.setMove("rock");

        ResponseEntity<String> playerOneRes = makeMoveReq(playerOne, "22");
        assertThat(playerOneRes.getBody()).contains("There is no player with this name or game with this id");
        assertThat(playerOneRes.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void seeResultWithInvalidId() {
        Player playerOne = new Player("Rami");
        Player playerTwo = new Player("Ted");
        String playerOneMove= "paper";
        String playerTwoMove= "rock";

        playGame(playerOne, playerTwo, playerOneMove, playerTwoMove);

        ResponseEntity<String> playerOneRes = this.restTemplate.getForEntity("http://localhost:" +
                port + "/api/games/2333332", String.class);
        assertThat(playerOneRes.getBody()).contains("There is no game with this id or it's finished");
        assertThat(playerOneRes.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void seeResultBeforeBothPlayersMadeMove() {
        Player playerOne = new Player("Rami");
        Player playerTwo = new Player("Ted");

        createAndJoinGame(playerOne, playerTwo, "22");
        playerOne.setMove("paper");

        ResponseEntity<String> playerOneRes = makeMoveReq(playerOne, "22");
        assertThat(playerOneRes.getBody()).contains("Go see your result " + playerOne.getName());
        assertThat(playerOneRes.getStatusCodeValue()).isEqualTo(200);

        ResponseEntity<String> res = this.restTemplate.getForEntity("http://localhost:" + port + "/api/games/22",
                String.class);
        assertThat(res.getBody()).contains("The other player has not made a move yet");
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void ThirdPlayerWantJoin() {
        Player playerOne = new Player("Rami");
        Player playerTwo = new Player("Ted");
        Player playerThree = new Player("Johan");

        createAndJoinGame(playerOne, playerTwo, "22");

        ResponseEntity<String> playerOneRes = joinGameReq(playerThree, "22");
        assertThat(playerOneRes.getBody()).contains("No more place for another player, 2 maximum.");
        assertThat(playerOneRes.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void playerOneWinner() {
        Player playerOne = new Player("Rami");
        Player playerTwo = new Player("Ted");
        String playerOneMove= "paper";
        String playerTwoMove= "rock";

        playGame(playerOne, playerTwo, playerOneMove, playerTwoMove);

        ResponseEntity<String> res = getResultReq("22");
        assertThat(res.getBody()).contains("Rami's move: paper\n" +
                "Ted's move: rock\n" +
                "The winner is Rami");
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void playerTwoWinner() {
        Player playerOne = new Player("Rafat");
        Player playerTwo = new Player("Johan");
        String playerOneMove= "paper";
        String playerTwoMove= "scissors";

        playGame(playerOne, playerTwo, playerOneMove, playerTwoMove);
        ResponseEntity<String> res = getResultReq("22");
        assertThat(res.getBody()).contains("Rafat's move: paper\n" +
                "Johan's move: scissors\n" +
                "The winner is Johan");
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void playerTwoLost() {
        Player playerOne = new Player("Rami");
        Player playerTwo = new Player("Ted");
        String playerOneMove= "rock";
        String playerTwoMove= "scissors";

        playGame(playerOne, playerTwo, playerOneMove, playerTwoMove);
        ResponseEntity<String> res = getResultReq("22");
        assertThat(res.getBody()).contains("Rami's move: rock\n" +
                "Ted's move: scissors\n" +
                "The winner is Rami");
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void draw() {
        Player playerOne = new Player("Rami");
        Player playerTwo = new Player("Ted");
        String playerOneMove= "paper";
        String playerTwoMove= "paper";

        playGame(playerOne, playerTwo, playerOneMove, playerTwoMove);
        ResponseEntity<String> res = getResultReq("22");
        assertThat(res.getBody()).contains("Rami's move: paper\n" +
                "Ted's move: paper\n" +
                "It's a draw guys");
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void joinSameGameAfterItsFinish() {
        Player playerOne = new Player("Rami");
        Player playerTwo = new Player("Ted");
        String playerOneMove= "paper";
        String playerTwoMove= "paper";

        playGame(playerOne, playerTwo, playerOneMove, playerTwoMove);
        ResponseEntity<String> res = createAndJoinGame(playerOne, playerTwo, "22");
        assertThat(res.getBody()).contains("The game with this id is finished.");
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void makeMoveAfterGameIsFinish() {
        Player playerOne = new Player("Rami");
        Player playerTwo = new Player("Ted");
        String playerOneMove= "paper";
        String playerTwoMove= "paper";

        playGame(playerOne, playerTwo, playerOneMove, playerTwoMove);
        ResponseEntity<String> playerOneRes = makeMoveReq(playerOne, "22");
        assertThat(playerOneRes.getBody()).contains("The game with this id is finished");
        assertThat(playerOneRes.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void playerOneWithTwoMoves() {
        Player playerOne = new Player("Rami");
        Player playerTwo = new Player("Ted");

        createAndJoinGame(playerOne, playerTwo, "22");
        playerOne.setMove("rock");

        ResponseEntity<String> playerOneRes = makeMoveReq(playerOne, "22");
        assertThat(playerOneRes.getBody()).contains("Go see your result " + playerOne.getName());
        assertThat(playerOneRes.getStatusCodeValue()).isEqualTo(200);

        ResponseEntity<String> playerOneRes2 = makeMoveReq(playerOne, "22");
        assertThat(playerOneRes2.getBody()).contains("You have no more moves");
        assertThat(playerOneRes2.getStatusCodeValue()).isEqualTo(200);
    }

    public ResponseEntity<String> getResultReq(String gameId){
        return this.restTemplate.getForEntity("http://localhost:" + port + "/api/games/" + gameId,
                String.class);
    }

    public void playGame(Player playerOne, Player playerTwo, String playerOneMove, String playerTwoMove){
        createAndJoinGame(playerOne, playerTwo, "22");

        playerOne.setMove(playerOneMove);
        playerTwo.setMove(playerTwoMove);

        ResponseEntity<String> playerOneRes = makeMoveReq(playerOne, "22");
        assertThat(playerOneRes.getBody()).contains("Go see your result " + playerOne.getName());
        assertThat(playerOneRes.getStatusCodeValue()).isEqualTo(200);

        ResponseEntity<String> playerTwoRes = makeMoveReq(playerTwo, "22");
        assertThat(playerTwoRes.getBody()).contains("Go see your result " + playerTwo.getName());
        assertThat(playerTwoRes.getStatusCodeValue()).isEqualTo(200);
    }

    public ResponseEntity<String> makeMoveReq(Player player, String gameId) {
        return this.restTemplate.postForEntity("http://localhost:" + port + "/api/games/" + gameId +"/move",
                player, String.class);
    }

    public ResponseEntity<String> createGameReq(Player player) {
        return this.restTemplate.postForEntity("http://localhost:" + port + "/api/games", player, String.class);

    }

    public ResponseEntity<String> joinGameReq(Player player, String gameId) {
        return this.restTemplate.postForEntity("http://localhost:" + port + "/api/games/"+gameId+"/join", player,
                String.class);

    }
}