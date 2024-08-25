var currentBg = 0;
$(document).ready(function () {
    changeStuffForDynamicTheme();
    $('#hide-icons').multiselect({
        includeSelectAllOption: true,
        templates: {
            button: '<button type="button" class="form-select multiselect dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false"><span class="multiselect-selected-text"></span></button>',
        },
    });
    /**
     * method to save preferences
     */

    $(document).on("click", "#save-preferences", function () {
        /*putting all stuff to the localstorage and reloading page as someone might have decided to change language*/
        localStorage.setItem("expandable-width", $("#expandable-width").val());
        localStorage.setItem("scrolling-stuff", $("#scrolling-stuff").prop("checked"));
        localStorage.setItem("static-leftmenu", $("#static-leftmenu").prop("checked"));
        localStorage.setItem("icons-size", $("#icons-size").val());
        localStorage.setItem("content-width", $("#content-width").val());
        localStorage.setItem("video-rendering-stuff", $("#video-rendering-stuff").prop("checked"));
        localStorage.setItem("hide-icons", JSON.stringify($("#hide-icons").val()));
        localStorage.setItem("hide-flags", $("#hide-flags").prop("checked"));
        localStorage.setItem("all-games", $("#all-games").prop("checked"));
        localStorage.setItem("lang", $("#lang-select").val());
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
        if (staticMenu != undefined) {
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
            currentBg = (Number(localStorage.getItem("dynamic-bg-index")) + 1) % 5;
        } else {
            currentBg = (currentBg + 1) % 5
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
});