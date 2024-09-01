package com.nfssoundtrack.racingsoundtracks.dbmodel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.io.Serializable;

/**
 * todo imagine one day we have such custom theme for all the games
 */
@Entity(name = "customtheme")
public class CustomTheme implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(name = "pc_theme")
    private String pcTheme;

    @Column(name = "mobile_theme")
    private String mobileTheme;

    @Column(name = "hyperlink_color")
    private String hyperlinkColor;

    @Column(name = "text_color")
    private String textColor;

    @Column(name = "night_mode", columnDefinition = "BIT")
    private Boolean nightMode;

    @Column(name = "gamegroup_bg_color")
    private String gameGroupBackgroundColor;

    @Column(name = "gamegroup_text_color")
    private String gameGroupTextColor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getPcTheme() {
        return pcTheme;
    }

    public void setPcTheme(String pcTheme) {
        this.pcTheme = pcTheme;
    }

    public String getMobileTheme() {
        return mobileTheme;
    }

    public void setMobileTheme(String mobileTheme) {
        this.mobileTheme = mobileTheme;
    }

    public String getHyperlinkColor() {
        return hyperlinkColor;
    }

    public void setHyperlinkColor(String hyperlinkColor) {
        this.hyperlinkColor = hyperlinkColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public Boolean getNightMode() {
        return nightMode;
    }

    public void setNightMode(Boolean nightMode) {
        this.nightMode = nightMode;
    }

    public String getGameGroupBackgroundColor() {
        return gameGroupBackgroundColor;
    }

    public void setGameGroupBackgroundColor(String gameGroupBackgroundColor) {
        this.gameGroupBackgroundColor = gameGroupBackgroundColor;
    }

    public String getGameGroupTextColor() {
        return gameGroupTextColor;
    }

    public void setGameGroupTextColor(String gameGroupTextColor) {
        this.gameGroupTextColor = gameGroupTextColor;
    }

    public CustomTheme() {
    }

    public CustomTheme(Game game, String pcTheme, String mobileTheme, String hyperlinkColor, String textColor, Boolean nightMode) {
        this.game = game;
        this.pcTheme = pcTheme;
        this.mobileTheme = mobileTheme;
        this.hyperlinkColor = hyperlinkColor;
        this.textColor = textColor;
        this.nightMode = nightMode;
    }
}
