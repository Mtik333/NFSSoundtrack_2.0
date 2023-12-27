package com.nfssoundtrack.NFSSoundtrack_20;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.repository.*;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
@Transactional
//public class Application  {
public class Application implements CommandLineRunner {

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
        if (args.length>0) {
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
                        List<Song> alreadyExistingSongs = songRepository.findByOfficialDisplayTitleAndOfficialDisplayBand(band, title);
                        if (alreadyExistingSongs.isEmpty()) {
                            mySong = new Song();
//                            mySong.setId(id);
                            mySong.setOfficialDisplayBand(band);
                            mySong.setOfficialDisplayTitle(title);
                            mySong.setSrcId(src_id);
                            mySong.setInfo(info);
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
//        System.exit(0);
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
