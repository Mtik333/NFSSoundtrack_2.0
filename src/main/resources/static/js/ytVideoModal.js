$(document).ready(function () {
    /**
     * function to show lyrics in video modal as by default lyrics are not showing up
     */
    $(document).on('click', '#showLyrics', function (e) {
        var divWithLyrics = $("#lyricsCollapse");
        var divContainer = divWithLyrics.parent();
        var videoContainer = divContainer.prev();
        var iframeContainer = videoContainer.children().first();
        if ($(divContainer).hasClass("col-md-6")) {
            //this is actually hiding lyrics and making video take whole modal
            $(divContainer).removeAttr("style");
            $(divContainer).removeClass("col-md-6");
            $(divContainer).css("display", "none");
            videoContainer.removeClass("col-md-6");
            videoContainer.addClass("col-md-12");
            divWithLyrics.empty();
        } else {
            //we make lyrics show up so lyrics and video column area equal probably
            $(divContainer).removeAttr("style");
            $(divContainer).addClass("col-md-6");
            videoContainer.addClass("col-md-6");
            videoContainer.removeClass("col-md-12");
            $(divContainer).css("max-height", iframeContainer.height());
            $(divContainer).css("overflow-y", "auto");
        }
    });

    /**
     * function to handle lyrics display in modal
     */
    window.handleModalLyrics = function(lyricsTxt, lyricsHtmlElem) {
        //if lyrics value is null or empty, then there's nothing
        if (lyricsTxt == "null" || lyricsTxt == "") {
            $("#showLyrics").text("Lyrics not found");
            $("#showLyrics").prop("disabled", true);
            //if lyrics value is 0 then it is instrumental
        } else if (lyricsHtmlElem.attr("data-lyricsState") == "instrumental") {
            $("#showLyrics").text("This is instrumental, no lyrics");
            $("#showLyrics").prop("disabled", true);
        } else {
            //otherwise just show lyrics
            $("#lyricsCollapse").append(lyricsTxt);
            $("#showLyrics").prop("disabled", false);
        }
    };
});