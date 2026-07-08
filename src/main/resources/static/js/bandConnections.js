$(document).ready(function () {

    // --- Autocomplete ---

    function setupAutocomplete(inputId, hiddenId, suggestionsId) {
        var $input = $("#" + inputId);
        var $hidden = $("#" + hiddenId);
        var $menu = $("#" + suggestionsId);
        var searchTimeout;

        $input.on("input", function () {
            clearTimeout(searchTimeout);
            $hidden.val("");
            var q = $input.val().trim();
            if (q.length < 2) { $menu.hide().empty(); return; }

            searchTimeout = setTimeout(function () {
                $.getJSON("/api/bands/search", { q: q }, function (results) {
                    $menu.empty();
                    if (results.length === 0) {
                        $("<div>").addClass("dropdown-item text-muted small")
                            .text("No artists found in the graph")
                            .appendTo($menu);
                    } else {
                        results.forEach(function (item) {
                            $("<a>").addClass("dropdown-item")
                                .text(item.name)
                                .on("mousedown", function (e) {
                                    e.preventDefault();
                                    $input.val(item.name);
                                    $hidden.val(item.id);
                                    $menu.hide().empty();
                                })
                                .appendTo($menu);
                        });
                    }
                    $menu.show();
                });
            }, 300);
        });

        $input.on("blur", function () {
            // Small delay so mousedown on a suggestion fires first
            setTimeout(function () { $menu.hide(); }, 150);
        });

        $input.on("focus", function () {
            if ($menu.children().length) $menu.show();
        });
    }

    setupAutocomplete("fromArtistInput", "fromArtistId", "fromArtistSuggestions");
    setupAutocomplete("toArtistInput",   "toArtistId",   "toArtistSuggestions");

    // --- Find connection ---

    $("#findConnectionBtn").on("click", function () {
        var fromId = $("#fromArtistId").val();
        var toId   = $("#toArtistId").val();
        var fromName = $("#fromArtistInput").val().trim();
        var toName   = $("#toArtistInput").val().trim();

        if (!fromName || !toName) {
            showResult('<div class="alert alert-warning">Please fill in both artist fields.</div>');
            return;
        }
        if (!fromId || !toId) {
            showResult('<div class="alert alert-warning">Please select artists from the suggestions list.</div>');
            return;
        }

        var sameGroup = $("#sameGroupToggle").is(":checked");

        $("#connection-spinner").show();
        $("#connection-result").empty();

        $.getJSON("/api/bands/connection", { from: fromId, to: toId, sameGroup: sameGroup }, function (data) {
            renderChain(data, sameGroup);
        }).fail(function (xhr) {
            showResult('<div class="alert alert-danger">Error: ' + xhr.statusText + '</div>');
        }).always(function () {
            $("#connection-spinner").hide();
        });
    });

    function showResult(html) {
        $("#connection-result").html(html);
    }

    function renderChain(data, sameGroup) {
        if (!data.found) {
            showResult('<div class="alert alert-warning">' + escHtml(data.message) + '</div>');
            return;
        }

        var path = data.path;
        var hopLabel = data.hops === 0 ? "Same artist"
                     : data.hops === 1 ? "1 hop"
                     : data.hops + " hops";

        var html = '<p class="text-muted mb-3 hop-count">' + escHtml(hopLabel) + '</p>';
        html += '<div class="band-chain">';

        path.forEach(function (node, i) {
            if (i > 0 && node.viaGames && node.viaGames.length) {
                html += '<div class="chain-connector d-flex align-items-center flex-wrap gap-2">';
                html += '<span class="chain-arrow">↓</span><span class="chain-via-label">via</span>';
                node.viaGames.forEach(function (game) {
                    var label = escHtml(game.title);
                    if (sameGroup && game.maingroupName) {
                        label += ' <span class="chain-group-label">(' + escHtml(game.maingroupName) + ')</span>';
                    }
                    html += '<a href="/game/' + escHtml(game.gameShort) + '" '
                          + 'class="chain-game-badge text-decoration-none">'
                          + label + '</a>';
                });
                html += '</div>';
            }
            html += '<div class="chain-node">'
                  + '<a href="/author/' + node.bandId + '" class="btn btn-outline-primary chain-artist-btn">'
                  + escHtml(node.bandName) + '</a>'
                  + '</div>';
        });

        html += '</div>';
        showResult(html);
    }

    function escHtml(str) {
        return String(str)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;");
    }

    // Pre-fill from URL params and auto-search if both are present
    var params = new URLSearchParams(window.location.search);
    var fromParam = params.get("from");
    var toParam   = params.get("to");
    if (fromParam && toParam) {
        // Resolve names via a quick search then trigger
        Promise.all([
            $.getJSON("/api/bands/search", { q: fromParam }),
            $.getJSON("/api/bands/search", { q: toParam })
        ]).then(function (results) {
            var fromMatch = results[0].find(function (r) { return String(r.id) === fromParam; });
            var toMatch   = results[1].find(function (r) { return String(r.id) === toParam; });
            if (fromMatch) { $("#fromArtistInput").val(fromMatch.name); $("#fromArtistId").val(fromMatch.id); }
            if (toMatch)   { $("#toArtistInput").val(toMatch.name);   $("#toArtistId").val(toMatch.id); }
            if (fromMatch && toMatch) { $("#findConnectionBtn").trigger("click"); }
        });
    }
});
