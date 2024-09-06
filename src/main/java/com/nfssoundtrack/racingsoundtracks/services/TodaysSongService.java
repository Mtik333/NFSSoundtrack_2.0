package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.controllers.WebsiteViewsController;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Correction;
import com.nfssoundtrack.racingsoundtracks.dbmodel.CorrectionStatus;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SongSubgroup;
import com.nfssoundtrack.racingsoundtracks.dbmodel.TodaysSong;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import com.nfssoundtrack.racingsoundtracks.repository.SongSubgroupRepository;
import com.nfssoundtrack.racingsoundtracks.repository.TodaysSongRepository;
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

    //todo use in the future for page with all corrections
    //todo check sizing of all-games when switching do this mode while pinned
    public List<TodaysSong> findAll() {
        return todaysSongRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
    }

    /**
     * logic to get today's song, happens when there's a new day
     *
     * @return entity of todays song
     * @throws ResourceNotFoundException
     * @throws InterruptedException
     * @throws LoginException
     */
    public TodaysSong getTodaysSong() throws ResourceNotFoundException, InterruptedException, LoginException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        LocalDate localDate = LocalDate.now();
        String dateAsString = dtf.format(localDate);
        Date dbDate = Date.valueOf(dateAsString);
        //obviously if todays song was already found, we don't do any more work here
        Optional<TodaysSong> foundSong = todaysSongRepository.findByDate(dbDate);
        if (foundSong.isPresent()) {
            return foundSong.get();
        } else {
            //we do the most stupid thing - looking for some id of song in db
            //and then just use it once the rendered song is already visible
            Long biggestId = songSubgroupRepository.findTopByOrderByIdDesc().getId();
            int nextSongId = ThreadLocalRandom.current().nextInt(1, Math.toIntExact(biggestId));
            //there's probably risk that we get id that does not exist in db but website would pick a new one anyway
            //with next access to main page
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

    /**
     * method used to call all discord users to explain situation about correction
     *
     * @throws InterruptedException
     * @throws LoginException
     */
    private void notifyAboutCorrections() throws InterruptedException, LoginException {
        List<Correction> corrections = correctionService.findByCorrectionStatus(CorrectionStatus.DONE);
        WebsiteViewsController.rebuildJda(botSecret);
        for (Correction correction : corrections) {
            //if user was mentioned in correction, we'll contact him but also mark correction as modified anyway
            if (!correction.getDiscordUser().isEmpty()) {
                List<Member> foundUsers = WebsiteViewsController.getJda().getGuilds().get(0)
                        .retrieveMembersByPrefix(correction.getDiscordUser(), 1).get();
                if (!foundUsers.isEmpty()) {
                    foundUsers.get(0).getUser().openPrivateChannel().queue(privateChannel -> privateChannel
                            .sendMessage("Your correction with ID " + correction.getId()
                                    + " was processed. Please check at " + correction.getPageUrl()).queue());
                }
            }
            correction.setCorrectionStatus(CorrectionStatus.NOTIFIED);
            correctionService.save(correction);
        }
    }

    /**
     * here we just get all todays songs from 30 days perspective
     * todo make page with all corrections
     *
     * @return list of todays song
     */
    public List<TodaysSong> findAllFromLast30Days() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        LocalDate localDate = LocalDate.now();
        String dateAsString = dtf.format(localDate);
        Date todayDate = Date.valueOf(dateAsString);
        LocalDate monthAgo = localDate.minusDays(30);
        String oldDateAsString = dtf.format(monthAgo);
        Date monthAgoDate = Date.valueOf(oldDateAsString);
        return todaysSongRepository.findByDateBetween(monthAgoDate, todayDate, Sort.by(Sort.Direction.DESC, "date"));
    }

    public List<TodaysSong> findAllBySongSubgroup(SongSubgroup songSubgroup) {
        return todaysSongRepository.findBySongSubgroup(songSubgroup);
    }

    public TodaysSong save(TodaysSong todaysSong) {
        return todaysSongRepository.save(todaysSong);
    }
}
