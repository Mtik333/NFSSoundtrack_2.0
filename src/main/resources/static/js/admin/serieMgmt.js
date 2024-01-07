function SeriePositionDef(serieId, position) {
    this.serieId = serieId;
    this.position = position;
}

function GamePositionDef(gameId, position) {
    this.gameId = gameId;
    this.position = position;
}

var currentlyEditedSerieId;
$(document).ready(function () {
    $('#success-alert').hide();

    function getAllSeriesAndDisplay() {
        $.ajax({
            async: false,
            type: "GET",
            url: "/serie/readAll",
            success: function (ooo) {
                var fullSeriesScope = JSON.parse(ooo);
                var divToAppend = $('#nfs-content');
                divToAppend.empty();
                divToAppend.append(successAlertHtml);
                var rowDiv = $('<div class="row">');
                var leftCellDiv = $('<div class="col">');
                var rightCellDiv = $('<div class="col">');
                divToAppend.append(rowDiv);
                rowDiv.append(leftCellDiv);
                rowDiv.append(rightCellDiv);
                rightCellDiv.append('<button id="updateSeriePositionsInDb" type="submit" class="btn btn-primary">Update positions in DB</button>');
                rightCellDiv.append('<button id="recounterSeriePositions" type="submit" class="btn btn-primary">Recounter positions</button>');
                leftCellDiv.append('<button id="newSerie" type="submit" class="btn btn-success">New series</button>');
                var tableToFill;
                if (fullSeriesScope.length > 0) {
                    tableToFill = displayAllSerie(fullSeriesScope);
                }
                divToAppend.append(tableToFill);
            },
            error: function (ooo) {
                console.log("e2");
            },
            done: function (ooo) {
                console.log("e3");
            }
        });

    }

    function displayAllSerie(series) {
        var tableToFill = $('<table id="series-table" class="table table-bordered table-hover table-striped">');
        tableToFill.append("<tbody>");
        for (let i = 0; i < series.length; i++) {
            var tr = $('<tr class="serie" data-serieId="' + series[i].id + '">');
            var positionTd = $('<td class="col-md-1">');
            positionTd.append('<input class="form-control seriePosition" type="text" value="' + series[i].position + '">');
            var songDisplay = series[i].name;
            var textTd = $('<td>');
            textTd.append(songDisplay);
            tr.append(positionTd);
            tr.append(textTd);
            tr.append('<td class="text-right"><button type="button" id="edit-serie-' + series[i].id + '" class="btn btn-warning edit-serie">Edit</button></td>');
            tr.append('<td class="text-right"><button type="button" id="delete-serie-' + series[i].id + '" class="btn btn-danger delete-serie">Delete</button></td>');
            tableToFill.append(tr);
        }
        return tableToFill;
    }

    $("#reposition-series").click(function (e) {
        getAllSeriesAndDisplay();
    });

    $(document).on('click', '.edit-serie', function (e) {
        var serieId = $(this).attr("id").replace("edit-serie-", "");
        var serieName = $(this).parent().prev().text();
        $.ajax({
            async: false,
            type: "GET",
            url: "/serie/read/" + serieId,
            success: function (ooo) {
                var fullGamesScope = JSON.parse(ooo);
                currentlyEditedSerieId = serieId;
                var divToAppend = $('#nfs-content');
                divToAppend.empty();
                divToAppend.append(successAlertHtml);
                var serieNameDiv = $('<div class="form-group serieDiv" id="serieDiv">');
                var serieInputName = $('<input class="form-control" id="serieName" value="' + serieName + '"/>');
                serieNameDiv.append('<label for="serieName">Serie name</label>');
                serieNameDiv.append(serieInputName);
                var rowDiv = $('<div class="row">');
                var leftCellDiv = $('<div class="col">');
                var rightCellDiv = $('<div class="col">');
                divToAppend.append(serieNameDiv);
                divToAppend.append(rowDiv);
                rowDiv.append(leftCellDiv);
                rowDiv.append(rightCellDiv);
                rightCellDiv.append('<button id="updateGamePositionsInDb" type="submit" class="btn btn-primary">Update positions in DB</button>');
                rightCellDiv.append('<button id="recounterGamePositions" type="submit" class="btn btn-primary">Recounter positions</button>');
                leftCellDiv.append('<button id="newGame" type="submit" class="btn btn-success">New game</button>');
                leftCellDiv.append('<button id="cancelNewGame" type="submit" class="btn btn-primary">Cancel</button>');
                var tableToFill;
                if (fullGamesScope.length > 0) {
                    tableToFill = displayAllGames(fullGamesScope);
                }
                divToAppend.append(tableToFill);
            },
            error: function (ooo) {
                console.log("e2");
            },
            done: function (ooo) {
                console.log("e3");
            }
        });

    });

    function displayAllGames(games) {
        var tableToFill = $('<table id="games-table" class="table table-bordered table-hover table-striped">');
        tableToFill.append("<tbody>");
        for (let i = 0; i < games.length; i++) {
            var tr = $('<tr class="game" data-gameId="' + games[i].id + '">');
            var positionTd = $('<td class="col-md-1">');
            positionTd.append('<input class="form-control gamePosition" type="text" value="' + games[i].position + '">');
            var songDisplay = games[i].displayTitle;
            var textTd = $('<td>');
            textTd.append(songDisplay);
            tr.append(positionTd);
            tr.append(textTd);
            tr.append('<td class="text-right"><button type="button" id="delete-game-' + games[i].id + '" class="btn btn-danger delete-game">Delete</button></td>');
            tableToFill.append(tr);
        }
        return tableToFill;
    }

    $(document).on('click', '#recounterSeriePositions', function (e) {
        $("#series-table").find("tr").each(function (index) {
            $($(this).find("input")[0]).val((index + 1) * 10);
        });
        sortTableSeries();
    });

    $(document).on('click', '#newSerie', function (e) {
        var divToAppend = $('#nfs-content');
        divToAppend.empty();
        divToAppend.append(successAlertHtml);
        var serieNameDiv = $('<div class="form-group newSerieDiv" id="newSerieDiv">');
        var serieInputName = $('<input class="form-control" id="newSerieName"/>');
        serieNameDiv.append('<label for="newSerieName">New serie name</label>');
        serieNameDiv.append(serieInputName);
        divToAppend.append('<button id="saveNewSerie" type="submit" class="btn btn-success">Save</button>');
        divToAppend.append(serieNameDiv);
    });

    $(document).on('click', '#cancelNewGame', function (e) {
        getAllSeriesAndDisplay();
    });

    $(document).on('click', '#saveNewSerie', function (e) {
        var formData = $("#newSerieName").val();
        $.ajax({
            async: false,
            type: "POST",
            data: JSON.stringify(formData),
            url: "/serie/save",
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function (ooo) {
                console.log("eee");
                $('#success-alert').fadeTo(2000, 500).slideUp(500, function () {
                    $('#success-alert').slideUp(500);
                    getAllSeriesAndDisplay();
                });
            },
            error: function (ooo) {
                console.log("e2");

            },
            done: function (ooo) {
                console.log("e3");

            }
        });
    });

    $(document).on('click', '#recounterGamePositions', function (e) {
        $("#games-table").find("tr").each(function (index) {
            $($(this).find("input")[0]).val((index + 1) * 10);
        });
        sortTableSeries();
    });


    $(document).on('click', '#updateSeriePositionsInDb', function (e) {
        var arrayOfSeries = new Array();
        $("#series-table").find("tr").each(function (index) {
            var rowPositionValue = $($(this).find("input")[0]).val();
            var serieId = $(this).attr("data-serieid");
            var myPositionChange = new SeriePositionDef(serieId, rowPositionValue);
            arrayOfSeries.push(myPositionChange);
        });
        $.ajax({
            async: false,
            type: "PUT",
            data: JSON.stringify(arrayOfSeries),
            contentType: 'application/json; charset=utf-8',
            url: "/serie/updatePositions",
            success: function (ooo) {
                console.log("eee");
                $('#success-alert').fadeTo(2000, 500).slideUp(500, function () {
                    $('#success-alert').slideUp(500, function () {
                        getAllSeriesAndDisplay();
                    });
                });
            }, error: function (ooo) {
                console.log("e2");
            },
            done: function (ooo) {
                console.log("e3");
            }
        });
    });


    $(document).on('click', '#updateGamePositionsInDb', function (e) {
        var objToSend = new Object();
        objToSend.serieId = currentlyEditedSerieId;
        objToSend.serieName = $("#serieName").val();
        var arrayOfGames = new Array();
        $("#games-table").find("tr").each(function (index) {
            var rowPositionValue = $($(this).find("input")[0]).val();
            var gameId = $(this).attr("data-gameid");
            var myPositionChange = new GamePositionDef(gameId, rowPositionValue);
            arrayOfGames.push(myPositionChange);
        });
        objToSend.arrayOfGames = arrayOfGames;
        $.ajax({
            async: false,
            type: "PUT",
            data: JSON.stringify(objToSend),
            contentType: 'application/json; charset=utf-8',
            url: "/serie/updateGames",
            success: function (ooo) {
                console.log("eee");
                $('#success-alert').fadeTo(2000, 500).slideUp(500, function () {
                    $('#success-alert').slideUp(500, function () {
                        getAllSeriesAndDisplay();
                    });
                });
            }, error: function (ooo) {
                console.log("e2");
            },
            done: function (ooo) {
                console.log("e3");
            }
        });

    });

    $(document).on('click', '#newGame', function (e) {
        var divToAppend = $('#nfs-content');
        divToAppend.empty();
        divToAppend.append(successAlertHtml);
        var newGameDiv = $('<div class="form-group newGameDiv" id="newGameDiv">');
        var firstRowDiv = $('<div class="row">');
        var gameTitleColDiv = $('<div class="col">');
        var gameDisplayColDiv = $('<div class="col">');
        var gameShortColDiv = $('<div class="col">');
        var prefixColDiv = $('<div class="col">');
        var gameStatusColDiv = $('<div class="col">');
        var gameTitleInput = $('<input class="form-control" id="gameTitle" type="text">');
        var gameDisplayInput = $('<input class="form-control" id="gameDisplay" type="text">');
        var gameShortInput = $('<input class="form-control" id="gameShort" type="text">');
        var gamePrefixInput = $('<input class="form-control" id="gamePrefix" type="text">');
        var gameStatusInput = $('<select class="form-select" id="gameStatus"><option value="RELEASED" selected>Released</option><option value="UNRELEASED">Unreleased</option><option value="UNPLAYABLE">Unplayable</option><option value="CANCELED">Canceled</option>');
        gameTitleColDiv.append('<label for="gameTitle">Game title</label>');
        gameTitleColDiv.append(gameTitleInput);
        gameDisplayColDiv.append('<label for="gameDisplay">Game display title</label>');
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
        var spotifyInput = $('<input class="form-control" id="spotifyPlaylistInput" type="text">');
        var deezerInput = $('<input class="form-control" id="deezerPlaylistInput" type="text">');
        var tidalInput = $('<input class="form-control" id="tidalPlaylistInput" type="text">');
        var youtubeInput = $('<input class="form-control" id="youtubePlaylistInput" type="text">');
        var soundcloudInput = $('<input class="form-control" id="soundcloudPlaylistInput" type="text">');
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
        divToAppend.append('<button id="saveNewGame" type="submit" class="btn btn-success">Save</button>');
        divToAppend.append('<button id="cancelNewGame" type="submit" class="btn btn-primary">Cancel</button>');
        divToAppend.append(newGameDiv);
    });

    $(document).on('click', '#cancelNewGame', function (e) {
        getAllSeriesAndDisplay();
    });

    $(document).on('click', '#saveNewGame', function (e) {
        var formData = new Object();
        formData.serieId = currentlyEditedSerieId;
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
        $.ajax({
            async: false,
            type: "POST",
            data: JSON.stringify(formData),
            url: "/gamedb/save",
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function (ooo) {
                console.log("eee");
                $('#success-alert').fadeTo(2000, 500).slideUp(500, function () {
                    $('#success-alert').slideUp(500);
                    getAllSeriesAndDisplay();
                });
            },
            error: function (ooo) {
                console.log("e2");

            },
            done: function (ooo) {
                console.log("e3");

            }
        });
    });

    $(document).on('focusout', '.seriePosition', sortTableSeries);
    $(document).on('focusout', '.gamePosition', sortTableGames);

    function sortTableSeries() {
        var $tbody = $('#series-table tbody');
        $("#series-table").find('tr').sort(function (a, b) {
            var tda = parseInt($($(a).find("input")[0]).val()); // target order attribute
            var tdb = parseInt($($(b).find("input")[0]).val()); // target order attribute
            // if a < b return 1
            return tda > tdb ? 1
                // else if a > b return -1
                : tda < tdb ? -1
                    // else they are equal - return 0    
                    : 0;
        }).appendTo($tbody);
    }

    function sortTableGames() {
        var $tbody = $('#games-table tbody');
        $("#games-table").find('tr').sort(function (a, b) {
            var tda = parseInt($($(a).find("input")[0]).val()); // target order attribute
            var tdb = parseInt($($(b).find("input")[0]).val()); // target order attribute
            // if a < b return 1
            return tda > tdb ? 1
                // else if a > b return -1
                : tda < tdb ? -1
                    // else they are equal - return 0    
                    : 0;
        }).appendTo($tbody);
    }
});