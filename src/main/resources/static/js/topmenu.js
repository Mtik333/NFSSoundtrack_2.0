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
        //putting all stuff to the localstorage and reloading page as someone might have decided to change language
        localStorage.setItem("expandable-width", $("#expandable-width").val());
        localStorage.setItem("scrolling-stuff", $("#scrolling-stuff").prop("checked"));
        localStorage.setItem("static-leftmenu", $("#static-leftmenu").prop("checked"));
        localStorage.setItem("icons-size", $("#icons-size").val());
        localStorage.setItem("content-width", $("#content-width").val());
        localStorage.setItem("video-rendering-stuff", $("#video-rendering-stuff").prop("checked"));
        localStorage.setItem("hide-icons", JSON.stringify($("#hide-icons").val()));
        localStorage.setItem("hide-flags", $("#hide-flags").prop("checked"));
        var langAlreadyThere = window.location.toString().indexOf("?lang") > -1;
        if (langAlreadyThere) {
            window.location.search = "?lang=" + $("#lang-select").val();
        } else {
            window.location = window.location + "?lang=" + $("#lang-select").val();
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