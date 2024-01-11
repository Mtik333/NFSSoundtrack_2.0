package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Serie;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SerieService {

	@Autowired
	SerieRepository serieRepository;

	public List<Serie> findAll(){
		return serieRepository.findAll();
	}

	public List<Serie> findAllSortedByPositionAsc(){
		return serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position"));
	}

	public Optional<Serie> findById(int id){
		return serieRepository.findById(id);
	}

}
