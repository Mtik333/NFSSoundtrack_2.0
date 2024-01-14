$(document).ready(function () {
    $('#infoSongModal').on('hide.bs.modal', function (e) {
        $("#officialArtist").contents().filter(function () {
            return this.nodeType === 3; //Node.TEXT_NODE
        }).each(function () {
            $(this).remove();
        });
        $("#officialTitle").contents().filter(function () {
            return this.nodeType === 3;
        }).each(function () {
            $(this).remove();
        });
        $("#composers").parent().find("a").each(function () {
            $(this).remove();
        });
        $("#subcomposers").parent().find("a").each(function () {
            $(this).remove();
        });
        $("#remixers").parent().find("a").each(function () {
            $(this).remove();
        });
        $("#featArtists").parent().find("a").each(function () {
            $(this).remove();
        });
        $("#externalLinks").parent().find("a").each(function () {
            $(this).remove();
        });
        $("#baseSong").parent().find("a").each(function () {
            $(this).remove();
        });
        $("#baseSongDiv").css("display", "none");
    });
});