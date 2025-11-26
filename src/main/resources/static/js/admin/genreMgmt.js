var foundGenre;

$(document).ready(function () {
    $("#manage-genres").click(function (e) {
        var divToAppend = $('#nfs-content');
        divToAppend.empty();
        divToAppend.append(successAlertHtml);
        divToAppend.append(failureAlertHtml);
        var buttonDiv = $('<div class="row p-1">');
        var buttonCol = $('<div class="col">');
        var rowDiv = $('<div class="row p-1">');
        var leftCellDiv = $('<div class="col">');
        divToAppend.append(rowDiv);
        rowDiv.append(leftCellDiv);
        var genreSelect = $('<input class="form-control" id="genreInput"/>');
        var genreSelectDescription = $('<textarea class="form-control" id="genreSelectDescription"/>');
        var genreSelectHidden = $('<input type="hidden" id="genreInputHidden"/>');
        leftCellDiv.append('<label for="genreInput">Genre</label>');
        leftCellDiv.append(genreSelect);
        leftCellDiv.append(genreSelectHidden);
        leftCellDiv.append('<label for="genreSelectDescription">Genre description</label>');
        leftCellDiv.append(genreSelectDescription);
        setupAutocompleteManageGenre(genreSelect, genreSelectHidden, "");
        buttonCol.append('<button id="save-genre" type="submit" class="btn btn-primary">Save</button>');
        buttonCol.append('<button id="cancel-genre" type="submit" class="btn btn-warning">Cancel</button>');
        buttonDiv.append(buttonCol);
        rowDiv.append(buttonDiv);
        rowDiv.append(leftCellDiv);
        divToAppend.append(rowDiv);
    });

    $(document).on('click', "#cancel-genre", function (e) {
        var divToAppend = $('#nfs-content');
        divToAppend.empty();
    });
});

function setupAutocompleteManageGenre(mySelect, mySelectHidden, valueToSet) {
    mySelect.autocomplete({
        source: function (request, response, url) {
            $.ajax({
                async: false,
                type: "GET",
                url: "/genre/genreName/" + $(mySelect).val(),
                success: function (ooo) {
                    foundGenre = JSON.parse(ooo);
                    if (foundGenre) {
                        response(foundGenre);
                    }
                },
                error: function (ooo) {
                    $(failureAlertHtml).show().fadeTo(500, 500).slideUp(500, function () {
                        $(failureAlertHtml).slideUp(500).hide();
                    });
                },
            });
        },
        select: function (event, ui) {
            event.preventDefault();
            $(mySelect).val(ui.item.label);
            $(mySelect).text(ui.item.label);
            $(mySelectHidden).val(ui.item.value);
            $("#genreSelectDescription").val(ui.item.desc);
        },
        minLength: 1
    });
}

$(document).on('click', '#save-genre', function (e) {
    var genreToSave = new Object();
    genreToSave.genreId = $("#genreInputHidden").val();
    genreToSave.genreName = $("#genreInput").val();
    genreToSave.description = $("#genreSelectDescription").val();
    $.ajax({
        async: false,
        type: "PUT",
        data: JSON.stringify(genreToSave),
        url: "/genre/put/"+genreToSave.genreId,
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        success: function (ooo) {
            $(successAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                $(successAlertHtml).slideUp(500).hide();
                var divToAppend = $('#nfs-content');
                divToAppend.empty();
            });
        },
        error: function (ooo) {
            $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                $(failureAlertHtml).slideUp(500).hide();
                var divToAppend = $('#nfs-content');
                divToAppend.empty();
            });

        },
    });
});