package com.nfssoundtrack.racingsoundtracks;

import com.nfssoundtrack.racingsoundtracks.dbmodel.MainGroup;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Serie;
import com.nfssoundtrack.racingsoundtracks.repository.GameRepository;
import com.nfssoundtrack.racingsoundtracks.repository.MainGroupRepository;
import com.nfssoundtrack.racingsoundtracks.repository.SerieRepository;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LazyDataTest {

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private SerieRepository serieRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private MainGroupRepository mainGroupRepository;

    @Test
    public void get_series_with_games() {
        List<Serie> serieList = serieRepository.findByIdNotNull();
        assert Hibernate.isInitialized(serieList.get(0).getGames());
    }

    @Test
    public void get_series_without_games() {
        List<Serie> serieList = serieRepository.findAll();
        assert !Hibernate.isInitialized(serieList.get(0).getGames());
    }

//    @Test
//    public void get_games_without_groups() {
//        List<Game> gameList = gameRepository.findAll();
//        assert !Hibernate.isInitialized(gameList.get(0).getMainGroups());
//    }

    //    @Test
//    public void get_games_with_groups() {
//        List<Game> gameList = gameRepository.findByIdNotNull();
//        assert Hibernate.isInitialized(gameList.get(0).getMainGroups());
//    }
    @Test
    public void get_groups_without_subgroups() {
        List<MainGroup> mainGroupList = mainGroupRepository.findAll();
        assert !Hibernate.isInitialized(mainGroupList.get(0).getSubgroups());
    }

    @Test
    public void get_groups_with_subgroups() {
        List<MainGroup> mainGroupList = mainGroupRepository.findByIdNotNull();
        assert Hibernate.isInitialized(mainGroupList.get(0).getSubgroups());
    }

    @Test
    public void get_groups_without_game() {
        List<MainGroup> mainGroupList = mainGroupRepository.findAll();
        assert !Hibernate.isInitialized(mainGroupList.get(0).getGame());
    }
}
