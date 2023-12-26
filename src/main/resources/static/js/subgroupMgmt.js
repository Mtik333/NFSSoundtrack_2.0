var modifiedSubgroupSongArray = new Array();
var currentSubgroupId;

function ModifiedSubgroupSongDef(subgroup_id, song_id, state) {
    this.subgroup_id = subgroup_id;
    this.song_id = song_id;
    this.state = state;
}

$(document).ready(function () {
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
                divToAppend.append('<button id="updateSubgroupSongs" type="submit" class="btn btn-primary">Update subgroup</button>');
                var dropdownDiv = $('<div id="selectSubgroup" class="dropdown">')
                dropdownDiv.append('<button class="btn btn-secondary dropdown-toggle" type="button" id="subgroupsDropdown" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">All</button>');
                var tableToFill;
                if (fullScopeOfEdit.length > 0) {
                    var dropdownMenuDiv = $('<div class="dropdown-menu" aria-labelledby="subgroupsDropdown">');
                    tableToFill = displayAllSongs(fullScopeOfEdit[0].subgroups[0].songSubgroupList, dropdownDiv);
                    for (let i = 0; i < fullScopeOfEdit.length; i++) {
                        var group = fullScopeOfEdit[i];
                        var groupName = fullScopeOfEdit[i].groupName;
                        for (let j = 0; j < group.subgroups.length; j++) {
                            var subgroup = group.subgroups[j];
                            dropdownMenuDiv.append('<a class="dropdown-item subgroupItem" href="#" data-groupId="' + group.id + '" data-subgroupId="' + subgroup.id + '">(' + subgroup.subgroupName + ') from group [' + groupName + ']');
                        }
                    }
                    dropdownDiv.append(dropdownMenuDiv);
                }
                divToAppend.append(dropdownDiv);
                divToAppend.append(tableToFill);
                $(divToAppend).find("a").first().click();
            }
        });
    }

    function displayAllSongs(songs, dropdownDiv) {
        var tableToFill = $('<table id="subgroups-table" class="table table-bordered table-hover">');
        tableToFill.append("<tbody>");
        for (let i = 0; i < songs.length; i++) {
            var tr = $('<tr class="subgroupSong" data-songId="' + songs[i].song.id + '">');
            var checkboxTd = $('<td class="col-md-1">');
            checkboxTd.append('<input class="form-check-input songSubgroupRow" type="checkbox" value="EXISTS">');
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
            tr.append(checkboxTd);
            tr.append(textTd);
            tableToFill.append(tr);
        }
        return tableToFill;
    }
    $("a.manage-subgroups").click(function (e) {
        gameId = e.target.attributes["data-gameid"].value;
        getSubgroupsFromGame();
    });

    $(document).on('click', 'tr.subgroupSong', function(e) {
        var checkboxToChange = $(this).find("td:first").find("input");
        checkboxToChange.prop('checked', !checkboxToChange.prop('checked'));
        checkboxToChange.trigger('change');
    });

    $(document).on('click', 'a.subgroupItem', function (e) {
        $("#subgroupsDropdown").text($(this).text());
        $("#subgroups-table").find("input").each(function () {
            $(this).prop('checked', false);
            $(this).val('');
        });
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
        for (let i = 0; i < subgroupSongs.length; i++) {
            var songId = subgroupSongs[i].song.id;
            var songToMark = $("#subgroups-table").find('tr[data-songId="' + songId + '"]').first();
            var inputToMark = songToMark.find("input").first()
            inputToMark.prop('checked', true);
            inputToMark.val('EXISTS');
        };
        console.log("e");
    });

    $(document).on('change', '.songSubgroupRow', function (e) {
        var mySubgroupChange;
        var backToOriginalState = false;
        var tr = $(this).parent().parent();
        var songId = $(tr).attr("data-songid");
        if ($(this).is(":checked")) {
            if ($(this).val() == "") {
                mySubgroupChange = new ModifiedSubgroupSongDef(currentSubgroupId, songId, "ADD");
            } else {
                mySubgroupChange = new ModifiedSubgroupSongDef(currentSubgroupId, songId, "REVERT");
            }
        } else {
            if ($(this).val() == "EXISTS") {
                mySubgroupChange = new ModifiedSubgroupSongDef(currentSubgroupId, songId, "DELETE");
            } else {
                mySubgroupChange = new ModifiedSubgroupSongDef(currentSubgroupId, songId, "REVERT");
            }
        }
        var entryFound = false;
        var detachEntry;
        for (let i = 0; i < modifiedSubgroupSongArray.length; i++) {
            var curSubgroupSong = modifiedSubgroupSongArray[i];
            if (curSubgroupSong.song_id == mySubgroupChange.song_id) {
                if (mySubgroupChange.state == "REVERT") {
                    detachEntry = i;
                    entryFound = true;
                }
            }
        }
        if (!entryFound) {
            modifiedSubgroupSongArray.push(mySubgroupChange);
        }
        if (detachEntry != undefined) {
            modifiedSubgroupSongArray.splice(detachEntry, 1);
        }
    });

    $(document).on('click', '#updateSubgroupSongs', function (e) {
        $.ajax({
            async: false,
            type: "PUT",
            data: JSON.stringify(modifiedSubgroupSongArray),
            contentType: 'application/json; charset=utf-8',
            url: "/subgroup/put/" + currentSubgroupId,
            success: function (ooo) {
                console.log("eee");
                $('#success-alert').fadeTo(2000, 500).slideUp(500, function () {
                    $('#success-alert').slideUp(500);
                });
                modifiedSubgroupSongArray.length=0;
                getSubgroupsFromGame();
            }, error: function (ooo) {
                console.log("e2");
            },
            done: function (ooo) {
                console.log("e3");
            }
        });
    });
});