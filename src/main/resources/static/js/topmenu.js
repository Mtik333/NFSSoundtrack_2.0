var currentBg = 0;
$(document).ready(function () {
    changeStuffForDynamicTheme();
    $('#hide-icons').multiselect({
        includeSelectAllOption: true,
        templates: {
            button: '<button type="button" class="form-select multiselect dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false"><span class="multiselect-selected-text"></span></button>',
        },
    });
    if (localStorage.getItem("lang")!=undefined){
        var lang = localStorage.getItem("lang");
        var foundImg = $(document).find("img[data-countryval='"+lang+"']").first()[0].src;
        $("#dropdownCountryImg")[0].src=foundImg;
    } 

    /**
     * method to save preferences
     */

    $(document).on("click", "#save-preferences", function () {
        /*putting all stuff to the localstorage and reloading page as someone might have decided to change language*/
        let localStorageInfo = new Object();
        localStorageInfo.expandableWidth = $("#expandable-width").val();
        localStorageInfo.scrollingStuff = $("#scrolling-stuff").prop("checked");
        localStorageInfo.staticLeftMenu = $("#static-leftmenu").prop("checked");
        localStorageInfo.iconsSize = $("#icons-size").val();
        localStorageInfo.contentWidth = $("#content-width").val();
        localStorageInfo.videoRenderingStuff = $("#video-rendering-stuff").prop("checked");
        localStorageInfo.hideIcons = JSON.stringify($("#hide-icons").val());
        localStorageInfo.hideFlags = $("#hide-flags").prop("checked");
        localStorageInfo.allGames = $("#all-games").prop("checked");
        localStorageInfo.composersFlagsOnly = $("#composers-flags-only").prop("checked");
        localStorageInfo.lang = $("#lang-select").val();
        localStorage.setItem("expandable-width", localStorageInfo.expandableWidth);
        localStorage.setItem("scrolling-stuff", localStorageInfo.scrollingStuff);
        localStorage.setItem("static-leftmenu", localStorageInfo.staticLeftMenu);
        localStorage.setItem("icons-size", localStorageInfo.iconsSize);
        localStorage.setItem("content-width", localStorageInfo.contentWidth);
        localStorage.setItem("video-rendering-stuff", localStorageInfo.videoRenderingStuff);
        localStorage.setItem("hide-icons", localStorageInfo.hideIcons);
        localStorage.setItem("hide-flags", localStorageInfo.hideFlags);
        localStorage.setItem("all-games", localStorageInfo.allGames);
        localStorage.setItem("composers-flags-only", localStorageInfo.composersFlagsOnly);
        localStorage.setItem("lang", $("#lang-select").val());
        $.ajax({
            url: "/preferences",
            type: "POST",
            data: JSON.stringify(localStorageInfo),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function(lyrics) {
                console.log("saved preferences to session");
            },
            error: function() {
                console.log("something wrong in session");
            }
        });
        var langAlreadyThere = window.location.toString().indexOf("?lang") > -1;
        if (langAlreadyThere) {
            window.location.search = "?lang=" + $("#lang-select").val();
        } else {
            window.location = window.location + "?lang=" + $("#lang-select").val();
        }
    });

    $(document).on("click", "#openPreferences", function () {
        var scrolling = localStorage.getItem("scrolling-stuff");
        if (scrolling != undefined) {
            $("#scrolling-stuff").prop("checked", JSON.parse(scrolling));
        }
        var renderingVideo = localStorage.getItem("video-rendering-stuff");
        if (renderingVideo != undefined) {
            $("#video-rendering-stuff").prop("checked", JSON.parse(renderingVideo));
        }
        var hideFlags = localStorage.getItem("hide-flags");
        if (hideFlags != undefined) {
            $("#hide-flags").prop("checked", JSON.parse(hideFlags));
        }
        var staticMenu = localStorage.getItem("static-leftmenu");
        if (staticMenu != undefined) {
            $("#static-leftmenu").prop("checked", JSON.parse(staticMenu));
        }
        var expandWidth = localStorage.getItem("expandable-width");
        if (expandWidth != undefined) {
            $("#expandable-width").val(expandWidth);
        }
        var iconsSize = localStorage.getItem("icons-size");
        if (iconsSize != undefined) {
            $("#icons-size").val(iconsSize);
        }
        var contentWidth = localStorage.getItem("content-width");
        if (contentWidth != undefined) {
            $("#content-width").val(contentWidth);
        }
        var lang = localStorage.getItem("lang");
        if (lang != undefined) {
            $("#lang-select").val(lang);
        }
        var hideIcons = JSON.parse(localStorage.getItem("hide-icons"));
        if (hideIcons != undefined) {
            $("#hide-icons").multiselect('select', hideIcons);
        }
        var allGames = localStorage.getItem("all-games");
        if (allGames != undefined) {
            $("#all-games").prop("checked", JSON.parse(allGames));
        }
        var composersFlagsOnly = localStorage.getItem("composers-flags-only");
        if (composersFlagsOnly != undefined) {
            $("#composers-flags-only").prop("checked", JSON.parse(composersFlagsOnly));
        }
    });

    /**
     * function to switch night mode (when you click outside checkbox) and save it in local storage
     */
    $("#nightModeSwitch").click(function (e) {
        e.preventDefault();
        localStorage.setItem("dark-mode", !$(this).prev().prop("checked"));
        $(this).prev().click();
    });

    $("#sunlight").click(function (e) {
        e.preventDefault();
        localStorage.setItem("dark-mode", true);
        localStorage.removeItem("dynamic-bg");
        $("#moonlight").css("display", "");
        $("#sunlight").css("display", "none");
        changeStuffForDarkMode();
        changeStuffForDynamicTheme();
        if (typeof DISQUS != "undefined") {
            DISQUS.reset({ reload: true });
        }
    });

    $("#moonlight").click(function (e) {
        e.preventDefault();
        localStorage.setItem("dark-mode", false);
        localStorage.removeItem("dynamic-bg");
        $("#moonlight").css("display", "none");
        $("#sunlight").css("display", "");
        changeStuffForDarkMode();
        changeStuffForDynamicTheme();
        if (typeof DISQUS != "undefined") {
            DISQUS.reset({ reload: true });
        }
    });
    /**
     * function to switch night mode (when you click inside checkbox) and reload disqus due to that
     */
    $("#flexSwitchCheckDefault").change(function (e) {
        localStorage.setItem("dark-mode", $(this).prop("checked"));
        changeStuffForDarkMode();
        if (typeof DISQUS != "undefined") {
            DISQUS.reset({ reload: true });
        }
    });

    $(document).on('click', '#dynamic_bg', function (e) {
        e.preventDefault();
        if (localStorage.hasOwnProperty("dynamic-bg")) {
            currentBg = (Number(localStorage.getItem("dynamic-bg-index")) + 1) % 6;
        } else {
            currentBg = (currentBg + 1) % 6;
        }
        if (currentBg == 0) {
            localStorage.removeItem("dynamic-bg");
            localStorage.setItem("dynamic-bg-index", 0);
            changeStuffForDarkMode();
            changeStuffForDynamicTheme();
        } else {
            $.ajax({
                async: false,
                type: "GET",
                url: "/dynamictheme/" + currentBg,
                success: function (ooo) {
                    localStorage.setItem("dynamic-bg", ooo);
                    localStorage.setItem("dynamic-bg-index", currentBg);
                    var theme = JSON.parse(ooo);
                    changeStuffForDarkMode(theme.nightMode);
                    changeStuffForDynamicTheme();
                },
            });
        }
    });

    $(document).on("click", "a.translate-link", function () {
        var langValue = $(this).children().first().attr("data-countryval");
        localStorage.setItem("lang", langValue);
        var langAlreadyThere = window.location.toString().indexOf("?lang") > -1;
        if (langAlreadyThere) {
            window.location.search = "?lang=" + langValue;
        } else {
            window.location = window.location + "?lang=" + langValue;
        }
    });
});