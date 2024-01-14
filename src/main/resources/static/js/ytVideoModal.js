$(document).ready(function () {
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
            divWithLyrics.empty();
        } else {
            $(divContainer).removeAttr("style");
            $(divContainer).addClass("col-md-6");
            videoContainer.addClass("col-md-6");
            videoContainer.removeClass("col-md-12");
            $(divContainer).css("max-height", iframeContainer.height());
            $(divContainer).css("overflow-y", "auto");
        }
    });
});