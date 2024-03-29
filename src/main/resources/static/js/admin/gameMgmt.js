var gameToEdit;

$(document).ready(function () {
    $(document).on('click', "a.edit-game", function (e) {
        var gameId = $(this).attr("data-gameid");
        $(successAlertHtml).hide();
        $(failureAlertHtml).hide();
        $.ajax({
            async: false,
            type: "GET",
            url: "/gamedb/read/" + Number(gameId),
            success: function (ooo) {
                gameToEdit = JSON.parse(ooo);
                var divToAppend = $('#nfs-content');
                divToAppend.empty();
                divToAppend.append(successAlertHtml);
                divToAppend.append(failureAlertHtml);
                var newGameDiv = $('<div class="form-group newGameDiv" id="newGameDiv">');
                var firstRowDiv = $('<div class="row">');
                var gameTitleColDiv = $('<div class="col">');
                var gameDisplayColDiv = $('<div class="col">');
                var gameShortColDiv = $('<div class="col">');
                var prefixColDiv = $('<div class="col">');
                var gameStatusColDiv = $('<div class="col">');
                var gameTitleInput = $('<input class="form-control" id="gameTitle" type="text" value="' + gameToEdit.gametitle + '">');
                var gameDisplayInput = $('<input class="form-control" id="gameDisplay" type="text" value="' + gameToEdit.displayTitle + '">');
                var gameShortInput = $('<input class="form-control" id="gameShort" type="text" value="' + gameToEdit.gameshort + '">');
                var gamePrefixInput = $('<input class="form-control" id="gamePrefix" type="text" value="' + gameToEdit.prefix + '">');
                var gameStatusInput = $('<select class="form-select" id="gameStatus"><option value="RELEASED" selected>Released</option><option value="UNRELEASED">Unreleased</option><option value="UNPLAYABLE">Unplayable</option><option value="CANCELED">Canceled</option>');
                gameStatusInput.val(gameToEdit.status);
                gameTitleColDiv.append('<label for="gameTitle">Game title</label>');
                gameTitleColDiv.append(gameTitleInput);
                gameDisplayColDiv.append('<label for="gameShort">Game display title</label>');
                gameDisplayColDiv.append(gameDisplayInput);
                gameShortColDiv.append('<label for="gameShort">Game short (URL path)</label>');
                gameShortColDiv.append(gameShortInput);
                prefixColDiv.append('<label for="gamePrefix">Game prefix</label>');
                prefixColDiv.append(gamePrefixInput);
                gameStatusColDiv.append('<label for="gameStatus">Game status</label>');
                gameStatusColDiv.append(gameStatusInput);
                firstRowDiv.append(gameTitleColDiv);
                firstRowDiv.append(gameDisplayColDiv);
                firstRowDiv.append(gameShortColDiv);
                firstRowDiv.append(prefixColDiv);
                firstRowDiv.append(gameStatusColDiv);
                newGameDiv.append('<h2>New game</h2>');
                newGameDiv.append(firstRowDiv);
                var secondRowDiv = $('<div class="row">');
                var spotifyColDiv = $('<div class="col">');
                var deezerColDiv = $('<div class="col">');
                var tidalColDiv = $('<div class="col">');
                var youtubeColDiv = $('<div class="col">');
                var soundcloudColDiv = $('<div class="col">');
                var spotifyInput = $('<input class="form-control" id="spotifyPlaylistInput" type="text" value="' + gameToEdit.spotify_id + '">');
                var deezerInput = $('<input class="form-control" id="deezerPlaylistInput" type="text" value="' + gameToEdit.deezer_id + '">');
                var tidalInput = $('<input class="form-control" id="tidalPlaylistInput" type="text" value="' + gameToEdit.tidal_id + '">');
                var youtubeInput = $('<input class="form-control" id="youtubePlaylistInput" type="text" value="' + gameToEdit.youtube_id + '">');
                var soundcloudInput = $('<input class="form-control" id="soundcloudPlaylistInput" type="text" value="' + gameToEdit.soundcloud_id + '">');
                spotifyColDiv.append('<label for="spotifyPlaylistInput">Spotify playlist ID</label>');
                spotifyColDiv.append(spotifyInput);
                deezerColDiv.append('<label for="deezerPlaylistInput">Deezer playlist ID</label>');
                deezerColDiv.append(deezerInput);
                tidalColDiv.append('<label for="tidalPlaylistInput">Tidal playlist ID</label>');
                tidalColDiv.append(tidalInput);
                youtubeColDiv.append('<label for="youtubePlaylistInput">YouTube playlist ID</label>');
                youtubeColDiv.append(youtubeInput);
                soundcloudColDiv.append('<label for="soundcloudPlaylistInput">SoundCloud playlist ID</label>');
                soundcloudColDiv.append(soundcloudInput);
                secondRowDiv.append(spotifyColDiv);
                secondRowDiv.append(deezerColDiv);
                secondRowDiv.append(tidalColDiv);
                secondRowDiv.append(youtubeColDiv);
                secondRowDiv.append(soundcloudColDiv);
                newGameDiv.append(secondRowDiv);
                var additionalInfoRowDiv = $('<div class="row">');
                var additionalInfoColDiv = $('<div class="col">');
                var additionalInfoInput = $('<textarea class="form-control" id="additionalInfoInput">');
                additionalInfoColDiv.append('<label for="additionalInfoInput">Additional game info</label>');
                additionalInfoInput.text(gameToEdit.additionalInfo);
                additionalInfoColDiv.append(additionalInfoInput);
                additionalInfoRowDiv.append(additionalInfoColDiv);
                newGameDiv.append(additionalInfoRowDiv);
                divToAppend.append('<button id="saveEditGame" type="submit" class="btn btn-primary">Save changes</button>');
                divToAppend.append(newGameDiv);

            }
        });
    });

    $(document).on('click', '#saveEditGame', function (e) {
        var formData = {};
        formData.id = gameToEdit.id;
        formData.gameTitle = $("#gameTitle").val();
        formData.displayTitle = $("#gameDisplay").val();
        formData.gameShort = $("#gameShort").val();
        formData.gamePrefix = $("#gamePrefix").val();
        formData.gameStatus = $("#gameStatus").val();
        formData.spotifyId = $("#spotifyPlaylistInput").val();
        formData.deezerId = $("#deezerPlaylistInput").val();
        formData.tidalId = $("#tidalPlaylistInput").val();
        formData.youtubeId = $("#youtubePlaylistInput").val();
        formData.soundcloudId = $("#soundcloudPlaylistInput").val();
        formData.additionalInfo = $("#additionalInfoInput").val();
        $.ajax({
            async: false,
            type: "PUT",
            data: JSON.stringify(formData),
            url: "/gamedb/put/" + Number(gameToEdit.id),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function (ooo) {
                $(successAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(successAlertHtml).slideUp(500);
                    var divToAppend = $('#nfs-content');
                    divToAppend.empty();
                });
            },
            error: function (ooo) {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500);
                    var divToAppend = $('#nfs-content');
                    divToAppend.empty();
                });

            },
        });
    });
});
