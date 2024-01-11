package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import com.nfssoundtrack.NFSSoundtrack_20.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameService {

	@Autowired
	GameRepository gameRepository;

	public Game findByGameShort(String gameShort){
		return gameRepository.findByGameShort(gameShort);
	}

	public Optional<Game> findById(int id){
		return gameRepository.findById(id);
	}

	public Game save(Game game){
		return gameRepository.save(game);
	}
}
