package com.nfssoundtrack.racingsoundtracks.repository;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Song;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Integer> {

    List<Song> findByLyrics(String lyrics);

    List<Song> findByLyricsContains(String lyrics);

    List<Song> findByOfficialDisplayTitle(String officialDisplayTitle);

    List<Song> findByOfficialDisplayTitleContains(String officialDisplayTitle);

    List<Song> findByOfficialDisplayBandAndOfficialDisplayTitle(String officialDisplayBand, String officialDisplayTitle);

    List<Song> findByOfficialDisplayBandAndOfficialDisplayTitleContains(String officialDisplayBand, String officialDisplayTitle);
}
