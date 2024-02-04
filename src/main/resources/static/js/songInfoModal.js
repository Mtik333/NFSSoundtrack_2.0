$(document).ready(function () {
    /**
     * method to remove all info appended to modal when modal is closed
     */
    $('#infoSongModal').on('hide.bs.modal', function (e) {
        //we just iterate over stuff and remove elements depending on type
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
        $("#filename").next("span").each(function () {
            $(this).remove();
        });
        $("#remixers").parent().find("a").each(function () {
            $(this).remove();
        });
        $("#genres").parent().find("a").each(function () {
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