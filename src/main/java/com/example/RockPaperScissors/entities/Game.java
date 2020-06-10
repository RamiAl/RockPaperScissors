package com.example.RockPaperScissors.entities;

public class Game {

    private String id;
    private Player playerOne;
    private Player playerTwo;

    private Boolean isFinished = false;

    public Game() {
        //id = UUID.randomUUID().toString();
        id = "22";
        playerOne = new Player();
    }

    public Boolean getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(Boolean isFinished) {
        this.isFinished = isFinished;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(Player playerOne) {
        this.playerOne = playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(Player playerTwo) {
        this.playerTwo = playerTwo;
    }

    public String getId() {
        return id;
    }
}
