package com.nfssoundtrack.NFSSoundtrack_20;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.repository.*;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.io.FileInputStream;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EnableCaching
@SpringBootApplication
@Transactional
//public class Application  {
public class Application implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

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

    @Override
    public void run(String... args) {
        if (args.length > 0) {
            Row currentRow = null;
            if (args[0].equals("SingleGame")) {
                try {
                    FileInputStream stream = new FileInputStream("D:\\Mateusza dane\\temp_db_dump\\gameid9.xlsx");
                    XSSFWorkbook excelWookBook = new XSSFWorkbook(stream);
                    XSSFSheet mySheet = excelWookBook.getSheet("Arkusz1");
                    Set<Row> filteredRows = new HashSet<>();
                    Game game = null;
                    MainGroup mainGroup = null;
                    Subgroup subgroup = null;
                    MainGroup tempGroup = null;
                    Subgroup tempSubgroup = null;
                    for (Row row : mySheet) {
                        if (row.getRowNum() == 0) {
                            continue;
                        }
                        Long id = (long) row.getCell(0).getNumericCellValue();
                        Long position = (long) row.getCell(1).getNumericCellValue();
                        String band = row.getCell(3).getStringCellValue();
                        Long country_id = (long) row.getCell(4).getNumericCellValue();
                        Long game_id = (long) row.getCell(5).getNumericCellValue();
                        String title = row.getCell(6).getStringCellValue();
                        String itunes_embed = null;
                        if (row.getCell(7) != null) {
                            itunes_embed = row.getCell(7).getStringCellValue();
                        }
                        String src_id = row.getCell(9).toString();
                        String info = null;
                        if (row.getCell(10) != null) {
                            info = row.getCell(10).getStringCellValue();
                        }
                        String genre = null;
                        if (row.getCell(14) != null) {
                            genre = row.getCell(14).getStringCellValue();
                        }
                        String lyrics = null;
                        if (row.getCell(15) != null) {
                            lyrics = row.getCell(15).toString();
                        }
                        if (game == null) {
                            game = gameRepository.findById(game_id.intValue()).get();
                            mainGroup = game.getMainGroups().stream().filter(mainGroup1 ->
                                    mainGroup1.getGroupName().equals("All")).findFirst().get();
                            subgroup = mainGroup.getSubgroups().stream().filter(subgroup1 ->
                                    subgroup1.getSubgroupName().equals("All")).findFirst().get();
                        }
                        if (band.equals("Somebody") || band.equals("Somebodies")) {
                            band = band + "game_id" + game_id;
                        }
                        if (band.equals("unknown")) {
                            if (tempGroup == null) {
                                tempGroup = new MainGroup();
                                tempGroup.setGame(game);
                                tempGroup.setGroupName("Custom");
                                tempGroup = mainGroupRepository.saveAndFlush(tempGroup);
                                tempSubgroup = new Subgroup();
                                tempSubgroup.setPosition(1);
                                tempSubgroup.setSubgroupName("All");
                                tempSubgroup.setMainGroup(tempGroup);
                                tempSubgroup = subgroupRepository.saveAndFlush(tempSubgroup);
                            }
                            tempSubgroup = new Subgroup();
                            tempSubgroup.setPosition(Math.toIntExact(position) * 10);
                            tempSubgroup.setSubgroupName(title);
                            tempSubgroup.setMainGroup(tempGroup);
                            tempSubgroup = subgroupRepository.saveAndFlush(tempSubgroup);
                        } else {
                            Author existingBand = authorRepository.findByName(band);
                            if (existingBand == null) {
                                Author newBand = new Author();
                                newBand.setName(band);
                                existingBand = authorRepository.saveAndFlush(newBand);
                                if (country_id != 0) {
                                    Country country = countryRepository.findById(country_id.intValue()).get();
                                    AuthorCountry authorCountry = new AuthorCountry();
                                    authorCountry.setCountry(country);
                                    authorCountry.setAuthor(existingBand);
                                    authorCountryRepository.saveAndFlush(authorCountry);
                                }
                            }
                            AuthorAlias authorAlias = authorAliasRepository.findByAlias(band);
                            if (authorAlias == null) {
                                AuthorAlias authorAlias1 = new AuthorAlias();
                                authorAlias1.setAlias(band);
                                authorAlias1.setAuthor(existingBand);
                                authorAlias = authorAliasRepository.saveAndFlush(authorAlias1);
                            }
                            Song mySong = null;
                            List<Song> alreadyExistingSongs = songRepository.findByOfficialDisplayBandAndOfficialDisplayTitle(band, title);
                            if (alreadyExistingSongs.isEmpty()) {
                                mySong = new Song();
//                            mySong.setId(id);
                                mySong.setOfficialDisplayBand(band);
                                mySong.setOfficialDisplayTitle(title);
                                mySong.setSrcId(src_id);
//                                mySong.setInfo(info);
                                mySong.setLyrics(lyrics);
                                mySong = songRepository.saveAndFlush(mySong);
                            } else {
                                mySong = alreadyExistingSongs.get(0);
                            }
                            if (genre != null && !genre.isEmpty()) {
                                Genre myGenre = genreRepository.findByGenreName(genre);
                                if (myGenre == null) {
                                    myGenre = new Genre();
                                    myGenre.setGenreName(genre);
                                    genreRepository.saveAndFlush(myGenre);
                                }
                                SongGenre songGenre = songGenreRepository.findBySong(mySong);
                                if (songGenre == null) {
                                    songGenre = new SongGenre();
                                    songGenre.setGenre(myGenre);
                                    songGenre.setSong(mySong);
                                    songGenreRepository.saveAndFlush(songGenre);
                                }
                            }
                            AuthorSong authorSong = authorSongRepository.findByAuthorAliasAndSong(authorAlias, mySong);
                            if (authorSong == null) {
                                authorSong = new AuthorSong();
                                authorSong.setSong(mySong);
                                authorSong.setAuthorAlias(authorAlias);
                                authorSong.setRole(Role.COMPOSER);
                                authorSongRepository.save(authorSong);
                            }
                            SongSubgroup songSubgroup = new SongSubgroup();
                            songSubgroup.setSong(mySong);
                            if (title.contains("Instrumental")) {
                                songSubgroup.setInstrumental(Instrumental.YES);
                            } else {
                                songSubgroup.setInstrumental(Instrumental.NO);
                            }
                            if (title.contains("Remix")) {
                                songSubgroup.setRemix(Remix.YES);
                            } else {
                                songSubgroup.setRemix(Remix.NO);
                            }
                            songSubgroup.setSrcId(src_id);
                            songSubgroup.setPosition(position * 10);
                            songSubgroup.setLyrics(lyrics);
                            if (itunes_embed != null && !itunes_embed.isEmpty()) {
                                int indexOfSpotify = itunes_embed.indexOf("spotify:track");
                                if (indexOfSpotify > -1) {
                                    String spotifyThing = itunes_embed.substring(indexOfSpotify, indexOfSpotify + 36);
                                    songSubgroup.setSpotifyId(spotifyThing);
                                }
                            }
                            songSubgroup.setSubgroup(subgroup);
                            songSubgroup = songSubgroupRepository.saveAndFlush(songSubgroup);
                            if (tempSubgroup != null) {
                                SongSubgroup tempSongSubgroup = new SongSubgroup();
                                tempSongSubgroup.setRemix(songSubgroup.getRemix());
                                tempSongSubgroup.setInstrumental(songSubgroup.getInstrumental());
                                tempSongSubgroup.setSrcId(songSubgroup.getSrcId());
                                tempSongSubgroup.setPosition(songSubgroup.getPosition());
                                tempSongSubgroup.setLyrics(songSubgroup.getLyrics());
                                tempSongSubgroup.setSubgroup(tempSubgroup);
                                tempSongSubgroup.setSong(songSubgroup.getSong());
                                tempSongSubgroup.setSpotifyId(songSubgroup.getSpotifyId());
                                songSubgroupRepository.saveAndFlush(tempSongSubgroup);
                            }
                        }
                        System.out.println("???");
                    }
                } catch (Throwable throwable) {
                    System.out.println("AAAAAAAAAAAAAA- " + throwable.getMessage());
                    throwable.printStackTrace();
                }
            } else if (args[0].equals("AllGames")) {
                try {
                    FileInputStream stream = new FileInputStream(args[1]);
                    XSSFWorkbook excelWookBook = new XSSFWorkbook(stream);
                    XSSFSheet mySheet = excelWookBook.getSheet(args[2]);
                    Set<Row> filteredRows = new HashSet<>();
                    int gameTraversedNow = 0;
                    Game game = null;
                    MainGroup tempGroup = null;
                    Subgroup tempSubgroup = null;
                    for (Row row : mySheet) {
//                        if (row.getRowNum() == 0) {
//                            continue;
//                        }
                        currentRow = row;
                        Long id = (long) row.getCell(0).getNumericCellValue();
                        Long position = (long) row.getCell(1).getNumericCellValue();
                        String band = row.getCell(3).toString().trim();
                        Long country_id = (long) row.getCell(4).getNumericCellValue();
                        Long game_id = (long) row.getCell(5).getNumericCellValue();
                        String title = row.getCell(6).toString().trim();
                        String itunes_embed = null;
                        if (row.getCell(7) != null) {
                            itunes_embed = row.getCell(7).getStringCellValue().trim();
                        }
                        String src_id = row.getCell(9).toString().trim();
                        String info = null;
                        if (row.getCell(10) != null) {
                            info = row.getCell(10).toString().trim();
                        }
                        String genre = null;
                        if (row.getCell(14) != null) {
                            genre = row.getCell(14).getStringCellValue().trim();
                        }
                        String lyrics = null;
                        if (row.getCell(15) != null) {
                            lyrics = row.getCell(15).toString().trim();
                        }
                        if (Math.toIntExact(game_id) != gameTraversedNow) {
                            game = gameRepository.findById(game_id.intValue()).get();
                            List<MainGroup> mainGroups = game.getMainGroups();
                            if (!mainGroups.isEmpty()) {
                                //we start with 2nd or 3rd or next sheet so we have to continue from where hwe stopped
                                tempGroup = mainGroups.stream().filter(mainGroup -> mainGroup.getGroupName().equals("Custom")).findFirst().get();
                                tempGroup.getSubgroups().sort(Comparator.comparing(Subgroup::getPosition));
                                tempSubgroup = tempGroup.getSubgroups().get(tempGroup.getSubgroups().size() - 1);
                            } else {
                                MainGroup allGroup = new MainGroup();
                                allGroup.setGroupName("All");
                                allGroup.setGame(game);
                                mainGroupRepository.save(allGroup);
                                Subgroup allSubgroup = new Subgroup();
                                allSubgroup.setSubgroupName("All");
                                allSubgroup.setMainGroup(allGroup);
                                allSubgroup.setPosition(1);
                                subgroupRepository.save(allSubgroup);
                                gameTraversedNow = Math.toIntExact(game_id);
                                tempGroup = new MainGroup();
                                tempGroup.setGroupName("Custom");
                                tempGroup.setGame(game);
                                tempGroup = mainGroupRepository.save(tempGroup);
                                tempSubgroup = new Subgroup();
                                tempSubgroup.setSubgroupName("All");
                                tempSubgroup.setMainGroup(tempGroup);
                                tempSubgroup.setPosition(1);
                                subgroupRepository.save(tempSubgroup);
                                tempSubgroup = new Subgroup();
                                tempSubgroup.setSubgroupName("Ungrouped");
                                tempSubgroup.setMainGroup(tempGroup);
                                tempSubgroup.setPosition(2);
                                subgroupRepository.save(tempSubgroup);
                            }
//                            tempSubgroup=null;
                        }
                        if (band.equals("Somebody") || band.equals("Somebodies")) {
                            band = band + "game_id" + game_id;
                        }
                        if (band.equals("unknown")) {
                            tempSubgroup = new Subgroup();
                            tempSubgroup.setPosition(Math.toIntExact(position) * 10);
                            tempSubgroup.setSubgroupName(title);
                            tempSubgroup.setMainGroup(tempGroup);
                            tempSubgroup = subgroupRepository.saveAndFlush(tempSubgroup);
                        } else {
                            Author existingBand = authorRepository.findByName(band);
                            if (existingBand == null) {
                                Author newBand = new Author();
                                newBand.setName(band);
                                existingBand = authorRepository.saveAndFlush(newBand);
                                if (country_id != 0) {
                                    Country country = countryRepository.findById(country_id.intValue()).get();
                                    AuthorCountry authorCountry = new AuthorCountry();
                                    authorCountry.setCountry(country);
                                    authorCountry.setAuthor(existingBand);
                                    authorCountryRepository.saveAndFlush(authorCountry);
                                }
                            }
                            AuthorAlias authorAlias = authorAliasRepository.findByAlias(band);
                            if (authorAlias == null) {
                                AuthorAlias authorAlias1 = new AuthorAlias();
                                authorAlias1.setAlias(band);
                                authorAlias1.setAuthor(existingBand);
                                authorAlias = authorAliasRepository.saveAndFlush(authorAlias1);
                            }
                            Song mySong = null;
                            List<Song> alreadyExistingSongs = songRepository.findByOfficialDisplayBandAndOfficialDisplayTitle(band, title);
                            if (alreadyExistingSongs.isEmpty()) {
                                mySong = new Song();
//                            mySong.setId(id);
                                mySong.setOfficialDisplayBand(band);
                                mySong.setOfficialDisplayTitle(title);
                                if (src_id != null) {
                                    if (src_id.equals("0") || src_id.equals("0.0")) {
                                        mySong.setSrcId(null);
                                    } else {
                                        mySong.setSrcId(src_id);
                                    }
                                } else {
                                    mySong.setSrcId(null);
                                }
                                if (lyrics != null) {
                                    if (lyrics.equals("0") || lyrics.equals("0.0")) {
                                        mySong.setLyrics(null);
                                    } else {
                                        mySong.setLyrics(lyrics);
                                    }
                                } else {
                                    mySong.setLyrics(null);
                                }
                                if (itunes_embed != null && !itunes_embed.isEmpty()) {
                                    int indexOfSpotify = itunes_embed.indexOf("spotify:track");
                                    if (indexOfSpotify > -1) {
                                        String spotifyThing = itunes_embed.substring(indexOfSpotify, indexOfSpotify + 36);
                                        mySong.setSpotifyId(spotifyThing.trim());
                                    }
                                    int indexOfDeezer = itunes_embed.indexOf("deezer:");
                                    if (indexOfDeezer > -1) {
                                        String deezerThing = itunes_embed.substring(indexOfDeezer, indexOfDeezer + 37);
                                        mySong.setDeezerId(deezerThing.trim());
                                    }
                                    String itunesThing = null;
                                    int indexOfItunesNew = itunes_embed.indexOf("https://itunes.apple.com");
                                    int indexOfReferer = itunes_embed.indexOf("at=11lJZP");
                                    if (indexOfItunesNew > -1 && indexOfReferer > -1) {
                                        itunesThing = itunes_embed.substring(indexOfItunesNew, indexOfReferer + 9);
                                    }
                                    int indexOfGeoMusic = itunes_embed.indexOf("https://geo.music.apple.com");
                                    if (indexOfGeoMusic > -1 && indexOfReferer > -1) {
                                        itunesThing = itunes_embed.substring(indexOfGeoMusic, indexOfReferer + 9);
                                    }
                                    int indexOfGeoItunes = itunes_embed.indexOf("https://geo.itunes.apple.com");
                                    if (indexOfGeoItunes > -1 && indexOfReferer > -1) {
                                        itunesThing = itunes_embed.substring(indexOfGeoItunes, indexOfReferer + 9);
                                    }
                                    if (itunesThing != null) {
                                        itunesThing = itunesThing.replace("geo.itunes.apple.com", "music.apple.com");
                                        itunesThing = itunesThing.replace("geo.music.apple.com", "music.apple.com");
                                        itunesThing = itunesThing.replace("itunes.apple.com", "music.apple.com");
                                        mySong.setItunesLink(itunesThing.trim());
                                    }
                                }
//                                mySong.setInfo(info);
//                                mySong.setLyrics(lyrics);
                                mySong = songRepository.saveAndFlush(mySong);
                            } else {
                                mySong = alreadyExistingSongs.get(0);
                            }
                            if (genre != null && !genre.isEmpty()) {
                                Genre myGenre = genreRepository.findByGenreName(genre);
                                if (myGenre == null) {
                                    myGenre = new Genre();
                                    myGenre.setGenreName(genre);
                                    genreRepository.saveAndFlush(myGenre);
                                }
                                SongGenre songGenre = songGenreRepository.findBySong(mySong);
                                if (songGenre == null) {
                                    songGenre = new SongGenre();
                                    songGenre.setGenre(myGenre);
                                    songGenre.setSong(mySong);
                                    songGenreRepository.saveAndFlush(songGenre);
                                }
                            }
                            AuthorSong authorSong = authorSongRepository.findByAuthorAliasAndSong(authorAlias, mySong);
                            if (authorSong == null) {
                                authorSong = new AuthorSong();
                                authorSong.setSong(mySong);
                                authorSong.setAuthorAlias(authorAlias);
                                authorSong.setRole(Role.COMPOSER);
                                authorSongRepository.save(authorSong);
                            }
                            SongSubgroup songSubgroup = new SongSubgroup();
                            songSubgroup.setSong(mySong);
                            if (title.contains("Instrumental")) {
                                songSubgroup.setInstrumental(Instrumental.YES);
                            } else {
                                songSubgroup.setInstrumental(Instrumental.NO);
                            }
                            if (title.contains("Remix")) {
                                songSubgroup.setRemix(Remix.YES);
                            } else {
                                songSubgroup.setRemix(Remix.NO);
                            }
                            songSubgroup.setSrcId(mySong.getSrcId());
                            songSubgroup.setPosition(position * 10);
                            songSubgroup.setLyrics(mySong.getLyrics());
                            songSubgroup.setSpotifyId(mySong.getSpotifyId());
                            songSubgroup.setDeezerId(mySong.getDeezerId());
                            songSubgroup.setItunesLink(mySong.getItunesLink());
                            songSubgroup.setSpotifyId(mySong.getSpotifyId());
                            songSubgroup.setSpotifyId(mySong.getSpotifyId());
//                            if (itunes_embed != null && !itunes_embed.isEmpty()) {
//                                int indexOfSpotify = itunes_embed.indexOf("spotify:track");
//                                if (indexOfSpotify > -1) {
//                                    String spotifyThing = itunes_embed.substring(indexOfSpotify, indexOfSpotify + 36);
//                                    songSubgroup.setSpotifyId(spotifyThing.trim());
//                                }
//                                int indexOfDeezer = itunes_embed.indexOf("deezer:");
//                                if (indexOfDeezer > -1) {
//                                    String deezerThing = itunes_embed.substring(indexOfDeezer, indexOfDeezer + 37);
//                                    songSubgroup.setDeezerId(deezerThing.trim());
//                                }
//                                String itunesThing = null;
//                                int indexOfItunesNew = itunes_embed.indexOf("https://itunes.apple.com");
//                                int indexOfReferer = itunes_embed.indexOf("at=11lJZP");
//                                if (indexOfItunesNew > -1 && indexOfReferer > -1) {
//                                    itunesThing = itunes_embed.substring(indexOfItunesNew, indexOfReferer + 9);
//                                }
//                                int indexOfGeoMusic = itunes_embed.indexOf("https://geo.music.apple.com");
//                                if (indexOfGeoMusic > -1 && indexOfReferer > -1) {
//                                    itunesThing = itunes_embed.substring(indexOfGeoMusic, indexOfReferer + 9);
//                                }
//                                int indexOfGeoItunes = itunes_embed.indexOf("https://geo.itunes.apple.com");
//                                if (indexOfGeoItunes > -1 && indexOfReferer > -1) {
//                                    itunesThing = itunes_embed.substring(indexOfGeoItunes, indexOfReferer + 9);
//                                }
//                                if (itunesThing != null) {
//                                    itunesThing = itunesThing.replace("geo.itunes.apple.com", "music.apple.com");
//                                    itunesThing = itunesThing.replace("geo.music.apple.com", "music.apple.com");
//                                    itunesThing = itunesThing.replace("itunes.apple.com", "music.apple.com");
//                                    songSubgroup.setItunesLink(itunesThing.trim());
//                                }
//                            }
                            songSubgroup.setSubgroup(tempSubgroup);
                            songSubgroup.setInfo(info);
                            songSubgroup = songSubgroupRepository.saveAndFlush(songSubgroup);
//                            if (tempSubgroup != null) {
//                                SongSubgroup tempSongSubgroup = new SongSubgroup();
//                                tempSongSubgroup.setRemix(songSubgroup.getRemix());
//                                tempSongSubgroup.setInstrumental(songSubgroup.getInstrumental());
//                                tempSongSubgroup.setSrcId(songSubgroup.getSrcId());
//                                tempSongSubgroup.setPosition(songSubgroup.getPosition());
//                                tempSongSubgroup.setLyrics(songSubgroup.getLyrics());
//                                tempSongSubgroup.setSubgroup(tempSubgroup);
//                                tempSongSubgroup.setSong(songSubgroup.getSong());
//                                tempSongSubgroup.setSpotifyId(songSubgroup.getSpotifyId());
//                                songSubgroupRepository.saveAndFlush(tempSongSubgroup);
//                            }
                        }
                        System.out.println("done with row " + row.getRowNum());
                    }
                } catch (Throwable throwable) {
                    System.out.println("row " + currentRow);
                    System.out.println("AAAAAAAAAAAAAA- " + throwable.getMessage());
                    throwable.printStackTrace();
                }
//        System.exit(0);
            }
        }
    }
/*
    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0) {
            File targetPath = new File(args[0]);
            String mode = args[1];
            if (mode.equals("AuthorCountry")) {
                List<String> oldDbStuff = Files.readAllLines(targetPath.toPath());
                for (String dbEntry : oldDbStuff) {
                    try {
                        String[] dividedEntry = dbEntry.split(",");
                        String bandName = dividedEntry[0].trim();
                        Long countryId = Long.valueOf(dividedEntry[1].trim());
                        Author author = authorRepository.findByName(bandName);
                        Country country = countryRepository.findById(countryId);
                        AuthorCountry authorCountry = new AuthorCountry();
                        authorCountry.setAuthor(author);
                        authorCountry.setCountry(country);
                        authorCountryRepository.save(authorCountry);
                    } catch (Exception e) {
                        System.out.println("e?? " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } else if (mode.equals("SongSubgroup")) {
                List<String> oldDbStuff = Files.readAllLines(targetPath.toPath());
                String potentialId =
                        targetPath.getName().substring(0, targetPath.getName().lastIndexOf("."))
                                .substring(targetPath.getName().lastIndexOf("_") + 1);
                Game targetGame = gameRepository.findById(Integer.valueOf(potentialId)).get();
                for (String dbEntry : oldDbStuff) {
                    try {
                        SongSubgroup subgroup = new SongSubgroup();
                        Song song = songRepository.findById(Integer.valueOf(dbEntry)).get();
                        subgroup.setSong(song);
                        MainGroup mainGroup = targetGame.getMainGroups().get(0);
                        Subgroup dbSubgroup = mainGroup.getSubgroups().get(0);
                        subgroup.setInstrumental(Instrumental.N);
                        subgroup.setSubgroup(dbSubgroup);
                        songSubgroupRepository.save(subgroup);
                    } catch (Exception e) {
                        System.out.println("e?? " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } else if (mode.equals("AuthorSong")) {
                List<String> oldDbStuff = Files.readAllLines(targetPath.toPath());
                for (String dbEntry : oldDbStuff) {
                    try {
                        String[] dividedEntry = dbEntry.split(",");
                        String bandName = dividedEntry[0].trim();
                        String songId = dividedEntry[1].trim();
                        AuthorAlias authorAlias = authorAliasRepository.findByAlias(bandName);
                        Song song = songRepository.findById(Integer.valueOf(songId)).get();
                        AuthorSong authorSong = new AuthorSong();
                        authorSong.setAuthorAlias(authorAlias);
                        authorSong.setSong(song);
                        authorSong.setRole(Role.COMPOSER);
                        authorSongRepository.save(authorSong);
                    } catch (Exception e) {
                        System.out.println("e?? " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            //System.exit(0);
        }
    }

 */
}
