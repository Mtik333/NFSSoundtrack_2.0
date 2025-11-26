$(document).ready(function () {
    $("#merge-alias-with-artist").click(function (e) {
        var divToAppend = $('#nfs-content');
        divToAppend.empty();
        divToAppend.append(successAlertHtml);
        divToAppend.append(failureAlertHtml);
        var buttonDiv = $('<div class="row p-1">');
        var buttonCol = $('<div class="col">');
        var rowDiv = $('<div class="row p-1">');
        var leftCellDiv = $('<div class="col">');
        var rightCellDiv = $('<div class="col">');
        divToAppend.append(rowDiv);
        rowDiv.append(leftCellDiv);
        rowDiv.append(rightCellDiv);
        var authorToMergeSelect = $('<input class="form-control" id="authorMergeInput"/>');
        var authorToMergeSelectSelectHidden = $('<input type="hidden" id="authorMergeInputHidden"/>');
        leftCellDiv.append('<label for="authorMergeInput">Author to merge</label>');
        leftCellDiv.append(authorToMergeSelect);
        leftCellDiv.append(authorToMergeSelectSelectHidden);
        setupAutocompleteMergeArtist(authorToMergeSelect, authorToMergeSelectSelectHidden, "");
        var targetAuthorSelect = $('<input class="form-control" id="targetAuthorInput"/>');
        var targetAuthorSelectSelectHidden = $('<input type="hidden" id="targetAuthorInputHidden"/>');
        rightCellDiv.append('<label for="targetAuthorInput">Target author</label>');
        rightCellDiv.append(targetAuthorSelect);
        rightCellDiv.append(targetAuthorSelectSelectHidden);
        setupAutocompleteMergeArtist(targetAuthorSelect, targetAuthorSelectSelectHidden, "");
        buttonCol.append('<button id="merge-artist" type="submit" class="btn btn-primary">Save</button>');
        buttonCol.append('<input type="checkbox" class="form-check-input m-3" id="mergeDeleteAlias"><label class="form-check-label pl-2 pt-2" for="propagate">Delete alias?</label>');
        buttonDiv.append(buttonCol);
        rowDiv.append(buttonDiv);
        rowDiv.append(leftCellDiv);
        rowDiv.append(rightCellDiv);
        divToAppend.append(rowDiv);
    });

    $(document).on('click', "#merge-artist", function (e) {
        var authorToMerge = $("#authorMergeInputHidden").val();
        var targetAuthor = $("#targetAuthorInputHidden").val();
        var mergeDeleteAlias = $("#mergeDeleteAlias").prop("checked");
        var objToSubmit = {};
        objToSubmit.authorToMergeId = Number(authorToMerge);
        objToSubmit.targetAuthorId = Number(targetAuthor);
        objToSubmit.mergeDeleteAlias = mergeDeleteAlias;
        $(successAlertHtml).hide();
        $(failureAlertHtml).hide();
        $.ajax({
            async: false,
            type: "PUT",
            data: JSON.stringify(objToSubmit),
            contentType: 'application/json; charset=utf-8',
            url: "/author/merge",
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

function setupAutocompleteMergeArtist(mySelect, mySelectHidden, valueToSet) {
    mySelect.autocomplete({
        source: function (request, response, url) {
            $.ajax({
                async: false,
                type: "GET",
                url: "/author/authorNameMgmt/" + encodeURIComponent($(mySelect).val().replace("/","__")),
                success: function (ooo) {
                    var foundArtist = JSON.parse(ooo);
                    if (foundArtist) {
                        response(foundArtist);
                    }
                },
                error: function (ooo) {
                    $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                        $(failureAlertHtml).slideUp(500);
                    });
                },
            });
        },
        select: function (event, ui) {
            event.preventDefault();
            $(mySelect).val(ui.item.label);
            $(mySelect).text(ui.item.label);
            $(mySelectHidden).val(ui.item.value);
        },
        minLength: 1
    });
}
