var modifiedSubgroupSongArray = [];
var currentSubgroupId;

function ModifiedSubgroupSongDef(subgroup_id, song_id, state) {
    this.subgroup_id = subgroup_id;
    this.song_id = song_id;
    this.state = state;
}

function ModifiedSubgroupSongPositionDef(songSubgroupId, position) {
    this.songSubgroupId = songSubgroupId;
    this.position = position;
}

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
                var rowDiv = $('<div class="row">');
                var leftCellDiv = $('<div class="col">');
                var rightCellDiv = $('<div class="col">');
                divToAppend.append(rowDiv);
                rowDiv.append(leftCellDiv);
                rowDiv.append(rightCellDiv);
                rightCellDiv.append('<button id="updateSubgroupSongs" type="submit" class="btn btn-primary">Update subgroup</button>');
                rightCellDiv.append('<button id="updatePositionsInDb" type="submit" class="btn btn-primary">Update positions in DB</button>');
                rightCellDiv.append('<button id="recounterPositions" type="submit" class="btn btn-primary">Recounter positions</button>');
                var dropdownDiv = $('<div id="selectSubgroup" class="dropdown">');
                dropdownDiv.append('<button class="btn btn-secondary dropdown-toggle" type="button" id="subgroupsDropdown" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">All</button>');
                leftCellDiv.append(dropdownDiv);
                var allSongSubgroups = [];
                for (let i = 0; i < fullScopeOfEdit.length; i++) {
                    const group = fullScopeOfEdit[i];
                    for (let j = 0; j < group.subgroups.length; j++) {
                        allSongSubgroups = allSongSubgroups.concat(fullScopeOfEdit[i].subgroups[j].songSubgroupList);
                    }
                }
                var tableToFill;
                if (fullScopeOfEdit.length > 0) {
                    var dropdownMenuDiv = $('<div class="dropdown-menu" aria-labelledby="subgroupsDropdown">');
                    tableToFill = displayAllSongs(allSongSubgroups, dropdownDiv);
                    for (let i = 0; i < fullScopeOfEdit.length; i++) {
                        const group = fullScopeOfEdit[i];
                        var groupName = fullScopeOfEdit[i].groupName;
                        for (let j = 0; j < group.subgroups.length; j++) {
                            var subgroup = group.subgroups[j];
                            dropdownMenuDiv.append('<a class="dropdown-item subgroupItem" href="#" data-groupId="' + group.id + '" data-subgroupId="' + subgroup.id + '">(' + subgroup.subgroupName + ') from group [' + groupName + ']');
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
        var tableToFill = $('<table id="subgroups-table" class="table table-bordered table-hover table-striped">');
        tableToFill.append("<tbody>");
        for (let i = 0; i < songs.length; i++) {
            var tr = $('<tr class="subgroupSong" data-songId="' + songs[i].song.id + '" data-songSubgroupId="' + songs[i].id + '">');
            var checkboxTd = $('<td class="col-md-1">');
            checkboxTd.append('<input class="form-check-input songSubgroupRow" type="checkbox" value="EXISTS">');
            var positionTd = $('<td class="col-md-1">');
            positionTd.append('<input class="form-control songSubgroupPosition" type="text" value="' + songs[i].position + '">');
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
            tr.append(positionTd);
            tr.append(textTd);
            tableToFill.append(tr);
        }
        return tableToFill;
    }
    $("a.manage-subgroups").click(function (e) {
        gameId = e.target.attributes["data-gameid"].value;
        getSubgroupsFromGame();
    });

    $(document).on('click', 'tr.subgroupSong', function (e) {
        var checkboxToChange = $(this).find("td:first").find("input");
        if (e.target.checked != undefined) {
            //checkboxToChange.trigger('change');
        } else {
            checkboxToChange.prop('checked', !checkboxToChange.prop('checked'));
            checkboxToChange.trigger('change');
        }
    });

    $(document).on('click', 'a.subgroupItem', function (e) {
        $("#subgroupsDropdown").text($(this).text());
        $("#subgroups-table").find("input:first").each(function () {
            $(this).prop('checked', false);
            $(this).val('');
        });
        var subgroupId = Number($(this).attr('data-subgroupId'));
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
        $("#subgroups-table").find("tr").each(function (index) {
            var inputStuffToReset = $(this).find("input");
            $(inputStuffToReset[0]).prop('checked', false);
            $(inputStuffToReset[0]).val('');
            $(inputStuffToReset[1]).val("1000000");
        });
        for (let i = 0; i < subgroupSongs.length; i++) {
            var songId = subgroupSongs[i].song.id;
            var songToMark = $("#subgroups-table").find('tr[data-songId="' + songId + '"]').first();
            songToMark.attr("data-songSubgroupId", subgroupSongs[i].id);
            var inputToMark = songToMark.find("input");
            $(inputToMark[0]).prop('checked', true);
            $(inputToMark[0]).val('EXISTS');
            $(inputToMark[1]).val(subgroupSongs[i].position);
        }
        console.log("e");
        if ($(this).text() == "(All) from group [All]") {
            $("#updateSubgroupSongs").prop('disabled', true);
        } else {
            $("#updateSubgroupSongs").prop('disabled', false);
        }
        sortTable();
    });

    $(document).on('change', '.songSubgroupRow', function (e) {
        var mySubgroupChange;
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
            url: "/subgroup/put/" + Number(currentSubgroupId),
            success: function (ooo) {
                console.log("eee");
                $(successAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(successAlertHtml).slideUp(500, function () {
                        getSubgroupsFromGame();
                    });
                });
                modifiedSubgroupSongArray.length = 0;
            }, error: function (ooo) {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500, function (){
                        getSubgroupsFromGame();
                    });
                });
            },
        });
    });

    $(document).on('click', '#recounterPositions', function (e) {
        $("#subgroups-table").find("tr").filter(function (index) {
            return $($(this).find("input")[0]).prop('checked') == true;
        }).each(function (index) {
            $($(this).find("input")[1]).val((index + 1) * 10);
        });
        sortTable();
    });

    $(document).on('click', '#updatePositionsInDb', function (e) {
        var arrayOfModifiedSubgroupSongPositionDef = [];
        $("#subgroups-table").find("tr").each(function (index) {
            var isRowChecked = $($(this).find("input")[0]).prop('checked');
            if (isRowChecked) {
                var rowPositionValue = $($(this).find("input")[1]).val();
                var songSubgroupId = $(this).attr("data-songSubgroupId");
                var myPositionChange = new ModifiedSubgroupSongPositionDef(songSubgroupId, rowPositionValue);
                arrayOfModifiedSubgroupSongPositionDef.push(myPositionChange);
            }
        });
        $.ajax({
            async: false,
            type: "PUT",
            data: JSON.stringify(arrayOfModifiedSubgroupSongPositionDef),
            contentType: 'application/json; charset=utf-8',
            url: "/songSubgroup/positions/" + currentSubgroupId,
            success: function (ooo) {
                console.log("eee");
                $(successAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(successAlertHtml).slideUp(500, function () {
                        getSubgroupsFromGame();
                    });
                });
            }, error: function (ooo) {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500, function (){
                        getSubgroupsFromGame();
                    });
                });
            },
        });
    });

    $(document).on('focusout', '.songSubgroupPosition', sortTable);

    function sortTable() {
        var $tbody = $('#subgroups-table tbody');
        $("#subgroups-table").find('tr').sort(function (a, b) {
            var tda = Number($($(a).find("input")[1]).val()); // target order attribute
            var tdb = Number($($(b).find("input")[1]).val()); // target order attribute
            // if a < b return 1
            return tda > tdb ? 1
                // else if a > b return -1
                : tda < tdb ? -1
                    // else they are equal - return 0    
                    : 0;
        }).appendTo($tbody);
    }
});