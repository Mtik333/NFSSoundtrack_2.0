package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Serie;
import com.nfssoundtrack.racingsoundtracks.repository.SerieRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SerieService {

    private final SerieRepository serieRepository;

    public SerieService(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }

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

    public List<Serie> saveAll(List<Serie> series) {
        return serieRepository.saveAll(series);
    }

}
