package com.example.RockPaperScissors.services;

import com.example.RockPaperScissors.entities.Game;
import com.example.RockPaperScissors.entities.Player;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GameService {

   private ArrayList<Game> gameList = new ArrayList<>();

    private List<String> movesOptions = Arrays.asList("rock", "scissors", "paper");

    public ResponseEntity<String> createGame (Player player){
        Game game = new Game();
        game.setPlayerOne(player);
        gameList.add(game);

        if (player.getName() == null) return new ResponseEntity<>("Name property is required", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>("Game created with id: "+ game.getId(), HttpStatus.OK);
    }

    public ResponseEntity<String> joinGame(Player player, String gameId) {
        if (player.getName() == null)
            return new ResponseEntity<>("Name property is required", HttpStatus.BAD_REQUEST);

        for (Game game : gameList) {
            if (game.getId().equals(gameId)) {
                if (!game.getIsFinished()) {
                    if (game.getPlayerTwo() == null) {
                        if (game.getPlayerOne().getName().toLowerCase().equals(player.getName().toLowerCase()))
                            return new ResponseEntity<>(player.getName() + " is already taken", HttpStatus.NOT_ACCEPTABLE);

                        game.setPlayerTwo(player);
                        return new ResponseEntity<>("Welcome to the game " + player.getName(), HttpStatus.OK);
                    }else
                        return new ResponseEntity<>("No more place for another player, 2 maximum.", HttpStatus.OK);
                }else
                    return new ResponseEntity<>("The game with this id is finished.", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("There is no game with this id.", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> makeMove(Player player, String gameId) {
        if (player.getName() == null || player.getMove() == null)
            return new ResponseEntity<>("Name and move properties is required", HttpStatus.BAD_REQUEST);

        for (Game game : gameList) {
            if (game.getId().equals(gameId)) {
                if (!game.getIsFinished()) {
                    if (game.getPlayerTwo() == null)
                        return new ResponseEntity<>("The other player has not joined the game yet", HttpStatus.NOT_ACCEPTABLE);

                    else if (game.getPlayerOne().getName().toLowerCase().equals(player.getName().toLowerCase())) {
                        if (!movesOptions.contains(player.getMove().toLowerCase()))
                            return new ResponseEntity<>("incorrect move", HttpStatus.NOT_ACCEPTABLE);

                        else {
                            if (game.getPlayerOne().getCanMove()) {
                                game.getPlayerOne().setMove(player.getMove().toLowerCase());
                                game.getPlayerOne().setCanMove(false);
                                if (game.getPlayerTwo().getMove() != null)
                                    game.setIsFinished(true);
                                return new ResponseEntity<>("Go see your result " + player.getName(), HttpStatus.OK);
                            }
                            return new ResponseEntity<>("You have no more moves", HttpStatus.OK);
                        }
                    }
                    else if (game.getPlayerTwo().getName().toLowerCase().equals(player.getName().toLowerCase())) {
                        if (!movesOptions.contains(player.getMove().toLowerCase()))
                            return new ResponseEntity<>("incorrect move", HttpStatus.NOT_ACCEPTABLE);

                        else {
                            if (game.getPlayerTwo().getCanMove()) {
                                game.getPlayerTwo().setMove(player.getMove().toLowerCase());
                                game.getPlayerTwo().setCanMove(false);
                                if (game.getPlayerOne().getMove() != null)
                                    game.setIsFinished(true);
                                return new ResponseEntity<>("Go see your result " + player.getName(), HttpStatus.OK);
                            }
                            return new ResponseEntity<>("You have no more moves", HttpStatus.OK);
                        }
                    }
                }else
                    return new ResponseEntity<>("The game with this id is finished", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("There is no player with this name or game with this id", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> getGame(String gameId) {
        String theWinner = null;
        boolean draw = false;
        String resultMessage;

        for (Game game : gameList){
            if (game.getId().equals(gameId)) {
                if (game.getPlayerOne().getMove() == null || game.getPlayerTwo().getMove() == null)
                    return new ResponseEntity<>("The other player has not made a move yet", HttpStatus.OK);

                else {
                    switch (game.getPlayerOne().getMove()) {
                        case "rock":
                            if (game.getPlayerTwo().getMove().equals("scissors"))
                                theWinner = game.getPlayerOne().getName();

                            else if (game.getPlayerTwo().getMove().equals("paper"))
                                theWinner = game.getPlayerTwo().getName();

                            else
                                draw = true;
                            break;
                        case "scissors":
                            if (game.getPlayerTwo().getMove().equals("paper"))
                                theWinner = game.getPlayerOne().getName();

                            else if (game.getPlayerOne().getMove().equals("rock"))
                                theWinner = game.getPlayerTwo().getName();

                            else
                                draw = true;
                            break;
                        case "paper":
                            if (game.getPlayerTwo().getMove().equals("rock"))
                                theWinner = game.getPlayerOne().getName();

                            else if (game.getPlayerTwo().getMove().equals("scissors"))
                                theWinner = game.getPlayerTwo().getName();
                            else
                                draw = true;
                            break;
                    }

                    if (!draw)
                        resultMessage = game.getPlayerOne().getName() + "'s move: " + game.getPlayerOne().getMove() + "\n" +
                                game.getPlayerTwo().getName() + "'s move: " + game.getPlayerTwo().getMove() +
                                "\nThe winner is " + theWinner;

                    else resultMessage = game.getPlayerOne().getName() + "'s move: " + game.getPlayerOne().getMove() + "\n" +
                            game.getPlayerTwo().getName() + "'s move: " + game.getPlayerTwo().getMove() +
                            "\nIt's a draw guys";

                    game.setIsFinished(true);
                    return new ResponseEntity<>(resultMessage, HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>("There is no game with this id or it's finished", HttpStatus.BAD_REQUEST);
    }

    public void deleteAllGames(){
        gameList.clear();
    }
}
