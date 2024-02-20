package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.controllers.WebsiteViewsController;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Correction;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.CorrectionStatus;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.TodaysSong;
import com.nfssoundtrack.NFSSoundtrack_20.others.ResourceNotFoundException;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongSubgroupRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.TodaysSongRepository;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TodaysSongService {

    @Autowired
    TodaysSongRepository todaysSongRepository;

    @Autowired
    SongSubgroupRepository songSubgroupRepository;

    @Autowired
    CorrectionService correctionService;

    @Value("${bot.token}")
    private String botSecret;

    public List<TodaysSong> findAll() {
        return todaysSongRepository.findAll();
    }

    public TodaysSong getTodaysSong() throws ResourceNotFoundException, InterruptedException, LoginException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        LocalDate localDate = LocalDate.now();
        String dateAsString = dtf.format(localDate);
        Date dbDate = Date.valueOf(dateAsString);
        Optional<TodaysSong> foundSong = todaysSongRepository.findByDate(dbDate);
        if (foundSong.isPresent()) {
            return foundSong.get();
        } else {
            Long biggestId = songSubgroupRepository.findTopByOrderByIdDesc().getId();
            int nextSongId = ThreadLocalRandom.current().nextInt(1, Math.toIntExact(biggestId));
            SongSubgroup targetSong = songSubgroupRepository.findById(nextSongId).orElseThrow(() ->
                    new ResourceNotFoundException("no songsubgroup found with id " + nextSongId));
            TodaysSong todaysSong = new TodaysSong();
            todaysSong.setSongSubgroup(targetSong);
            todaysSong.setDate(dbDate);
            todaysSong = todaysSongRepository.save(todaysSong);
            notifyAboutCorrections();
            return todaysSong;
        }
    }

    private void notifyAboutCorrections() throws InterruptedException, LoginException {
        List<Correction> corrections = correctionService.findByCorrectionStatus(CorrectionStatus.DONE);
        if (WebsiteViewsController.JDA == null) {
            WebsiteViewsController.JDA = JDABuilder.createDefault(botSecret).build();
            WebsiteViewsController.JDA.awaitReady();
        }
        for (Correction correction : corrections) {
            if (!correction.getDiscordUser().isEmpty()) {
                List<Member> foundUsers = WebsiteViewsController.JDA.getGuilds().get(0)
                        .retrieveMembersByPrefix(correction.getDiscordUser(), 1).get();
                if (!foundUsers.isEmpty()) {
                    foundUsers.get(0).getUser().openPrivateChannel().queue(privateChannel -> privateChannel
                            .sendMessage("Your correction with ID " + correction.getId()
                                    + " was processed. Please check at " + correction.getPageUrl()).queue());
                }
                correction.setCorrectionStatus(CorrectionStatus.NOTIFIED);
                correctionService.save(correction);
            }
        }
    }

    public List<TodaysSong> findAllFromLast30Days() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        LocalDate localDate = LocalDate.now();
        String dateAsString = dtf.format(localDate);
        Date todayDate = Date.valueOf(dateAsString);
        LocalDate monthAgo = localDate.minusDays(30);
        String oldDateAsString = dtf.format(monthAgo);
        Date monthAgoDate = Date.valueOf(oldDateAsString);
        List<TodaysSong> todaysSongs = todaysSongRepository.findByDateBetween(monthAgoDate, todayDate, Sort.by(Sort.Direction.DESC, "date"));
        return todaysSongs;
    }
}
