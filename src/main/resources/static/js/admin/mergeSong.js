var successAlertHtml = '<div class="alert alert-success" id="success-alert" style="display: none;"><strong>Success!</strong></div>';
var foundArtist;

$(document).ready(function () {
    $("#merge-songs").click(function (e) {
        var divToAppend = $('#nfs-content');
        divToAppend.empty();
        divToAppend.append(successAlertHtml);
        var rowDiv = $('<div class="row p-1">');
        //var countriesRowDiv = $('<div id="countriesRow" class="row p-1">');
        //var aliasesRow = $('<div id="aliasesRow" class="row p-1">');
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
        //divToAppend.append(aliasesRow);
        //divToAppend.append(countriesRowDiv);
    });

    $(document).on('click', "#merge-song", function (e) {
        var songToMerge = $("#songMergeInput").val();
        var targetSong = $("#targetSongInput").val();
        var objToSubmit = new Object();
        objToSubmit.songToMergeId=songToMerge;
        objToSubmit.targetSongId=targetSong;
        $('#success-alert').hide();
        $.ajax({
            async: false,
            type: "PUT",
            data: JSON.stringify(objToSubmit),
            contentType: 'application/json; charset=utf-8',
            url: "/song/merge",
            success: function (ooo) {
                console.log("eee");
                $('#success-alert').fadeTo(2000, 500).slideUp(500, function () {
                    $('#success-alert').slideUp(500, function () {
                        var divToAppend = $('#nfs-content');
                        divToAppend.empty();
                    });
                });
            }, error: function (ooo) {
                console.log("e2");
            },
            done: function (ooo) {
                console.log("e3");
            }
        });
    });
});