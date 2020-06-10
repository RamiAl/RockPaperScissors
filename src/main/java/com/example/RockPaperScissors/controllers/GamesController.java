package com.example.RockPaperScissors.controllers;

import com.example.RockPaperScissors.entities.Player;
import com.example.RockPaperScissors.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/")
public class GamesController {

    @Autowired
    GameService gameService;

    @PostMapping("games")
    public ResponseEntity<String> createGame(@RequestBody Player player) {
        return gameService.createGame(player);
    }

    @PostMapping("games/{gameId}/join")
    public ResponseEntity<String> joinGame(@RequestBody Player player,
                                           @PathVariable String gameId) {
        return gameService.joinGame(player, gameId);
    }

    @PostMapping("games/{gameId}/move")
    public ResponseEntity<String> makeMove(@RequestBody Player player,
                                           @PathVariable String gameId) {
        return gameService.makeMove(player, gameId);
    }

    @GetMapping("games/{gameId}")
    public ResponseEntity<String> getGame(@PathVariable String gameId) {
        return gameService.getGame(gameId);
    }

    @DeleteMapping("games")
    public void deleteAllGames(){
        gameService.deleteAllGames();
    }
}
