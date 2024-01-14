$(document).ready(function () {
    $("#merge-songs").click(function (e) {
        var divToAppend = $('#nfs-content');
        divToAppend.empty();
        divToAppend.append(successAlertHtml);
        divToAppend.append(failureAlertHtml);
        var rowDiv = $('<div class="row p-1">');
        var leftCellDiv = $('<div class="col">');
        var rightCellDiv = $('<div class="col">');
        divToAppend.append(rowDiv);
        rowDiv.append(leftCellDiv);
        rowDiv.append(rightCellDiv);
        var songToMergeInput = $('<input class="form-control" type="text" id="songMergeInput"/>');
        leftCellDiv.append('<label for="authorMergeInput">Song ID to merge</label>');
        leftCellDiv.append(songToMergeInput);
        var targetSongInput = $('<input class="form-control" type="text" id="targetSongInput"/>');
        rightCellDiv.append('<label for="targetAuthorInput">Target song ID</label>');
        rightCellDiv.append(targetSongInput);
        rowDiv.append('<button id="merge-song" type="submit" class="btn btn-primary">Save</button>');
        rowDiv.append(leftCellDiv);
        rowDiv.append(rightCellDiv);
        divToAppend.append(rowDiv);
    });

    $(document).on('click', "#merge-song", function (e) {
        var songToMerge = $("#songMergeInput").val();
        var targetSong = $("#targetSongInput").val();
        var objToSubmit = {};
        objToSubmit.songToMergeId=songToMerge;
        objToSubmit.targetSongId=targetSong;
        $(successAlertHtml).hide();
        $(failureAlertHtml).hide();
        $.ajax({
            async: false,
            type: "PUT",
            data: JSON.stringify(objToSubmit),
            contentType: 'application/json; charset=utf-8',
            url: "/song/merge",
            success: function (ooo) {
                $(successAlertHtml).fadeTo(2000, 500).slideUp(500, function () {
                    $(successAlertHtml).slideUp(500, function () {
                        var divToAppend = $('#nfs-content');
                        divToAppend.empty();
                    });
                });
            }, error: function (ooo) {
                $(failureAlertHtml).fadeTo(2000, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500, function () {
                        var divToAppend = $('#nfs-content');
                        divToAppend.empty();
                    });
                });
            },
        });
    });
});