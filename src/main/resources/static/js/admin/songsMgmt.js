var currentSongSubgroup;
var currentSubgroup;
var dropdownDiv;
var newSong = false;
$(document).ready(function () {

    $(successAlertHtml).hide();
    $(failureAlertHtml).hide();

    function getSingleSubgroupFromGame(subgroupId) {
        if (subgroupId != 0) {
            $.ajax({
                async: false,
                type: "GET",
                url: "/subgroup/read/" + subgroupId,
                success: function (ooo) {
                    var allSongsInSubgroup = JSON.parse(ooo);
                    tableToFill = displayAllSongs(allSongsInSubgroup.songSubgroupList, dropdownDiv);
                    $('#nfs-content').append(tableToFill);
                }, error: function (ooo) {
                    $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                        $(failureAlertHtml).slideUp(500);
                    });
                },
            });
        } else {
            $.ajax({
                async: false,
                type: "GET",
                url: "/maingroup/readForEditGroups/" + gameId,
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
                    dropdownDiv = $('<div id="selectSubgroup" class="dropdown">');
                    dropdownDiv.append('<button class="btn btn-secondary dropdown-toggle" type="button" id="subgroupsDropdown" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">All</button>');
                    leftCellDiv.append(dropdownDiv);
                    rightCellDiv.append(newGroupSpan);
                    if (fullScopeOfEdit.length > 0) {
                        var dropdownMenuDiv = $('<div class="dropdown-menu" style="max-height: 350px; overflow-y: auto;" aria-labelledby="subgroupsDropdown">');
                        for (let i = 0; i < fullScopeOfEdit.length; i++) {
                            var group = fullScopeOfEdit[i];
                            var groupName = fullScopeOfEdit[i].groupName;
                            for (let j = 0; j < group.subgroups.length; j++) {
                                var subgroup = group.subgroups[j];
                                var aSubgroup = $('<a class="dropdown-item songItem" href="#" data-groupId="' + group.id + '" data-subgroupId="' + subgroup.id + '">');
                                aSubgroup.text('(' + subgroup.subgroupName + ') from group [' + groupName + ']');
                                dropdownMenuDiv.append(aSubgroup);
                            }
                        }
                        dropdownDiv.append(dropdownMenuDiv);
                    }
                },
                error: function (ooo) {
                    $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                        $(failureAlertHtml).slideUp(500);
                    });
                },
            });
        }
    }

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
        if ($("#songs-table").length > 0) {
            $("#songs-table").remove();
        }
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
        getSingleSubgroupFromGame(0);
    });

    $(document).on('click', 'button.delete-song', function (e) {
        var songId = $(this).attr("data-songSubgroupId");
        $.ajax({
            async: false,
            type: "DELETE",
            url: "/songSubgroup/delete/" + Number(songId),
            success: function (ooo) {
                getSingleSubgroupFromGame(0);
                $(successAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(successAlertHtml).slideUp(500, function () {
                        $("#selectSubgroup").find("a[data-subgroupid='" + currentSubgroup + "']").click();
                    });
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
        saveCol.append('<input type="checkbox" class="form-check-input m-3" id="propagate"><label class="form-check-label pl-2 pt-2" for="propagate">Propagate links to in-game entry?</label>');
        saveOrCancelDiv.append(saveCol);
        var artistAndAliasDiv = $('<div class="row p-1">');
        var artistDiv = $('<div class="col">');
        var aliasDiv = $('<div class="col">');
        var instrumentalDiv = $('<div class="col">');
        var officialTitleDiv = $('<div class="col">');
        var mainComposerDiv = $('<div class="form-group mainComposer" id="mainComposer">');
        var ingameDisplayDiv = $('<div class="form-group inGameDisplay" id="inGameDisplay">');
        var spotifyOthersDiv = $('<div class="form-group spotifyDisplay" id="spotifyDisplay">');
        var featDiv = $('<div class="form-group featDiv" id="featDiv">');
        var featRowDiv = $('<div class="row p-1">');
        var featInputColDiv = $('<div class="col">');
        var featButtonColDiv = $('<div class="col mt-auto">');
        var remixDiv = $('<div class="form-group remixer" id="remixer">');
        var remixRowDiv = $('<div class="row p-1">');
        var remixInputColDiv = $('<div class="col">');
        var remixButtonColDiv = $('<div class="col mt-auto">');
        var remixValueDiv = $('<div class="col">');
        var remixOfDiv = $('<div class="col">');
        var subcomposerDiv = $('<div class="form-group subcomposerDiv" id="subcomposerDiv">');
        var subcomposerRowDiv = $('<div class="row p-1">');
        var subcomposerInputColDiv = $('<div class="col">');
        var subcomposerConcatColDiv = $('<div class="col">');
        var subcomposerButtonColDiv = $('<div class="col mt-auto">');
        var filenameColDiv = $('<div class="col">');
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
                var officialTitle = songSubgroup.song.officialDisplayTitle;
                var officialArtistName = authorSong.authorAlias.author.name;
                var aliasSelect = $('<input class="form-control w-100" id="aliasSelect-' + i + '" value="' + officialArtistName + '"/>');
                var aliasSelectHidden = $('<input type="hidden" id="aliasSelectHidden-' + i + '" value="' + authorSong.authorAlias.id + '"/>');
                if (authorSong.role == "COMPOSER") {
                    generateAuthorDiv(mainComposerDiv, officialArtistName, artistDiv, aliasDiv, artistAndAliasDiv, aliasSelect, aliasSelectHidden, i, authorSong, instrumentalDiv, officialTitleDiv, songSubgroup.instrumental, officialTitle, songSubgroup.showFeat, songSubgroup.showSubcomposer);
                }
                if (authorSong.role == "SUBCOMPOSER") {
                    howManySubcomposers++;
                    generateSubcomposerDiv(subcomposerSelect, subcomposerSelectHidden, subcomposerDiv, subcomposerInputColDiv, subcomposerConcatColDiv, subcomposerButtonColDiv, filenameColDiv, subcomposerRowDiv, howManySubcomposers, i, officialArtistName, authorSong, songSubgroup);
                }
                if (authorSong.role == "FEAT") {
                    howManyFeats++;
                    generateFeatDiv(featSelect, featSelectHidden, featDiv, featInputColDiv, featButtonColDiv, featRowDiv, howManyFeats, howManyFeats, officialArtistName, authorSong);
                    console.log(authorSong);
                }
                if (authorSong.role == "REMIX") {
                    howManyRemixes++;
                    console.log(authorSong);
                    generateRemixDiv(remixSelect, remixSelectHidden, remixDiv, songSubgroup, remixInputColDiv, remixButtonColDiv, remixValueDiv, remixOfDiv, remixRowDiv, howManyRemixes, officialArtistName, i, authorSong);
                }
            }
        } else {
            var aliasSelect = $('<input class="form-control w-100" id="aliasSelect-0"/>');
            var aliasSelectHidden = $('<input type="hidden" id="aliasSelectHidden-0"/>');
            generateAuthorDiv(mainComposerDiv, "", artistDiv, aliasDiv, artistAndAliasDiv, aliasSelect, aliasSelectHidden, 0, null, instrumentalDiv, officialTitleDiv, false, "", true);
        }
        if (howManySubcomposers == 0) {
            // howManySubcomposers++;
            generateSubcomposerDiv(subcomposerSelect, subcomposerSelectHidden, subcomposerDiv, subcomposerInputColDiv, subcomposerConcatColDiv, subcomposerButtonColDiv, filenameColDiv, subcomposerRowDiv, howManySubcomposers, howManySubcomposers, officialArtistName, undefined, songSubgroup);
        }
        if (howManyFeats == 0) {
            // howManyFeats++;
            generateFeatDiv(featSelect, featSelectHidden, featDiv, featInputColDiv, featButtonColDiv, featRowDiv, howManyFeats, howManyFeats, undefined);
        }
        if (howManyRemixes == 0) {
            // howManyRemixes++;
            generateRemixDiv(remixSelect, remixSelectHidden, remixDiv, songSubgroup, remixInputColDiv, remixButtonColDiv, remixValueDiv, remixOfDiv, remixRowDiv, howManyRemixes, officialArtistName, howManyFeats, undefined);
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
                saveCol.append('<input type="checkbox" class="form-check-input m-3" id="propagate"><label class="form-check-label pl-2 pt-2" for="propagate">Propagate links to in-game entry?</label>');
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
                        if (result) {
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
                if (previousValue != ui.item.value && !newSong) {
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
                        if (result) {
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
                $(mySelectHidden).val(ui.item.aliasId);
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
                        if (result) {
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
                        if (result) {
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
                $(mySelectHidden).val(ui.item.aliasId);
            },
            minLength: 1
        });
    }

    $(document).on('click', 'a.songItem', function (e) {
        $("#subgroupsDropdown").text($(this).text());
        var subgroupId = Number($(this).attr('data-subgroupId'));
        currentSubgroup = Number(subgroupId);
        getSingleSubgroupFromGame(currentSubgroup);
        currentSubgroupId = subgroupId;
        // var groupId = Number($(this).attr('data-groupId'));
        // var subgroupSongs;
        // for (let i = 0; i < fullScopeOfEdit.length; i++) {
        //     if (fullScopeOfEdit[i].id == groupId) {
        //         for (let j = 0; j < fullScopeOfEdit[i].subgroups.length; j++) {
        //             if (fullScopeOfEdit[i].subgroups[j].id == subgroupId) {
        //                 subgroupSongs = fullScopeOfEdit[i].subgroups[j].songSubgroupList;
        //             }
        //         }
        //     }
        // }
        // currentSubgroupId = subgroupId;
        // console.log("e");
        // $("#songs-table").find('tr').each(function (e) {
        //     $(this).addClass("visually-hidden");
        // });
        // for (let i = 0; i < subgroupSongs.length; i++) {
        //     var songId = subgroupSongs[i].song.id;
        //     var songToMark = $("#songs-table").find('tr[data-songId="' + songId + '"][data-songsubgroupid="' + subgroupSongs[i].id + '"]').first();
        //     songToMark.attr("data-songSubgroupId", subgroupSongs[i].id);
        //     songToMark.removeClass("visually-hidden");
        // };
    });

    function generateAuthorDiv(mainComposerDiv, officialArtistName, artistDiv, aliasDiv,
        artistAndAliasDiv, aliasSelect, aliasSelectHidden, i, authorSong, instrumentalDiv,officialTitleDiv, instrumentalValue, officialTitle, showFeat, showSubcomposer) {
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
        var showFeatInput;
        if (showFeat) {
            showFeatInput = ('<input type="checkbox" class="form-check-input mt-4" id="showFeatBox" checked>');
        } else {
            showFeatInput = ('<input type="checkbox" class="form-check-input mt-4" id="showFeatBox">');
        }
        var showFeatLabel = ('<label class="form-check-label mt-3" for="showFeatBox">Show feat?</label>');
        var showSubcomposerInput;
        if (showSubcomposer) {
            showSubcomposerInput = ('<input type="checkbox" class="form-check-input mt-4" id="showSubcomposerBox" checked>');
        } else {
            showSubcomposerInput = ('<input type="checkbox" class="form-check-input mt-4" id="showSubcomposerBox">');
        }
        var showSubcomposerLabel = ('<label class="form-check-label mt-3" for="showSubcomposerBox">Show subcomposer?</label>');
        instrumentalDiv.append('<br>');
        instrumentalDiv.append(showFeatInput);
        instrumentalDiv.append(showFeatLabel);
        instrumentalDiv.append('<br>');
        instrumentalDiv.append(showSubcomposerInput);
        instrumentalDiv.append(showSubcomposerLabel);
        if (officialTitle != undefined){
            var officialTitleInput = $('<input class="form-control" id="officialSongTitle" value="' + officialTitle + '" disabled/>');
            officialTitleDiv.append('<label class="form-check-label mt-3" for="officialSongTitle">Official title</label>');
            officialTitleDiv.append(officialTitleInput);
        }
        artistAndAliasDiv.append(instrumentalDiv);
        artistAndAliasDiv.append(officialTitleDiv);
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
        var featNextToComposer;
        if (songSubgroup==null){
            featNextToComposer = ('<input type="checkbox" class="form-check-input mt-4" id="featNextToComposer">');
        } else {
            if (songSubgroup.song.featNextToBand){
                featNextToComposer = ('<input type="checkbox" class="form-check-input mt-4" id="featNextToComposer" checked>');
            } else {
                featNextToComposer = ('<input type="checkbox" class="form-check-input mt-4" id="featNextToComposer">');
            }
        }
        var featNextToComposerLabel = ('<label class="form-check-label mt-3" for="featNextToComposer">Feat next to composer?</label>');
        officialBandDiv.append(officialBand);
        officialTitleDiv.append(officialTitle);
        officialSrcIdDiv.append(officialSrcId);
        existingSongIdDiv.append(existingSongId);
        existingSongIdDiv.append('<br>');
        existingSongIdDiv.append(featNextToComposer);
        existingSongIdDiv.append(featNextToComposerLabel);
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

    function generateSubcomposerDiv(subcomposerSelect, subcomposerSelectHidden, subcomposerDiv, subcomposerInputColDiv, subcomposerConcatColDiv, subcomposerButtonColDiv, filenameColDiv, subcomposerRowDiv, howManySubcomposers, i, officialArtistName, authorSong, songSubgroup) {
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
            var fileNameInput = $('<input class="form-control" id="fileNameInput"/>');
            if (songSubgroup!=undefined && songSubgroup.filename!=undefined){
                fileNameInput.val(songSubgroup.filename);
            } else {
                fileNameInput.val("");
            }
            filenameColDiv.append('<label for="fileNameInput">Song filename</label>');
            filenameColDiv.append(fileNameInput);
            if (authorSong == undefined) {
                deleteSubcomposerButton.prop("disabled", true);
            }
            subcomposerRowDiv.append(subcomposerInputColDiv);
            subcomposerRowDiv.append(subcomposerConcatColDiv);
            subcomposerRowDiv.append(subcomposerButtonColDiv);
            subcomposerRowDiv.append(filenameColDiv);
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
                    divNewCol.append('<label for="subcomposerConcatInput-' + howManySubcomposers + '">Subcomposer concat</label>');
                    divNewCol.append(subcomposerConcatInput);
                    subcomposerRowDivNext.append(divNewCol);
                }
            } else {
                setupAutocompleteAlias(subcomposerSelectNext, subcomposerSelectHiddenNext, "");
            }
            subcomposerRowDivNext.append(subcomposerButtonColDivNext);
            if (i<=1){
                var fileNameInput = $('<input class="form-control" id="fileNameInput"/>');
                if (songSubgroup!=undefined && songSubgroup.filename!=undefined){
                    fileNameInput.val(songSubgroup.filename);
                } else {
                    fileNameInput.val("");
                }
                filenameColDiv.append('<label for="fileNameInput">Song filename</label>');
                filenameColDiv.append(fileNameInput);
                subcomposerRowDivNext.append(filenameColDiv);
            }
            subcomposerDiv.append(subcomposerRowDivNext);
        }
    }

    function generateRemixDiv(remixSelect, remixSelectHidden, remixDiv, songSubgroup, remixInputColDiv, remixButtonColDiv, remixValueDiv, remixOfDiv, remixRowDiv, howManyRemixes, officialArtistName, i, authorSong) {
        if (howManyRemixes == 0) {
            if (authorSong != undefined) {
                setupAutocompleteAlias(remixSelect, remixSelectHidden, authorSong.authorAlias.id);
                remixSelectHidden.val(authorSong.authorAlias.id);
                remixSelect.val(officialArtistName);
            } else {
                setupAutocompleteAlias(remixSelect, remixSelectHidden, "");
            }
            remixDiv.append('<h4>Remix</h4>');
            var remixValue;
            var remixInput;
            var remixOf;
            if (authorSong != undefined) {
                remixInput = $('<input type="checkbox" class="form-check-input m-3" id="remixBox" checked></input>');
                remixValue = $('<input class="form-control" id="remixText" value="'+songSubgroup.remixText+'"></input>');
                if (songSubgroup.song.baseSong!=null){
                    remixOf = $('<input class="form-control" id="remixOf" value="'+songSubgroup.song.baseSong.id+'"></input>');
                } else {
                    remixOf = $('<input class="form-control" id="remixOf"></input>');
                }
            } else {
                remixInput = $('<input type="checkbox" class="form-check-input m-3" id="remixBox"></input>');
                remixValue = $('<input class="form-control" id="remixText"></input>');
                remixOf = $('<input class="form-control" id="remixOf"></input>');
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
            remixValueDiv.append('<label for="remixText">Remix value</label>');
            remixValueDiv.append(remixValue);
            remixOfDiv.append('<label for="remixOf">Remix of (Song ID)</label>');
            remixOfDiv.append(remixOf);
            remixRowDiv.append(remixInputColDiv);
            remixRowDiv.append(remixButtonColDiv);
            remixRowDiv.append(remixValueDiv);
            remixRowDiv.append(remixOfDiv);
            remixDiv.append(remixRowDiv);
        } else {
            var remixRowPrev = $('<div class="row p-1">');
            var remixRowDivNext = $('<div class="row p-1">');
            var remixRowPrevColCheckbox = $('<div class="col">');
            var remixRowPrevColValue = $('<div class="col">');
            var remixRowPrevColOf = $('<div class="col">');
            var remixInputColDivNext = $('<div class="col">');
            var remixButtonColDivNext = $('<div class="col mt-auto">');
            var remixSelectNext = $('<input class="form-control remix-select" id="remixSelect-' + i + '" value="' + officialArtistName + '"/>');
            var remixSelectHiddenNext = $('<input type="hidden" id="remixSelectHidden-' + i + '" value="' + officialArtistName + '"/>');
            remixInputColDivNext.append('<label for="remixSelect-' + i + '">Remix</label>');
            remixInputColDivNext.append(remixSelectNext);
            remixInputColDivNext.append(remixSelectHiddenNext);
            remixButtonColDivNext.append('<button id="add-remix-' + i + '" type="submit" class="btn btn-primary add-remix">+</button>');
            remixButtonColDivNext.append('<button id="delete-remix-' + i + '" type="submit" class="btn btn-danger delete-remix">-</button>');
            remixRowDivNext.append(remixInputColDivNext);
            if ($("#remixBox").length<1){
                var remixInput = $('<input type="checkbox" class="form-check-input m-3" id="remixBox" checked></input>');
                var remixValue = $('<input class="form-control" id="remixText"></input>');
                var remixOf = $('<input class="form-control" id="remixOf"></input>');
                if (songSubgroup!=null){
                    $(remixValue).val(songSubgroup.remixText);
                    if (songSubgroup.song.baseSong!=null){
                        $(remixOf).val(songSubgroup.song.baseSong.id);
                    }
                }
                var remixLabel = ('<label class="form-check-label m-2" for="remixBox">Remix?</label>');
                remixRowPrevColCheckbox.append(remixLabel);
                remixRowPrevColCheckbox.append(remixInput);
                remixRowPrevColValue.append('<label for="remixText">Remix value</label>');
                remixRowPrevColValue.append(remixValue);
                remixRowPrevColOf.append('<label for="remixOf">Remix of (Song ID)</label>');
                remixRowPrevColOf.append(remixOf);
            }
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
            remixRowPrev.append(remixRowPrevColCheckbox);
            remixRowPrev.append(remixRowPrevColValue);
            remixRowPrev.append(remixRowPrevColOf);
            remixDiv.append(remixRowPrev);
            remixDiv.append(remixRowDivNext);
        }
    }

    $(document).on('click', 'button.add-remix', function (e) {
        var col = $(this).parent();
        var rowCol = col.parent();
        var divCol = rowCol.parent();
        var thisId = Number($(this).attr("id").replace("add-remix-", ""));
        generateRemixDiv(null, null, divCol, null, null, null, null, null, (thisId + 1), "", (thisId + 1), undefined);
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
        generateSubcomposerDiv(null, null, divCol, null, null, null, null,null, thisId, thisId, "", null, null);
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
        getSingleSubgroupFromGame(0);
        $("#selectSubgroup").find("a[data-subgroupid='" + currentSubgroup + "']").click();
    });

    $(document).on('click', '#cancel-song-globally', function (e) {
        getSingleSubgroupFromGame(0);
        $("#selectSubgroup").find("a[data-subgroupid='" + currentSubgroup + "']").click();
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
        socialDiv.append('<br><br><br><br><br><br>')
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
            if ($(this).val() == "Somebody") {
                let somebodyVar = "Somebodygame_id" + gameId;
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
    //https://open.spotify.com/track/7zI6uJWfq93wgV20V2euv6?si=873a9557277548ac
    $(document).on('focusout', '#spotifyInput', function (e) {
        var typedSrcId = $(this).val();
        var indexOfMark = typedSrcId.indexOf("track/");
        var indexOfSi = typedSrcId.indexOf("?si");
        if (indexOfSi > -1) {
            $(this).val("spotify:track:" + typedSrcId.substring(indexOfSi - 22, indexOfSi));
        } else {
            $(this).val("spotify:track:" + typedSrcId.substring(indexOfMark + 6, indexOfTuDotBe + 28));
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
        songToSave.showFeat = $("#showFeatBox").prop("checked");
        songToSave.showSubcomposer = $("#showSubcomposerBox").prop("checked");
        songToSave.ingameBand = $("#ingameBand").val();
        songToSave.ingameTitle = $("#ingameTitle").val();
        songToSave.ingameSrcId = $("#ingameSrcId").val();
        songToSave.propagate = $("#propagate").prop("checked");
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
        songToSave.remixValue=$("#remixText").val();
        songToSave.remixOf=$("#remixOf").val();
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
        songToSave.filename=$("#fileNameInput").val();
        $.ajax({
            async: false,
            type: "PUT",
            data: JSON.stringify(songToSave),
            url: "/songSubgroup/put/" + Number(currentSongSubgroup.id),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function (ooo) {
                getSingleSubgroupFromGame(0);
                $(successAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(successAlertHtml).slideUp(500, function () {
                        $("#selectSubgroup").find("a[data-subgroupid='" + currentSubgroup + "']").click();
                    });
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
        songGloballyToSave.featNextToComposer = $("#featNextToComposer").prop("checked");
        songGloballyToSave.propagate = $("#propagate").prop("checked");
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
                getSingleSubgroupFromGame(0);
                $(successAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(successAlertHtml).slideUp(500, function () {
                        $("#selectSubgroup").find("a[data-subgroupid='" + currentSubgroup + "']").click();
                    });
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
        if ($("#existingSongId").length==1){
            $("#remixOf").parent().remove();
        }
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
        songToSave.showFeat = $("#showFeatBox").prop("checked");
        songToSave.remixValue=$("#remixText").val();
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
        songToSave.featNextToComposer = $("#featNextToComposer").prop("checked");
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
            if (!songToSave.subcomposer){
                songToSave.showSubcomposer=false;
            } else {
                songToSave.showSubcomposer=true;
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
        songToSave.filename = $("#fileNameInput").val();
        $.ajax({
            async: false,
            type: "POST",
            data: JSON.stringify(songToSave),
            url: "/songSubgroup/post/" + currentSubgroup,
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function (ooo) {
                getSingleSubgroupFromGame(0);
                $(successAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(successAlertHtml).slideUp(500, function () {
                        $("#selectSubgroup").find("a[data-subgroupid='" + currentSubgroup + "']").click();
                    });
                });
            },
            error: function (ooo) {
                getSingleSubgroupFromGame(0);
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500, function () {
                        $("#selectSubgroup").find("a[data-subgroupid='" + currentSubgroup + "']").click();
                    });
                });
            },
        });
    });
});