package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import com.nfssoundtrack.racingsoundtracks.repository.SongSubgroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public List<SongSubgroup> findByFilenameStartsWith(String filename) {
        return songSubgroupRepository.findByFilenameStartsWith(filename);
    }

    public SongSubgroup findTopByOrderByIdDesc() {
        return songSubgroupRepository.findTopByOrderByIdDesc();
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

    public void saveAll(List<SongSubgroup> songSubgroupList) {
        songSubgroupRepository.saveAll(songSubgroupList);
    }

    public SongSubgroup save(SongSubgroup songSubgroup) {
        return songSubgroupRepository.save(songSubgroup);
    }

    /**
     * can be that i just added new game and now there's nothing to render
     * so it is possible to have 0 songs at this point
     *
     * @param game the game we are accessing in website
     * @return true of there are some songs assigned to th egame
     */
    public boolean hasGameAnySongs(Game game) {
        for (MainGroup mainGroup : game.getMainGroups()) {
            for (Subgroup subgroup : mainGroup.getSubgroups()) {
                if (!subgroup.getSongSubgroupList().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * method used to update info about feat / subcomposer / feat
     * todo maybe rename this method to updateSubcomposers?
     *
     * @param objectMapper      just ootb mapper
     * @param comingInput       type of role really
     * @param comingConcatInput text between composers of this role
     * @param songSubgroup      entity of song-subgroup
     * @param role              type of role used for specific song
     * @param relatedSong       the song itself for some reason
     * @param propagate         used to propagate concat between feat / remix to song-subgroup entry
     * @throws ResourceNotFoundException
     */
    public void updateFeat(Map<String, String> objectMapper, String comingInput, String comingConcatInput,
                           SongSubgroup songSubgroup, Role role, Song relatedSong, Boolean propagate) throws ResourceNotFoundException {
        List<String> comingFeats = objectMapper.keySet().stream().filter(
                o -> o.contains(comingInput)).toList();
        Iterator<String> comingConcats = objectMapper.keySet().stream().filter(
                o -> o.contains(comingConcatInput)).toList().iterator();
        //so we have list of feat-artists and concat like & or , to render pretty display
        for (String comingFeat : comingFeats) {
            String concatVal = null;
            if (comingConcats.hasNext()) {
                concatVal = objectMapper.get(comingConcats.next());
            }
            String featValue = objectMapper.get(comingFeat);
            //we can either create totally new artist connected to song
            if (featValue.startsWith("NEW")) {
                //thing after minus is going to be name of this new artist
                String actualFeatValue = featValue.replace("NEW-", "");
                saveNewFeatOrRemixer(actualFeatValue, songSubgroup, role, concatVal, propagate);
            } else if (featValue.startsWith("DELETE")) {
                //or we want to remove association between author and song
                String deleteFeatId = featValue.replace("DELETE-", "");
                //here however after delete we have id of artist
                AuthorAlias authorAlias = authorAliasService.findById(Integer.parseInt(deleteFeatId))
                        .orElseThrow(() -> new ResourceNotFoundException("No authoralias found with id " + deleteFeatId));
                AuthorSong authorSong = authorSongService.findByAuthorAliasAndSong(authorAlias, relatedSong)
                        .orElseThrow(() -> new ResourceNotFoundException("No authorsong found"));
                //given we found association, we can delete it and in case of remix - un-remix the field
                authorSongService.delete(authorSong);
                if (Role.REMIX.equals(role)) {
                    songSubgroup.setRemix(Remix.NO);
                }
            } else {
                //here we just add association between song and existing artist
                saveNewAssignmentOfExistingFeatRemixer(featValue, songSubgroup, role, concatVal, propagate);
            }
        }
    }

    /**
     * method to save new author and assign to the song under specific role
     *
     * @param actualRemixValue
     * @param songSubgroup
     * @param role
     * @param concatValue
     * @param propagate
     */
    private void saveNewFeatOrRemixer(String actualRemixValue, SongSubgroup songSubgroup, Role role,
                                      String concatValue, Boolean propagate) {
        Author author = new Author();
        author.setName(actualRemixValue);
        author = authorService.save(author);
        AuthorAlias authorAlias = new AuthorAlias(author, actualRemixValue);
        authorAlias = authorAliasService.save(authorAlias);
        //created author and alias
        AuthorSong authorSong = new AuthorSong(authorAlias, songSubgroup.getSong(), role);
        if (role.equals(Role.REMIX)) {
            propagateRemixToSongOccurrences(songSubgroup, propagate, authorSong);
        }
        setConcatsToAuthorSong(role, authorSong, concatValue);
        //for feat / subcomposer it is easier
        authorSongService.save(authorSong);
    }

    /**
     * method to assign existing artist to existing song
     *
     * @param remixValue
     * @param songSubgroup
     * @param role
     * @param concatValue
     * @param propagate
     * @throws ResourceNotFoundException
     */
    private void saveNewAssignmentOfExistingFeatRemixer(String remixValue, SongSubgroup songSubgroup, Role role,
                                                        String concatValue, Boolean propagate) throws ResourceNotFoundException {
        AuthorAlias authorAlias = authorAliasService.findById(Integer.parseInt(remixValue))
                .orElseThrow(() -> new ResourceNotFoundException("No authoralias with id found " + remixValue));
        //we're looking for id of author (which is alias id apparently)
        boolean alreadyAssigned = false;
        for (AuthorSong authorSong : songSubgroup.getSong().getAuthorSongList()) {
            //so we iterate over artists associated with song
            //in case artist was already assigned, maybe we just change the concat?
            if (authorSong.getAuthorAlias().equals(authorAlias)) {
                if (!role.equals(authorSong.getRole())) {
                    continue;
                }
                alreadyAssigned = true;
                //so concat maybe is not changed but we save the value (even as it was) anyway
                setConcatsToAuthorSong(role, authorSong, concatValue);
                break;
            }
        }
        if (!alreadyAssigned) {
            //if artist was not assigned yet, we're going to save new association between song and author
            //and save concat between artist and title (or other composer)
            AuthorSong authorSong = new AuthorSong(authorAlias, songSubgroup.getSong(), role);
            if (role.equals(Role.REMIX)) {
                propagateRemixToSongOccurrences(songSubgroup, propagate, authorSong);
            }
            setConcatsToAuthorSong(role, authorSong, concatValue);
            authorSongService.save(authorSong);
        }
    }

    /**
     * in case of remix we additionally set remix concat and update entries of this song
     * in the case of it being used quite a lot here and there
     * but i feel it makes only sense if we have existing song multiple times and we just add new
     * remixer to this song
     *
     * @param songSubgroup song-subgroup entity
     * @param propagate    other song occurrences only get changed if boolean is set by admin
     * @param authorSong   author-song association
     */
    private void propagateRemixToSongOccurrences(SongSubgroup songSubgroup, Boolean propagate, AuthorSong authorSong) {
        if (Boolean.TRUE.equals(propagate)) {
            List<SongSubgroup> entriesToUpdateConcat = songSubgroupRepository.findBySong(songSubgroup.getSong());
            entriesToUpdateConcat.remove(songSubgroup);
            for (SongSubgroup localSubgroup : entriesToUpdateConcat) {
                localSubgroup.setRemix(Remix.YES);
                localSubgroup.setRemixText(songSubgroup.getRemixText());
                songSubgroupRepository.save(localSubgroup);
            }
        }
    }

    /**
     * just setting concat value depending on author's role
     *
     * @param role        role of composer
     * @param authorSong  song-author association
     * @param concatValue value to pretty display between authors
     */
    private void setConcatsToAuthorSong(Role role, AuthorSong authorSong, String concatValue) {
        if (Role.SUBCOMPOSER.equals(role)) {
            authorSong.setSubcomposerConcat(concatValue);
        } else if (Role.REMIX.equals(role)) {
            authorSong.setRemixConcat(concatValue);
        } else if (Role.FEAT.equals(role)) {
            authorSong.setFeatConcat(concatValue);
        }
    }
}
