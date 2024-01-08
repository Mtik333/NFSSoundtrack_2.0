var successAlertHtml = '<div class="alert alert-success" id="success-alert" style="display: none;"><strong>Success!</strong></div>';
var foundArtist;

$(document).ready(function () {
    $("#merge-alias-with-artist").click(function (e) {
        var divToAppend = $('#nfs-content');
        divToAppend.empty();
        divToAppend.append(successAlertHtml);
        var buttonDiv = $('<div class="row p-1">');
        var buttonCol = $('<div class="col">');
        var rowDiv = $('<div class="row p-1">');
        //var countriesRowDiv = $('<div id="countriesRow" class="row p-1">');
        //var aliasesRow = $('<div id="aliasesRow" class="row p-1">');
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
        buttonDiv.append(buttonCol);
        rowDiv.append(buttonDiv);
        rowDiv.append(leftCellDiv);
        rowDiv.append(rightCellDiv);
        divToAppend.append(rowDiv);
        //divToAppend.append(aliasesRow);
        //divToAppend.append(countriesRowDiv);
    });

    $(document).on('click', "#merge-artist", function (e) {
        var authorToMerge = $("#authorMergeInputHidden").val();
        var targetAuthor = $("#targetAuthorInputHidden").val();
        var objToSubmit = new Object();
        objToSubmit.authorToMergeId=authorToMerge;
        objToSubmit.targetAuthorId=targetAuthor;
        $('#success-alert').hide();
        $.ajax({
            async: false,
            type: "PUT",
            data: JSON.stringify(objToSubmit),
            contentType: 'application/json; charset=utf-8',
            url: "/author/merge",
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

function setupAutocompleteMergeArtist(mySelect, mySelectHidden, valueToSet) {
    mySelect.autocomplete({
        source: function (request, response, url) {
            $.ajax({
                async: false,
                type: "GET",
                url: "/author/authorNameMgmt/" + $(mySelect).val(),
                success: function (ooo) {
                    foundArtist = JSON.parse(ooo);
                    response(foundArtist);
                },
                error: function (ooo) {
                    console.log("e2");
                },
                done: function (ooo) {
                    console.log("e3");
                }
            });
        },
        select: function (event, ui) {
            event.preventDefault();
            $(mySelect).val(ui.item.label);
            $(mySelect).text(ui.item.label);
            $(mySelectHidden).val(ui.item.value);

        },
        minLength: 0
    });
}
