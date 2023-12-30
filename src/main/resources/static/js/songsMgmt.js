function SubmitSongChange(subgroup_id, song_id, state) {
    this.subgroup_id = subgroup_id;
    this.song_id = song_id;
    this.state = state;
}

var currentSongSubgroup;
$(document).ready(function () {
    // $.ajax({
    //     async: false,
    //     type: "GET",
    //     url: "/author/readAll",
    //     success: function (ooo) {
    //         allArtistsForDropdown = JSON.parse(ooo);
    //         console.log("e");
    //     },
    //     error: function (ooo) {
    //         console.log("e2");
    //     },
    //     done: function (ooo) {
    //         console.log("e3");
    //     }
    // });

    $("#success-alert").hide();
    function getSubgroupsFromGame() {
        $.ajax({
            async: false,
            type: "GET",
            url: "/maingroup/read/" + gameId,
            success: function (ooo) {
                fullScopeOfEdit = JSON.parse(ooo);
                var divToAppend = $('#nfs-content');
                divToAppend.empty();
                divToAppend.append(successAlertHtml);
                var rowDiv = $('<div class="row">');
                var leftCellDiv = $('<div class="col">');
                var rightCellDiv = $('<div class="col">');
                divToAppend.append(rowDiv);
                rowDiv.append(leftCellDiv);
                rowDiv.append(rightCellDiv);
                var newGroupSpan = $('<h2><button data-gameId=' + gameId + ' id="new-song" class="new-group btn btn-success">New song</button></h2>');
                var dropdownDiv = $('<div id="selectSubgroup" class="dropdown">');
                dropdownDiv.append('<button class="btn btn-secondary dropdown-toggle" type="button" id="subgroupsDropdown" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">All</button>');
                leftCellDiv.append(dropdownDiv);
                rightCellDiv.append(newGroupSpan);
                var tableToFill;
                if (fullScopeOfEdit.length > 0) {
                    var dropdownMenuDiv = $('<div class="dropdown-menu" aria-labelledby="subgroupsDropdown">');
                    tableToFill = displayAllSongs(fullScopeOfEdit[0].subgroups[0].songSubgroupList, dropdownDiv);
                    for (let i = 0; i < fullScopeOfEdit.length; i++) {
                        var group = fullScopeOfEdit[i];
                        var groupName = fullScopeOfEdit[i].groupName;
                        for (let j = 0; j < group.subgroups.length; j++) {
                            var subgroup = group.subgroups[j];
                            dropdownMenuDiv.append('<a class="dropdown-item songItem" href="#" data-groupId="' + group.id + '" data-subgroupId="' + subgroup.id + '">(' + subgroup.subgroupName + ') from group [' + groupName + ']');
                        }
                    }
                    dropdownDiv.append(dropdownMenuDiv);
                }
                divToAppend.append(tableToFill);
                $(divToAppend).find("a").first().click();
            }


        });
    }

    function displayAllSongs(songs, dropdownDiv) {
        var tableToFill = $('<table id="songs-table" class="table table-bordered table-hover table-striped">');
        tableToFill.append("<tbody>");
        for (let i = 0; i < songs.length; i++) {
            var tr = $('<tr class="song" data-songId="' + songs[i].song.id + '" data-songSubgroupId="' + songs[i].id + '">');
            var songDisplay = "";
            if (songs[i].ingameDisplayBand != null) {
                songDisplay += songs[i].ingameDisplayBand;
            } else {
                songDisplay += songs[i].song.officialDisplayBand;
            }
            songDisplay += " - ";
            if (songs[i].ingameDisplayTitle != null) {
                songDisplay += songs[i].ingameDisplayTitle;
            } else {
                songDisplay += songs[i].song.officialDisplayTitle;
            }
            var textTd = $('<td>');
            textTd.append(songDisplay);
            tr.append(textTd);
            tr.append('<td class="text-right"><button type="button" id="edit-song-' + songs[i].song.id + '" data-songSubgroupId="' + songs[i].id + '" class="btn btn-warning edit-song">Edit</button></td>');
            tr.append('<td class="text-right"><button type="button" id="delete-song-' + songs[i].song.id + '" data-songSubgroupId="' + songs[i].id + '" class="btn btn-danger delete-song">Delete</button></td>');
            tableToFill.append(tr);
        }
        return tableToFill;
    }

    $("a.manage-songs").click(function (e) {
        gameId = e.target.attributes["data-gameid"].value;
        getSubgroupsFromGame();
    });

    $(document).on('click', 'button.edit-song', function (e) {
        var divToAppend = $('#nfs-content');
        var songId = $(this).attr("data-songSubgroupId");
        divToAppend.empty();
        var songSubgroup;
        $.ajax({
            async: false,
            type: "GET",
            url: "/songSubgroup/read/" + songId,
            success: function (ooo) {
                console.log("e");
                songSubgroup = JSON.parse(ooo);
                currentSongSubgroup = songSubgroup;
                var saveCancelFormDiv = $('<div class="form-group">');
                var saveOrCancelDiv = $('<div class="row">');
                saveCancelFormDiv.append(saveOrCancelDiv);
                var saveCol = $('<div class="col">');
                saveCol.append('<button id="save-song" type="submit" class="btn btn-primary">Save</button>');
                saveCol.append('<button id="cancel-song" type="submit" class="btn btn-primary">Cancel</button>');
                saveOrCancelDiv.append(saveCol);
                var artistAndAliasDiv = $('<div class="row">');
                var artistDiv = $('<div class="col">');
                var aliasDiv = $('<div class="col">');
                var instrumentalDiv = $('<div class="col">');
                var mainComposerDiv = $('<div class="form-group mainComposer" id="mainComposer">');
                var ingameDisplayDiv = $('<div class="form-group inGameDisplay" id="inGameDisplay">');
                var spotifyOthersDiv = $('<div class="form-group spotifyDisplay" id="spotifyDisplay">');
                var subComposerDiv = $('<div class="form-group subComposer" id="subComposer">')
                var featDiv = $('<div class="form-group featDiv" id="featDiv">')
                var featRowDiv = $('<div class="row">')
                var featInputColDiv = $('<div class="col">')
                var featButtonColDiv = $('<div class="col">')
                var remixDiv = $('<div class="form-group remixer" id="remixer">');
                var remixRowDiv = $('<div class="row">')
                var remixInputColDiv = $('<div class="col">')
                var remixButtonColDiv = $('<div class="col">')
                var lyricsDiv = $('<div class="form-group lyrics_edit" id="lyrics_edit">');
                var featSelect = $('<input class="form-control" id="featSelect-0"/>');
                var featSelectHidden = $('<input type="hidden" id="featSelectHidden-0"/>');
                var remixSelect = $('<input class="form-control" id="remixSelect-0"/>');
                var remixSelectHidden = $('<input type="hidden" id="remixSelectHidden-0"/>');
                var howManyFeats = 0;
                var howManyRemixes = 0;
                for (let i = 0; i < songSubgroup.song.authorSongList.length; i++) {
                    var authorSong = songSubgroup.song.authorSongList[i];
                    var officialArtistName = authorSong.authorAlias.author.name;
                    var aliasSelect = $('<input class="form-control w-100" id="aliasSelect-' + i + '" value="' + officialArtistName + '"/>');
                    var aliasSelectHidden = $('<input type="hidden" id="aliasSelectHidden-' + i + '"/>');
                    if (authorSong.role == "COMPOSER") {
                        generateAuthorDiv(mainComposerDiv, officialArtistName,artistDiv, aliasDiv, artistAndAliasDiv, aliasSelect, aliasSelectHidden, i, authorSong, instrumentalDiv);
                    }
                    if (authorSong.role == "SUBCOMPOSER") {
                        console.log(authorSong);
                    }
                    if (authorSong.role == "FEAT") {
                        generateFeatDiv(featSelect, featSelectHidden, featDiv, featInputColDiv, featButtonColDiv, featRowDiv, howManyFeats);
                        howManyFeats++;
                        console.log(authorSong);
                    }
                    if (authorSong.role == "REMIX") {
                        console.log(authorSong);
                        generateRemixDiv(remixSelect, remixSelectHidden, remixDiv, songSubgroup, remixInputColDiv, remixButtonColDiv, remixRowDiv, howManyRemixes, officialArtistName, i);
                        howManyRemixes++;
                    }
                }
                if (howManyFeats == 0) {
                    generateFeatDiv(featSelect, featSelectHidden, featDiv, featInputColDiv, featButtonColDiv, featRowDiv, howManyFeats, howManyFeats, "");
                }
                if (howManyRemixes == 0) {
                    generateRemixDiv(remixSelect, remixSelectHidden, remixDiv, songSubgroup, remixInputColDiv, remixButtonColDiv, remixRowDiv, howManyRemixes, officialArtistName, howManyFeats);
                }
                divToAppend.append(saveOrCancelDiv);
                generateIngameDisplayDiv(ingameDisplayDiv, songSubgroup);
                divToAppend.append(mainComposerDiv);
                divToAppend.append(ingameDisplayDiv);
                divToAppend.append(featDiv);
                divToAppend.append(remixDiv);
                generateSpotifyAndLyrics(spotifyOthersDiv,songSubgroup);
                divToAppend.append(spotifyOthersDiv);
                /*
                divToAppend.append("<h3>Author country info</h3>");
                divToAppend.append("<h3>Song subgroup info</h3>");
                divToAppend.append("<h3>Genre info</h3>");
                */
                
            },
            error: function (ooo) {
                console.log("e2");
            },
            done: function (ooo) {
                console.log("e3");
            }
        });

    });

    function setupAutocompleteArtist(mySelect, mySelectHidden, valueToSet) {
        mySelect.autocomplete({
            source: function (request, response, url) {
                $.ajax({
                    async: false,
                    type: "GET",
                    url: "/author/authorName/" + $(mySelect).val(),
                    success: function (ooo) {
                        response(JSON.parse(ooo));
                    },
                    error: function (ooo) {
                        console.log("e2");
                    },
                    done: function (ooo) {
                        console.log("e3");
                    }
                });
            },
            select: function (event, ui) {
                event.preventDefault();
                $(mySelect).val(ui.item.label);
                $(mySelect).text(ui.item.label);
                $(mySelectHidden).val(ui.item.value);
            },
            minLength: 0
        });
    }

    function setupAutocompleteAlias(mySelect, mySelectHidden, valueToSet) {
        mySelect.autocomplete({
            source: function (request, response, url) {
                $.ajax({
                    async: false,
                    type: "GET",
                    url: "/author/aliasName/" + valueToSet,
                    success: function (ooo) {
                        response(JSON.parse(ooo));
                    },
                    error: function (ooo) {
                        console.log("e2");
                    },
                    done: function (ooo) {
                        console.log("e3");
                    }
                });
            },
            select: function (event, ui) {
                event.preventDefault();
                $(mySelect).val(ui.item.label);
                $(mySelect).text(ui.item.label);
                $(mySelectHidden).val(ui.item.value);
            },
            minLength: 0
        });
    }

    $(document).on('click', 'a.songItem', function (e) {
        $("#subgroupsDropdown").text($(this).text());
        var subgroupId = parseInt($(this).attr('data-subgroupId'));
        var groupId = parseInt($(this).attr('data-groupId'));
        var subgroupSongs;
        for (let i = 0; i < fullScopeOfEdit.length; i++) {
            if (fullScopeOfEdit[i].id == groupId) {
                for (let j = 0; j < fullScopeOfEdit[i].subgroups.length; j++) {
                    if (fullScopeOfEdit[i].subgroups[j].id == subgroupId) {
                        subgroupSongs = fullScopeOfEdit[i].subgroups[j].songSubgroupList;
                    }
                }
            }
        }
        currentSubgroupId = subgroupId;
        console.log("e");
        $("#songs-table").find('tr').each(function (e) {
            $(this).addClass("visually-hidden");
        });
        for (let i = 0; i < subgroupSongs.length; i++) {
            var songId = subgroupSongs[i].song.id;
            var songToMark = $("#songs-table").find('tr[data-songId="' + songId + '"]').first();
            songToMark.attr("data-songSubgroupId", subgroupSongs[i].id);
            songToMark.removeClass("visually-hidden");
        };
        console.log("e");
    });

    function generateAuthorDiv(mainComposerDiv, officialArtistName, artistDiv, aliasDiv, 
        artistAndAliasDiv, aliasSelect, aliasSelectHidden, i, authorSong, instrumentalDiv) {
        mainComposerDiv.append("<h4>Author / Alias info</h4>");
        var authorSelect = $('<input class="form-control" id="authorSelect-' + i + '" value="' + officialArtistName + '"/>');
        var authorSelectHidden = $('<input type="hidden" id="authorSelectHidden-' + i + '" value="'
        +authorSong.authorAlias.author.id+'"/>');
        artistDiv.append('<label for="authorSelect-"' + i + '">Author name</label>');
        aliasDiv.append('<label for="aliasSelect-"' + i + '">Alias</label>');
        setupAutocompleteArtist(authorSelect, authorSelectHidden, officialArtistName);
        artistDiv.append(authorSelect);
        artistDiv.append(authorSelectHidden);
        artistAndAliasDiv.append(artistDiv);
        setupAutocompleteAlias(aliasSelect, aliasSelectHidden, authorSong.id);
        aliasDiv.append(aliasSelect);
        aliasDiv.append(aliasSelectHidden);
        artistAndAliasDiv.append(aliasDiv);
        var instrumentalInput = ('<input type="checkbox" class="form-check-input" id="instrumentalBox">');
        var instrumentalLabel = ('<label class="form-check-label" for="instrumentalBox">Instrumental?</label>');
        instrumentalDiv.append(instrumentalInput);
        instrumentalDiv.append(instrumentalLabel);
        artistAndAliasDiv.append(instrumentalDiv);
        mainComposerDiv.append(artistAndAliasDiv);
    }

    function generateIngameDisplayDiv(ingameDisplayDiv, songSubgroup) {
        ingameDisplayDiv.append("<h4>In-game display</h4>");
        var ingameBand = $('<input class="form-control" id="ingameBand" value="' + songSubgroup.ingameDisplayBand + '"/>');
        var ingameTitle = $('<input class="form-control" id="ingameTitle" value="' + songSubgroup.ingameDisplayTitle + '"/>');
        var ingameDiv = $('<div class="row">');
        var ingameBandDiv = $('<div class="col">');
        var ingameTitleDiv = $('<div class="col">');
        ingameBandDiv.append('<label for="ingameBand">Ingame band display</label>');
        ingameTitleDiv.append('<label for="ingameTitle">Ingame band display</label>');
        ingameBandDiv.append(ingameBand);
        ingameTitleDiv.append(ingameTitle);
        ingameDiv.append(ingameBandDiv);
        ingameDiv.append(ingameTitleDiv);
        ingameDisplayDiv.append(ingameDiv);
    }

    function generateFeatDiv(featSelect, featSelectHidden, featDiv, featInputColDiv, featButtonColDiv,
        featRowDiv, howManyFeats, i,officialArtistName) {
        if (howManyFeats == 0) {
            setupAutocompleteArtist(featSelect, featSelectHidden, "");
            featDiv.append('<h4>Feat. display</h4>')
            featInputColDiv.append('<label for="featSelect-0">Feat.</label>');
            featInputColDiv.append(featSelect);
            featInputColDiv.append(featSelectHidden);
            featButtonColDiv.append('<button id="add-feat-0" type="submit" class="btn btn-primary add-feat">+</button>');
            featButtonColDiv.append('<button id="delete-feat-0" type="submit" class="btn btn-danger delete-feat">-</button>');
            featRowDiv.append(featInputColDiv);
            featRowDiv.append(featButtonColDiv);
            featDiv.append(featRowDiv);
        } else {
            var featRowDivNext = $('<div class="row">');
            var featInputColDivNext = $('<div class="col">');
            var featButtonColDivNext = $('<div class="col">');
            var featSelectNext = $('<input class="form-control" id="featSelect-' + i + '" value="' + officialArtistName + '"/>');
            var featSelectHiddenNext = $('<input type="hidden" id="featSelectHidden-' + i + '" value="' + officialArtistName + '"/>');
            featInputColDivNext.append(featSelectNext);
            featInputColDivNext.append(featSelectHiddenNext);
            featButtonColDivNext.append('<button id="add-feat-"' + i + '" type="submit" class="btn btn-primary add-feat">+</button>');
            featButtonColDivNext.append('<button id="delete-feat-"' + i + '" type="submit" class="btn btn-danger delete-feat">-</button>');
            featRowDivNext.append(featInputColDivNext);
            featRowDivNext.append(featButtonColDivNext);
            featDiv.append(featRowDivNext);
        }
    }

    function generateRemixDiv(remixSelect, remixSelectHidden, remixDiv, songSubgroup, remixInputColDiv, remixButtonColDiv, remixRowDiv, howManyRemixes, officialArtistName, i) {
        if (howManyRemixes == 0) {
            setupAutocompleteArtist(remixSelect, remixSelectHidden, "");
            remixDiv.append('<h4>Remix</h4>')
            var remixInput = ('<input type="checkbox" class="form-check-input" id="remixBox">');
            var remixLabel = ('<label class="form-check-label" for="remixBox">Remix?</label>');
            if (songSubgroup.remix == "YES") {
                remixInput.checked = true;
            }
            remixDiv.append(remixLabel);
            remixDiv.append(remixInput);
            remixInputColDiv.append('<label for="remixSelect-0">Remix</label>');
            remixInputColDiv.append(remixSelect);
            remixInputColDiv.append(remixSelectHidden);
            remixButtonColDiv.append('<button id="add-remix-0" type="submit" class="btn btn-primary add-remix">+</button>');
            remixButtonColDiv.append('<button id="delete-remix-0" type="submit" class="btn btn-danger delete-remix">-</button>');
            remixRowDiv.append(remixInputColDiv);
            remixRowDiv.append(remixButtonColDiv);
            remixDiv.append(remixRowDiv);
        } else {
            var remixRowDivNext = $('<div class="row">');
            var remixInputColDivNext = $('<div class="col">');
            var remixButtonColDivNext = $('<div class="col">');
            var remixSelectNext = $('<input class="form-control" id="remixSelect-' + i + '" value="' + officialArtistName + '"/>');
            var remixSelectHiddenNext = $('<input type="hidden" id="remixSelectHidden-' + i + '" value="' + officialArtistName + '"/>');
            remixInputColDivNext.append(remixSelectNext);
            remixInputColDivNext.append(remixSelectHiddenNext);
            remixButtonColDivNext.append('<button id="add-remix-"' + i + '" type="submit" class="btn btn-primary add-remix">+</button>');
            remixButtonColDivNext.append('<button id="delete-remix-"' + i + '" type="submit" class="btn btn-danger delete-remix">-</button>');
            remixRowDivNext.append(remixInputColDivNext);
            remixRowDivNext.append(remixButtonColDivNext);
            remixDiv.append(remixRowDivNext);
        }
    }

    $(document).on('click', 'button.add-remix', function (e) {
        var col = $(this).parent();
        var rowCol = col.parent();
        var divCol = rowCol.parent();
        var thisId=parseInt($(this).attr("id").replace("add-remix-", ""));
        generateRemixDiv(null,null,divCol,null,null,null,null,null,"",++thisId);
    });

    $(document).on('click', 'button.delete-remix', function (e) {
        var col = $(this).parent();
        var input = $(this).parent().prev().children()[2];
        if (input.value==""){
            var rowCol = col.parent();
            var divCol = rowCol.parent();
            divCol.remove();
        } else {
            $(input).addClass("text-decoration-line-through");
        }

    });

    $(document).on('click', 'button.add-feat', function (e) {
        var col = $(this).parent();
        var rowCol = col.parent();
        var divCol = rowCol.parent();
        var thisId=parseInt($(this).attr("id").replace("add-feat-", ""));
        generateFeatDiv(null, null, divCol, null, null, null, ++thisId, thisId,"");
    });

    $(document).on('click', 'button.delete-feat', function (e) {
        var col = $(this).parent();
        var rowCol = col.parent();
        if (input.value==""){
            var rowCol = col.parent();
            var divCol = rowCol.parent();
            divCol.remove();
        } else {
            $(input).addClass("text-decoration-line-through");
        }
    });

    $(document).on('click', '#cancel-song', function (e) {
        getSubgroupsFromGame();
    });

    function generateSpotifyAndLyrics(ingameDisplayDiv, songSubgroup) {
        ingameDisplayDiv.append("<h4>Spotify and others / lyrics</h4>");
        var itunesInput = $('<input type="text" class="form-control" id="itunesInput" value="' + songSubgroup.spotifyId + '"/>');
        var spotifyInput = $('<input type="text" class="form-control" id="spotifyInput" value="' + songSubgroup.spotifyId + '"/>');
        var soundcloudInput = $('<input type="text" class="form-control" id="soundcloudInput" value="' + songSubgroup.spotifyId + '"/>');
        var deezerInput = $('<input type="text" class="form-control" id="deezerInput" value="' + songSubgroup.spotifyId + '"/>');
        var tidalInput = $('<input type="text" class="form-control" id="tidalInput" value="' + songSubgroup.spotifyId + '"/>');
        var lyricsTextArea = $('<textarea class="form-control" id="lyrics">' + songSubgroup.lyrics + '</textarea/>');
        var socialDiv = $('<div class="row">');
        var itunesInputDiv = $('<div class="col">');
        var spotifyInputDiv = $('<div class="col">');
        var soundcloudInputDiv = $('<div class="col">');
        var deezerInputDiv = $('<div class="col">');
        var tidalInputDiv = $('<div class="col">');
        itunesInputDiv.append('<label for="itunesInput">Itunes link</label>');
        spotifyInputDiv.append('<label for="spotifyInput">Spotify link</label>');
        soundcloudInputDiv.append('<label for="soundcloudInput">Soundcloud link</label>');
        deezerInputDiv.append('<label for="deezerInput">Deezer link</label>');
        tidalInputDiv.append('<label for="tidalInput">Tidal link</label>');
        itunesInputDiv.append(itunesInput);
        spotifyInputDiv.append(spotifyInput);
        soundcloudInputDiv.append(soundcloudInput);
        deezerInputDiv.append(deezerInput);
        tidalInputDiv.append(tidalInput);
        socialDiv.append(itunesInputDiv);
        socialDiv.append(spotifyInputDiv);
        socialDiv.append(soundcloudInputDiv);
        socialDiv.append(deezerInputDiv);
        socialDiv.append(tidalInputDiv);
        socialDiv.append('<label for="lyrics">Lyrics</label>');
        socialDiv.append(lyricsTextArea);
        ingameDisplayDiv.append(socialDiv);
    }
});