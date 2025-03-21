$(document).ready(function () {
    /**
     * method to show only games that match input filter
     * @param {value typed by user as game title} searchValue 
     */
    function funcToShowOnlyFilteredGames(searchValue) {
        //so we go through 'series' buttons
        $(document).find("button.gamegroup-button").filter(function () {
            //each serie button has own "div" to show
            var divId = $(this).attr("data-bs-target");
            //if input is inside serie name string then we show it
            if ($(this).text().toLowerCase() > -1 && !$(divId).hasClass("show")) {
                $(this).click();
            } else {
                //otherwise we find all "games" from this series
                var allA = $(divId).find("a");
                var somethingFound = false;
                for (let i = 0; i < allA.length; i++) {
                    //we iterate over 'full title' of each game and check if user input is within this string
                    var gameDisplayTitle = $(allA[i]).attr("data-fullTitle");
                    if (gameDisplayTitle.toLowerCase().indexOf(searchValue) > -1) {
                        somethingFound = true;
                        //if so then we show this game
                        $(allA[i]).show();
                    } else {
                        $(allA[i]).hide();
                    }
                }
                //if there's at least one game that matches the filter, we have to show series
                if (somethingFound) {
                    if (!$(divId).is(":visible")) {
                        $(divId).parent().show();
                        //if it was already shown, then just some magic to re-show it
                        if (!$(divId).hasClass("show")) {
                            $(this).click();
                            $(this).removeClass("collapsed");
                        }
                    } else {
                        //otherwise, we reveal the series displayed afte page loaded since it contains the game we are in
                        $(divId).addClass("show");
                    }
                } else {
                    //otherwise we hide series entirely
                    $(divId).parent().hide();
                    $(divId).removeClass("show");
                }
            }
        });
    }

    /**
     * 
     * @param {text} searchValue The user input text
     */
    function funcToShowOnlyFilteredGamesSingleGroup(searchValue) {
        //should be only one button for one big group
        $(document).find("button.all-gamegroup-button").filter(function () {
            //in this big group there will be also dedicated div
            var divId = $("#all-games-div");
            //we find all "games" from this series
            var allA = $(divId).find("a");
            var somethingFound = false;
            for (let i = 0; i < allA.length; i++) {
                //we iterate over 'full title' of each game and check if user input is within this string
                var gameDisplayTitle = $(allA[i]).attr("data-fullTitle");
                if (gameDisplayTitle.toLowerCase().indexOf(searchValue) > -1) {
                    somethingFound = true;
                    //if so then we show this game
                    $(allA[i]).show();
                } else {
                    $(allA[i]).hide();
                }
            }
        });
    }
    /**
     * we go through divs of series
     */
    $(document).find("div.accordion-collapse").each(function () {
        $(this).on('shown.bs.collapse', function () {
            //if user disabled scrolling menu on click then we ignore this stuff
            var unscrollableMenu = localStorage.getItem("scrolling-stuff");
            if (unscrollableMenu === "false" || unscrollableMenu === null) {
                //if filtering by game name is not active or shorter than 3
                //as either pinned/unpinned mode is enabled it's better to check both search inputs
                if (($("#filter_games_menu").val() == "" || $("#filter_games_menu").val().length < 3)
                    && ($("#filter_games_menu_pinned").val() == "" || $("#filter_games_menu_pinned").val().length < 3)) {
                    //we simply check how far we are from top and scroll menu so that it is "on" top of the series menu
                    var currentScroll = $('div.offcanvas-body').scrollTop();
                    var origTop = $('div.offcanvas-body').offset().top;
                    var thisTop = $(this).offset().top;
                    var scrollTop;
                    if (currentScroll > 0) {
                        scrollTop = currentScroll + thisTop - origTop - $(this).parent().children().first().height();
                    } else {
                        scrollTop = thisTop - origTop - $(this).parent().children().first().height();
                    }
                    //and we do some animation but maybe i should remove it?
                    $('div.offcanvas-body').animate({
                        scrollTop: scrollTop
                    }, 500);
                }
            }
        });
    });
    /**
     * function when someone types letters in 'filter games' input
     * doing some timeout to avoid immediate reaction
     */
    $("#filter_games_menu").on("keyup", function () {
        var textInput = $(this);
        if (timeout) {
            clearTimeout(timeout);
        }
        timeout = setTimeout(function () {
            filterGames(textInput);
        }, delay);
    });

    //now that we have two search inputs we have to register listener on both
    $("#filter_games_menu_pinned").on("keyup", function () {
        var textInput = $(this);
        if (timeout) {
            clearTimeout(timeout);
        }
        timeout = setTimeout(function () {
            filterGames(textInput);
        }, delay);
    });

    function filterGames(info) {
        //to avoid issues when one-group is active, new method was introduced to handle this case
        if (localStorage.getItem("all-games") == "true") {
            return filterAllGamesGroup(info);
        }
        var searchValue = info.val().toLowerCase();
        //if serach value is over 3 letters
        if (searchValue.length > 3) {
            $(document).find("button.gamegroup-button").filter(function () {
                var divId = info.attr("data-bs-target");
                if ($(divId).hasClass("show")) {
                    info.click();
                    //actually should listen to event of collapsing this shit and then show all filtered stuff
                }
            });
            //doing some timeout of this filter function as some events take longer than expected
            setTimeout(function () {
                funcToShowOnlyFilteredGames(searchValue);
                //we scroll to top of list so user can really go through all possible games matching criteria
                $('div.offcanvas-body').animate({
                    scrollTop: 0
                }, 500);
            }, 400);
        } else {
            var buttonToClick;
            var divToShow;
            //otherwise we basically have to show all games again
            $(document).find("button.gamegroup-button").filter(function () {
                var divId = $(this).attr("data-bs-target");
                //if we are on 'game' page there will be one active element in series list
                if ($(divId).find("a.active").length > 0) {
                    //so we definitely have to show it + related series
                    var activeA = $($(divId).find("a.active")[0]);
                    $(divId).find("a").filter(function () {
                        $(this).show();
                    });
                    //this "show" method does 
                    $(divId).parent().show();
                    $(divId).addClass("show");
                    var mainDivForMenu = $(this).parent().parent().parent().parent();
                    $(this).parent().parent().parent().parent().animate({
                        scrollTop: mainDivForMenu.offset().top + scrollTopAfterLoad * (-1)
                    }, 500)
                } else {
                    //otherwise we have to collapse series but show all games within the series
                    $(this).collapse('hide');
                    $(divId).find("a").filter(function () {
                        info.show();
                    });
                    $(divId).parent().show();
                    $(divId).collapse('hide');
                    //not sure what this is doing, i guess we want to then expand the series
                    if ($(divId).find("a.active").length > 0) {
                        buttonToClick = info;
                        divToShow = divId;
                    }
                }
            });
            //this is probably about expanding the series that had active game before
            if (divToShow != undefined) {
                $(info.attr("data-bs-target")).parent().show();
                if ($(divToShow).hasClass("collapsed")) {
                    buttonToClick.click();
                }
            }
        }
    }

    //filtering similarly as in previous method
    function filterAllGamesGroup(info) {
        var searchValue = info.val().toLowerCase();
        //if serach value is over 3 letters
        if (searchValue.length > 3) {
            //doing some timeout of this filter function as some events take longer than expected
            setTimeout(function () {
                funcToShowOnlyFilteredGamesSingleGroup(searchValue);
            }, 400);
        } else {
            var buttonToClick;
            var divToShow;
            //otherwise we basically have to show all games again
            $(document).find("button.all-gamegroup-button").filter(function () {
                var divId = $("#all-games-div");
                //if we are on 'game' page there will be one actie element in series list
                $(divId).find("a").filter(function () {
                    $(this).show();
                });
            });
        }
    }
});