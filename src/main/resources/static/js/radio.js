var radioYtPlayer = null;
var currentSong = null;
var songQueue = [];
var recentHistory = [];
var preferredSeries = [];
var preferredGames = [];
var preferredGenres = [];
var preferredRatio = 70;
var searchDebounce = null;
var ytApiReady = false;
var pendingSong = null;

// Restore preferences from localStorage
(function () {
    try {
        var stored = localStorage.getItem('radio-prefs');
        if (stored) {
            var p = JSON.parse(stored);
            preferredSeries = p.series || [];
            preferredGames = p.games || [];
            preferredGenres = p.genres || [];
            preferredRatio = typeof p.ratio === 'number' ? p.ratio : 70;
        }
    } catch (e) {}
    $('#radio-ratio').val(preferredRatio);
    $('#radio-ratio-display').text(preferredRatio);
}());

function savePrefs() {
    localStorage.setItem('radio-prefs', JSON.stringify({
        series: preferredSeries,
        games: preferredGames,
        genres: preferredGenres,
        ratio: preferredRatio
    }));
}

function updatePrefsSummary() {
    var rows = [];

    if (cachedSeriesData) {
        if (preferredSeries.length > 0) {
            var seriesNames = cachedSeriesData
                .filter(function (s) { return preferredSeries.indexOf(Number(s.id)) >= 0; })
                .map(function (s) { return s.name; });
            var seriesLabel = seriesNames.length <= 3 ? seriesNames.join(', ') : seriesNames.length + ' series';
            rows.push('<div><strong>Series:</strong> ' + escapeHtml(seriesLabel) + '</div>');
        }

        if (preferredGames.length > 0) {
            var gameNames = [];
            cachedSeriesData.forEach(function (s) {
                if (s.games) s.games.forEach(function (g) {
                    if (preferredGames.indexOf(Number(g.id)) >= 0) gameNames.push(g.title);
                });
            });
            var gamesLabel = gameNames.length <= 3 ? gameNames.join(', ') : gameNames.length + ' games';
            rows.push('<div><strong>Games:</strong> ' + escapeHtml(gamesLabel) + '</div>');
        }
    }

    if (cachedGenres && preferredGenres.length > 0) {
        var genreNames = cachedGenres
            .filter(function (g) { return preferredGenres.indexOf(Number(g.id)) >= 0; })
            .map(function (g) { return g.name; });
        var genresLabel = genreNames.length <= 5 ? genreNames.join(', ') : genreNames.length + ' genres';
        rows.push('<div><strong>Genres:</strong> ' + escapeHtml(genresLabel) + '</div>');
    }

    if (rows.length === 0) {
        rows.push('<div>All games</div>');
    }

    rows.push('<div><strong>Ratio:</strong> ' + preferredRatio + '% preferred</div>');
    $('#radio-prefs-summary').html(rows.join(''));
    fetchAndShowCount();
}

function fetchAndShowCount() {
    clearTimeout(countDebounce);
    $('#radio-pref-count').text('counting…');
    countDebounce = setTimeout(function () {
        var parts = [];
        if (preferredGames.length > 0) parts.push('preferredGames=' + preferredGames.join(','));
        if (preferredSeries.length > 0) parts.push('preferredSeries=' + preferredSeries.join(','));
        if (preferredGenres.length > 0) parts.push('preferredGenres=' + preferredGenres.join(','));
        $.getJSON('/radio/count' + (parts.length ? '?' + parts.join('&') : ''), function (data) {
            $('#radio-pref-count').text('~' + data.count.toLocaleString() + ' matching songs');
        });
    }, 400);
}

function resetPreferences() {
    preferredSeries = [];
    preferredGames = [];
    preferredGenres = [];
    preferredRatio = 70;
    $('#radio-ratio').val(70);
    $('#radio-ratio-display').text(70);
    $('.radio-serie-cb, .radio-game-cb, .radio-genre-cb').prop('checked', false);
    $('#radio-series-list .collapse').collapse('hide');
    savePrefs();
    updatePrefsSummary();
}

$('#radio-reset-prefs').on('click', resetPreferences);

function buildNextParams() {
    var parts = ['preferredRatio=' + preferredRatio];
    if (recentHistory.length > 0) parts.push('exclude=' + recentHistory.join(','));
    if (preferredGames.length > 0) parts.push('preferredGames=' + preferredGames.join(','));
    if (preferredSeries.length > 0) parts.push('preferredSeries=' + preferredSeries.join(','));
    if (preferredGenres.length > 0) parts.push('preferredGenres=' + preferredGenres.join(','));
    return parts.join('&');
}

function renderQueue() {
    var $box = $('#radio-queued-box');
    if (songQueue.length === 0) {
        $box.hide().empty();
        return;
    }
    var html = '<div class="d-flex justify-content-between align-items-center mb-1">' +
        '<strong class="small">Up next (' + songQueue.length + ')</strong>' +
        '<button type="button" class="btn-close btn-sm" id="radio-clear-queue" aria-label="Clear all"></button>' +
        '</div>';
    songQueue.forEach(function (song, i) {
        var gl = groupLabel(song);
        var meta = escapeHtml(song.game) + (gl ? ' · ' + escapeHtml(gl) : '');
        html += '<div class="d-flex align-items-center gap-1">' +
            '<small class="text-truncate flex-grow-1">' + (i + 1) + '. ' + escapeHtml(song.artist) + ' — ' + escapeHtml(song.title) +
            ' <span class="text-muted">(' + meta + ')</span></small>' +
            '<button type="button" class="btn-close btn-sm flex-shrink-0 radio-queue-remove" data-idx="' + i + '" aria-label="Remove"></button>' +
            '</div>';
    });
    $box.html(html).show();
}

function addToQueue(song) {
    songQueue.push(song);
    renderQueue();
}

function fetchNextSong(callback) {
    if (songQueue.length > 0) {
        var s = songQueue.shift();
        renderQueue();
        callback(s);
        return;
    }
    $.getJSON('/radio/next?' + buildNextParams(), function (data) {
        if (data) callback(data);
    });
}

function playSong(songData) {
    if (!ytApiReady) {
        pendingSong = songData;
        return;
    }
    currentSong = songData;

    recentHistory.unshift(songData.id);
    if (recentHistory.length > 500) recentHistory.pop();

    var artistHtml = songData.authorId
        ? '<a class="table_link" href="/author/' + songData.authorId + '">' + escapeHtml(songData.artist) + '</a>'
        : escapeHtml(songData.artist);
    var gameHtml = '<a class="table_link" href="/game/' + escapeHtml(songData.gameShort) + '">' + escapeHtml(songData.game) + '</a>';
    $('#radio-now-playing').html(artistHtml + ' — ' + escapeHtml(songData.title) + ' &nbsp;·&nbsp; ' + gameHtml);

    $('#radio-player').html(
        '<iframe id="radio-yt-iframe" allow="autoplay" type="text/html"' +
        ' src="https://www.youtube.com/embed/' + songData.srcId +
        '?enablejsapi=1&autoplay=1&autohide=0&theme=light&wmode=transparent"' +
        ' frameborder="0" style="width:100%;height:100%;"></iframe>'
    );

    radioYtPlayer = new YT.Player('radio-yt-iframe', {
        events: {
            onReady: function (e) { e.target.playVideo(); },
            onStateChange: function (e) {
                if (e.data === 0) setTimeout(function () { fetchNextSong(playSong); }, 2500);
            },
            onError: function () {
                setTimeout(function () { fetchNextSong(playSong); }, 2500);
            }
        }
    });

    prependHistoryRow(songData);
    reportOverlay(songData);
    $('#radio-skip').prop('disabled', false);
}

function escapeHtml(str) {
    if (!str) return '';
    return $('<span>').text(str).html();
}

function groupLabel(song) {
    var parts = [song.mainGroup, song.subgroupName].filter(function (s) { return s && s !== 'All'; });
    return parts.join(' / ');
}

function prependHistoryRow(songData) {
    var gameLink = '<a class="table_link" href="/game/' + escapeHtml(songData.gameShort) + '">' + escapeHtml(songData.game) + '</a>';
    var badge = songData.subgroupType
        ? ' <span class="badge bg-warning text-dark">' + escapeHtml(songData.subgroupType) + '</span>'
        : '';
    var $row = $(
        '<tr class="radio-history-row">' +
        '<td class="band">' + escapeHtml(songData.artist) + '</td>' +
        '<td class="songtitle">' + escapeHtml(songData.title) + badge + '</td>' +
        '<td>' + gameLink + '</td>' +
        '<td><button class="btn btn-sm btn-outline-secondary radio-requeue" type="button" title="Play next">▶</button></td>' +
        '</tr>'
    );
    $row.data('song', songData);
    $('#radio-history-body').prepend($row);
}

function reportOverlay(songData) {
    var token = localStorage.getItem('streaming-token');
    if (!token || !songData) return;
    $.ajax({
        url: '/streaming/now-playing/' + token,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            artist: songData.artist || '',
            title: songData.title || '',
            game: songData.game || '',
            subgroupType: songData.subgroupType || ''
        })
    });
}

// Skip
$('#radio-skip').on('click', function () {
    $(this).prop('disabled', true);
    fetchNextSong(playSong);
});

// Clear entire queue
$(document).on('click', '#radio-clear-queue', function () {
    songQueue = [];
    renderQueue();
});

// Remove single item from queue
$(document).on('click', '.radio-queue-remove', function () {
    var idx = parseInt($(this).attr('data-idx'), 10);
    songQueue.splice(idx, 1);
    renderQueue();
});

// Add from history to queue
$(document).on('click', '.radio-requeue', function () {
    addToQueue($(this).closest('tr').data('song'));
});

// Ratio slider
$('#radio-ratio').on('input', function () {
    preferredRatio = parseInt($(this).val(), 10);
    $('#radio-ratio-display').text(preferredRatio);
    savePrefs();
    updatePrefsSummary();
});

// Search-as-you-type
function doSearch() {
    var band = $('#radio-queue-band').val().trim();
    var title = $('#radio-queue-title').val().trim();
    if (!band && !title) { $('#radio-search-results').hide().empty(); return; }

    $.getJSON('/radio/search?band=' + encodeURIComponent(band) + '&title=' + encodeURIComponent(title), function (results) {
        var $list = $('#radio-search-results').empty();
        if (!results || results.length === 0) {
            $list.append('<div class="list-group-item disabled small">No results found</div>');
        } else {
            results.forEach(function (item) {
                var gl = groupLabel(item);
                var label = item.artist + ' — ' + item.title + ' (' + item.game + (gl ? ' · ' + gl : '') + ')';
                $('<button type="button" class="list-group-item list-group-item-action small py-1"></button>')
                    .text(label)
                    .on('click', function () {
                        addToQueue(item);
                        $('#radio-search-results').hide().empty();
                        $('#radio-queue-band').val('');
                        $('#radio-queue-title').val('');
                    })
                    .appendTo($list);
            });
        }
        $list.show();
    });
}

$('#radio-queue-band, #radio-queue-title').on('input', function () {
    clearTimeout(searchDebounce);
    searchDebounce = setTimeout(doSearch, 350);
});

$(document).on('click', function (e) {
    if (!$(e.target).closest('#radio-queue-band, #radio-queue-title, #radio-search-results').length) {
        $('#radio-search-results').hide();
    }
});

var cachedSeriesData = null;
var cachedGenres = null;
var countDebounce = null;

function buildSeriesTree(seriesList) {
    var $container = $('#radio-series-list').empty();
    seriesList.forEach(function (serie) {
        var serieChecked = preferredSeries.indexOf(Number(serie.id)) >= 0;
        var $wrapper = $('<div class="mb-1"></div>');
        var $serieRow = $('<div class="form-check"></div>');
        var $serieCb = $('<input type="checkbox" class="form-check-input radio-serie-cb">')
            .attr({ id: 'rserie-' + serie.id, value: serie.id })
            .prop('checked', serieChecked);
        var $serieLabel = $('<label class="form-check-label fw-semibold pointable">')
            .attr('for', 'rserie-' + serie.id).text(serie.name);
        $serieRow.append($serieCb).append($serieLabel);
        $wrapper.append($serieRow);

        if (serie.games && serie.games.length > 0) {
            var anyGameSelected = serie.games.some(function (g) { return preferredGames.indexOf(Number(g.id)) >= 0; });
            var $games = $('<div class="ms-3 collapse' + (anyGameSelected ? ' show' : '') + '" id="rgames-' + serie.id + '"></div>');
            serie.games.forEach(function (game) {
                var gameChecked = preferredGames.indexOf(Number(game.id)) >= 0;
                var $gameRow = $('<div class="form-check"></div>');
                var $gameCb = $('<input type="checkbox" class="form-check-input radio-game-cb">')
                    .attr({ id: 'rgame-' + game.id, value: game.id, 'data-serie': serie.id })
                    .prop('checked', gameChecked);
                var $gameLabel = $('<label class="form-check-label pointable">')
                    .attr('for', 'rgame-' + game.id).text(game.title);
                $gameRow.append($gameCb).append($gameLabel);
                $games.append($gameRow);
            });
            $wrapper.append($games);
        }
        $container.append($wrapper);
    });
}

function buildAllGamesList(seriesList) {
    var allGames = [];
    seriesList.forEach(function (serie) {
        if (serie.games) {
            serie.games.forEach(function (game) {
                allGames.push(game);
            });
        }
    });
    allGames.sort(function (a, b) { return a.title.localeCompare(b.title); });

    var $container = $('#radio-all-games-list').empty();
    allGames.forEach(function (game) {
        var gameChecked = preferredGames.indexOf(Number(game.id)) >= 0;
        var $row = $('<div class="form-check"></div>');
        var $cb = $('<input type="checkbox" class="form-check-input radio-game-cb">')
            .attr({ id: 'rgame-flat-' + game.id, value: game.id })
            .prop('checked', gameChecked);
        var $label = $('<label class="form-check-label pointable">')
            .attr('for', 'rgame-flat-' + game.id).text(game.title);
        $row.append($cb).append($label);
        $container.append($row);
    });
}

function loadSeriesPreferences() {
    $.getJSON('/radio/series-with-games', function (seriesList) {
        cachedSeriesData = seriesList;
        buildSeriesTree(seriesList);
        buildAllGamesList(seriesList);
        updatePrefsSummary();
    });
}

function buildGenreList(genres) {
    var $container = $('#radio-genre-list').empty();
    genres.forEach(function (genre) {
        var checked = preferredGenres.indexOf(Number(genre.id)) >= 0;
        var $row = $('<div class="form-check"></div>');
        var $cb = $('<input type="checkbox" class="form-check-input radio-genre-cb">')
            .attr({ id: 'rgenre-' + genre.id, value: genre.id })
            .prop('checked', checked);
        var $label = $('<label class="form-check-label pointable">')
            .attr('for', 'rgenre-' + genre.id).text(genre.name);
        $row.append($cb).append($label);
        $container.append($row);
    });
}

function loadGenrePreferences() {
    $.getJSON('/radio/genres', function (genres) {
        cachedGenres = genres;
        buildGenreList(genres);
    });
}

$(document).on('change', '.radio-genre-cb', function () {
    var genreId = Number($(this).val());
    if ($(this).is(':checked')) {
        if (preferredGenres.indexOf(genreId) < 0) preferredGenres.push(genreId);
    } else {
        preferredGenres = preferredGenres.filter(function (id) { return id !== genreId; });
    }
    savePrefs();
    updatePrefsSummary();
});

$('#radio-games-filter').on('input', function () {
    var val = $(this).val().toLowerCase();
    $('#radio-all-games-list .form-check').each(function () {
        $(this).toggle($(this).find('label').text().toLowerCase().indexOf(val) >= 0);
    });
});

$(document).on('change', '.radio-serie-cb', function () {
    var serieId = Number($(this).val());
    var $gamesDiv = $('#rgames-' + serieId);
    if ($(this).is(':checked')) {
        if (preferredSeries.indexOf(serieId) < 0) preferredSeries.push(serieId);
        $gamesDiv.collapse('show');
    } else {
        preferredSeries = preferredSeries.filter(function (id) { return id !== serieId; });
        $gamesDiv.collapse('hide');
        $gamesDiv.find('.radio-game-cb').prop('checked', false).each(function () {
            var gid = Number($(this).val());
            preferredGames = preferredGames.filter(function (id) { return id !== gid; });
            $('.radio-game-cb[value="' + gid + '"]').prop('checked', false);
        });
    }
    savePrefs();
    updatePrefsSummary();
});

$(document).on('change', '.radio-game-cb', function () {
    var gameId = Number($(this).val());
    var checked = $(this).is(':checked');
    if (checked) {
        if (preferredGames.indexOf(gameId) < 0) preferredGames.push(gameId);
    } else {
        preferredGames = preferredGames.filter(function (id) { return id !== gameId; });
    }
    $('.radio-game-cb[value="' + gameId + '"]').prop('checked', checked);
    savePrefs();
    updatePrefsSummary();
});

// YT API initialization
function onYouTubeIframeAPIReady() {
    ytApiReady = true;
    if (pendingSong) {
        playSong(pendingSong);
        pendingSong = null;
    }
}

$(document).on('keydown', function (e) {
    if (e.key === 'ArrowRight' && !$(e.target).is('input, textarea')) {
        $('#radio-skip').prop('disabled', true);
        fetchNextSong(playSong);
    }
});

$(function () {
    loadSeriesPreferences();
    loadGenrePreferences();
    // If YT API already loaded (e.g. from playlist modal on same page)
    if (window.YT && window.YT.Player) {
        ytApiReady = true;
    }
    fetchNextSong(playSong);
});
