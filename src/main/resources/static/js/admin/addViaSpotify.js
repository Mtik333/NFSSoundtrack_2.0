var dataToAdd;
var subgroupId;
var successAlertHtml = $('<div class="alert alert-success" id="success-alert" style="display: none;"><strong>Success!</strong></div>');
var failureAlertHtml = $('<div class="alert alert-danger" id="failure-alert" style="display: none;"><strong>Failure! Check the logs</strong></div>');

function SpotifyBasedSong(songId, authorId, authorName, songTitle, spotifyId, ytId) {
    this.songId = songId;
    this.authorId = authorId;
    this.authorName = authorName;
    this.songTitle = songTitle;
    this.spotifyId = spotifyId;
    this.ytId = ytId;
}

$(document).ready(function () {
    $(document).on('click', "a.add-from-spotify", function (e) {
        gameId = e.target.attributes["data-gameid"].value;
        $(successAlertHtml).hide();
        $(failureAlertHtml).hide();
        $.ajax({
            async: false,
            type: "GET",
            url: "/maingroup/readForEditSubgroups/" + gameId,
            success: function (ooo) {
                fullScopeOfEdit = JSON.parse(ooo);
                var divToAppend = $('#nfs-content');
                divToAppend.empty();
                var spotifyNewSongsIdDiv = $('<div class="form-group spotifyNewSongsIdDiv" id="spotifyNewSongsIdDiv">');
                var firstRowDiv = $('<div class="row">');
                var spotifyIdInputColDiv = $('<div class="col">');
                var triggerDataFetchingColDiv = $('<div class="col">');
                var spotifyIdInput = $('<input class="form-control" id="spotifyLink" type="text">');
                spotifyIdInputColDiv.append('<label for="spotifyLink">Spotify Playlist or Album link</label>');
                spotifyIdInputColDiv.append(spotifyIdInput);
                triggerDataFetchingColDiv.append('<button id="callForSpotifyData" type="submit" class="btn btn-primary">Preview songs</button>');
                firstRowDiv.append(spotifyIdInputColDiv);
                firstRowDiv.append(triggerDataFetchingColDiv);
                spotifyNewSongsIdDiv.append(firstRowDiv);
                divToAppend.append('<button id="saveAddSongsViaSpotify" type="submit" class="btn btn-success">Add/create songs</button>');
                var dropdownDiv = $('<div id="selectSubgroup" class="dropdown">');
                dropdownDiv.append('<button class="btn btn-secondary dropdown-toggle" type="button" id="subgroupsDropdownSpotify" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">All</button>');
                if (fullScopeOfEdit.length > 0) {
                    var dropdownMenuDiv = $('<div class="dropdown-menu" style="max-height: 350px; overflow-y: auto;" aria-labelledby="subgroupsDropdownSpotify">');
                    for (let i = 0; i < fullScopeOfEdit.length; i++) {
                        const group = fullScopeOfEdit[i];
                        var groupName = fullScopeOfEdit[i].groupName;
                        for (let j = 0; j < group.subgroups.length; j++) {
                            var subgroup = group.subgroups[j];
                            dropdownMenuDiv.append('<a class="dropdown-item subgroupItemSpotify" href="#" data-groupId="' + group.id + '" data-subgroupId="' + subgroup.id + '">(' + subgroup.subgroupName + ') from group [' + groupName + ']');
                        }
                    }
                    dropdownDiv.append(dropdownMenuDiv);
                }
                divToAppend.append(dropdownDiv);
                divToAppend.append(spotifyNewSongsIdDiv);
            },
            error: function (ooo) {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500);
                });
            },
        });
    });

    $(document).on('click', '#callForSpotifyData', function (e) {
        var objToSend = {};
        objToSend.spotifyLink = $("#spotifyLink").val();
        $.ajax({
            async: false,
            type: "POST",
            data: JSON.stringify(objToSend),
            contentType: 'application/json; charset=utf-8',
            url: "/gamedb/spotifyData",
            success: function (ooo) {
                console.log("eee");
                dataToAdd = JSON.parse(ooo);
                var divToAppend = $('#nfs-content');
                $("#spotifySongsPreviewTable").remove();
                var tableApp = $('<table id="spotifySongsPreviewTable" class="table-bordered">');
                var trOverThElem = $('<tr>');
                trOverThElem.append('<th scope="col-sm-1">Song ID</th>');
                trOverThElem.append('<th scope="col-sm-1">Author ID</th>');
                trOverThElem.append('<th scope="col-sm-1">Author name</th>');
                trOverThElem.append('<th scope="col-sm-1">Song title</th>');
                trOverThElem.append('<th scope="col-sm-1">Spotify ID</th>');
                trOverThElem.append('<th scope="col-sm-1">YouTube ID</th>');
                tableApp.append("<tbody>");
                tableApp.append(trOverThElem);
                divToAppend.append(tableApp);
                for (let i = 0; i < dataToAdd.length; i++) {
                    let songId = dataToAdd[i].songId;
                    let authorId = dataToAdd[i].authorId;
                    let authorName = dataToAdd[i].authorName;
                    let songTitle = dataToAdd[i].songTitle;
                    let spotifyId = dataToAdd[i].spotifyId;
                    let youtubeId = dataToAdd[i].ytId;
                    var trElem = $('<tr data-songId="' + songId + '">');
                    trElem.append('<td class="col-sm-1"><input style="width: 80%" class="form-control song-id" type="text" value="' + songId + '"></td>');
                    trElem.append('<td class="col-sm-1"><input style="width: 80%" class="form-control author-id" type="text" value="' + authorId + '"></td>');
                    trElem.append('<td class="col-sm-1"><input style="width: 80%" class="form-control author-name" type="text" value="' + authorName + '"></td>');
                    trElem.append('<td class="col-sm-1"><input style="width: 80%" class="form-control song-title" type="text" value="' + songTitle + '"></td>');
                    trElem.append('<td class="col-sm-1"><input style="width: 80%" class="form-control spotify-id" type="text" value="' + spotifyId + '"></td>');
                    trElem.append('<td class="col-sm-1"><input style="width: 80%" class="form-control youtube-id" type="text" value="' + youtubeId + '"></td>');
                    trElem.append('<td class="col-sm-1"><button type="button" data-songId="'+songId+'" class="btn btn-danger spotify-row">Remove</button></td>');
                    tableApp.append(trElem);
                }
                divToAppend.append('</table>');
                $(successAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(successAlertHtml).slideUp(500, function () {
                        //getSubgroupsFromGame();
                    });
                });
                modifiedSubgroupSongArray.length = 0;
            }, error: function (ooo) {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500, function () {
                        //getSubgroupsFromGame();
                    });
                });
            },
        });
    });

    $(document).on('click', ".spotify-row", function (e) {
        console.log(e);
    });

    $(document).on('click', "#saveAddSongsViaSpotify", function (e) {
        let allRows = $("#spotifySongsPreviewTable tr");
        var arrayOfSongsToCreate = [];
        for (let i=1; i<allRows.length; i++){
            let myTr = allRows[i];
            let songIdTd = $(myTr).find("td")[0];
            let authorIdTd = $(myTr).find("td")[1];
            let authorNameTd = $(myTr).find("td")[2];
            let songTitleTd = $(myTr).find("td")[3];
            let spotifyIdTd = $(myTr).find("td")[4];
            let youtubeIdTd = $(myTr).find("td")[5];
            let songIdInput = $(songIdTd).find("input").val();
            let authorIdInput = $(authorIdTd).find("input").val();
            let authorNameInput = $(authorNameTd).find("input").val();
            let songTitleInput = $(songTitleTd).find("input").val();
            let spotifyIdInput = $(spotifyIdTd).find("input").val();
            let srcIdInput = $(youtubeIdTd).find("input").val();
            var myNewSong = new SpotifyBasedSong(Number(songIdInput), Number(authorIdInput),
                authorNameInput, songTitleInput, spotifyIdInput, srcIdInput);
            arrayOfSongsToCreate.push(myNewSong);
        }
        $.ajax({
            async: false,
            type: "POST",
            data: JSON.stringify(arrayOfSongsToCreate),
            contentType: 'application/json; charset=utf-8',
            url: "/gamedb/finalSpotifyData/"+subgroupId,
            success: function (ooo) {
                $(successAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(successAlertHtml).slideUp(500, function () {
                        var divToAppend = $('#nfs-content');
                        divToAppend.empty();
                    });
                });
            }, error: function (ooo) {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500, function () {
                        //getSubgroupsFromGame();
                    });
                });
            },
        });
    });

    $(document).on('focusout', '.youtube-id', function (e) {
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

    $(document).on('click', 'a.subgroupItemSpotify', function (e) {
        subgroupId = Number($(this).attr('data-subgroupId'));
        $("#subgroupsDropdownSpotify").text($(this).text());
    });
});
