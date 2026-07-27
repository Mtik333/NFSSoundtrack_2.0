package com.nfssoundtrack.racingsoundtracks.repository;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Song;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SongSubgroup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SongSubgroupRepository extends JpaRepository<SongSubgroup, Integer> {

    List<SongSubgroup> findBySong(Song song);

    List<SongSubgroup> findByFilenameStartsWith(String filename);

    List<SongSubgroup> findBySubgroupMainGroupGame(Game game);

    List<SongSubgroup> findBySongIn(List<Song> songs, Sort sort);

    SongSubgroup findTopByOrderByIdDesc();

    @Query(value = "SELECT ss.* FROM song_subgroup ss JOIN song s ON ss.song_id = s.id WHERE (ss.src_id IS NOT NULL OR s.src_id IS NOT NULL) AND ss.id NOT IN :excludeIds ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<SongSubgroup> findRandom(@Param("excludeIds") Collection<Long> excludeIds);

    @Query(value = "SELECT ss.* FROM song_subgroup ss JOIN song s ON ss.song_id = s.id JOIN subgroup sg ON ss.subgroup_id = sg.id JOIN maingroup mg ON sg.group_id = mg.id JOIN game g ON mg.game_id = g.id WHERE (ss.src_id IS NOT NULL OR s.src_id IS NOT NULL) AND ss.id NOT IN :excludeIds AND g.series_id IN :serieIds ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<SongSubgroup> findRandomFromSeries(@Param("excludeIds") Collection<Long> excludeIds, @Param("serieIds") Collection<Long> serieIds);

    @Query(value = "SELECT ss.* FROM song_subgroup ss JOIN song s ON ss.song_id = s.id JOIN subgroup sg ON ss.subgroup_id = sg.id JOIN maingroup mg ON sg.group_id = mg.id WHERE (ss.src_id IS NOT NULL OR s.src_id IS NOT NULL) AND ss.id NOT IN :excludeIds AND mg.game_id IN :gameIds ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<SongSubgroup> findRandomFromGames(@Param("excludeIds") Collection<Long> excludeIds, @Param("gameIds") Collection<Long> gameIds);

    @Query(value = "SELECT ss.* FROM song_subgroup ss JOIN song s ON ss.song_id = s.id JOIN song_genre sgen ON sgen.song_id = s.id WHERE (ss.src_id IS NOT NULL OR s.src_id IS NOT NULL) AND ss.id NOT IN :excludeIds AND sgen.genre_id IN :genreIds ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<SongSubgroup> findRandomFromGenres(@Param("excludeIds") Collection<Long> excludeIds, @Param("genreIds") Collection<Long> genreIds);

    @Query(value = "SELECT ss.* FROM song_subgroup ss JOIN song s ON ss.song_id = s.id JOIN subgroup sg ON ss.subgroup_id = sg.id JOIN maingroup mg ON sg.group_id = mg.id JOIN song_genre sgen ON sgen.song_id = s.id WHERE (ss.src_id IS NOT NULL OR s.src_id IS NOT NULL) AND ss.id NOT IN :excludeIds AND mg.game_id IN :gameIds AND sgen.genre_id IN :genreIds ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<SongSubgroup> findRandomFromGamesAndGenres(@Param("excludeIds") Collection<Long> excludeIds, @Param("gameIds") Collection<Long> gameIds, @Param("genreIds") Collection<Long> genreIds);

    @Query(value = "SELECT ss.* FROM song_subgroup ss JOIN song s ON ss.song_id = s.id JOIN subgroup sg ON ss.subgroup_id = sg.id JOIN maingroup mg ON sg.group_id = mg.id JOIN game g ON mg.game_id = g.id JOIN song_genre sgen ON sgen.song_id = s.id WHERE (ss.src_id IS NOT NULL OR s.src_id IS NOT NULL) AND ss.id NOT IN :excludeIds AND g.series_id IN :serieIds AND sgen.genre_id IN :genreIds ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<SongSubgroup> findRandomFromSeriesAndGenres(@Param("excludeIds") Collection<Long> excludeIds, @Param("serieIds") Collection<Long> serieIds, @Param("genreIds") Collection<Long> genreIds);

    @Query(value = "SELECT ss.* FROM song_subgroup ss JOIN song s ON ss.song_id = s.id JOIN author_song asng ON asng.song_id = s.id JOIN author_alias aa ON aa.id = asng.alias_id WHERE (ss.src_id IS NOT NULL OR s.src_id IS NOT NULL) AND ss.id NOT IN :excludeIds AND aa.author_id IN :authorIds ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<SongSubgroup> findRandomFromAuthors(@Param("excludeIds") Collection<Long> excludeIds, @Param("authorIds") Collection<Long> authorIds);

    @Query(value = "SELECT ss.* FROM song_subgroup ss JOIN song s ON ss.song_id = s.id JOIN subgroup sg ON ss.subgroup_id = sg.id JOIN maingroup mg ON sg.group_id = mg.id JOIN author_song asng ON asng.song_id = s.id JOIN author_alias aa ON aa.id = asng.alias_id WHERE (ss.src_id IS NOT NULL OR s.src_id IS NOT NULL) AND ss.id NOT IN :excludeIds AND mg.game_id IN :gameIds AND aa.author_id IN :authorIds ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<SongSubgroup> findRandomFromGamesAndAuthors(@Param("excludeIds") Collection<Long> excludeIds, @Param("gameIds") Collection<Long> gameIds, @Param("authorIds") Collection<Long> authorIds);

    @Query(value = "SELECT ss.* FROM song_subgroup ss JOIN song s ON ss.song_id = s.id JOIN subgroup sg ON ss.subgroup_id = sg.id JOIN maingroup mg ON sg.group_id = mg.id JOIN game g ON mg.game_id = g.id JOIN author_song asng ON asng.song_id = s.id JOIN author_alias aa ON aa.id = asng.alias_id WHERE (ss.src_id IS NOT NULL OR s.src_id IS NOT NULL) AND ss.id NOT IN :excludeIds AND g.series_id IN :serieIds AND aa.author_id IN :authorIds ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<SongSubgroup> findRandomFromSeriesAndAuthors(@Param("excludeIds") Collection<Long> excludeIds, @Param("serieIds") Collection<Long> serieIds, @Param("authorIds") Collection<Long> authorIds);

    @Query(value = "SELECT ss.id FROM song_subgroup ss JOIN subgroup sg ON ss.subgroup_id = sg.id JOIN maingroup mg ON sg.group_id = mg.id JOIN game g ON mg.game_id = g.id WHERE g.series_id IN :serieIds", nativeQuery = true)
    List<Long> findIdsBySeries(@Param("serieIds") Collection<Long> serieIds);

    @Query(value = "SELECT ss.id FROM song_subgroup ss JOIN subgroup sg ON ss.subgroup_id = sg.id JOIN maingroup mg ON sg.group_id = mg.id WHERE mg.game_id IN :gameIds", nativeQuery = true)
    List<Long> findIdsByGames(@Param("gameIds") Collection<Long> gameIds);

    @Query(value = "SELECT ss.id FROM song_subgroup ss JOIN song s ON ss.song_id = s.id JOIN song_genre sgen ON sgen.song_id = s.id WHERE sgen.genre_id IN :genreIds", nativeQuery = true)
    List<Long> findIdsByGenres(@Param("genreIds") Collection<Long> genreIds);

    @Query(value = "SELECT ss.id FROM song_subgroup ss JOIN song s ON ss.song_id = s.id JOIN author_song asng ON asng.song_id = s.id JOIN author_alias aa ON aa.id = asng.alias_id WHERE aa.author_id IN :authorIds", nativeQuery = true)
    List<Long> findIdsByAuthors(@Param("authorIds") Collection<Long> authorIds);

    @Query(value = "SELECT COUNT(ss.id) FROM song_subgroup ss JOIN song s ON ss.song_id = s.id WHERE (ss.src_id IS NOT NULL OR s.src_id IS NOT NULL) AND ss.id NOT IN :excludeIds", nativeQuery = true)
    long countAll(@Param("excludeIds") Collection<Long> excludeIds);

    @Query(value = "SELECT COUNT(ss.id) FROM song_subgroup ss JOIN song s ON ss.song_id = s.id JOIN subgroup sg ON ss.subgroup_id = sg.id JOIN maingroup mg ON sg.group_id = mg.id WHERE (ss.src_id IS NOT NULL OR s.src_id IS NOT NULL) AND ss.id NOT IN :excludeIds AND mg.game_id IN :gameIds", nativeQuery = true)
    long countFromGames(@Param("gameIds") Collection<Long> gameIds, @Param("excludeIds") Collection<Long> excludeIds);

    @Query(value = "SELECT COUNT(ss.id) FROM song_subgroup ss JOIN song s ON ss.song_id = s.id JOIN subgroup sg ON ss.subgroup_id = sg.id JOIN maingroup mg ON sg.group_id = mg.id JOIN game g ON mg.game_id = g.id WHERE (ss.src_id IS NOT NULL OR s.src_id IS NOT NULL) AND ss.id NOT IN :excludeIds AND g.series_id IN :serieIds", nativeQuery = true)
    long countFromSeries(@Param("serieIds") Collection<Long> serieIds, @Param("excludeIds") Collection<Long> excludeIds);

    @Query(value = "SELECT COUNT(ss.id) FROM song_subgroup ss JOIN song s ON ss.song_id = s.id JOIN song_genre sgen ON sgen.song_id = s.id WHERE (ss.src_id IS NOT NULL OR s.src_id IS NOT NULL) AND ss.id NOT IN :excludeIds AND sgen.genre_id IN :genreIds", nativeQuery = true)
    long countFromGenres(@Param("genreIds") Collection<Long> genreIds, @Param("excludeIds") Collection<Long> excludeIds);

    @Query(value = "SELECT COUNT(ss.id) FROM song_subgroup ss JOIN song s ON ss.song_id = s.id JOIN subgroup sg ON ss.subgroup_id = sg.id JOIN maingroup mg ON sg.group_id = mg.id JOIN song_genre sgen ON sgen.song_id = s.id WHERE (ss.src_id IS NOT NULL OR s.src_id IS NOT NULL) AND ss.id NOT IN :excludeIds AND mg.game_id IN :gameIds AND sgen.genre_id IN :genreIds", nativeQuery = true)
    long countFromGamesAndGenres(@Param("gameIds") Collection<Long> gameIds, @Param("genreIds") Collection<Long> genreIds, @Param("excludeIds") Collection<Long> excludeIds);

    @Query(value = "SELECT COUNT(ss.id) FROM song_subgroup ss JOIN song s ON ss.song_id = s.id JOIN subgroup sg ON ss.subgroup_id = sg.id JOIN maingroup mg ON sg.group_id = mg.id JOIN game g ON mg.game_id = g.id JOIN song_genre sgen ON sgen.song_id = s.id WHERE (ss.src_id IS NOT NULL OR s.src_id IS NOT NULL) AND ss.id NOT IN :excludeIds AND g.series_id IN :serieIds AND sgen.genre_id IN :genreIds", nativeQuery = true)
    long countFromSeriesAndGenres(@Param("serieIds") Collection<Long> serieIds, @Param("genreIds") Collection<Long> genreIds, @Param("excludeIds") Collection<Long> excludeIds);

    @Query(value = "SELECT COUNT(ss.id) FROM song_subgroup ss JOIN song s ON ss.song_id = s.id JOIN author_song asng ON asng.song_id = s.id JOIN author_alias aa ON aa.id = asng.alias_id WHERE (ss.src_id IS NOT NULL OR s.src_id IS NOT NULL) AND ss.id NOT IN :excludeIds AND aa.author_id IN :authorIds", nativeQuery = true)
    long countFromAuthors(@Param("authorIds") Collection<Long> authorIds, @Param("excludeIds") Collection<Long> excludeIds);

    @Query(value = "SELECT COUNT(ss.id) FROM song_subgroup ss JOIN song s ON ss.song_id = s.id JOIN subgroup sg ON ss.subgroup_id = sg.id JOIN maingroup mg ON sg.group_id = mg.id JOIN author_song asng ON asng.song_id = s.id JOIN author_alias aa ON aa.id = asng.alias_id WHERE (ss.src_id IS NOT NULL OR s.src_id IS NOT NULL) AND ss.id NOT IN :excludeIds AND mg.game_id IN :gameIds AND aa.author_id IN :authorIds", nativeQuery = true)
    long countFromGamesAndAuthors(@Param("gameIds") Collection<Long> gameIds, @Param("authorIds") Collection<Long> authorIds, @Param("excludeIds") Collection<Long> excludeIds);

    @Query(value = "SELECT COUNT(ss.id) FROM song_subgroup ss JOIN song s ON ss.song_id = s.id JOIN subgroup sg ON ss.subgroup_id = sg.id JOIN maingroup mg ON sg.group_id = mg.id JOIN game g ON mg.game_id = g.id JOIN author_song asng ON asng.song_id = s.id JOIN author_alias aa ON aa.id = asng.alias_id WHERE (ss.src_id IS NOT NULL OR s.src_id IS NOT NULL) AND ss.id NOT IN :excludeIds AND g.series_id IN :serieIds AND aa.author_id IN :authorIds", nativeQuery = true)
    long countFromSeriesAndAuthors(@Param("serieIds") Collection<Long> serieIds, @Param("authorIds") Collection<Long> authorIds, @Param("excludeIds") Collection<Long> excludeIds);

    @Query("SELECT ss FROM song_subgroup ss WHERE (ss.srcId IS NOT NULL OR ss.song.srcId IS NOT NULL) AND LOWER(ss.song.officialDisplayBand) LIKE LOWER(CONCAT('%', :band, '%')) AND LOWER(ss.song.officialDisplayTitle) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<SongSubgroup> searchByBandAndTitle(@Param("band") String band, @Param("title") String title, Pageable pageable);

    @Query("SELECT ss FROM song_subgroup ss WHERE (ss.srcId IS NOT NULL OR ss.song.srcId IS NOT NULL) AND LOWER(ss.song.officialDisplayTitle) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<SongSubgroup> searchByTitle(@Param("title") String title, Pageable pageable);
}

