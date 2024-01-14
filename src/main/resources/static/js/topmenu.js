$(document).ready(function () {
    $(document).on("click", "#save-preferences", function () {
        localStorage.setItem("expandable-width", $("#expandable-width").val());
        localStorage.setItem("scrolling-stuff", $("#scrolling-stuff").prop("checked"));
        localStorage.setItem("video-rendering-stuff", $("#video-rendering-stuff").prop("checked"));
        var langAlreadyThere = window.location.toString().indexOf("?lang") > -1;
        if (langAlreadyThere) {
            window.location.search = "?lang=" + $("#lang-select").val();
        } else {
            window.location = window.location + "?lang=" + $("#lang-select").val();
        }
    });
    $("#nightModeSwitch").click(function (e) {
        e.preventDefault();
        localStorage.setItem("dark-mode", !$(this).prev().prop("checked"));
        $(this).prev().click();
    });
    $("#flexSwitchCheckDefault").change(function (e) {
        localStorage.setItem("dark-mode", $(this).prop("checked"));
        changeStuffForDarkMode();
        if (typeof DISQUS != "undefined") {
            DISQUS.reset({ reload: true });
        }
    });
});