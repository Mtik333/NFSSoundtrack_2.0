package com.nfssoundtrack.racingsoundtracks.aspects;

import com.nfssoundtrack.racingsoundtracks.controllers.WebsiteViewsController;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Changelog;
import com.nfssoundtrack.racingsoundtracks.dbmodel.EntityType;
import com.nfssoundtrack.racingsoundtracks.dbmodel.EntityUrl;
import com.nfssoundtrack.racingsoundtracks.services.ChangelogService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class ChangelogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ChangelogAspect.class);

    @Value("${bot.token}")
    private String botSecret;

    @Value("${changelog.id}")
    private String changeLogId;

    @Autowired
    ChangelogService changelogService;

    private TextChannel channelForChangelog;
    static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public TextChannel getChannelForChangelog() throws LoginException, InterruptedException {
        if (channelForChangelog == null) {
            setupChannelForChangelog(changeLogId);
        }
        return channelForChangelog;
    }

    public void setupChannelForChangelog(String channelId) throws LoginException, InterruptedException {
        JDA jda = WebsiteViewsController.rebuildJda(botSecret);
        channelForChangelog = jda.getTextChannelById(channelId);
    }

    public void sendMessageToChannel(EntityType entityType, String operationType, String text,
                                     EntityUrl entityUrl, String urlLabel, String urlValue) {
        try {
            LocalDateTime localDate = LocalDateTime.now();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(entityType.value() + " - " + operationType + " at " + localDate.format(TIME_FORMATTER));
            eb.setThumbnail("https://racingsoundtracks.com/images/racingsoundtracks.png");
            eb.setColor(Color.red);
            eb.setDescription(text);
            MessageEmbed embed = eb.build();
            if (channelForChangelog == null) {
                setupChannelForChangelog(changeLogId);
            }
            channelForChangelog.sendMessageEmbeds(embed).queue();
            saveLog(entityType, operationType, text,
                    entityUrl, urlLabel, urlValue, Timestamp.valueOf(localDate));
        } catch (LoginException | InterruptedException e) {
            logger.error("exception in sending message: {}", e.getMessage());
        }
    }

    private void saveLog(EntityType entityType, String operationType, String logText,
                         EntityUrl entityUrl, String urlLabel, String urlValue,
                         Timestamp timestamp) {
        Changelog changelog = new Changelog(logText, operationType, entityType,
                entityUrl, urlLabel, urlValue, timestamp);
        changelogService.save(changelog);
    }
}
