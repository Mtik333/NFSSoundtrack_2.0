$(document).ready(function () {
    function funcToShowOnlyFilteredGames(searchValue) {
        $(document).find("button.gamegroup-button").filter(function () {
            var divId = $(this).attr("data-bs-target");
            if ($(this).text().toLowerCase() > -1 && !$(divId).hasClass("show")) {
                $(this).click();
            } else {
                var allA = $(divId).find("a");
                var somethingFound = false;
                for (let i = 0; i < allA.length; i++) {
                    var gameDisplayTitle = $(allA[i]).attr("data-fullTitle");
                    if (gameDisplayTitle.toLowerCase().indexOf(searchValue) > -1) {
                        somethingFound = true;
                        $(allA[i]).show();
                    } else {
                        $(allA[i]).hide();
                    }
                }
                if (somethingFound) {
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
                    var scrollTop;
                    if (currentScroll > 0) {
                        scrollTop = currentScroll + thisTop - origTop - $(this).parent().children().first().height();
                    } else {
                        scrollTop = thisTop - origTop - $(this).parent().children().first().height();
                    }
                    $('div.offcanvas-body').animate({
                        scrollTop: scrollTop
                    }, 500);
                }
            }
        });
    });
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

});