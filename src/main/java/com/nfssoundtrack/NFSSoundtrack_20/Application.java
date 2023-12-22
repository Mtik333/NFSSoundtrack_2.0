package com.nfssoundtrack.NFSSoundtrack_20;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@SpringBootApplication
@Transactional
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
    private GameRepository gameRepository;

    @Autowired
    private SongSubgroupRepository songSubgroupRepository;

    @Autowired
    private AuthorAliasRepository authorAliasRepository;

    @Autowired
    private AuthorSongRepository authorSongRepository;

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
}
