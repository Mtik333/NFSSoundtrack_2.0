package com.nfssoundtrack.racingsoundtracks;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Song;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SongSubgroup;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Subgroup;
import com.nfssoundtrack.racingsoundtracks.dbmodel.User;
import com.nfssoundtrack.racingsoundtracks.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@EnableCaching
@SpringBootApplication
@Transactional
//public class Application  {
public class Application implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private AuthorCountryRepository authorCountryRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private MainGroupRepository mainGroupRepository;

    @Autowired
    private SubgroupRepository subgroupRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private SongGenreRepository songGenreRepository;

    @Autowired
    private SongSubgroupRepository songSubgroupRepository;

    @Autowired
    private AuthorAliasRepository authorAliasRepository;

    @Autowired
    private AuthorSongRepository authorSongRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (args.length > 0) {
//            Row currentRow = null;
            if (args[0].equals("NewUser")) {
                String username = args[1];
                String password = args[2];
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
                String encodedPass = passwordEncoder.encode(password);
                User user = new User();
                user.setPass(encodedPass);
                user.setLogin(username);
                userRepository.save(user);
                System.out.println("successful?");
            }
            if (args[0].equals("FixStreamingLinks")) {
                CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
                CriteriaQuery<Song> cq = criteriaBuilder.createQuery(Song.class);
                Root<Song> songRoot = cq.from(Song.class);
                Predicate spotifyIdPredicate = criteriaBuilder.like(songRoot.get("spotifyId"), "spotify:track%");
                Predicate tidalPredicate = criteriaBuilder.isNull(songRoot.get("tidalLink"));
//                Predicate idBigger = criteriaBuilder.greaterThan(songRoot.get("id"),1);
//                cq.where(spotifyIdPredicate,tidalPredicate);
//                cq.where(spotifyIdPredicate,tidalPredicate,idBigger);
//                cq.orderBy(criteriaBuilder.asc(songRoot.get("id")));
                TypedQuery<Song> query = entityManager.createQuery(cq);
                List<Song> songs = query.getResultList();
                List<String> iTunesSqlStatements = new ArrayList<>();
                List<String> tidalSqlStatements = new ArrayList<>();
                List<String> soundcloudSqlStatements = new ArrayList<>();
                List<String> deezerSqlStatements = new ArrayList<>();
                Song song = null;
                for (int i = 0; i < 1000; i++) {
//                for (Song song : songs) {
                    System.out.println("index " + i);
                    try {
                        StringBuilder content = new StringBuilder();
                        song = songs.get(i);
                        URL url = new URL("https://odesli.co/embed?url=" + song.getSpotifyId());
                        URLConnection urlConnection = url.openConnection();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            content.append(line).append("\n");
                        }
                        bufferedReader.close();
                        String valueToCheck = content.toString();
                        int beginPositionITunes = valueToCheck.indexOf("https://geo.music.apple.com");
                        if (beginPositionITunes > -1) {
                            int endPosition = valueToCheck.indexOf("0026mt=1");
                            String iTunesLink = valueToCheck.substring(beginPositionITunes, endPosition - 2);
                            String fixItunesSql = "update nfs.song set itunes_link='" + iTunesLink + "' where id='" + song.getId() + "' and spotify_id='" + song.getSpotifyId() + "';";
                            iTunesSqlStatements.add(fixItunesSql);
                            String fixItunesSql2 = "update nfs.song_subgroup set itunes_link='" + iTunesLink + "' where  spotify_id='" + song.getSpotifyId() + "';";
                            iTunesSqlStatements.add(fixItunesSql2);
//                            System.out.println("???");
                        }
                        int beginPositionTidal = valueToCheck.indexOf("https://listen.tidal.com");
                        if (beginPositionTidal > -1) {
                            int endPosition = beginPositionTidal + 40;
                            String tidalLink = valueToCheck.substring(beginPositionTidal, endPosition);
                            String fixTidalLink = "update nfs.song set tidal_link='" + tidalLink + "' where id='" + song.getId() + "' and spotify_id='" + song.getSpotifyId() + "';";
                            fixTidalLink = fixTidalLink.replace("\"", "").replace("}", "").replace(",", "");
                            tidalSqlStatements.add(fixTidalLink);
                            String fixTidalLink2 = "update nfs.song_subgroup set tidal_link='" + tidalLink + "' where spotify_id='" + song.getSpotifyId() + "';";
                            fixTidalLink2 = fixTidalLink2.replace("\"", "").replace("}", "").replace(",", "");
                            tidalSqlStatements.add(fixTidalLink2);
//                            System.out.println("???");
                        }
                        int beginPositionDeezer = valueToCheck.indexOf("www.deezer.com");
                        if (beginPositionDeezer > -1) {
                            int endPosition = beginPositionDeezer + 30;
                            String deezerId = "deezer://" + valueToCheck.substring(beginPositionDeezer, endPosition);
                            String fixDeezerId = "update nfs.song set deezer_id='" + deezerId + "' where id='" + song.getId() + "' and spotify_id='" + song.getSpotifyId() + "';";
                            fixDeezerId = fixDeezerId.replace("\"", "").replace("}", "").replace(",", "");
                            deezerSqlStatements.add(fixDeezerId);
                            String fixDeezerId2 = "update nfs.song_subgroup set deezer_id='" + deezerId + "' where spotify_id='" + song.getSpotifyId() + "';";
                            fixDeezerId2 = fixDeezerId2.replace("\"", "").replace("}", "").replace(",", "");
                            deezerSqlStatements.add(fixDeezerId2);
//                            System.out.println("???");
                        }
                        int beginPositionSoundcloud = valueToCheck.indexOf("https://soundcloud.com");
                        if (beginPositionSoundcloud > -1) {
                            int endPosition = valueToCheck.indexOf("\"", beginPositionSoundcloud);
                            String soundcloudLink = valueToCheck.substring(beginPositionSoundcloud, endPosition);
                            String fixSoundcloudLink = "update nfs.song set soundcloud_link='" + soundcloudLink + "' where id='" + song.getId() + "' and spotify_id='" + song.getSpotifyId() + "';";
                            soundcloudSqlStatements.add(fixSoundcloudLink);
                            String fixSoundcloudLink2 = "update nfs.song_subgroup set soundcloud_link='" + soundcloudLink + "' where spotify_id='" + song.getSpotifyId() + "';";
                            soundcloudSqlStatements.add(fixSoundcloudLink2);
//                            System.out.println("???");
                        }
                        Thread.sleep(500);
                    } catch (Throwable thr) {
                        System.out.println("couldn't process response for song " + song);
                        System.out.println(thr.getMessage());
                        thr.printStackTrace();
                    }
                }
                try {
                    Path iTunesFilePath = Paths.get("iTunesFilePath.sql");
                    Path tidalFilePath = Paths.get("tidalFilePath.sql");
                    Path deezerFilePath = Paths.get("deezerFilePath.sql");
                    Path soundcloudFilePath = Paths.get("soundcloudFilePath.sql");
                    Path fullFixPath = Paths.get("fullFix.sql");
                    Files.deleteIfExists(iTunesFilePath);
                    Files.deleteIfExists(tidalFilePath);
                    Files.deleteIfExists(deezerFilePath);
                    Files.deleteIfExists(soundcloudFilePath);
                    Files.createFile(iTunesFilePath);
                    Files.createFile(tidalFilePath);
                    Files.createFile(deezerFilePath);
                    Files.createFile(soundcloudFilePath);
                    for (String str : iTunesSqlStatements) {
                        Files.writeString(iTunesFilePath, str + System.lineSeparator(),
                                StandardOpenOption.APPEND);
                        Files.writeString(fullFixPath, str + System.lineSeparator(),
                                StandardOpenOption.APPEND);
                    }
                    for (String str : tidalSqlStatements) {
                        Files.writeString(tidalFilePath, str + System.lineSeparator(),
                                StandardOpenOption.APPEND);
                        Files.writeString(fullFixPath, str + System.lineSeparator(),
                                StandardOpenOption.APPEND);
                    }
                    for (String str : deezerSqlStatements) {
                        Files.writeString(deezerFilePath, str + System.lineSeparator(),
                                StandardOpenOption.APPEND);
                        Files.writeString(fullFixPath, str + System.lineSeparator(),
                                StandardOpenOption.APPEND);
                    }
                    for (String str : soundcloudSqlStatements) {
                        Files.writeString(soundcloudFilePath, str + System.lineSeparator(),
                                StandardOpenOption.APPEND);
                        Files.writeString(fullFixPath, str + System.lineSeparator(),
                                StandardOpenOption.APPEND);
                    }
                } catch (Throwable ttt) {
                    System.out.println("couldn't creating files");
                    System.out.println(ttt.getMessage());
                    ttt.printStackTrace();
                }
                System.out.println("???");
            }
//            if (args[0].equals("SingleGame")) {
//                try {
//                    FileInputStream stream = new FileInputStream("D:\\Mateusza dane\\temp_db_dump\\gameid9.xlsx");
//                    XSSFWorkbook excelWookBook = new XSSFWorkbook(stream);
//                    XSSFSheet mySheet = excelWookBook.getSheet("Arkusz1");
//                    Set<Row> filteredRows = new HashSet<>();
//                    Game game = null;
//                    MainGroup mainGroup = null;
//                    Subgroup subgroup = null;
//                    MainGroup tempGroup = null;
//                    Subgroup tempSubgroup = null;
//                    for (Row row : mySheet) {
//                        if (row.getRowNum() == 0) {
//                            continue;
//                        }
//                        long id = (long) row.getCell(0).getNumericCellValue();
//                        long position = (long) row.getCell(1).getNumericCellValue();
//                        String band = row.getCell(3).getStringCellValue();
//                        long country_id = (long) row.getCell(4).getNumericCellValue();
//                        long game_id = (long) row.getCell(5).getNumericCellValue();
//                        String title = row.getCell(6).getStringCellValue();
//                        String itunes_embed = null;
//                        if (row.getCell(7) != null) {
//                            itunes_embed = row.getCell(7).getStringCellValue();
//                        }
//                        String src_id = row.getCell(9).toString();
//                        String info = null;
//                        if (row.getCell(10) != null) {
//                            info = row.getCell(10).getStringCellValue();
//                        }
//                        String genre = null;
//                        if (row.getCell(14) != null) {
//                            genre = row.getCell(14).getStringCellValue();
//                        }
//                        String lyrics = null;
//                        if (row.getCell(15) != null) {
//                            lyrics = row.getCell(15).toString();
//                        }
//                        if (game == null) {
//                            game = gameRepository.findById(Math.toIntExact(game_id)).get();
//                            mainGroup = game.getMainGroups().stream().filter(mainGroup1 ->
//                                    mainGroup1.getGroupName().equals("All")).findFirst().get();
//                            subgroup = mainGroup.getSubgroups().stream().filter(subgroup1 ->
//                                    subgroup1.getSubgroupName().equals("All")).findFirst().get();
//                        }
//                        if (band.equals("Somebody") || band.equals("Somebodies")) {
//                            band = band + "game_id" + game_id;
//                        }
//                        if (band.equals("unknown")) {
//                            if (tempGroup == null) {
//                                tempGroup = new MainGroup("Custom", game);
//                                tempGroup = mainGroupRepository.saveAndFlush(tempGroup);
//                                tempSubgroup = new Subgroup("All", 1, tempGroup);
//                                tempSubgroup = subgroupRepository.saveAndFlush(tempSubgroup);
//                            }
//                            tempSubgroup = new Subgroup(title, Math.toIntExact(position) * 10, tempGroup);
//                            tempSubgroup = subgroupRepository.saveAndFlush(tempSubgroup);
//                        } else {
//                            Author existingBand = null;
//                            Optional<Author> optionalExistingBand = authorRepository.findByName(band);
//                            if (optionalExistingBand.isEmpty()) {
//                                Author newBand = new Author();
//                                newBand.setName(band);
//                                existingBand = authorRepository.saveAndFlush(newBand);
//                                if (country_id != 0) {
//                                    Country country = countryRepository.findById(Math.toIntExact(country_id)).get();
//                                    AuthorCountry authorCountry = new AuthorCountry();
//                                    authorCountry.setCountry(country);
//                                    authorCountry.setAuthor(existingBand);
//                                    authorCountryRepository.saveAndFlush(authorCountry);
//                                }
//                            } else {
//                                existingBand = optionalExistingBand.get();
//                            }
//                            AuthorAlias authorAliasToPersist = null;
//                            Optional<AuthorAlias> authorAlias = authorAliasRepository.findByAlias(band);
//                            if (authorAlias.isEmpty()) {
//                                AuthorAlias authorAlias1 = new AuthorAlias(existingBand, band);
//                                authorAliasToPersist = authorAliasRepository.saveAndFlush(authorAlias1);
//                            } else {
//                                authorAliasToPersist = authorAlias.get();
//                            }
//                            Song mySong;
//                            List<Song> alreadyExistingSongs = songRepository.findByOfficialDisplayBandAndOfficialDisplayTitle(
//                                    band, title);
//                            if (alreadyExistingSongs.isEmpty()) {
//                                mySong = new Song(band, title, src_id, lyrics, null, null, null, null, null);
////                            mySong.setId(id);
//                                mySong.setOfficialDisplayBand(band);
//                                mySong.setOfficialDisplayTitle(title);
//                                mySong.setSrcId(src_id);
////                                mySong.setInfo(info);
//                                mySong.setLyrics(lyrics);
//                                mySong = songRepository.saveAndFlush(mySong);
//                            } else {
//                                mySong = alreadyExistingSongs.get(0);
//                            }
//                            if (genre != null && !genre.isEmpty()) {
//                                Genre myGenre;
//                                Optional<Genre> optionalGenre = genreRepository.findByGenreName(genre);
//                                if (optionalGenre.isEmpty()) {
//                                    myGenre = new Genre();
//                                    myGenre.setGenreName(genre);
//                                    genreRepository.saveAndFlush(myGenre);
//                                } else {
//                                    myGenre = optionalGenre.get();
//                                }
//                                List<SongGenre> songGenres = songGenreRepository.findBySong(mySong);
//                                if (songGenres.isEmpty()) {
//                                    SongGenre songGenre = new SongGenre(mySong, myGenre);
//                                    songGenreRepository.saveAndFlush(songGenre);
//                                }
//                            }
//                            AuthorSong authorSong;
//                            Optional<AuthorSong> optionalAuthorSong =
//                                    authorSongRepository.findByAuthorAliasAndSong(authorAliasToPersist, mySong);
//                            if (optionalAuthorSong.isEmpty()) {
//                                authorSong = new AuthorSong(authorAliasToPersist, mySong, Role.COMPOSER);
//                                authorSongRepository.save(authorSong);
//                            }
//                            SongSubgroup songSubgroup = new SongSubgroup();
//                            songSubgroup.setSong(mySong);
//                            if (title.contains("Instrumental")) {
//                                songSubgroup.setInstrumental(Instrumental.YES);
//                            } else {
//                                songSubgroup.setInstrumental(Instrumental.NO);
//                            }
//                            if (title.contains("Remix")) {
//                                songSubgroup.setRemix(Remix.YES);
//                            } else {
//                                songSubgroup.setRemix(Remix.NO);
//                            }
//                            songSubgroup.setSrcId(src_id);
//                            songSubgroup.setPosition(position * 10);
//                            songSubgroup.setLyrics(lyrics);
//                            if (itunes_embed != null && !itunes_embed.isEmpty()) {
//                                int indexOfSpotify = itunes_embed.indexOf("spotify:track");
//                                if (indexOfSpotify > -1) {
//                                    String spotifyThing = itunes_embed.substring(indexOfSpotify, indexOfSpotify + 36);
//                                    songSubgroup.setSpotifyId(spotifyThing);
//                                }
//                            }
//                            songSubgroup.setSubgroup(subgroup);
//                            songSubgroup = songSubgroupRepository.saveAndFlush(songSubgroup);
//                            if (tempSubgroup != null) {
//                                SongSubgroup tempSongSubgroup = getSongSubgroup(songSubgroup, tempSubgroup);
//                                songSubgroupRepository.saveAndFlush(tempSongSubgroup);
//                            }
//                        }
//                        System.out.println("???");
//                    }
//                } catch (Throwable throwable) {
//                    System.out.println("AAAAAAAAAAAAAA- " + throwable.getMessage());
//                    throwable.printStackTrace();
//                }
//            } else if (args[0].equals("AllGames")) {
//                try {
//                    FileInputStream stream = new FileInputStream(args[1]);
//                    XSSFWorkbook excelWookBook = new XSSFWorkbook(stream);
//                    XSSFSheet mySheet = excelWookBook.getSheet(args[2]);
//                    Set<Row> filteredRows = new HashSet<>();
//                    int gameTraversedNow = 0;
//                    Game game = null;
//                    MainGroup tempGroup = null;
//                    Subgroup tempSubgroup = null;
//                    for (Row row : mySheet) {
//                        currentRow = row;
//                        Long id = (long) row.getCell(0).getNumericCellValue();
//                        Long position = (long) row.getCell(1).getNumericCellValue();
//                        String band = row.getCell(3).toString().trim();
//                        Long country_id = (long) row.getCell(4).getNumericCellValue();
//                        Long game_id = (long) row.getCell(5).getNumericCellValue();
//                        String title = row.getCell(6).toString().trim();
//                        String itunes_embed = null;
//                        if (row.getCell(7) != null) {
//                            itunes_embed = row.getCell(7).getStringCellValue().trim();
//                        }
//                        String src_id = row.getCell(9).toString().trim();
//                        String info = null;
//                        if (row.getCell(10) != null) {
//                            info = row.getCell(10).toString().trim();
//                        }
//                        String genre = null;
//                        if (row.getCell(14) != null) {
//                            genre = row.getCell(14).getStringCellValue().trim();
//                        }
//                        String lyrics = null;
//                        if (row.getCell(15) != null) {
//                            lyrics = row.getCell(15).toString().trim();
//                        }
//                        if (Math.toIntExact(game_id) != gameTraversedNow) {
//                            game = gameRepository.findById(game_id.intValue()).get();
//                            List<MainGroup> mainGroups = game.getMainGroups();
//                            if (!mainGroups.isEmpty()) {
//                                //we start with 2nd or 3rd or next sheet so we have to continue from where hwe stopped
//                                tempGroup = mainGroups.stream().filter(
//                                        mainGroup -> mainGroup.getGroupName().equals("Custom")).findFirst().get();
//                                tempGroup.getSubgroups().sort(Comparator.comparing(Subgroup::getPosition));
//                                tempSubgroup = tempGroup.getSubgroups().get(tempGroup.getSubgroups().size() - 1);
//                            } else {
//                                MainGroup allGroup = new MainGroup("All", game);
//                                mainGroupRepository.save(allGroup);
//                                Subgroup allSubgroup = new Subgroup("All", 1, allGroup);
//                                subgroupRepository.save(allSubgroup);
//                                gameTraversedNow = Math.toIntExact(game_id);
//                                tempGroup = new MainGroup("Custom", game);
//                                tempGroup = mainGroupRepository.save(tempGroup);
//                                tempSubgroup = new Subgroup("All", 1, tempGroup);
//                                subgroupRepository.save(tempSubgroup);
//                                tempSubgroup = new Subgroup("Ungrouped", 2, tempGroup);
//                                subgroupRepository.save(tempSubgroup);
//                            }
////                            tempSubgroup=null;
//                        }
//                        if (band.equals("Somebody") || band.equals("Somebodies")) {
//                            band = band + "game_id" + game_id;
//                        }
//                        if (band.equals("unknown")) {
//                            tempSubgroup = new Subgroup(title, Math.toIntExact(position) * 10, tempGroup);
//                            tempSubgroup = subgroupRepository.saveAndFlush(tempSubgroup);
//                        } else {
//                            Author existingBand = null;
//                            Optional<Author> optionalExistingBand = authorRepository.findByName(band);
//                            if (optionalExistingBand.isEmpty()) {
//                                Author newBand = new Author();
//                                newBand.setName(band);
//                                existingBand = authorRepository.saveAndFlush(newBand);
//                                if (country_id != 0) {
//                                    Country country = countryRepository.findById(country_id.intValue()).get();
//                                    AuthorCountry authorCountry = new AuthorCountry();
//                                    authorCountry.setCountry(country);
//                                    authorCountry.setAuthor(existingBand);
//                                    authorCountryRepository.saveAndFlush(authorCountry);
//                                }
//                            }
//                            AuthorAlias authorAlias = null;
//                            Optional<AuthorAlias> optionalAuthorAlias = authorAliasRepository.findByAlias(band);
//                            if (optionalAuthorAlias.isEmpty()) {
//                                AuthorAlias authorAlias1 = new AuthorAlias(existingBand, band);
//                                authorAlias = authorAliasRepository.saveAndFlush(authorAlias1);
//                            }
//                            Song mySong = null;
//                            List<Song> alreadyExistingSongs = songRepository.findByOfficialDisplayBandAndOfficialDisplayTitle(
//                                    band, title);
//                            if (alreadyExistingSongs.isEmpty()) {
//                                mySong = new Song();
////                            mySong.setId(id);
//                                mySong.setOfficialDisplayBand(band);
//                                mySong.setOfficialDisplayTitle(title);
//                                if (src_id.equals("0") || src_id.equals("0.0")) {
//                                    mySong.setSrcId(null);
//                                } else {
//                                    mySong.setSrcId(src_id);
//                                }
//                                if (lyrics != null) {
//                                    if (lyrics.equals("0") || lyrics.equals("0.0")) {
//                                        mySong.setLyrics(null);
//                                    } else {
//                                        mySong.setLyrics(lyrics);
//                                    }
//                                } else {
//                                    mySong.setLyrics(null);
//                                }
//                                if (itunes_embed != null && !itunes_embed.isEmpty()) {
//                                    int indexOfSpotify = itunes_embed.indexOf("spotify:track");
//                                    if (indexOfSpotify > -1) {
//                                        String spotifyThing = itunes_embed.substring(indexOfSpotify,
//                                                indexOfSpotify + 36);
//                                        mySong.setSpotifyId(spotifyThing.trim());
//                                    }
//                                    int indexOfDeezer = itunes_embed.indexOf("deezer:");
//                                    if (indexOfDeezer > -1) {
//                                        String deezerThing = itunes_embed.substring(indexOfDeezer, indexOfDeezer + 37);
//                                        mySong.setDeezerId(deezerThing.trim());
//                                    }
//                                    String itunesThing = null;
//                                    int indexOfItunesNew = itunes_embed.indexOf("https://itunes.apple.com");
//                                    int indexOfReferer = itunes_embed.indexOf("at=11lJZP");
//                                    if (indexOfItunesNew > -1 && indexOfReferer > -1) {
//                                        itunesThing = itunes_embed.substring(indexOfItunesNew, indexOfReferer + 9);
//                                    }
//                                    int indexOfGeoMusic = itunes_embed.indexOf("https://geo.music.apple.com");
//                                    if (indexOfGeoMusic > -1 && indexOfReferer > -1) {
//                                        itunesThing = itunes_embed.substring(indexOfGeoMusic, indexOfReferer + 9);
//                                    }
//                                    int indexOfGeoItunes = itunes_embed.indexOf("https://geo.itunes.apple.com");
//                                    if (indexOfGeoItunes > -1 && indexOfReferer > -1) {
//                                        itunesThing = itunes_embed.substring(indexOfGeoItunes, indexOfReferer + 9);
//                                    }
//                                    if (itunesThing != null) {
//                                        itunesThing = itunesThing.replace("geo.itunes.apple.com", "music.apple.com");
//                                        itunesThing = itunesThing.replace("geo.music.apple.com", "music.apple.com");
//                                        itunesThing = itunesThing.replace("itunes.apple.com", "music.apple.com");
//                                        mySong.setItunesLink(itunesThing.trim());
//                                    }
//                                }
//                                mySong = songRepository.saveAndFlush(mySong);
//                            } else {
//                                mySong = alreadyExistingSongs.get(0);
//                            }
//                            if (genre != null && !genre.isEmpty()) {
//                                Genre myGenre;
//                                Optional<Genre> optionalGenre = genreRepository.findByGenreName(genre);
//                                if (optionalGenre.isEmpty()) {
//                                    myGenre = new Genre();
//                                    myGenre.setGenreName(genre);
//                                    genreRepository.saveAndFlush(myGenre);
//                                } else {
//                                    myGenre = optionalGenre.get();
//                                }
//                                List<SongGenre> songGenres = songGenreRepository.findBySong(mySong);
//                                if (songGenres.isEmpty()) {
//                                    SongGenre songGenre = new SongGenre();
//                                    songGenre.setGenre(myGenre);
//                                    songGenre.setSong(mySong);
//                                    songGenreRepository.saveAndFlush(songGenre);
//                                }
//                            }
//                            AuthorSong authorSong;
//                            Optional<AuthorSong> optionalAuthorSong =
//                                    authorSongRepository.findByAuthorAliasAndSong(authorAlias, mySong);
//                            if (optionalAuthorSong.isEmpty()) {
//                                authorSong = new AuthorSong(authorAlias, mySong, Role.COMPOSER);
//                                authorSongRepository.save(authorSong);
//                            }
//                            SongSubgroup songSubgroup = new SongSubgroup();
//                            songSubgroup.setSong(mySong);
//                            if (title.contains("Instrumental")) {
//                                songSubgroup.setInstrumental(Instrumental.YES);
//                            } else {
//                                songSubgroup.setInstrumental(Instrumental.NO);
//                            }
//                            if (title.contains("Remix")) {
//                                songSubgroup.setRemix(Remix.YES);
//                            } else {
//                                songSubgroup.setRemix(Remix.NO);
//                            }
//                            songSubgroup.setSrcId(mySong.getSrcId());
//                            songSubgroup.setPosition(position * 10);
//                            songSubgroup.setLyrics(mySong.getLyrics());
//                            songSubgroup.setSpotifyId(mySong.getSpotifyId());
//                            songSubgroup.setDeezerId(mySong.getDeezerId());
//                            songSubgroup.setItunesLink(mySong.getItunesLink());
//                            songSubgroup.setSpotifyId(mySong.getSpotifyId());
//                            songSubgroup.setSpotifyId(mySong.getSpotifyId());
//                            songSubgroup.setSubgroup(tempSubgroup);
//                            songSubgroup.setInfo(info);
//                            songSubgroup = songSubgroupRepository.saveAndFlush(songSubgroup);
//                        }
//                        System.out.println("done with row " + row.getRowNum());
//                    }
//                } catch (Throwable throwable) {
//                    System.out.println("row " + currentRow);
//                    System.out.println("AAAAAAAAAAAAAA- " + throwable.getMessage());
//                    throwable.printStackTrace();
//                }
//        System.exit(0);

//            if (args[1].equals("FindCloseSongEntries")){
//                try {
//                    List<String> allStrings = FileUtils.readLines(new File("song_grouping.csv"), StandardCharsets.UTF_8);
//                    Map<String, List<String>> authorToComparableText = new TreeMap<>();
//                    for (String singleLine : allStrings){
//                        String[] split = singleLine.replaceAll("\"","").split(";");
//                        if (authorToComparableText.get(split[0])!=null){
//                            List<String> songsList = authorToComparableText.get(split[0]);
//                            songsList.add(split[1]);
//                            authorToComparableText.replace(split[0], songsList);
//                        } else {
//                            List<String> songsList = new ArrayList<>();
//                            songsList.add(split[1]);
//                            authorToComparableText.put(split[0], songsList);
//                        }
//                    }
            //map filled so let's do the levenshtein comparison
//                    LevenshteinDistance distance = LevenshteinDistance.getDefaultInstance();
//                    for (Map.Entry<String,List<String>> entry : authorToComparableText.entrySet()){
//                        List<String> songTitles = entry.getValue();
//                        if (entry.getKey().startsWith("Somebodygame")){
//                            continue;
//                        }
//                        for (int i=0; i<songTitles.size(); i++){
//                            String leftSide = songTitles.get(i);
////                            if (leftSide.length()>6){
//                                for (int j=i+1; j<songTitles.size(); j++){
//                                    String rightSide = songTitles.get(j);
//                                    Integer songDistance = distance.apply(leftSide,rightSide);
//                                    if (songDistance==4){
//                                        System.out.println("\n\npotentially close match for\nauthor: " + entry.getKey()
//                                                + "\nTitle 1: " + leftSide + "\nTitle 2: " + rightSide);
//                                    }
//                                }
////                            }
//                        }
//                    }
//                    String[] authors = authorToComparableText.keySet().toArray(new String[0]);
//                    for (int i=0; i<authors.length; i++){
//                        String authorString = authors[i];
//                        if (authorString.startsWith("Somebodygame")){
//                            continue;
//                        }
//                        if (authorString.length()<=4){
//                            continue;
//                        }
//                        for (int j=i+1; j<authors.length; j++){
//                            String toCompareAuthor = authors[j];
//                            Integer songDistance = distance.apply(authorString,toCompareAuthor);
//                            if (songDistance==3){
//                                System.out.println("\n\npotentially close match for\n" +
//                                        "\nAuthor 1: " + authorString + "\nAuthor 2: " + toCompareAuthor);
//                            }
//                        }
//                    }
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }

        }
    }

    private static SongSubgroup getSongSubgroup(SongSubgroup songSubgroup, Subgroup tempSubgroup) {
        SongSubgroup tempSongSubgroup = new SongSubgroup();
        tempSongSubgroup.setRemix(songSubgroup.getRemix());
        tempSongSubgroup.setInstrumental(songSubgroup.getInstrumental());
        tempSongSubgroup.setSrcId(songSubgroup.getSrcId());
        tempSongSubgroup.setPosition(songSubgroup.getPosition());
        tempSongSubgroup.setLyrics(songSubgroup.getLyrics());
        tempSongSubgroup.setSubgroup(tempSubgroup);
        tempSongSubgroup.setSong(songSubgroup.getSong());
        tempSongSubgroup.setSpotifyId(songSubgroup.getSpotifyId());
        return tempSongSubgroup;
    }

}
