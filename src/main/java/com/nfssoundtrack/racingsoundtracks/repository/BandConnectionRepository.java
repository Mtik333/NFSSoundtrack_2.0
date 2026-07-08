package com.nfssoundtrack.racingsoundtracks.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BandConnectionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Returns (author_id, author_name, game_id, game_displaytitle, game_gameshort,
     *          maingroup_id, maingroup_groupname)
     * for every COMPOSER credit, with aliases collapsed to their parent author
     * and placeholder authors excluded.
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findAllBandGameEdges() {
        String sql = """
                SELECT DISTINCT
                  aa.author_id,
                  a.name,
                  mg.game_id,
                  g.displaytitle,
                  g.gameshort,
                  mg.id,
                  mg.groupname
                FROM author_song asng
                JOIN author_alias aa   ON aa.id   = asng.alias_id
                JOIN author a          ON a.id    = aa.author_id
                JOIN song_subgroup ssg ON ssg.song_id  = asng.song_id
                JOIN subgroup sg       ON sg.id   = ssg.subgroup_id
                JOIN maingroup mg      ON mg.id   = sg.group_id
                JOIN game g            ON g.id    = mg.game_id
                WHERE asng.role = 'COMPOSER'
                  AND LOWER(a.name) NOT LIKE 'somebodygame%'
                """;
        return entityManager.createNativeQuery(sql).getResultList();
    }
}
