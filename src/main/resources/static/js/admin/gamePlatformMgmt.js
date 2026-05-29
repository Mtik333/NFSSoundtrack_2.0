$(document).ready(function () {

    $(document).on('click', '.manage-game-platforms', function (e) {
        var gameId = $(this).attr('data-gameId');
        var gameTitle = $(this).attr('data-gameTitle');
        loadGamePlatforms(gameId, gameTitle);
    });

    function loadGamePlatforms(gameId, gameTitle) {
        var allPlatforms, gamePlatforms;
        $.ajax({
            async: false,
            type: 'GET',
            url: '/gameplatform/platforms',
            success: function (data) { allPlatforms = JSON.parse(data); }
        });
        $.ajax({
            async: false,
            type: 'GET',
            url: '/gameplatform/readByGame/' + gameId,
            success: function (data) { gamePlatforms = JSON.parse(data); }
        });

        var divToAppend = $('#nfs-content');
        divToAppend.empty();
        divToAppend.append(successAlertHtml);
        divToAppend.append(failureAlertHtml);
        divToAppend.append('<h2>Platforms: ' + gameTitle + '</h2>');
        divToAppend.append(buildGamePlatformsTable(gamePlatforms));
        divToAppend.append(buildAddPlatformForm(gameId, gameTitle, allPlatforms));
    }

    function buildGamePlatformsTable(gamePlatforms) {
        if (gamePlatforms.length === 0) {
            return $('<p class="text-muted">No platform associations yet.</p>');
        }
        var table = $('<table class="table table-bordered table-hover table-striped mt-2">');
        table.append('<thead><tr><th>Platform</th><th>RetroAchievements URL</th><th class="text-center">Has set</th><th></th><th></th></tr></thead>');
        var tbody = $('<tbody>');
        for (var i = 0; i < gamePlatforms.length; i++) {
            var gp = gamePlatforms[i];
            var tr = $('<tr>').attr('data-gpid', gp.id);
            tr.append($('<td>').text(gp.platformName));
            var urlTd = $('<td>');
            urlTd.append('<input class="form-control gp-url" type="text" value="' + nullToEmpty(gp.retroachievementsUrl) + '">');
            tr.append(urlTd);
            var hasSetTd = $('<td class="text-center align-middle">');
            var checkbox = $('<input type="checkbox" class="form-check-input gp-hasset">');
            if (gp.hasSet) checkbox.prop('checked', true);
            hasSetTd.append(checkbox);
            tr.append(hasSetTd);
            tr.append('<td><button type="button" class="btn btn-primary btn-sm save-gp">Save</button></td>');
            tr.append('<td><button type="button" class="btn btn-danger btn-sm delete-gp">Delete</button></td>');
            tbody.append(tr);
        }
        table.append(tbody);
        return table;
    }

    function buildAddPlatformForm(gameId, gameTitle, allPlatforms) {
        var div = $('<div class="mt-4">');
        div.append('<h4>Add platform association</h4>');
        var row = $('<div class="row g-2 align-items-end">');

        var platformCol = $('<div class="col-auto">');
        platformCol.append('<label class="form-label">Platform</label>');
        var select = $('<select class="form-select" id="newPlatformSelect">');
        for (var i = 0; i < allPlatforms.length; i++) {
            select.append('<option value="' + allPlatforms[i].id + '">' + allPlatforms[i].platform + '</option>');
        }
        platformCol.append(select);

        var urlCol = $('<div class="col">');
        urlCol.append('<label class="form-label">RetroAchievements URL</label>');
        urlCol.append('<input class="form-control" id="newGpUrl" type="text">');

        var hasSetCol = $('<div class="col-auto">');
        hasSetCol.append('<label class="form-label d-block">Has set</label>');
        hasSetCol.append('<input type="checkbox" class="form-check-input" id="newGpHasSet">');

        var btnCol = $('<div class="col-auto">');
        btnCol.append(
            $('<button type="button" class="btn btn-success" id="addGamePlatform">')
                .attr('data-gameId', gameId)
                .attr('data-gameTitle', gameTitle)
                .text('Add')
        );

        row.append(platformCol).append(urlCol).append(hasSetCol).append(btnCol);
        div.append(row);
        return div;
    }

    $(document).on('click', '.save-gp', function (e) {
        var tr = $(this).closest('tr');
        var gpId = tr.attr('data-gpid');
        var formData = {
            retroachievementsUrl: tr.find('.gp-url').val(),
            hasSet: tr.find('.gp-hasset').is(':checked')
        };
        $.ajax({
            async: false,
            type: 'PUT',
            data: JSON.stringify(formData),
            url: '/gameplatform/put/' + gpId,
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function () {
                $(successAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(successAlertHtml).slideUp(500);
                });
            },
            error: function () {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500);
                });
            }
        });
    });

    $(document).on('click', '.delete-gp', function (e) {
        var tr = $(this).closest('tr');
        var gpId = tr.attr('data-gpid');
        $.ajax({
            async: false,
            type: 'DELETE',
            url: '/gameplatform/delete/' + gpId,
            success: function () {
                tr.remove();
            },
            error: function () {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500);
                });
            }
        });
    });

    $(document).on('click', '#addGamePlatform', function (e) {
        var gameId = $(this).attr('data-gameId');
        var gameTitle = $(this).attr('data-gameTitle');
        var formData = {
            gameId: parseInt(gameId),
            platformId: parseInt($('#newPlatformSelect').val()),
            retroachievementsUrl: $('#newGpUrl').val(),
            hasSet: $('#newGpHasSet').is(':checked')
        };
        $.ajax({
            async: false,
            type: 'POST',
            data: JSON.stringify(formData),
            url: '/gameplatform/save',
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function () {
                loadGamePlatforms(gameId, gameTitle);
            },
            error: function () {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500);
                });
            }
        });
    });

    function nullToEmpty(value) {
        return value === null || value === undefined ? '' : value;
    }
});
