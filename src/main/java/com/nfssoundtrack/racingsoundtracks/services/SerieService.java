package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Serie;
import com.nfssoundtrack.racingsoundtracks.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SerieService {

    @Autowired
    SerieRepository serieRepository;

    public List<Serie> findAll() {
        return serieRepository.findAll();
    }

    public List<Serie> findAllSortedByPositionAsc() {
        return serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position"));
    }

    public Optional<Serie> findById(int id) {
        return serieRepository.findById(id);
    }

    public Serie save(Serie serie) {
        return serieRepository.save(serie);
    }

}
