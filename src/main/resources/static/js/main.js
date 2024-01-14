var current_id = 0;
var activeSubgroups;
var baseVideoSrc;
$(document).ready(function () {
    var newWidth = localStorage.getItem("expandable-width");
    if (window.location.href.indexOf("/home") > -1) {
        $("#nfs-top-home").parent().addClass("nfs-top-item-active");
    }
    if (newWidth != undefined) {
        $("#offcanvas").removeClass("w-25");
        $("#offcanvas").removeClass("w-35");
        $("#offcanvas").removeClass("w-50");
        $("#offcanvas").addClass("w-" + newWidth);
    }
    if (localStorage.getItem("custom-playlist") != undefined) {
        var customPlaylistArrayTrigger = JSON.parse(localStorage.getItem("custom-playlist"));
        var jsonEdArrayTrigger = JSON.stringify(customPlaylistArrayTrigger);
        localStorage.setItem("custom-playlist", jsonEdArrayTrigger);
        $("#playlistContent").val(jsonEdArrayTrigger);
    } else {
        $("#customPlaylistSubmit").prop("disabled", true);
        $("#customPlaylistSubmit").parent().parent().attr("data-toggle", "tooltip");
        $("#customPlaylistSubmit").parent().parent().attr("data-placement", "top");
        $("#customPlaylistSubmit").parent().parent().attr("data-bs-original-title", "Custom playlist is empty");
        $("#customPlaylistSubmit").parent().parent().tooltip();
    }
    var useRowDisplay = localStorage.getItem("video-rendering-stuff");
    if (useRowDisplay == undefined || useRowDisplay == "true") {
        $(document).find(".play_icon").each(function (e) {
            $(this).removeAttr("data-bs-target");
            $(this).removeAttr("data-bs-toggle");
        });
    }
    $("#searchStuff").tooltip({ 'trigger': 'focus', 'title': $("#searchStuff").attr("data-tooltip") });
    $("#filter_games_menu").val("");
    $('[data-toggle="tooltip"]').tooltip();
    var currentGame = window.location.href.replace(document.location.origin, "");
    if (currentGame.indexOf("custom/playlist") > -1) {
        $("#customPlaylistSubmit").parent().parent().addClass("nfs-top-item-active");
    } else {
        $(document).find("a[href='" + currentGame + "']").each(function (e) {
            if (!$(this).hasClass("nav-link")) {
                if ($(this).hasClass("genreLink")) {
                    return;
                }
                $(this).addClass("active");
                $(this).parent().parent().parent().parent().find("button").click();
            } else {
                $(this).parent().addClass("nfs-top-item-active");
            }
        });
    }
    //when loading list of songs, we want to trigger main group by default
    var firstGameGroup = $('a.gamegroup:first')[0];
    if (firstGameGroup != null) {
        $(firstGameGroup).first().click();
        //we have to hit first subgroup in main group to have stuff displayed
    }

    $(document).find("td.countries").each(function () {
        var imgsOfCountries = $(this).children();
        var arrayOfLinks = [];
        for (let i = 0; i < imgsOfCountries.length; i++) {
            var srcOfImg = imgsOfCountries[i].src;
            if (arrayOfLinks.indexOf(srcOfImg) == -1) {
                arrayOfLinks.push(srcOfImg);
            } else {
                $(imgsOfCountries[i]).remove();
            }
        }
    });

    $(".play_icon").on("click", function () {
        var useRowDisplay = localStorage.getItem("video-rendering-stuff");
        if (useRowDisplay == undefined || useRowDisplay == "true") {
            var existingTr = $("#listen-music");
            if (existingTr.length != 0) {
                existingTr.remove();
            } else {
                var videoToUse = $(this).attr("data-tagvideo");
                var lyricsText = $(this).next().text();
                var parentTr = $(this).parent().parent().parent();
                var newTr = $('<tr id="listen-music">');
                var newTd = $('<td colspan="7" id="listen-music-id">');
                var iframeToPut = $('<iframe id="ytrow" frameborder="0">');
                var lyricsDiv = $('<div id="lyrics_main">');
                var clearDiv = $('<div style="clear:both;">');
                var pElem = $('<p>');
                iframeToPut.attr("src", videoToUse + "?autoplay=1&amp;autohide=0&amp;theme=light&amp;wmode=transparent");
                pElem.text(lyricsText);
                lyricsDiv.append(pElem);
                newTd.append(iframeToPut);
                newTd.append(lyricsDiv);
                newTd.append(clearDiv);
                newTr.append(newTd);
                newTr.insertAfter(parentTr);
            }
        } else {
            $("#lyricsCollapse").empty();
            var lyricsTxt = $(this).next().text();
            if (lyricsTxt == "null" || lyricsTxt == "") {
                $("#showLyrics").text("Lyrics not found");
                $("#showLyrics").prop("disabled", true);
            } else if (lyricsTxt == "0.0") {
                $("#showLyrics").text("This is instrumental, no lyrics");
                $("#showLyrics").prop("disabled", true);
            } else {
                $("#lyricsCollapse").append(lyricsTxt);
                $("#showLyrics").prop("disabled", false);
            }
        }

    });

    $('.play_icon').mouseover(function () {
        $(this).attr("src", $(this).attr("src").replace("znakwodny", "znakwodny2"));
        // }
    }).mouseout(function () {
        $(this).attr("src", $(this).attr("src").replace("znakwodny2", "znakwodny"));
    });

    $("#filter_songs").on("keyup", function () {
        var searchValue = $(this).val().toLowerCase();
        if (searchValue.length > 3) {
            $("table").find("tr:has(td):not(.subgroup-separator)").filter(function () {
                if ($(this).find("td.band").text().toLowerCase().indexOf(searchValue) > -1 || $(this).find("td.songtitle").text().toLowerCase().indexOf(searchValue) > -1) {
                    $(this).show();
                }
                else {
                    $(this).hide();
                }
            });
        } else {
            $("table").find("tr:has(td)").filter(function () {
                $(this).show();
            });
        }
    });

    $('#videoModal').on('show.bs.modal', function (e) {
        // set the video src to autoplay and not to show related video. Youtube related video is like a box of chocolates... you never know what you're gonna get
        var myImgSource = e.relatedTarget;
        $(myImgSource).attr("src", $(myImgSource).attr("src").replace("znakwodny2", "znakwodny"));
        baseVideoSrc = $(myImgSource).attr("data-tagVideo") + "?autoplay=1&amp;modestbranding=1&amp;showinfo=0";
        $("#lyricsCollapse").empty();
        var lyricsTxt = $(myImgSource).next().text();
        if (lyricsTxt == "null" || lyricsTxt == "") {
            $("#showLyrics").text($("#showLyrics").attr("data-lyricsMissing"));
            $("#showLyrics").prop("disabled", true);
        } else if (lyricsTxt == "0.0") {
            $("#showLyrics").text($("#showLyrics").attr("data-instrumental"));
            $("#showLyrics").prop("disabled", true);
        } else {
            $("#lyricsCollapse").append(lyricsTxt);
            $("#showLyrics").prop("disabled", false);
        }
        $("#video").attr('src', baseVideoSrc);
    });

    $('#videoModal').on('hide.bs.modal', function (e) {
        $("#video").attr('src', '');
    });

    $('#disqusModal').on('show.bs.modal', function (e) {
        var disqusTarget = e.relatedTarget.id;
        var linkToUse;
        if (disqusTarget == "newDisqusLink") {
            linkToUse = window.location.href;
        } else {
            linkToUse = $("#disqusModal").attr("data-disqusLink");
        }
        if ($("#disqus_thread").children().length == 0) {
            var script = document.createElement('script');
            script.innerHTML = "var disqus_config = function () {     this.page.url = '" + linkToUse + "';  }; (function () {  var d = document, s = d.createElement('script'); s.src = 'https://nfssoundtrack.disqus.com/embed.js'; s.setAttribute('data-timestamp', +new Date());(d.head || d.body).appendChild(s);})();";
            var noscript = document.createElement('noscript');
            noscript.innerHTML = 'Please enable JavaScript to view the <a href="https://disqus.com/?ref_noscript">comments powered by Disqus.</a>';
            $(this).append(script);
            $(this).append(noscript);
        } else {
            DISQUS.reset({
                reload: true,
                config: function () {
                    this.page.url = linkToUse;
                }
            });
        }
    });

    $('#playlistModeModal').on('show.bs.modal', function (e) {
        initPlayer(0);
    });

    $('#playlistModeModal').on('shown.bs.modal', function (e) {
        var divWithVideo = $("#playlistModePlayer");
        var divContainer = divWithVideo.parent();
        var playlistContainer = divContainer.next();
        var iframeContainer = divWithVideo.children().first();
        $(playlistContainer).css("max-height", iframeContainer.height());
        $(playlistContainer).css("overflow-y", "auto");
    });
    $('#playlistModeModal').on('hide.bs.modal', function (e) {
        disablePlayer(true);
    });
    //this is some youtube player stuff below

    function disablePlayer(cleanDiv) {
        $('#playlistModePlayer').html('');
        if (cleanDiv) {
            $('#playlist_progress').empty();
        }
    }

    function onPlayerReady(event) {
        event.target.playVideo();
    }
    function onPlayerStateChange(event) {
        if (event.data === 0) {
            initPlayer(current_id + 1);
        }
    }

    function initPlayer(changeId) {
        disablePlayer(false);
        // var tbody = $('#playlistModePlayer').append("<tbody>");
        if (changeId >= 0) {
            current_id = Number(changeId);
        }
        var localI = 0;
        if ($('#playlist_progress').is(":empty")) {
            $("table.playlist_table").find("tr:visible:not(.subgroup-separator):not(.visually-hidden)").each(function () {
                var bandText = $(this).find("td.band").html();
                var titleText = $(this).find("td.songtitle").html();
                bandText = $.trim(bandText).replaceAll("\n", "").replaceAll("<span>", "").replaceAll("</span>", "").replaceAll("> ", ">");
                titleText = $.trim(titleText).replaceAll("\n", "").replaceAll("<span>", "").replaceAll("</span>", "").replaceAll("> ", ">");
                var youtubePlayIcon = $(this).find("img.play_icon");
                if (youtubePlayIcon.length > 0) {
                    var youtubeLink = youtubePlayIcon.attr("data-tagvideo")
                        .replace("https://www.youtube.com/embed/", "");
                    var trElem = $('<tr id="' + localI + '" rel="' + youtubeLink + '"></tr>');
                    trElem.append('<td class="playlist_play_it"><img class="img-responsive-playlist-modal-icon pointable" src="/images/znakwodny.png"></td>');
                    trElem.append('<td class="playlist_row">' + bandText + ' - ' + titleText + '</td>');
                    trElem.append('<td class="playlist_disable_song"><img class="img-responsive-playlist-modal-icon pointable" src="/images/fullres/trashcan_big.png"></td>');
                    $("#playlist_progress").append(trElem);
                    localI++;
                }
            });
        }
        if (localI == 0) {
            //show modal that there is nothing to play
            $("#errorThing").parent().fadeIn(500, function () {
                setTimeout(function () {
                    $("#errorThing").parent().fadeOut(500);
                }, 3000);
            });
        }
        var data_song = [];
        i = 0;
        $('#playlist_progress tr').each(function () {
            data_song[i] = $(this).attr("rel");
            i++;
        });
        if (current_id >= i) {
            current_id = 0;
        }
        if ($('#playlist_progress tr:nth-child(' + (current_id + 1) + ')').hasClass("disabled")) {
            initPlayer(current_id + 1);
        } else {
            $('#playlistModePlayer').html('<iframe id="player" type="text/html" src="https://www.youtube.com/embed/' + data_song[current_id] + '?enablejsapi=1&autoplay=1&autohide=0&theme=light&wmode=transparent" frameborder="0"></iframe>');

            var player = new YT.Player('player', {
                events: {
                    'onReady': onPlayerReady,
                    'onStateChange': onPlayerStateChange
                }
            });
            var el = $('#playlist_progress tr:nth-child(' + (current_id + 1) + ')');
            var p = $('#playlist_progress');
            $('#playlist_progress').animate({
                scrollTop: p.scrollTop() + el.position().top - (p.height() / 2) + (el.height() / 2)
            }, 300);
            var previousTr = $('#playlist_progress tr');
            previousTr.removeClass("current");
            previousTr.find("td.playlist_disable_song").show();
            previousTr.find("td.playlist_play_it").show();
            previousTr.find("td.playlist_row").attr("colspan", 1);
            var currentTr = $('#playlist_progress tr:nth-child(' + (current_id + 1) + ')');
            currentTr.addClass("current");
            currentTr.find("td.playlist_disable_song").hide();
            currentTr.find("td.playlist_play_it").hide();
            currentTr.find("td.playlist_row").attr("colspan", 3);
        }
    }
    $(document).on("click", "td.playlist_play_it", function () {
        if (!$(this).parent().hasClass("disabled")) {
            initPlayer(this.parentNode.id);
        }
    });

    $(document).on("click", "td.playlist_disable_song", function () {
        if ($(this).parent().hasClass("disabled")) {
            $(this).parent().removeClass("disabled");
        } else {
            $(this).parent().addClass("disabled");
        }
    });

    $(document).on("click", "img.add-to-custom-playlist", function () {
        var customPlaylistArray = localStorage.getItem("custom-playlist");
        if (customPlaylistArray == undefined) {
            customPlaylistArray = [];
        }
        else if (customPlaylistArray != undefined) {
            customPlaylistArray = JSON.parse(customPlaylistArray);
        }
        var relatedTr = $(this).parent().parent();
        var songSubgroupId = $(relatedTr).attr("data-songSubgroup-id");
        customPlaylistArray.push(songSubgroupId);
        var jsonEdArray = JSON.stringify(customPlaylistArray);
        localStorage.setItem("custom-playlist", jsonEdArray);
        $("#playlistContent").val(jsonEdArray);
        $("#customPlaylistSubmit").prop("disabled", false);
        $("#customPlaylistSubmit").parent().parent().tooltip('dispose');
        $("#successAddToCustomPlaylist").parent().fadeIn(500, function () {
            setTimeout(function () {
                $("#successAddToCustomPlaylist").parent().fadeOut(500);
            }, 3000);
        });
    });


    $(document).on("click", "img.info-about-song", function () {
        var trElem = $(this).parent().parent();
        var songIdAttr = $(trElem).attr("data-song_id");
        $("#getAllUsages").attr("href", "/song/" + Number(songIdAttr));
        $.ajax({
            async: false,
            type: "GET",
            url: "/songInfo/" + songIdAttr,
            success: function (ooo) {
                var songInfo = JSON.parse(ooo);
                $("#officialArtist").append(document.createTextNode(songInfo.officialArtist));
                $("#officialTitle").append(document.createTextNode(songInfo.officialTitle));
                if (songInfo.composers.length != 0) {
                    for (let i = 0; i < songInfo.composers.length; i++) {
                        $(songInfo.composers[i]).insertAfter($("#composers"));
                    }
                }
                if (songInfo.subcomposers.length != 0) {
                    for (let i = 0; i < songInfo.subcomposers.length; i++) {
                        $(songInfo.subcomposers[i]).insertAfter($("#subcomposers"));
                    }
                }
                if (songInfo.remixers.length != 0) {
                    for (let i = 0; i < songInfo.remixers.length; i++) {
                        $(songInfo.remixers[i]).insertAfter($("#remixers"));
                    }
                }
                if (songInfo.featArtists.length != 0) {
                    for (let i = 0; i < songInfo.featArtists.length; i++) {
                        $(songInfo.featArtists[i]).insertAfter($("#featArtists"));
                    }
                }
                if (songInfo.youtube != null) {
                    $(songInfo.youtube).insertAfter("#externalLinks");
                }
                if (songInfo.spotify != null) {
                    $(songInfo.spotify).insertAfter("#externalLinks");
                }
                if (songInfo.deezer != null) {
                    $(songInfo.deezer).insertAfter("#externalLinks");
                }
                if (songInfo.itunes != null) {
                    $(songInfo.itunes).insertAfter("#externalLinks");
                }
                if (songInfo.baseSongId != null) {
                    $(songInfo.baseSongId).insertAfter("#baseSong");
                    $("#baseSongDiv").css("display", "");
                }
                $("#infoSongModal").modal('show');
            },
            error: function (ooo) {
                console.log("e2");
            },
            done: function (ooo) {
                console.log("e3");
            }
        });
    });

});
