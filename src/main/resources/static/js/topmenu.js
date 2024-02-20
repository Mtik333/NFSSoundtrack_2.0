$(document).ready(function () {
    /**
     * method to save preferences
     */
    $(document).on("click", "#save-preferences", function () {
        //putting all stuff to the localstorage and reloading page as someone might have decided to change language
        localStorage.setItem("expandable-width", $("#expandable-width").val());
        localStorage.setItem("scrolling-stuff", $("#scrolling-stuff").prop("checked"));
        localStorage.setItem("static-leftmenu", $("#static-leftmenu").prop("checked"));
        localStorage.setItem("icons-size", $("#icons-size").val());
        localStorage.setItem("video-rendering-stuff", $("#video-rendering-stuff").prop("checked"));
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
});