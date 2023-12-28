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
            var tr = $('<tr class="song" data-songId="' + songs[i].song.id + '">');
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
            tr.append('<td class="text-right"><button type="button" id="edit-song-'+songs[i].song.id+'" class="btn btn-warning edit-song">Edit</button></td>');
            tr.append('<td class="text-right"><button type="button" id="delete-song-'+songs[i].song.id+'" class="btn btn-danger delete-song">Delete</button></td>');
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
        divToAppend.empty();
        

    });


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
        $("#songs-table").find('tr').each(function(e){
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
});