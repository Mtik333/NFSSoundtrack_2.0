//using this to wait until user finishes typing before filtering out the games
var timeout;
var delay = 300;
/**
 * id of currently used video in playlist mode
 */
var current_id = 0;
/**
 * variable to track active subgroups in game's soundtrack page
 */
var activeSubgroups;
/**
 * curently played video in video modal
 */
var baseVideoSrc;
/**
 * for mobile trying to remember last clicked play button to de-activate it again
 */
var lastActivePlayButton;
var spotifyApi;
var spotifyController;
var currentlyPlayedSpotify;
/**
 * using this to know, how far the non-static left menu is scrolled into
 */
var scrollTopAfterLoad;

var isMobileAgent = navigator.userAgent.includes("Mobile");

async function doLoadingCrap() {
    var contentWidth = localStorage.getItem("content-width");
    if (localStorage.getItem("static-leftmenu") == "false") {
        $("#unpin-menu").css("display", "none");
        $("#unpin-menu").parent().css("display", "");
        $("#unpin-menu").prev().css("display", "none");
        $("#unpin-menu").prev().prev().css("display", "none");
        $("#filter_games_menu").css("display", "none");
        $("#filter_games_menu_pinned").css("display", "");
        var contentDiv = $("#offcanvas").next().next();
        if (contentWidth != null) {
            contentDiv.addClass("col-sm-" + contentWidth);
            contentDiv.removeClass("col");
        }
        //probably need to make this less convoluted as code is quite repetitive
        if (localStorage.getItem("all-games") == "true") {
            $("#all-games-div").css("overflow-y", "");
            $("#all-games-div").css("max-height", "");
            $("#pin-menu").prev().css("display", "");
            $("#pin-menu").prev().prev().css("display", "none");
        } else {
            $("#pin-menu").prev().css("display", "none");
            $("#pin-menu").prev().prev().css("display", "");
        }
        $("#leftMenuTodaySong").css("display", "none");
    } else if (!isMobileAgent) {
        $("#offcanvas").removeClass("offcanvas");
        var headerDiv = $("#offcanvas").find(".offcanvas-header")[0];
        $(headerDiv).css("display", "none");
        $("#offcanvasSpan").removeAttr("data-bs-toggle");
        $("#offcanvasSpan").css("display", "none");
        $("#filter_games_menu").css("display", "");
        $("#filter_games_menu_pinned").css("display", "none");
        $("#unpin-menu").css("display", "");
        $("#unpin-menu").parent().css("display", "flex");
        $("#pin-menu").prev().css("display", "none");
        $("#pin-menu").prev().prev().css("display", "none");
        var contentDiv = $("#offcanvas").next().next();
        if (contentWidth != null) {
            contentDiv.addClass("col");
            contentDiv.removeClass("col-sm-" + contentWidth);
        }
        $("#leftMenuTodaySong").css("display", "");
        //if we shouw all games, need to introduce the scrollbar to that group display as we dont want game menu to be shown entirely with 1000 or so games
        if (localStorage.getItem("all-games") == "true") {
            $("#all-games-div").css("overflow-y", "scroll");
            //unpin menu button is placed after buttons to trigger alphabetical and normal order
            $("#unpin-menu").prev().css("display", "");
            $("#unpin-menu").prev().prev().css("display", "none");
            //we are in case of non-game page like main one
            if ($("div.centerKeeper").length == 3) {
                $("#all-games-div").css("max-height",
                    ($("div.centerKeeper").first().height() - $("#search-games-bar").height() - $("#allgames-heading").height()) + "px");
            } else {
                //otherwise we want to make menu not too big and not too small depending on how many songs are in the game
                var minVw = 36;
                var maxVw = 144;
                var tenPercentOfWholePageHeight = Math.floor((($("header").height() + $("div.real-page-content").height()) / window.innerHeight * 100) / 20);
                if (tenPercentOfWholePageHeight <= maxVw && tenPercentOfWholePageHeight >= minVw) {
                    $("#all-games-div").css("max-height", tenPercentOfWholePageHeight + "vw");
                } else if (tenPercentOfWholePageHeight > maxVw) {
                    $("#all-games-div").css("max-height", "144vw");
                } else if (tenPercentOfWholePageHeight < minVw) {
                    $("#all-games-div").css("max-height", $("div.parentTabs").height() + "px");
                }
            }
        } else {
            $("#unpin-menu").prev().css("display", "none");
            $("#unpin-menu").prev().prev().css("display", "");
        }
    }
    if ('ontouchstart' in window && isMobileAgent) {
        $("td.info_button").css("display", "none");
        $("th.info_button").css("display", "none");
        $("col.info_button").css("display", "none");
        $(".a-external-music-link").css("display", "none");
        $("td.info_button").css("display", "none");
        $("td.contextButton").css("display", "");
        $("th.contextButton").css("display", "");
        $("col.contextButton").css("display", "");
        $("#pin-menu").css("display", "none");
        $("#offcanvasSpan").text("");
        $("#offcanvasSpan").prev().css("display", "");
        $("#moreDiv").css("position", "absolute");
        $("#moreCountriesDiv").css("position", "absolute");
        $("#filter_games_menu_pinned").css("display", "none");
        $("#sort-games-pinned").css("display", "none");
        $(document).find("header").addClass("sticky-top");
    } else {
        //in case of desktop version putting red text in the very top of the page next to links and logo
        $("#warnUser").css("position", "absolute");
        $("#warnUser").css("margin-top", "1vw");
        var iconsSize = localStorage.getItem("icons-size");
        if (iconsSize != undefined) {
            $(document).find("img.img-responsive-row-icon").css("max-height", iconsSize + "vw");
            $(document).find("svg.img-responsive-row-icon").css("max-height", iconsSize + "vw");
        }
        var iconsToHide = localStorage.getItem("hide-icons");
        if (iconsToHide != undefined && iconsToHide.length > 0) {
            iconsToHide = JSON.parse(iconsToHide);
            for (let i = 0; i < iconsToHide.length; i++) {
                var iconToHide = iconsToHide[i];
                $("a." + iconToHide).hide();
                $("a." + iconToHide).css("display", "none");
                $("img." + iconToHide).hide();
                $("img." + iconToHide).css("display", "none");
                $("svg." + iconToHide).hide();
                $("svg." + iconToHide).css("display", "none");
            }
            if ($($("td.info_button")[0]).children(":visible").length == 0) {
                $("td.info_button").hide();
                $("th.info_button").hide();
            }
        }
        var flagsToHide = localStorage.getItem("hide-flags");
        if (flagsToHide == "true") {
            $("td.countries").hide();
            $("th.countries").hide();
        }
    }
    var langDisplayed = localStorage.getItem("suggest-lang");
    if (langDisplayed == undefined) {
        var userLang = navigator.language || navigator.userLanguage;
        const langs = ["ar", "de", "el", "es", "fr", "hi", "hu", "id", "it", "jp", "pl", "pt", "ru", "tr", "uk", "zh"];
        for (let i = 0; i < langs.length; i++) {
            var thisLang = langs[i];
            if (userLang.indexOf(thisLang) > -1) {
                $.ajax({
                    async: false,
                    type: "GET",
                    url: "/langdisplay/" + userLang,
                    success: function (ooo) {
                        var langDisplay = ooo;
                        $("#appendLang").append(userLang + " (" + langDisplay + ")");
                        $("#langToChange").val(userLang);
                        $("#switchLangModal").modal('show');
                        localStorage.setItem("suggest-lang", true);
                    },
                });
            }
        }
    }
    //we make top menu button active
    if (window.location.href.indexOf("/home") > -1) {
        $("#nfs-top-home").parent().addClass("nfs-top-item-active");
    }
    //we modify width of the left-side menu depending on what's saved in local storage due to change in preferences
    var newWidth = localStorage.getItem("expandable-width");
    if (newWidth != undefined) {
        $("#offcanvas").removeClass("w-25");
        $("#offcanvas").removeClass("w-35");
        $("#offcanvas").removeClass("w-50");
        $("#offcanvas").addClass("w-" + newWidth);
    }
    //if we display all games together, we show such group and hide all normal groups
    var allGames = localStorage.getItem("all-games");
    if (allGames == "true") {
        $("#all-games-group").attr("hidden", false);
        $(".single-serie").attr("hidden", true);
    }
    //pushing content of custom playlist from local storage to related input as it is always rendered by server
    if (localStorage.getItem("custom-playlist") != undefined) {
        var customPlaylistArrayTrigger = JSON.parse(localStorage.getItem("custom-playlist"));
        var jsonEdArrayTrigger = JSON.stringify(customPlaylistArrayTrigger);
        localStorage.setItem("custom-playlist", jsonEdArrayTrigger);
        $("#playlistContent").val(jsonEdArrayTrigger);
    } else {
        //if playlist is empty, we have to make it disabled and show tooltip to user about the reason
        $("#customPlaylistSubmit").prop("disabled", true);
        $("#customPlaylistSubmit").parent().parent().attr("data-toggle", "tooltip");
        $("#customPlaylistSubmit").parent().parent().attr("data-placement", "top");
        $("#customPlaylistSubmit").parent().parent().attr("data-bs-original-title", $("#customPlaylistSubmit").attr("data-tooltip"));
        $("#customPlaylistSubmit").parent().parent().tooltip();
    }
    //if user does not want 'modal' render then we disable this modal
    var useRowDisplay = localStorage.getItem("video-rendering-stuff");
    if (useRowDisplay == undefined || useRowDisplay == "true") {
        $(document).find(".play_icon").each(function (e) {
            $(this).removeAttr("data-bs-target");
            $(this).removeAttr("data-bs-toggle");
        });
    }
    //making tooltip for 'search' as user will not know by default about using of quotation marks
    $("#searchStuff").tooltip({ 'trigger': 'focus', 'title': $("#searchStuff").attr("data-tooltip") });
    //resetting value of game filtering
    $("#filter_games_menu").val("");
    $("#filter_games_menu_pinned").val("");
    //triggering all declared tooltips to show up
    $('[data-toggle="tooltip"]').tooltip();
    //if we are in custom playlist mode then we make button active
    var currentGame = window.location.pathname;
    if (currentGame.indexOf("custom/playlist") > -1) {
        $("#customPlaylistSubmit").parent().parent().addClass("nfs-top-item-active");
    } else {
        //then depending on value we active top menu
        $(document).find("a.list-group-item[href='" + currentGame + "']").each(function (e) {
            if (!$(this).hasClass("nav-link")) {
                if ($(this).hasClass("genreLink")) {
                    return;
                }
                if (currentGame.indexOf('game/')==-1){
                    return;
                }
                $(this).addClass("active");
                //moved logic to separate function as it sometimes just didn't fire because of lack of delay
                var aHrefElem = $(this);
                setTimeout(function () {
                    rollGamesDuringPageLoad(aHrefElem);
                }, delay);
            } else {
                $(this).parent().addClass("nfs-top-item-active");
            }
        });
    }
}

//here we scroll all-games group to the game that we are in right now
//since we can have either static menu or expandable, need to handle this too
function rollGamesDuringPageLoad(gameAHrefElem) {
    if (!$("#all-games-group").attr("hidden")) {
        if (localStorage.getItem("static-leftmenu") == "true") {
            scrollTopAfterLoad = gameAHrefElem.offset().top + gameAHrefElem.height() * -5 + $("#search-games-bar").height() * -1;
            $($("a.active")[1]).parent().parent().parent()
                .animate({
                    scrollTop: scrollTopAfterLoad
                }, 0)
        } else {
            scrollTopAfterLoad = gameAHrefElem.offset().top + gameAHrefElem.height() * -5 + $("div.offcanvas-header").height() * -1
            $($("a.active")[1]).parent().parent().parent().parent().parent().parent()
                .animate({
                    scrollTop: scrollTopAfterLoad
                }, 0)
        }
    } else {
        gameAHrefElem.parent().parent().parent().parent().find("button").click();
        setTimeout(function () {
            scrollTopAfterLoad = $("#accordionExample").offset().top;
        }, 1000);
    }
}

$(document).ready(function () {
    doLoadingCrap();

    /**
     * method to remove duplicate countries from column because i couldn't develop it on backend in a way to return only distinct countries
     */
    $(document).find("td.countries").each(function () {
        var imgsOfCountries = $(this).children();
        var arrayOfLinks = [];
        for (let i = 0; i < imgsOfCountries.length; i++) {
            //since image has now this a-href parent, need to change this
            var srcOfImg = imgsOfCountries[i].children[0].src;
            if (arrayOfLinks.indexOf(srcOfImg) == -1) {
                arrayOfLinks.push(srcOfImg);
            } else {
                $(imgsOfCountries[i]).remove();
            }
        }
    });

    /**
     * method to handle clicking on 'play' button
     */
    $(".play_icon").on("click", function () {
        var sameSongClicked = false;
        if (window.matchMedia("(pointer: coarse)").matches) {
            let newSrc = $(this).attr("src").replace("znakwodny", "znakwodny2");
            $(this).attr("src", newSrc);
        }
        if (lastActivePlayButton != null) {
            let lastActiveSrc = lastActivePlayButton.attr("src").replace("znakwodny2", "znakwodny");
            lastActivePlayButton.attr("src", lastActiveSrc);
            if ($(this).attr("data-tagVideo")
                .localeCompare(lastActivePlayButton.attr("data-tagVideo")) == 0) {
                sameSongClicked = true;
            }
            lastActivePlayButton = $(this);
        }
        else {
            lastActivePlayButton = $(this);
        }
        var useRowDisplay = localStorage.getItem("video-rendering-stuff");
        //if user wants to have video in a 'row'
        if (useRowDisplay == undefined || useRowDisplay == "true") {
            //if there was already video active we have to remove such row
            var existingTr = $("#listen-music");
            if (existingTr.length != 0) {
                existingTr.remove();
            }
            if (!sameSongClicked) {
                //then we create row with video and lyrics
                var videoToUse = $(this).attr("data-tagvideo");
                var lyricsHtmlElem = $(this).next().next();
                var songSubgroupId = lyricsHtmlElem.attr("data-songsubgroup-id");
                var parentTr = $(this).parent().parent().parent();
                
                // Load lyrics via AJAX if not already loaded
                if (lyricsHtmlElem.text() === "" && lyricsHtmlElem.attr("data-lyricsState") !== "instrumental") {
                    // Show video immediately with loading indicator
                    createVideoRow(videoToUse, '<i style="color: #888;">Loading lyrics...</i>', lyricsHtmlElem, parentTr);
                    
                    loadLyrics(songSubgroupId, lyricsHtmlElem, function(lyricsText) {
                        // Update lyrics in existing row
                        updateLyricsInRow(lyricsText, lyricsHtmlElem);
                    });
                } else {
                    var lyricsText = lyricsHtmlElem.text();
                    createVideoRow(videoToUse, lyricsText, lyricsHtmlElem, parentTr);
                }
            } else {
                let newSrc = $(this).attr("src").replace("znakwodny2", "znakwodny");
                $(this).attr("src", newSrc);
                lastActivePlayButton = null;
            }
        } else {
            //otherwise we render modal with video and show lyrics if there are any
            $("#lyricsCollapse").empty();
            var lyricsHtmlElem = $(this).next().next();
            var songSubgroupId = lyricsHtmlElem.attr("data-songsubgroup-id");
            var lyricsTxt = lyricsHtmlElem.text();
            
            // Load lyrics via AJAX if not already loaded
            if (lyricsTxt === "" && lyricsHtmlElem.attr("data-lyricsState") !== "instrumental") {
                // Show loading indicator immediately
                $("#showLyrics").text("Loading lyrics...");
                $("#showLyrics").prop("disabled", true);
                
                loadLyrics(songSubgroupId, lyricsHtmlElem, function(lyrics) {
                    handleModalLyrics(lyrics, lyricsHtmlElem);
                });
            } else {
                handleModalLyrics(lyricsTxt, lyricsHtmlElem);
            }
        }

    });

    /**
     * function to load lyrics via AJAX
     */
    function loadLyrics(songSubgroupId, lyricsElement, callback) {
        $.ajax({
            url: "/lyrics/" + songSubgroupId,
            type: "GET",
            success: function(lyrics) {
                lyricsElement.text(lyrics);
                callback(lyrics);
            },
            error: function() {
                lyricsElement.text(""); // Clear loading state
                callback(""); // Empty if failed to load
            }
        });
    }

    /**
     * function to update lyrics in existing video row
     */
    function updateLyricsInRow(lyricsText, lyricsHtmlElem) {
        var noLyricsAtAll = false;
        if (lyricsHtmlElem.attr("data-lyricsState") == "instrumental") {
            lyricsText = '<h5 style="word-wrap: break-word;">' + lyricsHtmlElem.attr("data-instrumental") + '</h5>';
            noLyricsAtAll = true;
        } else if (lyricsText == "" || lyricsText == "null") {
            lyricsText = '<h5 style="word-wrap: break-word;">' + lyricsHtmlElem.attr("data-noLyrics") + '</h5>';
            noLyricsAtAll = true;
        }
        
        // Update the lyrics in the existing row
        $("#lyrics_main p").html(lyricsText);
        
        // Adjust layout for mobile if no lyrics
        if (noLyricsAtAll && 'ontouchstart' in window && isMobileAgent) {
            $("#lyrics_main").width("20%");
            $("#ytrow").width("76%");
        }
    }


    /**
     * function to create video row with lyrics
     */
    function createVideoRow(videoToUse, lyricsText, lyricsHtmlElem, parentTr) {
        var noLyricsAtAll = false;
        if (lyricsHtmlElem.attr("data-lyricsState") == "instrumental") {
            lyricsText = '<h5 style="word-wrap: break-word;">' + lyricsHtmlElem.attr("data-instrumental") + '</h5>';
            noLyricsAtAll = true;
        } else if (lyricsText == "" || lyricsText == "null") {
            lyricsText = '<h5 style="word-wrap: break-word;">' + lyricsHtmlElem.attr("data-noLyrics") + '</h5>';
            noLyricsAtAll = true;
        }
        
        var newTr = $('<tr id="listen-music">');
        newTr.attr("data-el_id", parentTr.attr("data-el_id"));
        newTr.attr("data-group_id", parentTr.attr("data-group_id"));
        var visibleTh = parentTr.parent().parent().find('th:visible').length;
        var newTd = $('<td colspan="' + visibleTh + '" id="listen-music-id">');
        var iframeToPut = $('<iframe id="ytrow" frameborder="0">');
        var lyricsDiv = $('<div id="lyrics_main">');
        var clearDiv = $('<div style="clear:both;">');
        var pElem = $('<p>');
        
        if (noLyricsAtAll && 'ontouchstart' in window && isMobileAgent) {
            lyricsDiv.width("20%");
            iframeToPut.width("76%");
        }
        
        iframeToPut.attr("src", videoToUse + "?autoplay=1&amp;autohide=0&amp;theme=light&amp;wmode=transparent");
        iframeToPut.attr("allow", "autoplay");
        pElem.append(lyricsText);
        lyricsDiv.append(pElem);
        newTd.append(iframeToPut);
        newTd.append(lyricsDiv);
        newTd.append(clearDiv);
        newTr.append(newTd);
        newTr.insertAfter(parentTr);
    }

    /**
     * method to show 'active' and 'unactive' play button
     */
    $('.play_icon').mouseover(function () {
        if (!window.matchMedia("(pointer: coarse)").matches) {
            $(this).attr("src", $(this).attr("src").replace("znakwodny", "znakwodny2"));
        }
        // }
    }).mouseout(function () {
        if (!window.matchMedia("(pointer: coarse)").matches) {
            $(this).attr("src", $(this).attr("src").replace("znakwodny2", "znakwodny"));
        }
    });

    /**
     * method to filter songs in table
     */
    $("#filter_songs").on("keyup", function () {
        var searchValue = $(this).val().toLowerCase();
        if (searchValue.length > 3) {
            //if filter input is over 3 letters, then we check if band or songtitle matches the input and keep row visible
            $("table").find("tr:has(td):not(.subgroup-separator)").filter(function () {
                if ($(this).find("td.band").text().toLowerCase().indexOf(searchValue) > -1 || $(this).find("td.songtitle").text().toLowerCase().indexOf(searchValue) > -1) {
                    $(this).show();
                }
                else {
                    $(this).hide();
                }
            });
        } else {
            //otherwise for less-equal to 3 letters we show everything again
            $("table").find("tr:has(td)").filter(function () {
                $(this).show();
            });
        }
    });

    /**
     * function when youtube video modal gets show trigger
     */
    $('#videoModal').on('show.bs.modal', function (e) {
        var myImgSource = e.relatedTarget;
        //we set play image back to the normal one and push video id to the variable
        $(myImgSource).attr("src", $(myImgSource).attr("src").replace("znakwodny2", "znakwodny"));
        baseVideoSrc = $(myImgSource).attr("data-tagVideo") + "?autoplay=1&amp;modestbranding=1&amp;showinfo=0";
        //then we do the same stuff with lyrics situation
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
        //and we apply the embed src video attribute to the modal
        $("#video").attr('src', baseVideoSrc);
    });

    /**
     * function to unload video modal when hiding it
     */
    $('#videoModal').on('hide.bs.modal', function (e) {
        $("#video").attr('src', '');
    });

    /**
     * function to load disqus comments
     */
    $('#disqusModal').on('show.bs.modal', function (e) {
        var disqusTarget = e.relatedTarget.id;
        var linkToUse;
        var srcToUse;
        //for the beginning legacy time, there will be link to new and old disqus stuff so we have to handle that one might want to render archived or new comments
        if (disqusTarget == "newDisqusLink") {
            linkToUse = window.location.href;
            srcToUse = "racingsoundtracks";

        } else {
            linkToUse = $("#disqusModal").attr("data-disqusLink");
            srcToUse = "nfssoundtrack";
        }
        /**
         * if disqus was not yet loaded we load it by injecting this javascript inside
         */
        if ($("#disqus_thread").children().length == 0) {
            var script = document.createElement('script');
            script.innerHTML = "var disqus_config = function () {     this.page.url = '" + linkToUse + "';  }; (function () {  var d = document, s = d.createElement('script'); s.type = 'text/javascript'; s.async = true; s.src = 'https://" + srcToUse + ".disqus.com/embed.js'; s.setAttribute('data-timestamp', +new Date());(d.head || d.body).appendChild(s);})();";
            var noscript = document.createElement('noscript');
            noscript.innerHTML = 'Please enable JavaScript to view the <a href="https://disqus.com/?ref_noscript">comments powered by Disqus.</a>';
            $("#disqus_thread").append(script);
            $("#disqus_thread").append(noscript);
        }
        else {
            //otherwise we reload it just for safety to be sure the right link is used
            DISQUS.reset({
                reload: true,
                config: function () {
                    this.forum = srcToUse;
                    this.page.url = linkToUse;
                }
            });
        }
    });

    /**
     * function to initialize playlist mode when modal starts showing up
     */
    $('#playlistModeModal').on('show.bs.modal', function (e) {
        initPlayer(0);
    });

    /**
     * function to handle modal width/height then modal has been loaded
     */
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

    /**
     * function to play video when it is ready
     * @param {yt event} event 
     */
    function onPlayerReady(event) {
        event.target.playVideo();
    }
    /**
     * function to go to next video in playlist
     * @param {yt event} event 
     */
    function onPlayerStateChange(event) {
        if (event.data === 0) {
            initPlayer(current_id + 1);
        }
    }

    /**
     * function to render playlist mode
     * @param {id of video} changeId 
     */
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
                    trElem.append('<td class="playlist_play_it"><img class="img-responsive-playlist-modal-icon pointable" src="/images/znakwodny.webp"></td>');
                    trElem.append('<td class="playlist_row">' + bandText + ' - ' + titleText + '</td>');
                    trElem.append('<td class="playlist_disable_song"><img class="img-responsive-playlist-modal-icon pointable" src="/images/fullres/trashcan_big.webp"></td>');
                    $("#playlist_progress").append(trElem);
                    localI++;
                }
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
            if (current_id == 0) {
                $("#errorThing").parent().fadeIn(500, function () {
                    setTimeout(function () {
                        $("#errorThing").parent().fadeOut(500);
                    }, 3000);
                });
            }
            initPlayer(current_id + 1);
        } else {
            $('#playlistModePlayer').html('<iframe id="player" allow="autoplay" type="text/html" src="https://www.youtube.com/embed/' + data_song[current_id] + '?enablejsapi=1&autoplay=1&autohide=0&theme=light&wmode=transparent" frameborder="0"></iframe>');

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

    /**
     * function to trigger playing specific video from playlist if it is not disabled
     */
    $(document).on("click", "td.playlist_play_it", function () {
        if (!$(this).parent().hasClass("disabled")) {
            initPlayer(this.parentNode.id);
        }
    });

    /**
     * function to mark song as disabled when in playlist mode
     */
    $(document).on("click", "td.playlist_disable_song", function () {
        if ($(this).parent().hasClass("disabled")) {
            $(this).parent().removeClass("disabled");
        } else {
            $(this).parent().addClass("disabled");
        }
    });

    /**
     * function to handle adding song to custom playlist
     */
    $(document).on("click", "img.add-to-custom-playlist", function () {
        var relatedTr = $(this).parent().parent();
        var songSubgroupId = $(relatedTr).attr("data-songSubgroup-id");
        addToCustomPlaylist(songSubgroupId);
    });

    function addToCustomPlaylist(songSubgroupId) {
        var customPlaylistArray = localStorage.getItem("custom-playlist");
        if (customPlaylistArray == undefined) {
            customPlaylistArray = [];
        }
        else if (customPlaylistArray != undefined) {
            customPlaylistArray = JSON.parse(customPlaylistArray);
        }
        //we check the id of song we want to add to custom playlist
        customPlaylistArray.push(songSubgroupId);
        var jsonEdArray = JSON.stringify(customPlaylistArray);
        //and then we simply store it in playlist + show confirmation alert that it worked
        localStorage.setItem("custom-playlist", jsonEdArray);
        $("#playlistContent").val(jsonEdArray);
        $("#customPlaylistSubmit").prop("disabled", false);
        $("#customPlaylistSubmit").parent().parent().tooltip('dispose');
        $("#successAddToCustomPlaylist").parent().fadeIn(500, function () {
            setTimeout(function () {
                $("#successAddToCustomPlaylist").parent().fadeOut(500);
            }, 3000);
        });
    }

    $(document).on("click", "button.justHideAlert", function (e) {
        e.preventDefault();
        $(this).parent().parent().fadeOut(100);
    });

    /**
     * function to render modal with song information
     */
    $(document).on("click", "img.info-about-song", function () {
        //handling search case
        var potentialTd = $(this).parent();
        if (potentialTd.hasClass("info_button")) {
            var trElem = $(this).parent().parent();
            $(this).tooltip('dispose');
            var songIdAttr = $(trElem).attr("data-song_id");
            var additionalInfoA = $(trElem).find("td.infowarn>span");
            var filenameInfo = $(trElem).find("td.infowarn>span");
            var infoLabel = null;
            var filenameLabel = null;
            if (additionalInfoA.length > 0) {
                infoLabel = additionalInfoA.attr("aria-label");
            }
            if (filenameInfo.length > 0) {
                filenameLabel = filenameInfo.text();
            }
            fetchInfoSong(songIdAttr, infoLabel, filenameLabel);
        } else {
            var aWithSongId = $(this).next();
            $(this).tooltip('dispose');
            var songIdAttr = $(aWithSongId).attr("data-song_id");
            fetchInfoSong(songIdAttr, null, null);
        }
    });

    /**
         * function to render modal with song information
         */
    $(document).on("click", "svg.report-bug", function () {
        //handling search case
        var potentialTd = $(this).parent();
        if (potentialTd.hasClass("info_button")) {
            var trElem = $(this).parent().parent();
            var cloneOfTr = trElem.clone();
            $(this).tooltip('dispose');
            var songSubgroupIdAttr = $(cloneOfTr).attr("data-songsubgroup-id");
            $("#affected-songsubgroup").val(songSubgroupIdAttr);
            $("#source-url").val(window.location.href);
            cloneOfTr.find(".info_button").remove();
            $("#affected-songsubgroup").prev().remove();
            cloneOfTr.insertBefore($("#affected-songsubgroup"));
            $("#reportProblemModal").modal('show');
        }
    });

    $(document).on("click", "#reportProblemMobile", function () {
        //handling search case
        var songSubgroupIdAttr = $(this).attr("data-songsubgroup-id");
        var relatedTr = $("tbody").find("tr[data-songSubgroup-id='" + songSubgroupIdAttr + "']");
        var cloneOfTr = relatedTr.clone();
        $("#affected-songsubgroup").val(songSubgroupIdAttr);
        $("#source-url").val(window.location.href);
        $("#affected-songsubgroup").prev().remove();
        cloneOfTr.insertBefore($("#affected-songsubgroup"));
        $("#mobile_context").removeClass("show").hide();
        $("#reportProblemModal").modal('show');
    });

    $(document).on("click", "#reportGeneralProblem", function () {
        $("#affected-songsubgroup").val(-1);
        $("#source-url").val(window.location.href);
        $("#reportProblemModal").modal('show');
        $("#problem-type").val("OTHER-PROBLEM").change();
    });

    /**
    * function to render modal with song information
    */
    function fetchInfoSong(songIdAttr, infoLabel, filenameLabel) {
        $("#getAllUsages").attr("href", "/song/" + Number(songIdAttr));
        //we have to call server to provide info about the song
        $.ajax({
            async: false,
            type: "GET",
            url: "/songInfo/" + songIdAttr,
            success: function (ooo) {
                var songInfo = JSON.parse(ooo);
                //if all good then we dynamically fill this modal with received information
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
                if (songInfo.genres.length != 0) {
                    $("#genres").parent().find(">:not(#genres)").each(function () {
                        $(this).remove();
                    });
                    for (let i = songInfo.genres.length - 1; i >= 0; i--) {
                        $(songInfo.genres[i]).insertAfter($("#genres"));
                    }
                }
                if (songInfo.youtube != null) {
                    $(songInfo.youtube).insertAfter("#externalLinks");
                }
                if (infoLabel != null) {
                    var infoSpan = $("<span>");
                    infoSpan.append(infoLabel);
                    $(infoSpan).insertAfter("#additionalInfo");
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
                if (songInfo.tidal != null) {
                    $(songInfo.tidal).insertAfter("#externalLinks");
                }
                if (songInfo.soundcloud != null) {
                    $(songInfo.soundcloud).insertAfter("#externalLinks");
                }
                if (filenameLabel != null) {
                    var infoSpan = $("<span>");
                    infoSpan.append(filenameLabel);
                    $(infoSpan).insertAfter("#filename");
                }
                if (songInfo.baseSongId != null) {
                    $(songInfo.baseSongId).insertAfter("#baseSong");
                    $("#baseSongDiv").css("display", "");
                }
                //and we finally show the modal
                $("#infoSongModal").modal('show');
            },
            error: function (ooo) {
                console.log("e2");
                console.log(ooo);
            },
        });
    }

    $(document).on("touchstart", "td:not(.infowarn)", function (e) {
        if ('ontouchstart' in window && isMobileAgent) {
            $(document).find("div.tooltip").hide();
        }
    });

    $(document).on("touchstart", "#dynamic_bg", function (e) {
        if ('ontouchstart' in window && isMobileAgent) {
            $(document).find("div.tooltip").hide();
        }
    });

    $(document).on("touchstart", "td.infowarn", function (e) {
        e.preventDefault();
        $(this).children().tooltip('show');
    });

    $(document).on("touchstart", "#moreCountriesDiv", function (e) {
        if (e.target.id.localeCompare("moreCountriesDiv") == 0) {
            if ($("#moreCountriesDiv").hasClass("show")) {
                $("#moreCountriesDiv").removeClass("show").hide();
            }
        }
    });

    $(document).on("touchstart", "#moreDiv", function (e) {
        if (e.target.id.localeCompare("moreDiv") == 0) {
            if ($("#moreDiv").hasClass("show")) {
                $("#moreDiv").removeClass("show").hide();
            }
        }
    });

    $(document).on("touchstart", "svg.context_menu_thing", function (e) {
        e.preventDefault();
        var trElem = $(this).parent().parent();
        var songIdAttr = $(trElem).attr("data-song_id");
        var songSubgroupIdAttr = $(trElem).attr("data-songsubgroup-id");
        var externalLinks = $(trElem).find("a.a-external-music-link");
        $("#mobileAddToPlaylist").attr("data-songsubgroup-id", songSubgroupIdAttr);
        $("#reportProblemMobile").attr("data-songsubgroup-id", songSubgroupIdAttr);
        $("#mobileRemoveFromPlaylist").attr("data-songsubgroup-id", songSubgroupIdAttr);
        $("#mobileShowSongInfo").attr("data-song_id", songIdAttr);
        $("#playSpotifySample").attr("data-songsubgroup-id", songSubgroupIdAttr);
        var additionalInfoA = $(trElem).find("td.infowarn>a");
        var infoLabel = null;
        var filenameLabel = null;
        if (additionalInfoA.length > 0) {
            infoLabel = additionalInfoA.attr("aria-label");
            $("#mobileShowSongInfo").attr("infoLabel", infoLabel);
        }
        var additionalFileName = $(trElem).find("td.infowarn>span");
        if (additionalFileName.length > 0) {
            filenameLabel = additionalFileName.text();
            $("#mobileShowSongInfo").attr("filenameLabel", filenameLabel);
        }
        var youtubeElem = $(trElem).find("a.externalYT");
        var spotifyElem = null;
        var deezerElem = null;
        var itunesElem = null;
        var tidalElem = null;
        var soundCloudElem = null;
        for (let i = 0; i < externalLinks.length; i++) {
            var actualAObj = $(externalLinks[i]);
            if (actualAObj.attr("href").indexOf("spotify:") > -1) {
                spotifyElem = actualAObj;
            } else if (actualAObj.attr("href").indexOf("deezer") > -1) {
                deezerElem = actualAObj;
            } else if (actualAObj.attr("href").indexOf("music.apple.com") > -1) {
                itunesElem = actualAObj;
            } else if (actualAObj.attr("href").indexOf("tidal.com") > -1) {
                tidalElem = actualAObj;
            } else if (actualAObj.attr("href").indexOf("soundcloud.com") > -1) {
                soundCloudElem = actualAObj;
            }
        }
        if (youtubeElem.length > 0) {
            $("#mobileExternalYoutube").attr("href", youtubeElem.attr("href"));
        } else {
            $("#mobileExternalYoutube").css("display", "none");
        }
        if (spotifyElem != null) {
            $("#mobileLaunchSpotify").attr("href", spotifyElem.attr("href"));
            $("#playSpotifySample").css("display", "");
        } else {
            $("#mobileLaunchSpotify").css("display", "none");
            $("#playSpotifySample").css("display", "none");
        }
        if (deezerElem != null) {
            $("#mobileLaunchDeezer").attr("href", deezerElem.attr("href"));
        } else {
            $("#mobileLaunchDeezer").css("display", "none");
        }
        if (itunesElem != null) {
            $("#mobileLaunchItunes").attr("href", itunesElem.attr("href"));
        } else {
            $("#mobileLaunchItunes").css("display", "none");
        }
        if (tidalElem != null) {
            $("#mobileLaunchTidal").attr("href", tidalElem.attr("href"));
        } else {
            $("#mobileLaunchTidal").css("display", "none");
        }
        if (soundCloudElem != null) {
            $("#mobileLaunchSoundCloud").attr("href", soundCloudElem.attr("href"));
        } else {
            $("#mobileLaunchSoundCloud").css("display", "none");
        }
        if ($("#mobile_context").hasClass("show")) {
            $("#mobile_context").removeClass("show").hide();
            $("#mobileLaunchSpotify").css("display", "");
            $("#playSpotifySample").css("display", "");
            $("#mobileLaunchItunes").css("display", "");
            $("#mobileLaunchDeezer").css("display", "");
            $("#mobileLaunchTidal").css("display", "");
            $("#mobileLaunchSoundCloud").css("display", "");
            $("#mobileExternalYoutube").css("display", "");
            if (window.location.href.indexOf("/game/") > -1) {
                $("#newDisqusLinkToOpen").css("display", "");
                $("#newDisqusLinkToOpen").attr("data-bs-toggle", "modal");
                $("#newDisqusLinkToOpen").attr("data-bs-target", "#disqusModal");
            }
        } else {
            var top = e.originalEvent.touches[0].pageY + (window.innerHeight * 0.02);
            var left = e.originalEvent.touches[0].pageX + 15;
            $("#mobile_context").css({
                position: "absolute",
                display: "block",
                top: top,
                left: left
            }).addClass("show");
            return false; //blocks default Webbrowser right click menu
        }
    });

    $(document).on("touchstart", "#newDisqusLinkToOpen", function (e) {
        //$("#newDisqusLink").toggle('show');
    });

    $(document).on("touchstart", "tr", function (e) {
        $("#mobile_context").removeClass("show").hide();
        $("#mobileLaunchSpotify").css("display", "");
        $("#playSpotifySample").css("display", "");
        $("#mobileLaunchItunes").css("display", "");
        $("#mobileLaunchDeezer").css("display", "");
        $("#mobileLaunchTidal").css("display", "");
        $("#mobileLaunchSoundCloud").css("display", "");
    });

    $("#mobileAddToPlaylist").on("click", function (e) {
        var songSubgroupId = $(this).attr("data-songSubgroup-id");
        addToCustomPlaylist(songSubgroupId);
        $("#mobile_context").removeClass("show").hide();
        $("#mobileLaunchSpotify").css("display", "");
        $("#playSpotifySample").css("display", "");
        $("#mobileLaunchItunes").css("display", "");
        $("#mobileLaunchDeezer").css("display", "");
        $("#mobileLaunchTidal").css("display", "");
        $("#mobileLaunchSoundCloud").css("display", "");
    });

    $("#mobileShowSongInfo").on("click", function (e) {
        var songIdAttr = $(this).attr("data-song_id");
        var infoLabel = $(this).attr("infoLabel");
        var filenameLabel = $(this).attr("filenameLabel");
        fetchInfoSong(songIdAttr, infoLabel, filenameLabel);
        $("#mobile_context").removeClass("show").hide();
        $("#mobileLaunchSpotify").css("display", "");
        $("#playSpotifySample").css("display", "");
        $("#mobileLaunchItunes").css("display", "");
        $("#mobileLaunchDeezer").css("display", "");
        $("#mobileLaunchTidal").css("display", "");
        $("#mobileLaunchSoundCloud").css("display", "");
    });

    $("#playSpotifySample").on("click", function (e) {
        var linkToUse = $("#mobileLaunchSpotify")[0].href;
        var songSubgroupId = Number($(e.target).attr("data-songsubgroup-id"));
        if ($("#spotify-iframeapi-script").length > 0) {
            playSpotifySampleMobile(linkToUse, songSubgroupId);
        }
        else {
            var script = document.createElement('script');
            script.src = "https://open.spotify.com/embed/iframe-api/v1";
            document.head.appendChild(script);
            setTimeout(function () {
                playSpotifySampleMobile(linkToUse, songSubgroupId);
            }, 1000);
        }
        $("#mobile_context").removeClass("show").hide();
        $("#mobileLaunchSpotify").css("display", "");
        $("#playSpotifySample").css("display", "");
        $("#mobileLaunchItunes").css("display", "");
        $("#mobileLaunchDeezer").css("display", "");
        $("#mobileLaunchTidal").css("display", "");
        $("#mobileLaunchSoundCloud").css("display", "");
    });

    $("a.single_action").on("click", function (e) {
        $("#mobile_context").removeClass("show").hide();
        $("#mobileLaunchSpotify").css("display", "");
        $("#playSpotifySample").css("display", "");
        $("#mobileLaunchItunes").css("display", "");
        $("#mobileLaunchDeezer").css("display", "");
        $("#mobileLaunchTidal").css("display", "");
        $("#mobileLaunchSoundCloud").css("display", "");
    });

    $("#pin-menu").on("click", function (e) {
        $("#closeInHeader").click();
        $("#offcanvas").removeClass("offcanvas");
        var headerDiv = $("#offcanvas").find(".offcanvas-header")[0];
        $(headerDiv).css("display", "none");
        $("#offcanvasSpan").removeAttr("data-bs-toggle");
        $("#offcanvasSpan").css("display", "none");
        $("#unpin-menu").css("display", "");
        $("#unpin-menu").parent().css("display", "flex");
        //search inputs are duplicated on page so we need to hide the right one when we switch that pin/unpin view
        $("#filter_games_menu_pinned").css("display", "none");
        $("#filter_games_menu").css("display", "");
        localStorage.setItem("static-leftmenu", true);
        var contentDiv = $("#offcanvas").next().next();
        var contentWidth = localStorage.getItem("content-width");
        if (contentWidth != null) {
            contentDiv.addClass("col");
            contentDiv.removeClass("col-sm-" + contentWidth);
        }
        $("#leftMenuTodaySong").css("display", "");
        //we again scroll the group content since pin/unpin menu will reset the scroll
        if (localStorage.getItem("all-games") == "true") {
            $("#all-games-div").css("overflow-y", "scroll");
            $("#unpin-menu").prev().css("display", "");
            if ($("div.centerKeeper").length == 3) {
                $("#all-games-div").css("max-height", $("div.centerKeeper").first().height() + "px");
            } else {
                var minVw = 36;
                var maxVw = 144;
                var tenPercentOfWholePageHeight = Math.floor((($("header").height() + $("div.real-page-content").height()) / window.innerHeight * 100) / 10);
                if (tenPercentOfWholePageHeight <= maxVw && tenPercentOfWholePageHeight >= minVw) {
                    $("#all-games-div").css("max-height", tenPercentOfWholePageHeight + "vw");
                } else if (tenPercentOfWholePageHeight > maxVw) {
                    $("#all-games-div").css("max-height", "144vw");
                } else if (tenPercentOfWholePageHeight < minVw) {
                    $("#all-games-div").css("max-height", $("div.parentTabs").height() + "px");
                }
            }
            $($("a.active")[1]).parent().parent().parent()
                .animate({
                    scrollTop: $($("a.active")[1]).offset().top + $($("a.active")[1]).height() * -5 + $("#search-games-bar").height() * -2
                }, 0)
        } else {
            $("#unpin-menu").prev().prev().css("display", "");
        }
    });

    $("#unpin-menu").on("click", function (e) {
        $("#offcanvas").addClass("offcanvas");
        var headerDiv = $("#offcanvas").find(".offcanvas-header")[0];
        $(headerDiv).css("display", "");
        $("#offcanvasSpan").attr("data-bs-toggle", "offcanvas");
        $("#offcanvasSpan").css("display", "");
        $("#unpin-menu").css("display", "none");
        $("#unpin-menu").parent().css("display", "");
        //just see what happens in case of clicking to pin menu, it's basically other way around
        $("#unpin-menu").prev().css("display", "none");
        $("#unpin-menu").prev().prev().css("display", "none");
        $("#filter_games_menu_pinned").css("display", "");
        $("#filter_games_menu").css("display", "none");
        localStorage.setItem("static-leftmenu", false);
        var contentDiv = $("#offcanvas").next().next();
        var contentWidth = localStorage.getItem("content-width");
        if (contentWidth != null) {
            contentDiv.addClass("col-sm-" + contentWidth);
            contentDiv.removeClass("col");
        }
        $("#leftMenuTodaySong").css("display", "none");
        if (localStorage.getItem("all-games") == "true") {
            $("#all-games-div").css("overflow-y", "");
            $("#all-games-div").css("max-height", "");
            $("#pin-menu").prev().css("display", "");
            $("#pin-menu").prev().prev().css("display", "none");
            $($("a.active")[1]).parent().parent().parent().parent().parent().parent()
                .animate({
                    scrollTop: $($("a.active")[1]).offset().top + $($("a.active")[1]).height() * -3 + $("div.offcanvas-header").height() * -1
                }, 0)
        } else {
            $("#pin-menu").prev().css("display", "none");
            $("#pin-menu").prev().prev().css("display", "");
        }
    });

    //when we decide to group games normally, we hide this big group and show the other button, to display games alphabetically
    //again since these buttons appear twice in the canvas, we use this to show the right one
    $("button.grouped-games").on("click", function (e) {
        localStorage.setItem("all-games", false);
        $(this).prev().css("display", "");
        $("button.grouped-games").css("display", "none");
        $("#all-games-group").attr("hidden", true);
        $(".single-serie").attr("hidden", false);
    });

    $("button.alphabetical-games").on("click", function (e) {
        localStorage.setItem("all-games", true);
        $("button.alphabetical-games").css("display", "none");
        $(this).next().css("display", "");
        $("#all-games-group").attr("hidden", false);
        $(".single-serie").attr("hidden", true);
        if (localStorage.getItem("static-leftmenu") == "true") {
            if ($("div.centerKeeper").length == 3) {
                $("#all-games-div").css("max-height", ($("div.centerKeeper").first().height() - $("#search-games-bar").height() - $("#allgames-heading").height()) + "px");
            } else {
                var minVw = 36;
                var maxVw = 144;
                var tenPercentOfWholePageHeight = Math.floor((($("header").height() + $("div.real-page-content").height()) / window.innerHeight * 100) / 20);
                if (tenPercentOfWholePageHeight <= maxVw && tenPercentOfWholePageHeight >= minVw) {
                    $("#all-games-div").css("max-height", tenPercentOfWholePageHeight + "vw");
                } else if (tenPercentOfWholePageHeight > maxVw) {
                    $("#all-games-div").css("max-height", "144vw");
                } else if (tenPercentOfWholePageHeight < minVw) {
                    $("#all-games-div").css("max-height", $("div.parentTabs").height() + "px");
                }
            }
        }
    });

    $(document).on("click", "img.30-player", function (elem) {
        //        var spotifyIframe = $('<iframe style="border-radius:12px" src="https://open.spotify.com/embed/track/6p7CD2XsLUVI88aB6rCaXr?utm_source=generator" width="100%" height="352" frameBorder="0" allowfullscreen="" allow="autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture"></iframe>');
        if ($("#spotify-iframeapi-script").length > 0) {
            playSpotifySample(elem, false);
        }
        else {
            var script = document.createElement('script');
            script.src = "https://open.spotify.com/embed/iframe-api/v1";
            document.head.appendChild(script);
            setTimeout(function () {
                playSpotifySample(elem, false);
            }, 1000);
        }
    });

    function playSpotifySample(elem) {
        var spotifyToPlay = elem.target.previousElementSibling.href;
        if (currentlyPlayedSpotify != null) {
            $(currentlyPlayedSpotify.nextElementSibling).css("display", "none");
            $(currentlyPlayedSpotify).css("display", "");
            spotifyController.togglePlay();
        }
        if (elem.target.src.indexOf("play") > -1) {
            //gonna play the song
            $(elem.target.nextElementSibling).css("display", "");
            $(elem.target).css("display", "none");
        } else {
            $(elem.target.previousElementSibling).css("display", "");
            $(elem.target).css("display", "none");
            spotifyController.togglePlay();
            currentlyPlayedSpotify = null;
            return;
        }
        currentlyPlayedSpotify = elem.target;
        if (spotifyController != undefined) {
            spotifyController.loadUri(spotifyToPlay);
            spotifyController.play();
        } else {
            var divForSpotify = $("#embed-iframe");
            if ($("#embed-iframe").length == 0) {
                divForSpotify = $('<div id="embed-iframe"></div>');
            }
            $(elem.target.parentElement).append(divForSpotify);
            $(divForSpotify).css("display", "none");
            const element = divForSpotify[0];
            const options = {
                width: '0',
                height: '0',
                uri: spotifyToPlay,
            };
            const callback = (EmbedController) => {
                spotifyController = EmbedController;
                EmbedController.addListener('ready', () => {
                    console.log('The Embed has initialized ' + new Date().toString());
                });
                EmbedController.addListener('playback_update', e => {
                    if (e.data.duration != 0) {
                        if (e.data.duration == e.data.position) {
                            if (currentlyPlayedSpotify) {
                                $(currentlyPlayedSpotify.nextElementSibling).css("display", "none");
                                $(currentlyPlayedSpotify).css("display", "");
                                currentlyPlayedSpotify = null;
                            }
                        }
                    }
                });
            };
            //            console.log("before creating controller " + console.log(new Date().toString()));
            spotifyApi.createController(element, options, callback);
            //            console.log("after creating controller " + console.log(new Date().toString()));
            spotifyController.play();
            //            console.log("after play trigger " + console.log(new Date().toString()));
        }
    }

    function playSpotifySampleMobile(spotifyToPlay, snogSubgroupId) {
        if (currentlyPlayedSpotify != null) {
            spotifyController.togglePlay();
        }
        currentlyPlayedSpotify = spotifyToPlay;
        if (spotifyController != undefined) {
            spotifyController.loadUri(spotifyToPlay);
            spotifyController.play();
        } else {
            var divForSpotify = $("#embed-iframe");
            if ($("#embed-iframe").length == 0) {
                divForSpotify = $('<div id="embed-iframe"></div>');
            }
            $("tr[data-songsubgroup-id=" + snogSubgroupId + "]").append(divForSpotify);
            //            $("#mobile_context").append(divForSpotify);
            const element = divForSpotify[0];
            const options = {
                width: '0%',
                height: '0',
                uri: spotifyToPlay
            };
            const callback = (EmbedController) => {
                spotifyController = EmbedController;
                EmbedController.addListener('ready', () => {
                    console.log('The Embed has initialized ' + new Date().toString());
                });
                EmbedController.addListener('playback_update', e => {
                    if (e.data.duration != 0) {
                        if (e.data.duration == e.data.position) {
                            if (currentlyPlayedSpotify) {
                                currentlyPlayedSpotify = null;
                            }
                        }
                    }
                });
            };
            //            console.log("before creating controller " + console.log(new Date().toString()));
            spotifyApi.createController(element, options, callback);
            //            console.log("after creating controller " + console.log(new Date().toString()));
            spotifyController.play();
            //            console.log("after play trigger " + console.log(new Date().toString()));
        }
    }
    window.onSpotifyIframeApiReady = (IFrameAPI) => {
        console.log("loaded");
        spotifyApi = IFrameAPI;
    };

    /**
     * function to mark song as disabled when in playlist mode
     */
    $(document).keydown(function (e) {
        if ($("#playlistModeModal").is(":visible")) {
            var currentTr = $("tr.current")[0];
            if (e.keyCode == 40) {
                //down key
                var nextTr;
                while (nextTr == null) {
                    var potentialNextTr = $(currentTr).next();
                    if (!potentialNextTr.hasClass("disabled")) {
                        nextTr = potentialNextTr;
                    } else {
                        currentTr = potentialNextTr;
                    }
                }
                if (nextTr == null) {
                    return;
                }
                var startVideo = $(nextTr).find("td.playlist_play_it");
                if (startVideo.length > 0) {
                    startVideo.click();
                    if (!isScrolledIntoView(nextTr)) {
                        $('#playlist_progress').parent().animate({
                            scrollTop: $('#playlist_progress').parent().scrollTop() + nextTr.height() * 10
                        }, 700);
                    }
                }
            } else if (e.keyCode == 38) {
                //up key
                var nextTr;
                while (nextTr == null) {
                    var potentialNextTr = $(currentTr).prev();
                    if (!potentialNextTr.hasClass("disabled")) {
                        nextTr = potentialNextTr;
                    } else {
                        currentTr = potentialNextTr;
                    }
                }
                if (nextTr == null) {
                    return;
                }
                var startVideo = $(nextTr).find("td.playlist_play_it");
                if (startVideo.length > 0) {
                    startVideo.click();
                    if (!isScrolledIntoView(nextTr)) {
                        $('#playlist_progress').parent().animate({
                            scrollTop: $('#playlist_progress').parent().scrollTop() + nextTr.height() * (-1) * 10
                        }, 700);
                    }
                }
            }
        }
    });

    function isScrolledIntoView(elem) {
        var docViewTop = $('#playlist_progress').parent().offset().top;
        var docViewBottom = docViewTop + $('#playlist_progress').parent().height();
        var elemTop = $(elem).offset().top;
        var elemBottom = elemTop + $(elem).height() * 2;
        return ((elemBottom <= docViewBottom) && (elemTop >= docViewTop));
    }
});
