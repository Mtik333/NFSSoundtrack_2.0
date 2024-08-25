package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import com.nfssoundtrack.racingsoundtracks.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    @Autowired
    GameRepository gameRepository;

    public Game findByGameShort(String gameShort) {
        return gameRepository.findByGameShort(gameShort);
    }

    public List<Game> findAllSortedByDisplayTitleAsc() {
        return gameRepository.findAll(Sort.by(Sort.Direction.ASC, "displayTitle"));
    }

    public Optional<Game> findById(int id) {
        return gameRepository.findById(id);
    }

    public Game save(Game game) {
        return gameRepository.save(game);
    }
}
