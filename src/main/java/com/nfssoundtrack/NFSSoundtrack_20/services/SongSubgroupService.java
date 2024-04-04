package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.others.ResourceNotFoundException;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongSubgroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SongSubgroupService {

    @Autowired
    AuthorAliasService authorAliasService;

    @Autowired
    AuthorSongService authorSongService;

    @Autowired
    AuthorService authorService;

    @Autowired
    SongSubgroupRepository songSubgroupRepository;


    public Optional<SongSubgroup> findById(Integer id) {
        return songSubgroupRepository.findById(id);
    }

    public List<SongSubgroup> findBySong(Song song) {
        return songSubgroupRepository.findBySong(song);
    }

    public List<SongSubgroup> findBySongInSortedByIdAsc(List<Song> songs) {
        return songSubgroupRepository.findBySongIn(songs, Sort.by(Sort.Direction.ASC, "id"));
    }

    public void deleteAllInBatch(List<SongSubgroup> songSubgroupList) {
        songSubgroupRepository.deleteAllInBatch(songSubgroupList);
    }

    public void delete(SongSubgroup songSubgroup) {
        songSubgroupRepository.delete(songSubgroup);
    }

    public List<SongSubgroup> saveAll(List<SongSubgroup> songSubgroupList) {
        return songSubgroupRepository.saveAll(songSubgroupList);
    }

    public SongSubgroup save(SongSubgroup songSubgroup) {
        return songSubgroupRepository.save(songSubgroup);
    }

    public List<SongSubgroup> fetchFromGame(Game game) {
        List<SongSubgroup> allSongs = new ArrayList<>();
        for (MainGroup mainGroup : game.getMainGroups()) {
            for (Subgroup subgroup : mainGroup.getSubgroups()) {
                allSongs.addAll(subgroup.getSongSubgroupList());
            }
        }
        return allSongs;
    }

    public void updateFeat(Map<String, String> objectMapper, String comingInput, String comingConcatInput,
                           SongSubgroup songSubgroup, Role role, Song relatedSong, Boolean propagate) throws Exception {
        List<String> comingFeats = objectMapper.keySet().stream().filter(
                o -> o.contains(comingInput)).toList();
        Iterator<String> comingConcats = objectMapper.keySet().stream().filter(
                o -> o.contains(comingConcatInput)).toList().iterator();
        for (String comingFeat : comingFeats) {
            String concatVal = null;
            if (comingConcats.hasNext()) {
                concatVal = objectMapper.get(comingConcats.next());
            }
            String featValue = objectMapper.get(comingFeat);
            if (featValue.startsWith("NEW")) {
                String actualFeatValue = featValue.replace("NEW-", "");
                saveNewFeatOrRemixer(actualFeatValue, songSubgroup, role, concatVal, propagate);
            } else if (featValue.startsWith("DELETE")) {
                String deleteFeatId = featValue.replace("DELETE-", "");
                AuthorAlias authorAlias = authorAliasService.findById(Integer.parseInt(deleteFeatId))
                        .orElseThrow(() -> new ResourceNotFoundException("No authoralias found with id " + deleteFeatId));
                AuthorSong authorSong = authorSongService.findByAuthorAliasAndSong(authorAlias, relatedSong)
                        .orElseThrow(() -> new ResourceNotFoundException("No authorsong found"));
                authorSongService.delete(authorSong);
                if (Role.REMIX.equals(role)){
                    songSubgroup.setRemix(Remix.NO);
                }
            } else {
                saveNewAssignmentOfExistingFeatRemixer(featValue, songSubgroup, role, concatVal, propagate);
            }
        }
    }

    private void saveNewFeatOrRemixer(String actualRemixValue, SongSubgroup songSubgroup, Role role,
                                      String concatValue, Boolean propagate) {
        Author author = new Author();
        author.setName(actualRemixValue);
        author = authorService.save(author);
        AuthorAlias authorAlias = new AuthorAlias(author, actualRemixValue);
        authorAlias = authorAliasService.save(authorAlias);
        AuthorSong authorSong = new AuthorSong(authorAlias, songSubgroup.getSong(), role);
        if (role.equals(Role.REMIX)) {
            authorSong.setRemixConcat(concatValue);
            if (propagate){
                List<SongSubgroup> entriesToUpdateConcat = songSubgroupRepository.findBySong(songSubgroup.getSong());
                entriesToUpdateConcat.remove(songSubgroup);
                for (SongSubgroup localSubgroup : entriesToUpdateConcat){
                    localSubgroup.setRemix(Remix.YES);
                    localSubgroup.setRemixText(songSubgroup.getRemixText());
                    songSubgroupRepository.save(localSubgroup);
                }
            }
        }
        if (role.equals(Role.FEAT)) {
            authorSong.setFeatConcat(concatValue);
        }
        if (role.equals(Role.SUBCOMPOSER)) {
            authorSong.setSubcomposerConcat(concatValue);
        }
        authorSongService.save(authorSong);
    }


    private void saveNewAssignmentOfExistingFeatRemixer(String remixValue, SongSubgroup songSubgroup, Role role,
                                                        String concatValue, Boolean propagate) throws ResourceNotFoundException {
        AuthorAlias authorAlias = authorAliasService.findById(Integer.parseInt(remixValue))
                .orElseThrow(() -> new ResourceNotFoundException("No authoralias with id found " + remixValue));
        boolean alreadyAssigned = false;
        for (AuthorSong authorSong : songSubgroup.getSong().getAuthorSongList()) {
            if (authorSong.getAuthorAlias().equals(authorAlias)) {
                if (!role.equals(authorSong.getRole())){
                    continue;
                }
                alreadyAssigned = true;
                if (Role.SUBCOMPOSER.equals(role)){
                    authorSong.setSubcomposerConcat(concatValue);
                } else if (Role.REMIX.equals(role)){
                    authorSong.setRemixConcat(concatValue);
                } else if (Role.FEAT.equals(role)){
                    authorSong.setFeatConcat(concatValue);
                }
                break;
            }
        }
        if (!alreadyAssigned) {
            AuthorSong authorSong = new AuthorSong(authorAlias, songSubgroup.getSong(), role);
            if (role.equals(Role.REMIX)) {
                authorSong.setRemixConcat(concatValue);
                if (propagate){
                    List<SongSubgroup> entriesToUpdateConcat = songSubgroupRepository.findBySong(songSubgroup.getSong());
                    entriesToUpdateConcat.remove(songSubgroup);
                    for (SongSubgroup localSubgroup : entriesToUpdateConcat){
                        localSubgroup.setRemix(Remix.YES);
                        localSubgroup.setRemixText(songSubgroup.getRemixText());
                        songSubgroupRepository.save(localSubgroup);
                    }
                }
            }
            if (role.equals(Role.FEAT)) {
                authorSong.setFeatConcat(concatValue);
            }
            if (role.equals(Role.SUBCOMPOSER)) {
                authorSong.setSubcomposerConcat(concatValue);
            }
            authorSongService.save(authorSong);
        }
    }
}
