var current_id = 0;

$(document).ready(function () {

    var newWidth = localStorage.getItem("expandable-width");
    if (newWidth != undefined) {
        $("#offcanvas").removeClass("w-25");
        $("#offcanvas").removeClass("w-35");
        $("#offcanvas").removeClass("w-50");
        $("#offcanvas").addClass("w-" + newWidth);
    }
    // else {
    //     $("#offcanvas").removeClass("w-25");
    //     $("#offcanvas").removeClass("w-35");
    //     $("#offcanvas").removeClass("w-50");
    //     $("#offcanvas").addClass("w-25");
    // }
    if (localStorage.getItem("custom-playlist") != undefined) {
        var customPlaylistArrayTrigger = JSON.parse(localStorage.getItem("custom-playlist"));
        var jsonEdArrayTrigger = JSON.stringify(customPlaylistArrayTrigger)
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
    $("#flexSwitchCheckDefault").change(function (e) {
        localStorage.setItem("dark-mode", $(this).prop("checked"));
        changeStuffForDarkMode();
        if (DISQUS != undefined) {
            DISQUS.reset({ reload: true });
        }
    });

    $("#nightModeSwitch").click(function (e) {
        e.preventDefault();
        localStorage.setItem("dark-mode", !$(this).prev().prop("checked"));
        $(this).prev().click();
    });
    /*$(document).find("span.display-text").each(function(e){
        $(this).text().trim();
    })*/
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
    $(document).find("td.countries").each(function () {
        var imgsOfCountries = $(this).children();
        var arrayOfLinks = new Array();
        // var duplicatesToRemove = new Array();
        for (let i = 0; i < imgsOfCountries.length; i++) {
            var srcOfImg = imgsOfCountries[i].src;
            if (arrayOfLinks.indexOf(srcOfImg) == -1) {
                arrayOfLinks.push(srcOfImg);
            } else {
                // duplicatesToRemove.push(imgsOfCountries[i]);
                $(imgsOfCountries[i]).remove();
            }
        }
    });
    var activeSubgroups;
    var baseVideoSrc;
    var lyricsToDisplay;
    if (window.location.href.indexOf("/home") > -1) {
        $("#nfs-top-home").parent().addClass("nfs-top-item-active");
    }

    // $(".play_icon").on("click", function () {
    //     $(this).attr("src", $(this).attr("src").replace("znakwodny2", "znakwodny"));
    //     baseVideoSrc = $(this).attr("data-tagVideo") + "?autoplay=1&amp;modestbranding=1&amp;showinfo=0";
    //     $("#lyricsCollapse").empty()
    //     var lyricsTxt = $(this).next().text();
    //     if (lyricsTxt == "null" || lyricsTxt == "") {
    //         $("#showLyrics").text("Lyrics not found");
    //         $("#showLyrics").prop("disabled", true);
    //     } else if (lyricsTxt == "0.0") {
    //         $("#showLyrics").text("This is instrumental, no lyrics");
    //         $("#showLyrics").prop("disabled", true);
    //     } else {
    //         $("#lyricsCollapse").append(lyricsTxt);
    //         $("#showLyrics").prop("disabled", false);
    //     }
    // });

    $(".play_icon").on("click", function () {
        var useRowDisplay = localStorage.getItem("video-rendering-stuff");
        if (useRowDisplay==undefined || useRowDisplay == "true") {
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
                var lyricsDiv = $('<div class="lyrics_main">');
                var clearDiv = $('<div style="clear:both;">');
                var pElem = $('<p>');
                iframeToPut.attr("src", videoToUse + "?autoplay=1&amp;autohide=0&amp;theme=light&amp;wmode=transparent");
                pElem.text(lyricsText);
                lyricsDiv.append(pElem);
                newTd.append(iframeToPut);
                newTd.append(lyricsDiv);
                newTd.append(clearDiv);
                newTr.append(newTd)
                newTr.insertAfter(parentTr);
            }
        }

    });

    $('.play_icon').mouseover(function () {
        $(this).attr("src", $(this).attr("src").replace("znakwodny", "znakwodny2"));
        // }
    }).mouseout(function () {
        $(this).attr("src", $(this).attr("src").replace("znakwodny2", "znakwodny"));
    });

    $(document).find("div.accordion-collapse").each(function () {
        $(this).on('shown.bs.collapse', function () {
            var unscrollableMenu = localStorage.getItem("scrolling-stuff");
            if (!unscrollableMenu) {
                //i think i should fix this somehow
                if ($("#filter_games_menu").val() == "" || $("#filter_games_menu").val().length < 3) {
                    console.log($(this).offset());
                    var currentScroll = $('div.offcanvas-body').scrollTop();
                    var origTop = $('div.offcanvas-body').offset().top;
                    var thisTop = $(this).offset().top;
                    if (currentScroll > 0) {
                        var scrollTop = currentScroll + thisTop - origTop - $(this).parent().children().first().height();
                    } else {
                        var scrollTop = thisTop - origTop - $(this).parent().children().first().height();
                    }
                    $('div.offcanvas-body').animate({
                        scrollTop: scrollTop
                    }, 500);
                }
            }
        });
    });
    // $(document).on('click', 'button.gamegroup-button', function (e) { 
    //     $('div.offcanvas-body ').animate({
    //         scrollTop: $(this).offset().top
    //     }, 1000);
    // });

    $("#filter_games_menu").on("keyup", function () {
        var searchValue = $(this).val().toLowerCase();
        if (searchValue.length > 3) {
            $(document).find("button.gamegroup-button").filter(function () {
                var divId = $(this).attr("data-bs-target");
                if ($(divId).hasClass("show")) {
                    $(this).click();
                    //actually should listen to event of collapsing this shit and then show all filtered stuff
                }
            });
            setTimeout(function () {
                funcToShowOnlyFilteredGames(searchValue);
            }, 400);
        } else {
            var buttonToClick;
            var divToShow;
            $(document).find("button.gamegroup-button").filter(function () {
                var divId = $(this).attr("data-bs-target");
                if ($(divId).find("a.active").length > 0) {
                    $(divId).find("a").filter(function () {
                        $(this).show();
                    });
                    $(divId).parent().show();
                    $(divId).collapse('show');
                } else {
                    $(this).collapse('hide');
                    $(divId).find("a").filter(function () {
                        $(this).show();
                    });
                    $(divId).parent().show();
                    $(divId).collapse('hide');
                    if ($(divId).find("a.active").length > 0) {
                        buttonToClick = $(this);
                        divToShow = divId;
                    }
                }
            });
            if (divToShow != undefined) {
                $($(this).attr("data-bs-target")).parent().show();
                if ($(divToShow).hasClass("collapsed")) {
                    buttonToClick.click();
                }
                //$(this).show();
            }

        }
    });


    function funcToShowOnlyFilteredGames(searchValue) {
        $(document).find("button.gamegroup-button").filter(function () {
            var divId = $(this).attr("data-bs-target");
            if ($(this).text().toLowerCase() > -1 && !$(divId).hasClass("show")) {
                $(this).click();
            } else {
                var allA = $(divId).find("a");
                var somethingFound = false;
                for (let i = 0; i < allA.length; i++) {
                    if ($(allA[i]).text().toLowerCase().indexOf(searchValue) > -1) {
                        somethingFound = true;
                        $(allA[i]).show();
                    } else {
                        $(allA[i]).hide();
                    }
                }
                if (somethingFound) {
                    // if ($(divId).find("a.active").length > 0) {
                    //     $(divId).parent().show();
                    //     $(this).removeClass("collapsed");
                    //     console.log("???");
                    // } else {
                    if (!$(divId).is(":visible")) {
                        $(divId).parent().show();
                    }
                    if (!$(divId).hasClass("show")) {
                        $(this).click();
                        $(this).removeClass("collapsed");
                    }
                    // }
                } else {
                    $(divId).parent().hide();
                    $(divId).removeClass("show");
                }
            }
        });
    }

    $("#filter_songs").on("keyup", function () {
        var searchValue = $(this).val().toLowerCase();
        if (searchValue.length > 3) {
            $("table").find("tr:has(td):not(.subgroup-separator)").filter(function () {
                if ($(this).find("td.band").text().toLowerCase().indexOf(searchValue) > -1
                    || $(this).find("td.songtitle").text().toLowerCase().indexOf(searchValue) > -1) {
                    $(this).show();
                }
                // if ($(this).text().toLowerCase().indexOf(searchValue) > -1) {
                //     $(this).show();
                // } 
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

    $(document).on('click', '#showLyrics', function (e) {
        var divWithLyrics = $("#lyricsCollapse");
        var divContainer = divWithLyrics.parent();
        var videoContainer = divContainer.prev();
        var iframeContainer = videoContainer.children().first();
        if ($(divContainer).hasClass("col-md-6")) {
            $(divContainer).removeAttr("style");
            $(divContainer).removeClass("col-md-6");
            $(divContainer).css("display", "none");
            videoContainer.removeClass("col-md-6");
            videoContainer.addClass("col-md-12");
            $(divContainer).css("display", none);
            divWithLyrics.empty();
        } else {
            $(divContainer).removeAttr("style");
            $(divContainer).addClass("col-md-6");
            videoContainer.addClass("col-md-6");
            videoContainer.removeClass("col-md-12");
            $(divContainer).css("max-height", iframeContainer.height());
            $(divContainer).css("overflow-y", "auto");
            divWithLyrics.append(lyricsToDisplay);
        }
    });

    $(document).on('click', 'a.gamegroup', function (e) {
        console.log("jesus");
        e.preventDefault();
        $('.gamegroup.active').each(function () {
            $(this).removeClass('active');
            //we get rid of any previously active groups
        });
        $(this).addClass('active');
        var aId = $(this).attr('data-el_id');
        $(document).find('ul.subgroup[data-el_id="' + aId + '"]').each(function () {
            $(this).removeClass('visually-hidden');
            //we remove visually-hidden class from subgroups that belong to this group
        });
        $(document).find('ul.subgroup:not([data-el_id="' + aId + '"])').each(function () {
            $(this).addClass('visually-hidden');
            //and we hide subgroups that belong to other groups
        });
        $(this).removeClass('visually-hidden');
        var div = $($(this).attr('href'))[0];
        $(div).addClass('active');
        $(div).removeClass('visually-hidden');
        $(div).find("button").first().click();
        activeSubgroups = 1;
    });
    $(document).on('click', 'button.subgroup', function (e) {
        //handling clicking on subgroup
        e.preventDefault();
        var groupId = $(this).attr('data-group_id');
        if ($(this).text() == "All") {
            $('.subgroup.active').each(function () {
                $(this).removeClass('active');
                //we again remove active mark from any active subgroup
            });
            // $(document).find('tr:has(td)').each(function () {
            //     $(this).removeClass('visually-hidden');
            //     //first we hide all rows
            // });
            if ($(this).attr("data-gameGroupTxt") == "All") {
                $(document).find('tr').each(function () {
                    $(this).removeClass('visually-hidden');
                    //first we hide all rows
                });
            } else {
                if (groupId != undefined) {
                    $(document).find('tr:has(td):not([data-group_id="' + groupId + '"])').each(function () {
                        $(this).addClass('visually-hidden');
                        //first we hide all rows
                    });
                    $(document).find('tr:has(td)[data-group_id="' + groupId + '"]').each(function () {
                        $(this).removeClass('visually-hidden');
                        //first we hide all rows
                    });
                }
            }
            activeSubgroups = 1;
        } else {
            var aId = $(this).attr('id');
            var allSubgroup = $(this).parent().parent().find("button[data-subgroupTxt='All']").first();
            if (allSubgroup.hasClass('active')) {
                allSubgroup.removeClass('active');
                //$(document).find('tr[data-group_id="' + aId + '"]');
                /*
                $(document).find('tr:has(td)').each(function () {
                    $(this).addClass('visually-hidden');
                    //first we hide all rows
                });
                */
                $(document).find('tr:has(td):not([data-el_id="' + aId + '"])').each(function () {
                    $(this).addClass('visually-hidden');
                    //first we hide all rows
                });
                activeSubgroups--;
                //DODAC GROUP ID DO TYCH SEPARATOROW
            }
            if ($(this).hasClass("active")) {
                $(this).removeClass('active');
                $(document).find('tr:has(td)[data-el_id="' + aId + '"]').each(function () {
                    $(this).addClass('visually-hidden');
                });
                activeSubgroups--;
            } else {
                $(this).addClass('active');
                $(document).find('tr:has(td)[data-el_id="' + aId + '"]').each(function () {
                    $(this).removeClass('visually-hidden');
                });
                activeSubgroups++
            }
            if (activeSubgroups == 0) {
                var allSubgroup = $(this).parent().parent().find("button[data-subgroupTxt='All']").first();
                $(allSubgroup).click();
                activeSubgroups == 1;
                return;
            }
        }
        if ($(e.target).text() == "All" && $(e.target).attr("data-gameGroupTxt") == "All") {
            $(this).addClass('active');
        }
        if ($(e.target).text() == "All" && $(e.target).attr("data-gameGroupTxt") != "All") {
            //we are in not-root subgroup as i call it
            console.log('zonk');
            $(document).find('tr:has(td)').not('.subgroup-separator').each(function () {
                $(this).addClass('visually-hidden');
                //first we hide all rows
            });
            $(e.target.parentElement.parentElement).find("button").each(function () {
                //then for each subgroup we remove this hidden class
                var aId = $(this).attr('id');
                $(document).find('tr[data-el_id="' + aId + '"]').each(function () {
                    $(this).removeClass('visually-hidden');
                });
                var aText = $(this).text();

            });
            $(this).addClass('active');
        } else {
            //we're in main group and main subgroup so we just make basically everything visible
            /*
            var aId = $(this).attr('id');
            $(document).find('tr[data-el_id="' + aId + '"]').each(function () {
                $(this).removeClass('visually-hidden');
            });
            $(document).find('tr:has(td):not([data-el_id=' + aId + '])').each(function () {
                $(this).addClass('visually-hidden');
            });
            */
        }
    });

    $('#videoModal').on('show.bs.modal', function (e) {
        // set the video src to autoplay and not to show related video. Youtube related video is like a box of chocolates... you never know what you're gonna get
        var myImgSource = e.relatedTarget;
        $(myImgSource).attr("src", $(myImgSource).attr("src").replace("znakwodny2", "znakwodny"));
        baseVideoSrc = $(myImgSource).attr("data-tagVideo") + "?autoplay=1&amp;modestbranding=1&amp;showinfo=0";
        $("#lyricsCollapse").empty()
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
    })

    $('#videoModal').on('hide.bs.modal', function (e) {
        $("#video").attr('src', '');
    })

    $('#spotifyModal').on('show.bs.modal', function (e) {
        // set the video src to autoplay and not to show related video. Youtube related video is like a box of chocolates... you never know what you're gonna get
        $("#spotifyVideo").attr('src', "https://open.spotify.com/embed/playlist/" + $("#spotifyLink").attr('data-tagVideo'));
        $("#spotify-ext").attr('href', "spotify:playlist:" + $("#spotifyLink").attr('data-tagVideo'));
    })

    $('#spotifyModal').on('hide.bs.modal', function (e) {
        $("#spotifyVideo").attr('src', '');
    })

    $('#soundcloudModal').on('show.bs.modal', function (e) {
        // set the video src to autoplay and not to show related video. Youtube related video is like a box of chocolates... you never know what you're gonna get
        $("#soundcloudVideo").attr('src', "https://w.soundcloud.com/player/?url=https%253A//api.soundcloud.com/playlists/" + $("#soundcloudLink").attr('data-tagVideo')
            + "&auto_play=false&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true&visual=true");
    })

    $('#soundcloudModal').on('hide.bs.modal', function (e) {
        $("#spotifyVideo").attr('src', '');
    })

    $('#tidalModal').on('show.bs.modal', function (e) {
        // set the video src to autoplay and not to show related video. Youtube related video is like a box of chocolates... you never know what you're gonna get
        $("#tidalVideo").attr('src', "https://embed.tidal.com/playlists/" + $("#tidalLink").attr('data-tagVideo'));
        $("#tidal-ext").attr('href', "https://listen.tidal.com/playlist/" + $("#tidalLink").attr('data-tagVideo'));
    })

    $('#tidalModal').on('hide.bs.modal', function (e) {
        $("#tidalVideo").attr('src', '');
    })

    $('#deezerModal').on('show.bs.modal', function (e) {
        // set the video src to autoplay and not to show related video. Youtube related video is like a box of chocolates... you never know what you're gonna get
        $("#deezerVideo").attr('src', "https://widget.deezer.com/widget/auto/playlist/" + $("#deezerLink").attr('data-tagVideo'));
        $("#deezer-ext").attr('href', "deezer://www.deezer.com/playlist/" + $("#deezerLink").attr('data-tagVideo'));
    })

    $('#deezerModal').on('hide.bs.modal', function (e) {
        $("#deezerVideo").attr('src', '');
    })

    //when loading list of songs, we want to trigger main group by default
    console.log("freeman");
    var firstGameGroup = $('a.gamegroup:first')[0];
    if (firstGameGroup != null) {
        $(firstGameGroup).first().click();
        //we have to hit first subgroup in main group to have stuff displayed
    }

    $('#disqusModal').on('show.bs.modal', function (e) {
        if ($("#disqus_thread").children().length == 0) {
            var script = document.createElement('script');
            script.innerHTML = "(function () {  var d = document, s = d.createElement('script'); s.src = 'https://vps-6a670942-vps-ovh-net.disqus.com/embed.js'; s.setAttribute('data-timestamp', +new Date());(d.head || d.body).appendChild(s);})();";
            var noscript = document.createElement('noscript');
            noscript.innerHTML = 'Please enable JavaScript to view the <a href="https://disqus.com/?ref_noscript">comments powered by Disqus.</a>';
            $(this).append(script);
            $(this).append(noscript);
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

    function initPlayer(changeId) {
        disablePlayer(false);
        // var tbody = $('#playlistModePlayer').append("<tbody>");
        if (changeId >= 0) {
            current_id = parseInt(changeId);
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
                    trElem.append('<td class="playlist_play_it"><img width="25" height="25" src="/images/znakwodny.png" style="background-color: transparent"></td>');
                    trElem.append('<td class="playlist_row">' + bandText + ' - ' + titleText + '</td>');
                    trElem.append('<td class="playlist_disable_song"><img width="25" height="25" src="/images/trashcan3.png" style="background-color: transparent"></td>');
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
            function onPlayerReady(event) {
                event.target.playVideo();
            }
            function onPlayerStateChange(event) {
                if (event.data === 0) {
                    initPlayer(current_id + 1);
                }
            }
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
    })

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
            customPlaylistArray = new Array();
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
        var parentDiv = $("#disqusModal").parent();
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
        $("#getAllUsages").attr("href", "/song/" + songIdAttr);
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
                if (songInfo.spotify != null) {
                    $(songInfo.spotify).insertAfter("#externalLinks");
                }
                if (songInfo.deezer != null) {
                    $(songInfo.deezer).insertAfter("#externalLinks");
                }
                if (songInfo.itunes != null) {
                    $(songInfo.itunes).insertAfter("#externalLinks");
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

    $('#infoSongModal').on('hide.bs.modal', function (e) {
        $("#officialArtist").contents().filter(function () {
            return this.nodeType === 3; //Node.TEXT_NODE
        }).each(function () {
            $(this).remove();
        });
        $("#officialTitle").contents().filter(function () {
            return this.nodeType === 3;
        }).each(function () {
            $(this).remove();
        });
        $("#composers").parent().find("a").each(function () {
            $(this).remove();
        })
        $("#subcomposers").parent().find("a").each(function () {
            $(this).remove();
        })
        $("#remixers").parent().find("a").each(function () {
            $(this).remove();
        })
        $("#featArtists").parent().find("a").each(function () {
            $(this).remove();
        })
        $("#externalLinks").parent().find("a").each(function () {
            $(this).remove();
        })
    });

});
