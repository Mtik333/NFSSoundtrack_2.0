package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import com.nfssoundtrack.racingsoundtracks.repository.SongSubgroupRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SongSubgroupService {

    private final AuthorAliasService authorAliasService;

    private final AuthorSongService authorSongService;

    private final SongSubgroupRepository songSubgroupRepository;

    public SongSubgroupService(AuthorAliasService authorAliasService, AuthorSongService authorSongService, SongSubgroupRepository songSubgroupRepository) {
        this.authorAliasService = authorAliasService;
        this.authorSongService = authorSongService;
        this.songSubgroupRepository = songSubgroupRepository;
    }

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
     * method to assign existing artist to existing song
     *
     * @param remixValue
     * @param songSubgroup
     * @param role
     * @param concatValue
     * @param propagate
     * @throws ResourceNotFoundException
     */
    public void saveNewAssignmentOfExistingFeatRemixer(String remixValue, SongSubgroup songSubgroup, Role role,
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
    public void propagateRemixToSongOccurrences(SongSubgroup songSubgroup, Boolean propagate, AuthorSong authorSong) {
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
    public void setConcatsToAuthorSong(Role role, AuthorSong authorSong, String concatValue) {
        if (Role.SUBCOMPOSER.equals(role)) {
            authorSong.setSubcomposerConcat(concatValue);
        } else if (Role.REMIX.equals(role)) {
            authorSong.setRemixConcat(concatValue);
        } else if (Role.FEAT.equals(role)) {
            authorSong.setFeatConcat(concatValue);
        }
    }
}
