package com.nfssoundtrack.racingsoundtracks;

import com.nfssoundtrack.racingsoundtracks.dbmodel.MainGroup;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Serie;
import com.nfssoundtrack.racingsoundtracks.repository.MainGroupRepository;
import com.nfssoundtrack.racingsoundtracks.repository.SerieRepository;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LazyDataTest {

    private final SerieRepository serieRepository;
    private final MainGroupRepository mainGroupRepository;

    public LazyDataTest(SerieRepository serieRepository, MainGroupRepository mainGroupRepository) {
        this.serieRepository = serieRepository;
        this.mainGroupRepository = mainGroupRepository;
    }

    @Test
    void get_series_with_games() {
        List<Serie> serieList = serieRepository.findByIdNotNull();
        assert Hibernate.isInitialized(serieList.get(0).getGames());
    }

    @Test
    void get_series_without_games() {
        List<Serie> serieList = serieRepository.findAll();
        assert !Hibernate.isInitialized(serieList.get(0).getGames());
    }

    @Test
    void get_groups_without_subgroups() {
        List<MainGroup> mainGroupList = mainGroupRepository.findAll();
        assert !Hibernate.isInitialized(mainGroupList.get(0).getSubgroups());
    }

    @Test
    void get_groups_with_subgroups() {
        List<MainGroup> mainGroupList = mainGroupRepository.findByIdNotNull();
        assert Hibernate.isInitialized(mainGroupList.get(0).getSubgroups());
    }

    @Test
    void get_groups_without_game() {
        List<MainGroup> mainGroupList = mainGroupRepository.findAll();
        assert !Hibernate.isInitialized(mainGroupList.get(0).getGame());
    }
}
