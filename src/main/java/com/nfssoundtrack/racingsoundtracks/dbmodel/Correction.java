package com.nfssoundtrack.racingsoundtracks.dbmodel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "correction")
public class Correction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * correction can be started from page with songs from game
     */
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "songsubgroup_id")
    private SongSubgroup songSubgroup;

    @Column(name = "page_url")
    private String pageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "problem_type")
    private ProblemType problemType;

    @Column(name = "correct_value")
    private String correctValue;

    @Column(name = "discord_user")
    private String discordUser;

    /**
     * todo this might be expanded in the future to have some conclusion comments about the correction
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "correction_status")
    private CorrectionStatus correctionStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SongSubgroup getSongSubgroup() {
        return songSubgroup;
    }

    public void setSongSubgroup(SongSubgroup songSubgroup) {
        this.songSubgroup = songSubgroup;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public ProblemType getProblemType() {
        return problemType;
    }

    public void setProblemType(ProblemType problemType) {
        this.problemType = problemType;
    }

    public String getCorrectValue() {
        return correctValue;
    }

    public void setCorrectValue(String correctValue) {
        this.correctValue = correctValue;
    }

    public String getDiscordUser() {
        return discordUser;
    }

    public void setDiscordUser(String discordUser) {
        this.discordUser = discordUser;
    }

    public CorrectionStatus getCorrectionStatus() {
        return correctionStatus;
    }

    public void setCorrectionStatus(CorrectionStatus correctionStatus) {
        this.correctionStatus = correctionStatus;
    }

    public Correction() {
    }

    public Correction(SongSubgroup songSubgroup, String pageUrl, ProblemType problemType, String correctValue, String discordUser) {
        this.songSubgroup = songSubgroup;
        this.pageUrl = pageUrl;
        this.problemType = problemType;
        this.correctValue = correctValue;
        this.discordUser = discordUser;
    }
}
