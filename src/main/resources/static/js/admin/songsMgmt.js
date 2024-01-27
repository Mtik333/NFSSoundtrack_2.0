var currentSongSubgroup;
var currentSubgroup;
var newSong = false;
$(document).ready(function () {

    $(successAlertHtml).hide();
    $(failureAlertHtml).hide();
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
                divToAppend.append(failureAlertHtml);
                var rowDiv = $('<div class="row p-1">');
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
                var allSongSubgroups = [];
                for (let i = 0; i < fullScopeOfEdit.length; i++) {
                    var group = fullScopeOfEdit[i];
                    for (let j = 0; j < group.subgroups.length; j++) {
                        allSongSubgroups = allSongSubgroups.concat(fullScopeOfEdit[i].subgroups[j].songSubgroupList);
                    }
                }
                if (fullScopeOfEdit.length > 0) {
                    var dropdownMenuDiv = $('<div class="dropdown-menu" style="max-height: 350px; overflow-y: auto;" aria-labelledby="subgroupsDropdown">');
                    tableToFill = displayAllSongs(allSongSubgroups, dropdownDiv);
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
            },
            error: function (ooo) {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500);
                });
            },
        });
    }

    function displayAllSongs(songs, dropdownDiv) {
        var tableToFill = $('<table id="songs-table" class="table table-bordered table-hover table-striped">');
        tableToFill.append("<tbody>");
        for (let i = 0; i < songs.length; i++) {
            var tr = $('<tr class="song" data-songId="' + songs[i].song.id + '" data-songsubgroupid="' + songs[i].id + '">');
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
            var textTd = $('<td class="col-md-9">');
            textTd.append(songDisplay);
            tr.append(textTd);
            tr.append('<td class="text-right col-md-1"><button type="button" id="edit-song-' + songs[i].song.id + '" data-songsubgroupid="' + songs[i].id + '" data-songId="' + songs[i].song.id + '" class="btn btn-warning edit-song">Edit (in subgroup)</button></td>');
            tr.append('<td class="text-right col-md-1"><button type="button" id="edit-song-globally-' + songs[i].song.id + '" data-songsubgroupid="' + songs[i].id + '" data-songId="' + songs[i].song.id + '" class="btn btn-warning edit-song-globally">Edit globally</button></td>');
            tr.append('<td class="text-right col-md-1"><button type="button" id="delete-song-' + songs[i].song.id + '" data-songsubgroupid="' + songs[i].id + '" data-songId="' + songs[i].song.id + '" class="btn btn-danger delete-song">Delete from subgroup</button></td>');
            tableToFill.append(tr);
        }
        return tableToFill;
    }

    $("a.manage-songs").click(function (e) {
        gameId = e.target.attributes["data-gameid"].value;
        getSubgroupsFromGame();
    });

    $(document).on('click', 'button.delete-song', function (e) {
        var divToAppend = $('#nfs-content');
        var songId = $(this).attr("data-songSubgroupId");
        divToAppend.empty();
        $.ajax({
            async: false,
            type: "DELETE",
            url: "/songSubgroup/delete/" + Number(songId),
            success: function (ooo) {
                console.log("e");
                getSubgroupsFromGame();
                $(successAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(successAlertHtml).slideUp(500);
                });
            }, error: function (ooo) {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500);
                });
            },
        });
    });

    $(document).on('click', 'button.edit-song', function (e) {
        var divToAppend = $('#nfs-content');
        var songId = $(this).attr("data-songSubgroupId");
        divToAppend.empty();
        newSong = false;
        var songSubgroup;
        $.ajax({
            async: false,
            type: "GET",
            url: "/songSubgroup/read/" + Number(songId),
            success: function (ooo) {
                songSubgroup = JSON.parse(ooo);
                currentSongSubgroup = songSubgroup;
                renderEditCreateSong(divToAppend, songSubgroup);
            },
            error: function (ooo) {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500);
                });
            },
        });
    });

    function renderEditCreateSong(divToAppend, songSubgroup) {
        var saveCancelFormDiv = $('<div class="form-group">');
        var saveOrCancelDiv = $('<div class="row p-1">');
        saveCancelFormDiv.append(saveOrCancelDiv);
        var saveCol = $('<div class="col">');
        if (songSubgroup != null) {
            saveCol.append('<button id="save-song" type="submit" class="btn btn-success m-2">Save song </button>');
        } else {
            saveCol.append('<button id="save-new-song" type="submit" class="btn btn-success m-2">Save new song </button>');
        }
        saveCol.append('<button id="cancel-song" type="submit" class="btn btn-primary">Cancel</button>');
        saveCol.append('<input type="checkbox" class="form-check-input m-3" id="propagate"><label class="form-check-label pl-2 pt-2" for="propagate">Propagate to other subgroups?</label>');
        saveOrCancelDiv.append(saveCol);
        var artistAndAliasDiv = $('<div class="row p-1">');
        var artistDiv = $('<div class="col">');
        var aliasDiv = $('<div class="col">');
        var instrumentalDiv = $('<div class="col">');
        var mainComposerDiv = $('<div class="form-group mainComposer" id="mainComposer">');
        var ingameDisplayDiv = $('<div class="form-group inGameDisplay" id="inGameDisplay">');
        var spotifyOthersDiv = $('<div class="form-group spotifyDisplay" id="spotifyDisplay">');
        var featDiv = $('<div class="form-group featDiv" id="featDiv">');
        var featRowDiv = $('<div class="row p-1">');
        var featInputColDiv = $('<div class="col">');
        var featButtonColDiv = $('<div class="col">');
        var remixDiv = $('<div class="form-group remixer" id="remixer">');
        var remixRowDiv = $('<div class="row p-1">');
        var remixInputColDiv = $('<div class="col">');
        var remixButtonColDiv = $('<div class="col">');
        var subcomposerDiv = $('<div class="form-group subcomposerDiv" id="subcomposerDiv">');
        var subcomposerRowDiv = $('<div class="row p-1">');
        var subcomposerInputColDiv = $('<div class="col">');
        var subcomposerConcatColDiv = $('<div class="col">');
        var subcomposerButtonColDiv = $('<div class="col">');
        var featSelect = $('<input class="form-control feat-select" id="featSelect-1"/>');
        var featSelectHidden = $('<input type="hidden" id="featSelectHidden-1"/>');
        var subcomposerSelect = $('<input class="form-control subcomposer-select" id="subcomposerSelect-1"/>');
        var subcomposerSelectHidden = $('<input type="hidden" id="subcomposerSelectHidden-1"/>');
        var remixSelect = $('<input class="form-control remix-select" id="remixSelect-1"/>');
        var remixSelectHidden = $('<input type="hidden" id="remixSelectHidden-1"/>');
        var howManyFeats = 0;
        var howManyRemixes = 0;
        var howManySubcomposers = 0;
        if (songSubgroup != null) {
            for (let i = 0; i < songSubgroup.song.authorSongList.length; i++) {
                var authorSong = songSubgroup.song.authorSongList[i];
                var officialArtistName = authorSong.authorAlias.author.name;
                var aliasSelect = $('<input class="form-control w-100" id="aliasSelect-' + i + '" value="' + officialArtistName + '"/>');
                var aliasSelectHidden = $('<input type="hidden" id="aliasSelectHidden-' + i + '" value="' + authorSong.authorAlias.id + '"/>');
                if (authorSong.role == "COMPOSER") {
                    generateAuthorDiv(mainComposerDiv, officialArtistName, artistDiv, aliasDiv, artistAndAliasDiv, aliasSelect, aliasSelectHidden, i, authorSong, instrumentalDiv,
                        songSubgroup.instrumental);
                }
                if (authorSong.role == "SUBCOMPOSER") {
                    howManySubcomposers++;
                    generateSubcomposerDiv(subcomposerSelect, subcomposerSelectHidden, subcomposerDiv, subcomposerInputColDiv, subcomposerConcatColDiv, subcomposerButtonColDiv, subcomposerRowDiv, howManySubcomposers, i, officialArtistName, authorSong);
                }
                if (authorSong.role == "FEAT") {
                    howManyFeats++;
                    generateFeatDiv(featSelect, featSelectHidden, featDiv, featInputColDiv, featButtonColDiv, featRowDiv, howManyFeats, howManyFeats, officialArtistName, authorSong);
                    console.log(authorSong);
                }
                if (authorSong.role == "REMIX") {
                    howManyRemixes++;
                    console.log(authorSong);
                    generateRemixDiv(remixSelect, remixSelectHidden, remixDiv, songSubgroup, remixInputColDiv, remixButtonColDiv, remixRowDiv, howManyRemixes, officialArtistName, i, authorSong);
                }
            }
        } else {
            var aliasSelect = $('<input class="form-control w-100" id="aliasSelect-0"/>');
            var aliasSelectHidden = $('<input type="hidden" id="aliasSelectHidden-0"/>');
            generateAuthorDiv(mainComposerDiv, "", artistDiv, aliasDiv, artistAndAliasDiv, aliasSelect, aliasSelectHidden, 0, null, instrumentalDiv,
                false);
        }
        if (howManySubcomposers == 0) {
            // howManySubcomposers++;
            generateSubcomposerDiv(subcomposerSelect, subcomposerSelectHidden, subcomposerDiv, subcomposerInputColDiv, subcomposerConcatColDiv, subcomposerButtonColDiv, subcomposerRowDiv, howManySubcomposers, howManySubcomposers, officialArtistName, undefined);
        }
        if (howManyFeats == 0) {
            // howManyFeats++;
            generateFeatDiv(featSelect, featSelectHidden, featDiv, featInputColDiv, featButtonColDiv, featRowDiv, howManyFeats, howManyFeats, undefined);
        }
        if (howManyRemixes == 0) {
            // howManyRemixes++;
            generateRemixDiv(remixSelect, remixSelectHidden, remixDiv, songSubgroup, remixInputColDiv, remixButtonColDiv, remixRowDiv, howManyRemixes, officialArtistName, howManyFeats, undefined);
        }
        divToAppend.append(saveOrCancelDiv);
        divToAppend.append(mainComposerDiv);
        if (songSubgroup == null) {
            var officialDisplayDiv = $('<div class="form-group officialDisplay" id="officialDisplay">');
            generateOfficialDisplayDiv(officialDisplayDiv, null);
            divToAppend.append(officialDisplayDiv);
            var genreDisplayDiv = $('<div class="form-group genreDisplay" id="genreDisplay">')
            genreDisplayDiv.append('<h4>Genre display</h4>');
            generateGenreDiv(genreDisplayDiv, null, 0);
            divToAppend.append(genreDisplayDiv);
        }
        generateIngameDisplayDiv(ingameDisplayDiv, songSubgroup);
        divToAppend.append(ingameDisplayDiv);
        divToAppend.append(subcomposerDiv);
        divToAppend.append(featDiv);
        divToAppend.append(remixDiv);
        generateSpotifyAndLyrics(spotifyOthersDiv, songSubgroup);
        divToAppend.append(spotifyOthersDiv);
    }

    $(document).on('click', 'button.edit-song-globally', function (e) {
        var divToAppend = $('#nfs-content');
        var songId = $(this).attr("data-songSubgroupId");
        divToAppend.empty();
        var songSubgroup;
        $.ajax({
            async: false,
            type: "GET",
            url: "/songSubgroup/read/" + Number(songId),
            success: function (ooo) {
                console.log("e");
                songSubgroup = JSON.parse(ooo);
                currentSongSubgroup = songSubgroup;
                var globallySaveCancelFormDiv = $('<div class="form-group">');
                var globallySaveOrCancelDiv = $('<div class="row p-1">');
                globallySaveCancelFormDiv.append(globallySaveOrCancelDiv);
                var saveCol = $('<div class="col">');
                saveCol.append('<button id="save-song-globally" type="submit" class="btn btn-success m-2">Save</button>');
                saveCol.append('<button id="cancel-song-globally" type="submit" class="btn btn-primary">Cancel</button>');
                saveCol.append('<input type="checkbox" class="form-check-input m-3" id="propagate"><label class="form-check-label pl-2 pt-2" for="propagate">Propagate to other subgroups?</label>');
                globallySaveOrCancelDiv.append(saveCol);
                var officialDisplayDiv = $('<div class="form-group officialDisplay" id="officialDisplay">');
                generateOfficialDisplayDiv(officialDisplayDiv, songSubgroup);
                var genreDisplayDiv = $('<div class="form-group genreDisplay" id="genreDisplay">');
                genreDisplayDiv.append('<h4>Genre display</h4>');
                generateGenreDiv(genreDisplayDiv, songSubgroup.song, 0);
                var spotifyOthersDiv = $('<div class="form-group spotifyDisplay" id="spotifyDisplay">');
                generateSpotifyAndLyrics(spotifyOthersDiv, songSubgroup.song);
                divToAppend.append(globallySaveCancelFormDiv);
                divToAppend.append(officialDisplayDiv);
                divToAppend.append(genreDisplayDiv);
                divToAppend.append(spotifyOthersDiv);
            }, error: function (ooo) {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500);
                });
            },
        });
    });

    function setupAutocompleteArtist(mySelect, mySelectHidden, valueToSet) {
        var previousValue = $(mySelect).val();
        mySelect.autocomplete({
            source: function (request, response, url) {
                $.ajax({
                    async: false,
                    type: "GET",
                    url: "/author/authorName/" + $(mySelect).val(),
                    success: function (ooo) {
                        var result = JSON.parse(ooo);
                        if (result){
                            response(result);
                        }
                    },
                    error: function (ooo) {
                        $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                            $(failureAlertHtml).slideUp(500);
                        });
                    },
                });
            },
            select: function (event, ui) {
                event.preventDefault();
                $(mySelect).val(ui.item.label);
                $(mySelect).text(ui.item.label);
                $(mySelectHidden).val(ui.item.value);
                if (previousValue!=ui.item.value && !newSong){
                    $("#aliasSelect-0").val(ui.item.label);
                    $("#aliasSelect-0").text(ui.item.label);
                    $("#aliasSelectHidden-0").val(ui.item.aliasId);
                }
                if (newSong) {
                    if ($(mySelect).hasClass("authorSelect")) {
                        $("#aliasSelect-0").val(ui.item.label);
                        $("#aliasSelectHidden-0").val(ui.item.aliasId);
                        $("#officialBand").val(ui.item.label);
                    }
                }
            },
            minLength: 1
        });
    }

    function setupAutocompleteAlias(mySelect, mySelectHidden, valueToSet) {
        mySelect.autocomplete({
            source: function (request, response, url) {
                $.ajax({
                    async: false,
                    type: "GET",
                    url: "/author/aliasName/" + $(mySelect).val(),
                    success: function (ooo) {
                        var result = JSON.parse(ooo);
                        if (result){
                            response(result);
                        }
                    },
                    error: function (ooo) {
                        $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                            $(failureAlertHtml).slideUp(500);
                        });
                    },
                });
            },
            select: function (event, ui) {
                event.preventDefault();
                $(mySelect).val(ui.item.label);
                $(mySelect).text(ui.item.label);
                $(mySelectHidden).val(ui.item.value);
            },
            minLength: 1
        });
    }

    function setupAutocompleteGenre(mySelect, mySelectHidden, valueToSet) {
        mySelect.autocomplete({
            source: function (request, response, url) {
                $.ajax({
                    async: false,
                    type: "GET",
                    url: "/genre/genreName/" + $(mySelect).val(),
                    success: function (ooo) {
                        var result = JSON.parse(ooo);
                        if (result){
                            response(result);
                        }
                    },
                    error: function (ooo) {
                        $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                            $(failureAlertHtml).slideUp(500);
                        });
                    },
                });
            },
            select: function (event, ui) {
                event.preventDefault();
                $(mySelect).val(ui.item.label);
                $(mySelect).text(ui.item.label);
                $(mySelectHidden).val(ui.item.value);
            },
            minLength: 1
        });
    }

    function setupAutocompleteAliasArtist(mySelect, mySelectHidden, valueToSet) {
        mySelect.autocomplete({
            source: function (request, response, url) {
                $.ajax({
                    async: false,
                    type: "GET",
                    url: "/author/authorAlias/" + valueToSet,
                    success: function (ooo) {
                        var result = JSON.parse(ooo);
                        if (result){
                            response(result);
                        }
                    },
                    error: function (ooo) {
                        $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                            $(failureAlertHtml).slideUp(500);
                        });
                    },
                });
            },
            select: function (event, ui) {
                event.preventDefault();
                $(mySelect).val(ui.item.label);
                $(mySelect).text(ui.item.label);
                $(mySelectHidden).val(ui.item.value);
            },
            minLength: 1
        });
    }

    $(document).on('click', 'a.songItem', function (e) {
        $("#subgroupsDropdown").text($(this).text());
        var subgroupId = Number($(this).attr('data-subgroupId'));
        currentSubgroup = Number(subgroupId);
        var groupId = Number($(this).attr('data-groupId'));
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
            var songToMark = $("#songs-table").find('tr[data-songId="' + songId + '"][data-songsubgroupid="' + subgroupSongs[i].id + '"]').first();
            songToMark.attr("data-songSubgroupId", subgroupSongs[i].id);
            songToMark.removeClass("visually-hidden");
        };
        console.log("e");
    });

    function generateAuthorDiv(mainComposerDiv, officialArtistName, artistDiv, aliasDiv,
        artistAndAliasDiv, aliasSelect, aliasSelectHidden, i, authorSong, instrumentalDiv,
        instrumentalValue) {
        mainComposerDiv.append("<h4>Author / Alias info</h4>");
        var authorSelect = $('<input class="form-control authorSelect" id="authorSelect-' + i + '" value="' + officialArtistName + '"/>');
        var authorSelectHidden = $('<input type="hidden" id="authorSelectHidden-' + i + '"/>');
        if (authorSong != null) {
            authorSelectHidden.val(authorSong.authorAlias.author.id);
        }
        artistDiv.append('<label for="authorSelect-"' + i + '">Author name</label>');
        aliasDiv.append('<label for="aliasSelect-"' + i + '">Alias</label>');
        setupAutocompleteArtist(authorSelect, authorSelectHidden, officialArtistName);
        artistDiv.append(authorSelect);
        artistDiv.append(authorSelectHidden);
        artistAndAliasDiv.append(artistDiv);
        if (authorSong != null) {
            setupAutocompleteAliasArtist(aliasSelect, aliasSelectHidden, authorSong.id);
        } else {
            setupAutocompleteAlias(aliasSelect, aliasSelectHidden, authorSelect.val());
        }
        aliasDiv.append(aliasSelect);
        aliasDiv.append(aliasSelectHidden);
        artistAndAliasDiv.append(aliasDiv);
        var instrumentalInput;
        if (instrumentalValue == "YES") {
            instrumentalInput = ('<input type="checkbox" class="form-check-input mt-4" id="instrumentalBox" checked>');
        } else {
            instrumentalInput = ('<input type="checkbox" class="form-check-input mt-4" id="instrumentalBox">');
        }
        var instrumentalLabel = ('<label class="form-check-label mt-3" for="instrumentalBox">Instrumental?</label>');
        instrumentalDiv.append(instrumentalInput);
        instrumentalDiv.append(instrumentalLabel);
        artistAndAliasDiv.append(instrumentalDiv);
        mainComposerDiv.append(artistAndAliasDiv);
    }

    function generateIngameDisplayDiv(ingameDisplayDiv, songSubgroup) {
        ingameDisplayDiv.append("<h4>In-game display</h4>");
        var ingameBand = $(`<input class="form-control" id="ingameBand"/>`);
        var ingameTitle = $(`<input class="form-control" id="ingameTitle"/>`);
        var ingameSrcId = $('<input class="form-control" id="ingameSrcId"/>');
        var ingameInfo = $(`<input class="form-control" id="ingameInfo"/>`);
        if (songSubgroup != null) {
            ingameBand.val(songSubgroup.ingameDisplayBand);
            ingameSrcId.val(songSubgroup.srcId);
            ingameTitle.val(songSubgroup.ingameDisplayTitle);
            ingameInfo.val(songSubgroup.info);
        }
        var ingameDiv = $('<div class="row p-1">');
        var ingameBandDiv = $('<div class="col">');
        var ingameTitleDiv = $('<div class="col">');
        var ingameSrcIdDiv = $('<div class="col">');
        var ingameInfoDiv = $('<div class="col">');
        ingameBandDiv.append('<label for="ingameBand">Ingame band display</label>');
        ingameTitleDiv.append('<label for="ingameTitle">Ingame title display</label>');
        ingameSrcIdDiv.append('<label for="ingameSrcId">Ingame Youtube Source Id</label>');
        ingameInfoDiv.append('<label for="ingameInfo">Info</label>');
        ingameBandDiv.append(ingameBand);
        ingameTitleDiv.append(ingameTitle);
        ingameSrcIdDiv.append(ingameSrcId);
        ingameInfoDiv.append(ingameInfo);
        ingameDiv.append(ingameBandDiv);
        ingameDiv.append(ingameTitleDiv);
        ingameDiv.append(ingameSrcIdDiv);
        ingameDiv.append(ingameInfoDiv);
        ingameDisplayDiv.append(ingameDiv);
    }

    function generateOfficialDisplayDiv(officialDisplayDiv, songSubgroup) {
        officialDisplayDiv.append("<h4>Official display</h4>");
        var officialBand = $(`<input class="form-control" id="officialBand"/>`);
        var officialTitle = $(`<input class="form-control" id="officialTitle"/>`);
        var officialSrcId = $('<input class="form-control" id="officialSrcId"/>');
        var existingSongId = $('<input class="form-control" id="existingSongId"/>');
        if (songSubgroup != null) {
            officialBand.val(songSubgroup.song.officialDisplayBand);
            officialTitle.val(songSubgroup.song.officialDisplayTitle);
            officialSrcId.val(songSubgroup.song.srcId);
        }
        var officialDiv = $('<div class="row p-1">');
        var officialBandDiv = $('<div class="col">');
        var officialTitleDiv = $('<div class="col">');
        var officialSrcIdDiv = $('<div class="col">');
        var existingSongIdDiv = $('<div class="col">');
        officialBandDiv.append('<label for="officialBand">Official band display</label>');
        officialTitleDiv.append('<label for="officialTitle">Official title display</label>');
        officialSrcIdDiv.append('<label for="officialSrcId">Official YouTube Src ID</label>');
        existingSongIdDiv.append('<label for="existingSongId">Existing Song ID</label>');
        officialBandDiv.append(officialBand);
        officialTitleDiv.append(officialTitle);
        officialSrcIdDiv.append(officialSrcId);
        existingSongIdDiv.append(existingSongId);
        officialDiv.append(officialBandDiv);
        officialDiv.append(officialTitleDiv);
        officialDiv.append(officialSrcIdDiv);
        officialDiv.append(existingSongIdDiv);
        if (songSubgroup != null) {
            existingSongId.val(songSubgroup.id);
        }
        officialDisplayDiv.append(officialDiv);
    }

    function generateGenreDiv(genreDiv, song, index) {
        if (song != undefined) {
            var songGenreList = song.songGenreList;
            if (songGenreList.length > 0) {
                for (let i = 0; i < songGenreList.length; i++) {
                    var genreRowDiv = $('<div class="row p-1">');
                    var genreInputColDiv = $('<div class="col">');
                    var genreButtonColDiv = $('<div class="col">');
                    var genreSelectNext = $('<input class="form-control genre-select" id="genreSelect-' + i + '" value="' + songGenreList[i].genre.genreName + '"/>');
                    var genreSelectHiddenNext = $('<input type="hidden" id="genreSelectHidden-' + i + '" value="' + songGenreList[i].genre.id + '"/>');
                    genreInputColDiv.append('<label for="genreSelect-' + i + '">Genre</label>');
                    genreInputColDiv.append(genreSelectNext);
                    genreInputColDiv.append(genreSelectHiddenNext);
                    var addGenreButton = $('<button id="add-genre-' + i + '" type="submit" class="btn btn-primary add-genre">+</button>');
                    var deleteGenreButton = $('<button id="delete-genre-' + i + '" type="submit" class="btn btn-danger delete-genre">-</button>');
                    genreButtonColDiv.append(addGenreButton);
                    genreButtonColDiv.append(deleteGenreButton);
                    genreRowDiv.append(genreInputColDiv);
                    genreRowDiv.append(genreButtonColDiv);
                    genreDiv.append(genreRowDiv);
                    setupAutocompleteGenre(genreSelectNext, genreSelectHiddenNext, "");
                }
            } else {
                var genreRowDiv = $('<div class="row p-1">');
                var genreInputColDiv = $('<div class="col">');
                var genreButtonColDiv = $('<div class="col">');
                var genreSelectNext = $('<input class="form-control genre-select" id="genreSelect-' + index + '"/>');
                var genreSelectHiddenNext = $('<input type="hidden" id="genreSelectHidden-' + index + '"/>');
                genreInputColDiv.append('<label for="genreSelect-' + index + '">Genre</label>');
                genreInputColDiv.append(genreSelectNext);
                genreInputColDiv.append(genreSelectHiddenNext);
                var addGenreButton = $('<button id="add-genre-' + index + '" type="submit" class="btn btn-primary add-genre">+</button>');
                var deleteGenreButton = $('<button id="delete-genre-' + index + '" type="submit" class="btn btn-danger delete-genre">-</button>');
                genreButtonColDiv.append(addGenreButton);
                genreButtonColDiv.append(deleteGenreButton);
                genreRowDiv.append(genreInputColDiv);
                genreRowDiv.append(genreButtonColDiv);
                genreDiv.append(genreRowDiv);
                setupAutocompleteGenre(genreSelectNext, genreSelectHiddenNext, "");
            }
        } else {
            var genreRowDiv = $('<div class="row p-1">');
            var genreInputColDiv = $('<div class="col">');
            var genreButtonColDiv = $('<div class="col">');
            var genreSelectNext = $('<input class="form-control genre-select" id="genreSelect-' + index + '"/>');
            var genreSelectHiddenNext = $('<input type="hidden" id="genreSelectHidden-' + index + '"/>');
            genreInputColDiv.append('<label for="genreSelect-' + index + '">Genre</label>');
            genreInputColDiv.append(genreSelectNext);
            genreInputColDiv.append(genreSelectHiddenNext);
            var addGenreButton = $('<button id="add-genre-' + index + '" type="submit" class="btn btn-primary add-genre">+</button>');
            var deleteGenreButton = $('<button id="delete-genre-' + index + '" type="submit" class="btn btn-danger delete-genre">-</button>');
            genreButtonColDiv.append(addGenreButton);
            genreButtonColDiv.append(deleteGenreButton);
            genreRowDiv.append(genreInputColDiv);
            genreRowDiv.append(genreButtonColDiv);
            genreDiv.append(genreRowDiv);
            setupAutocompleteGenre(genreSelectNext, genreSelectHiddenNext, "");
        }
    }

    function generateFeatDiv(featSelect, featSelectHidden, featDiv, featInputColDiv, featButtonColDiv,
        featRowDiv, howManyFeats, i, officialArtistName, authorSong) {
        if (howManyFeats == 0) {
            if (authorSong != undefined) {
                setupAutocompleteAlias(featSelect, featSelectHidden, authorSong.authorAlias.id);
                featSelectHidden.val(authorSong.authorAlias.id);
                featSelect.val(officialArtistName);
            } else {
                setupAutocompleteAlias(featSelect, featSelectHidden, "");
            }
            featDiv.append('<h4>Feat. display</h4>');
            featInputColDiv.append('<label for="featSelect-1">Feat.</label>');
            featInputColDiv.append(featSelect);
            featInputColDiv.append(featSelectHidden);
            var addFeatButton = $('<button id="add-feat-1" type="submit" class="btn btn-primary add-feat">+</button>');
            var deleteFeatButton = $('<button id="delete-feat-1" type="submit" class="btn btn-danger delete-feat">-</button>');
            featButtonColDiv.append(addFeatButton);
            featButtonColDiv.append(deleteFeatButton);
            if (authorSong == undefined) {
                deleteFeatButton.prop("disabled", true);
            }
            featRowDiv.append(featInputColDiv);
            featRowDiv.append(featButtonColDiv);
            featDiv.append(featRowDiv);
        } else {
            var featRowDivNext = $('<div class="row p-1">');
            var featInputColDivNext = $('<div class="col">');
            var featButtonColDivNext = $('<div class="col">');
            var featSelectNext = $('<input class="form-control feat-select" id="featSelect-' + i + '" value="' + officialArtistName + '"/>');
            var featSelectHiddenNext = $('<input type="hidden" id="featSelectHidden-' + i + '" value="' + officialArtistName + '"/>');
            featInputColDivNext.append('<label for="featSelect-' + i + '">Feat.</label>');
            featInputColDivNext.append(featSelectNext);
            featInputColDivNext.append(featSelectHiddenNext);
            featButtonColDivNext.append('<button id="add-feat-' + i + '" type="submit" class="btn btn-primary add-feat">+</button>');
            featButtonColDivNext.append('<button id="delete-feat-' + i + '" type="submit" class="btn btn-danger delete-feat">-</button>');
            featRowDivNext.append(featInputColDivNext);
            if (authorSong != undefined) {
                setupAutocompleteAlias(featSelectNext, featSelectHiddenNext, authorSong.authorAlias.id);
                featSelectHiddenNext.val(authorSong.authorAlias.id);
                featSelectNext.val(officialArtistName);
                if (authorSong.featConcat != null) {
                    var divNewCol = $('<div class="col"></div>');
                    var featConcatInput = $('<input type="text" class="form-control" id="featConcatInput-' + howManyFeats + '"/>');
                    featConcatInput.val(authorSong.featConcat);
                    divNewCol.append('<label for="featConcatInput-' + howManyFeats + '">Feat. concat string</label>');
                    divNewCol.append(featConcatInput);
                    featRowDivNext.append(divNewCol);
                }
            } else {
                setupAutocompleteAlias(featSelectNext, featSelectHiddenNext, "");
            }
            featRowDivNext.append(featButtonColDivNext);
            featDiv.append(featRowDivNext);
        }
    }

    function generateSubcomposerDiv(subcomposerSelect, subcomposerSelectHidden, subcomposerDiv, subcomposerInputColDiv, subcomposerConcatColDiv, subcomposerButtonColDiv, subcomposerRowDiv, howManySubcomposers, i, officialArtistName, authorSong) {
        if (howManySubcomposers == 0) {
            var subcomposerConcatInput;
            if (authorSong != undefined) {
                setupAutocompleteAlias(subcomposerSelect, subcomposerSelectHidden, authorSong.authorAlias.id);
                subcomposerSelectHidden.val(authorSong.authorAlias.id);
                subcomposerSelect.val(officialArtistName);
                subcomposerConcatInput = $('<input class="form-control" id="subcomposerConcatInput-1" value="' + authorSong.subcomposerConcat + '"/>');
            } else {
                setupAutocompleteAlias(subcomposerSelect, subcomposerSelectHidden, "");
                subcomposerConcatInput = $('<input class="form-control" id="subcomposerConcatInput-1" value=""/>');
            }
            subcomposerDiv.append('<h4>Subcomposers</h4>')
            subcomposerInputColDiv.append('<label for="subcomposerSelect-1">Subcomposer</label>');
            subcomposerInputColDiv.append(subcomposerSelect);
            subcomposerInputColDiv.append(subcomposerSelectHidden);
            subcomposerConcatColDiv.append('<label for="subcomposerConcat-1">Subcomposer concat</label>');
            subcomposerConcatColDiv.append(subcomposerConcatInput);
            var addSubcomposerButton = $('<button id="add-subcomposer-1" type="submit" class="btn btn-primary add-subcomposer">+</button>');
            var deleteSubcomposerButton = $('<button id="delete-subcomposer-1" type="submit" class="btn btn-danger delete-subcomposer">-</button>');
            subcomposerButtonColDiv.append(addSubcomposerButton);
            subcomposerButtonColDiv.append(deleteSubcomposerButton);
            if (authorSong == undefined) {
                deleteSubcomposerButton.prop("disabled", true);
            }
            subcomposerRowDiv.append(subcomposerInputColDiv);
            subcomposerRowDiv.append(subcomposerConcatColDiv);
            subcomposerRowDiv.append(subcomposerButtonColDiv);
            subcomposerDiv.append(subcomposerRowDiv);
        } else {
            var subcomposerRowDivNext = $('<div class="row p-1">');
            var subcomposerInputColDivNext = $('<div class="col">');
            var subcomposerButtonColDivNext = $('<div class="col">');
            var subcomposerSelectNext = $('<input class="form-control subcomposer-select" id="subcomposerSelect-' + i + '" value="' + officialArtistName + '"/>');
            var subcomposerSelectHiddenNext = $('<input type="hidden" id="subcomposerSelectHidden-' + i + '" value="' + officialArtistName + '"/>');
            subcomposerInputColDivNext.append('<label for="subcomposerSelect-' + i + '">Subcomposer</label>');
            subcomposerInputColDivNext.append(subcomposerSelectNext);
            subcomposerInputColDivNext.append(subcomposerSelectHiddenNext);
            subcomposerButtonColDivNext.append('<button id="add-subcomposer-' + i + '" type="submit" class="btn btn-primary add-subcomposer">+</button>');
            subcomposerButtonColDivNext.append('<button id="delete-subcomposer-' + i + '" type="submit" class="btn btn-danger delete-subcomposer">-</button>');
            subcomposerRowDivNext.append(subcomposerInputColDivNext);
            if (authorSong != undefined) {
                setupAutocompleteAlias(subcomposerSelectNext, subcomposerSelectHiddenNext, authorSong.authorAlias.id);
                subcomposerSelectHiddenNext.val(authorSong.authorAlias.id);
                subcomposerSelectNext.val(officialArtistName);
                if (authorSong.subcomposerConcat != null) {
                    var divNewCol = $('<div class="col"></div>');
                    var subcomposerConcatInput = $('<input type="text" class="form-control" id="subcomposerConcatInput-' + howManySubcomposers + '"/>');
                    subcomposerConcatInput.val(authorSong.subcomposerConcat);
                    divNewCol.append('<label for="subcomposerConcatInput-' + howManySubcomposers + '">Subcomposer cncat</label>');
                    divNewCol.append(subcomposerConcatInput);
                    subcomposerRowDivNext.append(divNewCol);
                }
            } else {
                setupAutocompleteAlias(subcomposerSelectNext, subcomposerSelectHiddenNext, "");
            }
            subcomposerRowDivNext.append(subcomposerButtonColDivNext);
            subcomposerDiv.append(subcomposerRowDivNext);
        }
    }

    function generateRemixDiv(remixSelect, remixSelectHidden, remixDiv, songSubgroup, remixInputColDiv, remixButtonColDiv, remixRowDiv, howManyRemixes, officialArtistName, i, authorSong) {
        if (howManyRemixes == 0) {
            if (authorSong != undefined) {
                setupAutocompleteAlias(remixSelect, remixSelectHidden, authorSong.authorAlias.id);
                remixSelectHidden.val(authorSong.authorAlias.id);
                remixSelect.val(officialArtistName);
            } else {
                setupAutocompleteAlias(remixSelect, remixSelectHidden, "");
            }
            remixDiv.append('<h4>Remix</h4>')
            var remixInput;
            if (authorSong != undefined) {
                remixInput = ('<input type="checkbox" class="form-check-input m-3" id="remixBox" checked></input>');
            } else {
                remixInput = ('<input type="checkbox" class="form-check-input m-3" id="remixBox"></input>');
                $(remixRowDiv).hide();
            }
            var remixLabel = ('<label class="form-check-label m-2" for="remixBox">Remix?</label>');
            remixDiv.append(remixLabel);
            remixDiv.append(remixInput);
            remixInputColDiv.append('<label for="remixSelect-1">Remix</label>');
            remixInputColDiv.append(remixSelect);
            remixInputColDiv.append(remixSelectHidden);
            var addRemixButton = $('<button id="add-remix-1" type="submit" class="btn btn-primary add-remix">+</button>');
            var deleteRemixButton = $('<button id="delete-remix-1" type="submit" class="btn btn-danger delete-remix">-</button>');
            remixButtonColDiv.append(addRemixButton);
            remixButtonColDiv.append(deleteRemixButton);
            remixRowDiv.append(remixInputColDiv);
            remixRowDiv.append(remixButtonColDiv);
            remixDiv.append(remixRowDiv);
        } else {
            var remixRowDivNext = $('<div class="row p-1">');
            var remixInputColDivNext = $('<div class="col">');
            var remixButtonColDivNext = $('<div class="col">');
            var remixSelectNext = $('<input class="form-control remix-select" id="remixSelect-' + i + '" value="' + officialArtistName + '"/>');
            var remixSelectHiddenNext = $('<input type="hidden" id="remixSelectHidden-' + i + '" value="' + officialArtistName + '"/>');
            remixInputColDivNext.append('<label for="remixSelect-' + i + '">Remix</label>');
            remixInputColDivNext.append(remixSelectNext);
            remixInputColDivNext.append(remixSelectHiddenNext);
            remixButtonColDivNext.append('<button id="add-remix-' + i + '" type="submit" class="btn btn-primary add-remix">+</button>');
            remixButtonColDivNext.append('<button id="delete-remix-' + i + '" type="submit" class="btn btn-danger delete-remix">-</button>');
            remixRowDivNext.append(remixInputColDivNext);
            if (authorSong != undefined) {
                setupAutocompleteAlias(remixSelectNext, remixSelectHiddenNext, authorSong.authorAlias.id);
                remixSelectHiddenNext.val(authorSong.authorAlias.id);
                remixSelectNext.val(officialArtistName);
                if (authorSong.remixConcat != null) {
                    var divNewCol = $('<div class="col"></div>');
                    var remixConcatInput = $('<input type="text" class="form-control" id="remixConcatInput-' + howManyRemixes + '"/>');
                    remixConcatInput.val(authorSong.remixConcat);
                    divNewCol.append('<label for="featConcatInput-' + howManyRemixes + '">Remix concat</label>');
                    divNewCol.append(remixConcatInput);
                    remixRowDivNext.append(divNewCol);
                }
            } else {
                setupAutocompleteAlias(remixSelectNext, remixSelectHiddenNext, "");
            }
            remixRowDivNext.append(remixButtonColDivNext);
            remixDiv.append(remixRowDivNext);
        }
    }

    $(document).on('click', 'button.add-remix', function (e) {
        var col = $(this).parent();
        var rowCol = col.parent();
        var divCol = rowCol.parent();
        var thisId = Number($(this).attr("id").replace("add-remix-", ""));
        generateRemixDiv(null, null, divCol, null, null, null, null, null, "", (thisId + 1));
        var divNewCol = $('<div class="col"></div>');
        var remixConcatInput = $('<input type="text" class="form-control" id="remixConcatInput-' + thisId + '"/>');
        divNewCol.append('<label for="remixConcatInput-' + thisId + '">Remix concat string</label>');
        divNewCol.append(remixConcatInput);
        divNewCol.insertBefore(col);
    });

    $(document).on('click', 'button.delete-remix', function (e) {
        var col = $(this).parent();
        var rowCol = col.parent();
        var idOfInput = $(this).attr("id").replace("delete-remix-", "remixSelect-");
        if ($("#" + idOfInput).val() == "" || $("#" + idOfInput).val() == undefined) {
            var rowCol = col.parent();
            var divCol = rowCol.parent();
            divCol.remove();
        } else {
            $("#" + idOfInput).addClass("text-decoration-line-through");
        }

    });

    $(document).on('click', 'button.add-feat', function (e) {
        var col = $(this).parent();
        var rowCol = col.parent();
        var divCol = rowCol.parent();
        var thisId = Number($(this).attr("id").replace("add-feat-", ""));
        generateFeatDiv(null, null, divCol, null, null, null, (thisId + 1), (thisId + 1), "");
        var divNewCol = $('<div class="col"></div>');
        var featConcatInput = $('<input type="text" class="form-control" id="featConcatInput-' + thisId + '"/>');
        divNewCol.append('<label for="featConcatInput-' + thisId + '">Feat. concat string</label>');
        divNewCol.append(featConcatInput);
        divNewCol.insertBefore(col);
    });

    $(document).on('click', 'button.delete-feat', function (e) {
        var col = $(this).parent();
        var rowCol = col.parent();
        var idOfInput = $(this).attr("id").replace("delete-feat-", "featSelect-");
        if ($("#" + idOfInput).val() == "" || $("#" + idOfInput).val() == undefined) {
            var rowCol = col.parent();
            var divCol = rowCol.parent();
            divCol.remove();
        } else {
            $("#" + idOfInput).addClass("text-decoration-line-through");
        }
    });

    $(document).on('click', 'button.add-subcomposer', function (e) {
        var col = $(this).parent();
        var rowCol = col.parent();
        var divCol = rowCol.parent();
        var thisId = Number($(this).attr("id").replace("add-subcomposer-", "")) + 1;
        generateSubcomposerDiv(null, null, divCol, null, null, null, null, thisId, thisId, "", null);
        var divNewCol = $('<div class="col"></div>');
        var subcomposerConcatInput = $('<input type="text" class="form-control" id="subcomposerConcatInput-' + thisId + '"/>');
        divNewCol.append('<label for="subcomposerConcatInput-' + thisId + '">Subcomposer concat</label>');
        divNewCol.append(subcomposerConcatInput);
        var childrenOfDivCol = divCol.children();
        var rowToPutConcat = $(divCol.children()[thisId + 1]);
        if (rowToPutConcat.length == 0) {
            rowToPutConcat = $(divCol.children()[childrenOfDivCol.length - 1]);
        }
        var colWithButtons = rowToPutConcat.children()[1];
        divNewCol.insertBefore(colWithButtons);
    });

    $(document).on('click', 'button.delete-subcomposer', function (e) {
        var col = $(this).parent();
        var rowCol = col.parent();
        var idOfInput = $(this).attr("id").replace("delete-subcomposer-", "subcomposerSelect-");
        if ($("#" + idOfInput).val() == "" || $("#" + idOfInput).val() == undefined) {
            var rowCol = col.parent();
            rowCol.remove();
        } else {
            $("#" + idOfInput).addClass("text-decoration-line-through");
        }
    });

    $(document).on('click', 'button.add-genre', function (e) {
        var thisId = Number($(this).attr("id").replace("add-genre-", ""));
        generateGenreDiv($("#genreDisplay"), null, (thisId + 1));
    });

    $(document).on('click', 'button.delete-genre', function (e) {
        var col = $(this).parent();
        var rowCol = col.parent();
        var idOfInput = $(this).attr("id").replace("delete-genre-", "genreSelect-");
        if ($("#" + idOfInput).val() == "" || $("#" + idOfInput).val() == undefined) {
            var rowCol = col.parent();
            var divCol = rowCol.parent();
            divCol.remove();
        } else {
            $("#" + idOfInput).addClass("text-decoration-line-through");
        }
    });

    $(document).on('click', '#cancel-song', function (e) {
        getSubgroupsFromGame();
    });

    $(document).on('click', '#cancel-song-globally', function (e) {
        getSubgroupsFromGame();
    });

    function generateSpotifyAndLyrics(ingameDisplayDiv, songSubgroup) {
        ingameDisplayDiv.append("<h4>Spotify and others / lyrics</h4>");
        var itunesInput = $('<input type="text" class="form-control" id="itunesInput"/>');
        var spotifyInput = $('<input type="text" class="form-control" id="spotifyInput"/>');
        var soundcloudInput = $('<input type="text" class="form-control" id="soundcloudInput"/>');
        var deezerInput = $('<input type="text" class="form-control" id="deezerInput"/>');
        var tidalInput = $('<input type="text" class="form-control" id="tidalInput"/>');
        var lyricsTextArea = $('<textarea class="form-control" id="lyrics"></textarea/>');
        if (songSubgroup != null) {
            itunesInput.val(songSubgroup.itunesLink);
            spotifyInput.val(songSubgroup.spotifyId);
            soundcloudInput.val(songSubgroup.soundcloudLink);
            deezerInput.val(songSubgroup.deezerId);
            tidalInput.val(songSubgroup.tidalLink);
            lyricsTextArea.text(songSubgroup.lyrics);
        }
        var socialDiv = $('<div class="row p-1">');
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

    $(document).on('focusin', '#authorSelect-0', function (e) {
        $("#authorSelectHidden-0").val("");
    });

    $(document).on('focusout', '#authorSelect-0', function (e) {
        if (newSong) {
            var hiddenVal = $("#authorSelectHidden-0").val();
            if (hiddenVal == "") {
                $("#aliasSelect-0").val($(this).val());
                $("#officialBand").val($(this).val());
            }
            if ($(this).val()=="Somebody"){
                let somebodyVar = "Somebodygame_id"+gameId;
                $(this).val(somebodyVar);
                $("#aliasSelect-0").val(somebodyVar);
                $("#officialBand").val(somebodyVar);
            }
        }
    });

    $(document).on('focusout', '#officialTitle', function (e) {
        if (newSong) {
            var songToFind = new Object();
            songToFind.band = $("#officialBand").val();
            songToFind.title = $(this).val();
            $.ajax({
                async: false,
                type: "PUT",
                data: JSON.stringify(songToFind),
                url: "/song/findExact",
                contentType: 'application/json; charset=utf-8',
                // dataType: 'json',
                success: function (ooo) {
                    var existingSong = JSON.parse(ooo);
                    if (existingSong) {
                        $("#existingSongId").val(existingSong.id);
                        $("#officialTitle").val(existingSong.officialDisplayTitle);
                        $("#itunesInput").val(existingSong.itunesLink);
                        $("#spotifyInput").val(existingSong.spotifyId);
                        $("#soundcloudInput").val(existingSong.soundcloudLink);
                        $("#deezerInput").val(existingSong.deezerId);
                        $("#tidalInput").val(existingSong.tidalLink);
                        $("#lyricsTextArea").text(existingSong.lyrics);
                        $("#officialSrcId").val(existingSong.srcId);
                    }
                },
                error: function (ooo) {
                    $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                        $(failureAlertHtml).slideUp(500);
                    });
                },
            });
        }
    });

    $(document).on('focusin', '#aliasSelect-0', function (e) {
        $("#aliasSelectHidden-0").val("");
    });

    $(document).on('focusin', 'input.feat-select', function (e) {
        $(this).next().val("");
    });

    $(document).on('focusin', 'input.remix-select', function (e) {
        $(this).next().val("");
    });

    $(document).on('focusin', 'input.subcomposer-select', function (e) {
        $(this).next().val("");
    });

    $(document).on('click', "#remixBox", function (e) {
        if ($(this).prop("checked")) {
            $(this).next().show();
        } else {
            $(this).next().hide();
        }
    });

    $(document).on('focusout', '#ingameSrcId', function (e) {
        var typedSrcId = $(this).val();
        var indexOfMark = typedSrcId.indexOf("?v=");
        if (indexOfMark > -1) {
            $(this).val(typedSrcId.substring(indexOfMark + 3, indexOfMark + 14));
        } else {
            var indexOfTuDotBe = typedSrcId.indexOf(".be");
            if (indexOfTuDotBe > -1) {
                $(this).val(typedSrcId.substring(indexOfTuDotBe + 4, indexOfTuDotBe + 15));
            }
        }
    });

    $(document).on('focusout', '#officialSrcId', function (e) {
        var typedSrcId = $(this).val();
        var indexOfMark = typedSrcId.indexOf("?v=");
        if (indexOfMark > -1) {
            $(this).val(typedSrcId.substring(indexOfMark + 3, indexOfMark + 14));
        } else {
            var indexOfTuDotBe = typedSrcId.indexOf(".be");
            if (indexOfTuDotBe > -1) {
                $(this).val(typedSrcId.substring(indexOfTuDotBe + 4, indexOfTuDotBe + 15));
            }
        }
    });

    $(document).on('click', '#save-song', function (e) {
        songToSave = new Object();
        if ($("#authorSelectHidden-0").val() == "") {
            songToSave.authorId = "NEW-" + $("#authorSelect-0").val();
        } else {
            songToSave.authorId = $("#authorSelectHidden-0").val();
        }
        if ($("#aliasSelectHidden-0").val() == "") {
            songToSave.aliasId = "NEW-" + $("#aliasSelect-0").val();
        } else {
            songToSave.aliasId = $("#aliasSelectHidden-0").val();
        }
        songToSave.instrumental = $("#instrumentalBox").prop("checked");
        songToSave.ingameBand = $("#ingameBand").val();
        songToSave.ingameTitle = $("#ingameTitle").val();
        songToSave.ingameSrcId = $("#ingameSrcId").val();
        var feats = $("#featDiv").find("input.feat-select");
        songToSave.feat = false;
        for (let i = 0; i < feats.length; i++) {
            var featInput = feats[i];
            if ($(featInput).val() != "") {
                songToSave.feat = true;
                if ($(featInput).next().val() != "") {
                    if ($(featInput).hasClass("text-decoration-line-through")) {
                        songToSave[featInput.id] = "DELETE-" + $(featInput).next().val();
                    } else {
                        songToSave[featInput.id] = $(featInput).next().val();
                    }
                } else {
                    songToSave[featInput.id] = "NEW-" + $(featInput).val();
                }
                var concatInput = $("#featConcatInput-" + i);
                if (concatInput.length > 0) {
                    songToSave[concatInput.attr("id")] = $(concatInput).val();
                }
            }
        }
        var remixes = $("#remixer").find("input.remix-select");
        songToSave.remix = false;
        for (let i = 0; i < remixes.length; i++) {
            var remixInput = remixes[i];
            if ($(remixInput).val() != "") {
                songToSave.remix = true;
                if ($(remixInput).next().val() != "") {
                    if ($(remixInput).hasClass("text-decoration-line-through")) {
                        songToSave[remixInput.id] = "DELETE-" + $(remixInput).next().val();
                    } else {
                        songToSave[remixInput.id] = $(remixInput).next().val();
                    }
                } else {
                    songToSave[remixInput.id] = "NEW-" + $(remixInput).val();
                }
                var concatInput = $("#remixConcatInput-" + i);
                if (concatInput.length > 0) {
                    songToSave[concatInput.attr("id")] = $(concatInput).val();
                }
            }
        }
        var subcomposers = $("#subcomposerDiv").find("input.subcomposer-select");
        songToSave.subcomposer = false;
        for (let i = 0; i < subcomposers.length; i++) {
            var subcomposerInput = subcomposers[i];
            if ($(subcomposerInput).val() != "") {
                songToSave.subcomposer = true;
                if ($(subcomposerInput).next().val() != "") {
                    if ($(subcomposerInput).hasClass("text-decoration-line-through")) {
                        songToSave[subcomposerInput.id] = "DELETE-" + $(subcomposerInput).next().val();
                    } else {
                        songToSave[subcomposerInput.id] = $(subcomposerInput).next().val();
                    }
                } else {
                    songToSave[subcomposerInput.id] = "NEW-" + $(subcomposerInput).val();
                }
                var concatInput = $("#subcomposerConcatInput-" + (i + 1));
                if (concatInput.length > 0) {
                    songToSave[concatInput.attr("id")] = $(concatInput).val();
                }
            }
        }
        songToSave.lyrics = $("#lyrics").val();
        songToSave.spotify = $("#spotifyInput").val();
        songToSave.itunes = $("#itunesInput").val();
        songToSave.soundcloud = $("#soundcloudInput").val();
        songToSave.deezer = $("#deezerInput").val();
        songToSave.tidal = $("#tidalInput").val();
        songToSave.info = $("#ingameInfo").val();
        $.ajax({
            async: false,
            type: "PUT",
            data: JSON.stringify(songToSave),
            url: "/songSubgroup/put/" + Number(currentSongSubgroup.id),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function (ooo) {
                getSubgroupsFromGame();
                $(successAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(successAlertHtml).slideUp(500);
                });
            },
            error: function (ooo) {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500);
                });
            },
        });
    });

    $(document).on('click', '#save-song-globally', function (e) {
        var divToAppend = $('#nfs-content');
        var songGloballyToSave = new Object();
        songGloballyToSave.officialBand = $("#officialBand").val();
        songGloballyToSave.officialTitle = $("#officialTitle").val();
        songGloballyToSave.officialSrcId = $("#officialSrcId").val();
        songGloballyToSave.lyrics = $("#lyrics").val();
        songGloballyToSave.spotify = $("#spotifyInput").val();
        songGloballyToSave.itunes = $("#itunesInput").val();
        songGloballyToSave.soundcloud = $("#soundcloudInput").val();
        songGloballyToSave.deezer = $("#deezerInput").val();
        songGloballyToSave.tidal = $("#tidalInput").val();
        songGloballyToSave.info = $("#ingameInfo").val();
        var genres = $("#genreDisplay").find("input.genre-select");
        for (let i = 0; i < genres.length; i++) {
            var genreInput = genres[i];
            if ($(genreInput).val() != "") {
                if ($(genreInput).next().val() != "") {
                    if ($(genreInput).hasClass("text-decoration-line-through")) {
                        songGloballyToSave[genreInput.id] = "DELETE-" + $(genreInput).next().val();
                    } else {
                        songGloballyToSave[genreInput.id] = $(genreInput).next().val();
                    }
                } else {
                    songGloballyToSave[genreInput.id] = "NEW-" + $(genreInput).val();
                }
            }
        }
        $.ajax({
            async: false,
            type: "PUT",
            data: JSON.stringify(songGloballyToSave),
            url: "/songSubgroup/putGlobally/" + Number(currentSongSubgroup.id),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function (ooo) {
                console.log("eee");
                getSubgroupsFromGame();
                $(successAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(successAlertHtml).slideUp(500);
                    divToAppend.empty();
                });
            },
            error: function (ooo) {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500);
                });
            },
        });
    });

    $(document).on('click', '#new-song', function (e) {
        var divToAppend = $('#nfs-content');
        divToAppend.empty();
        newSong = true;
        renderEditCreateSong(divToAppend, null);
    });

    $(document).on('click', '#save-new-song', function (e) {
        var songToSave = new Object();
        if ($("#authorSelectHidden-0").val() == "") {
            songToSave.authorId = "NEW-" + $("#authorSelect-0").val();
        } else {
            songToSave.authorId = $("#authorSelectHidden-0").val();
        }
        if ($("#aliasSelectHidden-0").val() == "") {
            songToSave.aliasId = "NEW-" + $("#aliasSelect-0").val();
        } else {
            songToSave.aliasId = $("#aliasSelectHidden-0").val();
        }
        songToSave.officialBand = $("#officialBand").val();
        songToSave.officialTitle = $("#officialTitle").val();
        songToSave.officialSrcId = $("#officialSrcId").val();
        songToSave.instrumental = $("#instrumentalBox").prop("checked");
        if ($("#ingameBand").val() != "") {
            songToSave.ingameBand = $("#ingameBand").val();
        }
        if ($("#ingameTitle").val() != "") {
            songToSave.ingameTitle = $("#ingameTitle").val();
        }
        if ($("#ingameSrcId").val() != "") {
            songToSave.ingameSrcId = $("#ingameSrcId").val();
        }
        songToSave.info = $("#ingameInfo").val();
        songToSave.subgroup = currentSubgroup;
        if ($("#existingSongId").val() != "") {
            songToSave.existingSongId = $("#existingSongId").val();
            //fix when there is a remix
        } else {
            var feats = $("#featDiv").find("input.feat-select");
            songToSave.feat = false;
            for (let i = 0; i < feats.length; i++) {
                var featInput = feats[i];
                if ($(featInput).val() != "") {
                    songToSave.feat = true;
                    if ($(featInput).next().val() != "") {
                        songToSave[featInput.id] = $(featInput).next().val();
                    } else {
                        songToSave[featInput.id] = "NEW-" + $(featInput).val();
                    }
                    var concatInput = $("#featConcatInput-" + i);
                    if (concatInput.length > 0) {
                        songToSave[concatInput.attr("id")] = $(concatInput).val();
                    }
                }
            }
            var remixes = $("#remixer").find("input.remix-select");
            songToSave.remix = false;
            for (let i = 0; i < remixes.length; i++) {
                var remixInput = remixes[i];
                if ($(remixInput).val() != "") {
                    songToSave.remix = true;
                    if ($(remixInput).next().val() != "") {
                        songToSave[remixInput.id] = $(remixInput).next().val();

                    } else {
                        songToSave[remixInput.id] = "NEW-" + $(remixInput).val();
                    }
                    var concatInput = $("#remixConcatInput-" + i);
                    if (concatInput.length > 0) {
                        songToSave[concatInput.attr("id")] = $(concatInput).val();
                    }
                }
            }
            var subcomposers = $("#subcomposerDiv").find("input.subcomposer-select");
            songToSave.subcomposer = false;
            for (let i = 0; i < subcomposers.length; i++) {
                var subcomposerInput = subcomposers[i];
                if ($(subcomposerInput).val() != "") {
                    songToSave.subcomposer = true;
                    if ($(subcomposerInput).next().val() != "") {
                        songToSave[subcomposerInput.id] = $(subcomposerInput).next().val();
                    } else {
                        songToSave[subcomposerInput.id] = "NEW-" + $(subcomposerInput).val();
                    }
                    var concatInput = $("#subcomposerConcatInput-" + (i + 1));
                    if (concatInput.length > 0) {
                        songToSave[concatInput.attr("id")] = $(concatInput).val();
                    }
                }
            }
            var genres = $("#genreDisplay").find("input.genre-select");
            for (let i = 0; i < genres.length; i++) {
                var genreInput = genres[i];
                if ($(genreInput).val() != "") {
                    if ($(genreInput).next().val() != "") {
                        songToSave[genreInput.id] = $(genreInput).next().val();
                    } else {
                        songToSave[genreInput.id] = "NEW-" + $(genreInput).val();
                    }
                }
            }
        }
        songToSave.lyrics = $("#lyrics").val();
        songToSave.spotify = $("#spotifyInput").val();
        songToSave.itunes = $("#itunesInput").val();
        songToSave.soundcloud = $("#soundcloudInput").val();
        songToSave.deezer = $("#deezerInput").val();
        songToSave.tidal = $("#tidalInput").val();
        $.ajax({
            async: false,
            type: "POST",
            data: JSON.stringify(songToSave),
            url: "/songSubgroup/post/" + currentSubgroup,
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function (ooo) {
                console.log("eee");
                $(successAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(successAlertHtml).slideUp(500);
                    $('#nfs-content').empty();
                    getSubgroupsFromGame();
                });
            },
            error: function (ooo) {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500);
                    $('#nfs-content').empty();
                    getSubgroupsFromGame();
                });
            },
        });
    });
});