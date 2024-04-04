$(document).ready(function () {
    $("#merge-duplicate-artist").click(function (e) {
        var divToAppend = $('#nfs-content');
        divToAppend.empty();
        divToAppend.append(successAlertHtml);
        divToAppend.append(failureAlertHtml);
        var rowDiv = $('<div class="row p-1">');
        var leftCellDiv = $('<div class="col">');
        divToAppend.append(rowDiv);
        rowDiv.append(leftCellDiv);
        var songToMergeInput = $('<input class="form-control" type="text" id="duplicateArtistInput"/>');
        leftCellDiv.append('<label for="authorMergeInput">Artist name to fix duplicate</label>');
        leftCellDiv.append(songToMergeInput);
        rowDiv.append('<button id="fix-artist" type="submit" class="btn btn-primary">Save</button>');
        rowDiv.append(leftCellDiv);
        divToAppend.append(rowDiv);
    });

    $(document).on('click', "#fix-artist", function (e) {
            var artistToMerge = $("#duplicateArtistInput").val();
            var objToSubmit = {};
            objToSubmit.artistName = artistToMerge;
            $(successAlertHtml).hide();
            $(failureAlertHtml).hide();
            $.ajax({
                async: false,
                type: "PUT",
                data: JSON.stringify(objToSubmit),
                contentType: 'application/json; charset=utf-8',
                url: "/author/fixDuplicate",
                success: function (ooo) {
                    $(successAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                        $(successAlertHtml).slideUp(500, function () {
                            var divToAppend = $('#nfs-content');
                            divToAppend.empty();
                        });
                    });
                }, error: function (ooo) {
                    $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                        $(failureAlertHtml).slideUp(500, function () {
                            var divToAppend = $('#nfs-content');
                            divToAppend.empty();
                        });
                    });
                },
            });
        });

});