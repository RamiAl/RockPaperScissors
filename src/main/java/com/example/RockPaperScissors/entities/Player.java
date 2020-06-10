package com.example.RockPaperScissors.entities;

public class Player {

    private String name;
    private String move;
    private Boolean canMove = true;

    public Player() {
    }

    public Player(String name) {
        this.name = name;
    }

    public Boolean getCanMove() {
        return canMove;
    }

    public void setCanMove(Boolean canMove) {
        this.canMove = canMove;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }
}
