$(document).ready(function () {
    $(document).on("click", "#change-lang-yes", function () {
        window.location = window.location + "?lang=" + $("#langToChange").val();
    });
    $(document).on("click", "#change-lang-no", function () {
        window.location = window.location + "?lang=en";
    });
});