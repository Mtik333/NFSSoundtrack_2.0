var current_id = 0;
$(document).ready(function () {
    $("#filter_games_menu").val("");
    /*$(document).find("span.display-text").each(function(e){
        $(this).text().trim();
    })*/
    var currentGame = window.location.href.replace(document.location.origin, "");
    $(document).find("a[href='" + currentGame + "']").each(function (e) {
        if (!$(this).hasClass("nav-link")) {
            $(this).addClass("active");
            $(this).parent().parent().parent().parent().find("button").click();
        } else {
            $(this).parent().addClass("nfs-top-item-active");
        }
    })
    $(document).find("td.countries").each(function () {
        $(this).find('.country-img:nth-child(n+2)').remove();
    });
    var activeSubgroups;
    var baseVideoSrc;
    var lyricsToDisplay;
    if (window.location.href.indexOf("/home") > -1) {
        $("#nfs-top-home").parent().addClass("nfs-top-item-active");
    }

    $('.play_icon').mouseover(function () {
        $(this).attr("src", $(this).attr("src").replace("znakwodny", "znakwodny2"));
        baseVideoSrc = $(this).attr("data-tagVideo") + "?autoplay=1&amp;modestbranding=1&amp;showinfo=0";
        $("#lyricsCollapse").empty()
        var lyricsTxt = $(this).next().text();
        if (lyricsTxt == "null") {
            $("#showLyrics").text("Lyrics not found");
            $("#showLyrics").prop("disabled", true);
        } else if (lyricsTxt == "0.0") {
            $("#showLyrics").text("This is instrumental, no lyrics");
            $("#showLyrics").prop("disabled", true);
        } else {
            $("#lyricsCollapse").append(lyricsTxt);
            $("#showLyrics").prop("disabled", false);
        }
    }).mouseout(function () {
        $(this).attr("src", $(this).attr("src").replace("znakwodny2", "znakwodny"));
    });

    $(document).find("div.accordion-collapse").each(function () {
        $(this).on('shown.bs.collapse', function () {

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
            $("#game_stuff").find("tr:has(td):not(.subgroup-separator)").filter(function () {
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
            $("#game_stuff").find("tr:has(td)").filter(function () {
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
            script.innerHTML = "(function () {  var d = document, s = d.createElement('script'); s.src = 'https://nfssoundtrack.disqus.com/embed.js'; s.setAttribute('data-timestamp', +new Date());(d.head || d.body).appendChild(s);})();";
            var noscript = document.createElement('noscript');
            noscript.innerHTML = 'Please enable JavaScript to view the <a href="https://disqus.com/?ref_noscript">comments powered by Disqus.</a>';
            $(this).append(script);
            $(this).append(noscript);
        }
    });

    $('#playlistModeModal').on('show.bs.modal', function (e) {
        initPlayer(0);
    });

    $('#playlistModeModal').on('hide.bs.modal', function (e) {
        disablePlayer();
    });
    //this is some youtube player stuff below

    function disablePlayer() {
        $('#playlistModePlayer').html('');
        $('#playlist_progress').empty();
    }

    function initPlayer(changeId) {
        disablePlayer();
        if (changeId >= 0) {
            current_id = parseInt(changeId);
        }
        $("#game_stuff").find("tr:visible:not(.subgroup-separator)").each(function () {
            var bandText = $(this).find("td.band").html();
            var titleText = $(this).find("td.songtitle").html();
            var youtubePlayIcon = $(this).find("img.play_icon");
            if (youtubePlayIcon.length>0) {
                var youtubeLink = youtubePlayIcon.attr("data-tagvideo")
                    .replace("https://www.youtube.com/embed/", "");
                var liElem = $('<li rel="' + youtubeLink + '"></li>');
                liElem.append('<span class="playlist_play_it"><img width="25" height="25" src="/images/znakwodny.png"></span>');
                liElem.append("<span>" + bandText + " - " + titleText + "</span>")
                liElem.append('<span class="playlist_disable_song"><img width="25" height="25" src="/images/thrashcan2.png"></span>');
                $("#playlist_progress").append(liElem);
            }
        })

        var data_song = [];
        i = 0;
        $('#playlist_progress li').each(function () {
            data_song[i] = $(this).attr("rel");
            i++;
        });
        if (current_id >= i) {
            current_id = 0;
        }
        if ($('#playlist_progress li:nth-child(' + (current_id + 1) + ')').hasClass("disabled")) {
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
            var el = $('#playlist_progress li:nth-child(' + (current_id + 1) + ')');
            var p = $('#playlist_progress');
            $('#playlist_progress').animate({
                scrollTop: p.scrollTop() + el.position().top - (p.height() / 2) + (el.height() / 2)
            }, 300);
            $('#playlist_progress li').removeClass("current");
            $('#playlist_progress li:nth-child(' + (current_id + 1) + ')').addClass("current");
        }
    }
    $(document).on("click", "#playlist_progress li:not(.current,.disabled) span.play_it", function () {
        initPlayer(this.parentNode.id);
    })


    $(document).on("click", "#playlist_progress li .disable_song", function () {
        if ($(this).parent().hasClass("disabled")) {
            $(this).parent().removeClass("disabled");
        } else {
            $(this).parent().addClass("disabled");
        }
    });
});
