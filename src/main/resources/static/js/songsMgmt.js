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
                var rowDiv = $('<div class="row">');
                var leftCellDiv = $('<div class="col">');
                var rightCellDiv = $('<div class="col">');
                divToAppend.append(rowDiv);
                rowDiv.append(leftCellDiv);
                rowDiv.append(rightCellDiv);
                rightCellDiv.append('<button id="updateSubgroupSongs" type="submit" class="btn btn-primary">Update positions</button>');
                var dropdownDiv = $('<div id="selectSubgroup" class="dropdown">');
                dropdownDiv.append('<button class="btn btn-secondary dropdown-toggle" type="button" id="subgroupsDropdown" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">All</button>');
                leftCellDiv.append(dropdownDiv);
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
                divToAppend.append(tableToFill);
                $(divToAppend).find("a").first().click();
            }

            
        });
    }

    function displayAllSongsWithPositions(songs, dropdownDiv) {
        var tableToFill = $('<table id="subgroups-table" class="table table-bordered table-hover">');
        tableToFill.append("<tbody>");
        for (let i = 0; i < songs.length; i++) {
            var tr = $('<tr class="subgroupSong" data-songId="' + songs[i].song.id + '">');
            var idTd = $('<td class="col-md-1">');
            idTd.append('<input class="form-control songSubgroupPosition" type="text" value="'+songs[i].id+'">');
            var positionTd = $('<td class="col-md-1">');
            positionTd.append('<input class="form-control songSubgroupPosition" type="text" value="'+songs[i].position+'">');
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
            tr.append(idTd);
            tr.append(positionTd);
            tr.append(textTd);
            tableToFill.append(tr);
        }
        return tableToFill;
    }
});