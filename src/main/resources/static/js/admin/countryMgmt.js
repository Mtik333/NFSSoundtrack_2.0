function CountryDef(gameId, position) {
    this.gameId = gameId;
    this.position = position;
}

var currentlyEditedCountryId;
$(document).ready(function () {
    $(successAlertHtml).hide();
    $(failureAlertHtml).hide();

    function getAllCountriesAndDisplay() {
        $.ajax({
            async: false,
            type: "GET",
            url: "/country/readAll",
            success: function (ooo) {
                var fullCountryScope = JSON.parse(ooo);
                var divToAppend = $('#nfs-content');
                divToAppend.empty();
                divToAppend.append(successAlertHtml);
                divToAppend.append(failureAlertHtml);
                var rowDiv = $('<div class="row">');
                var leftCellDiv = $('<div class="col">');
                var rightCellDiv = $('<div class="col">');
                divToAppend.append(rowDiv);
                rowDiv.append(leftCellDiv);
                rowDiv.append(rightCellDiv);
                leftCellDiv.append('<button id="newCountry" type="submit" class="btn btn-success">New country</button>');
                var tableToFill;
                if (fullCountryScope.length > 0) {
                    tableToFill = displayAllCountries(fullCountryScope);
                }
                divToAppend.append(tableToFill);
            },
            error: function (ooo) {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500);
                });
            },
        });
    }

    function displayAllCountries(countries) {
        var tableToFill = $('<table id="countries-table" class="table table-bordered table-hover table-striped">');
        tableToFill.append("<tbody>");
        for (let i = 0; i < countries.length; i++) {
            var tr = $('<tr class="country" data-serieId="' + countries[i].id + '">');
            var countryName = countries[i].countryName;
            var textTd = $('<td>');
            textTd.append(countryName);
            tr.append(textTd);
            tr.append('<td class="text-right"><button type="button" id="edit-country-' + countries[i].id + '" class="btn btn-warning edit-country">Edit</button></td>');
            tr.append('<td class="text-right"><button type="button" id="delete-country-' + countries[i].id + '" class="btn btn-danger delete-country">Delete</button></td>');
            tableToFill.append(tr);
        }
        return tableToFill;
    }

    $("#manage-countries").click(function (e) {
        getAllCountriesAndDisplay();
    });

    $(document).on('click', '.edit-country', function (e) {
        var countryId = $(this).attr("id").replace("edit-country-", "");
        $.ajax({
            async: false,
            type: "GET",
            url: "/country/read/" + Number(countryId),
            success: function (ooo) {
                var countryScope = JSON.parse(ooo);
                currentlyEditedCountryId = countryId;
                var divToAppend = $('#nfs-content');
                divToAppend.empty();
                var rowDiv = $('<div class="row p-1">');
                var leftCellDiv = $('<div class="col">');
                var rightCellDiv = $('<div class="col">');
                divToAppend.append(rowDiv);
                rowDiv.append(leftCellDiv);
                var countryInput = $('<input class="form-control" id="countryInput" value="' + countryScope.countryName + '"/>');
                var countryInputHidden = $('<input type="hidden" id="countyInputHidden" value="' + countryScope.id + '"/>');
                var countryLink = $('<input class="form-control w-100" id="countryLink" value="' + countryScope.countryLink + '"/>');
                var countryPreview = $('<img id="countryPreview" src="' + countryScope.countryLink + '"/>');
                leftCellDiv.append('<button id="modifyCountry" type="submit" class="btn btn-primary">Save</button>');
                leftCellDiv.append('<button id="cancelCountry" type="submit" class="btn btn-warning">Cancel</button>');
                leftCellDiv.append('<br>');
                leftCellDiv.append('<label for="countryInput">Country name</label>');
                leftCellDiv.append(countryInput);
                leftCellDiv.append(countryInputHidden);
                leftCellDiv.append('<label for="countryLink">Country link</label>');
                leftCellDiv.append(countryLink);
                leftCellDiv.append('<br>');
                leftCellDiv.append(countryPreview);
            },
            error: function (ooo) {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500);
                });
            },
        });

    });

    $(document).on('click', '#newCountry', function (e) {
        var divToAppend = $('#nfs-content');
        divToAppend.empty();
        var rowDiv = $('<div class="row p-1">');
        var leftCellDiv = $('<div class="col">');
        divToAppend.append(rowDiv);
        rowDiv.append(leftCellDiv);
        var countryInput = $('<input class="form-control" id="countryInput"/>');
        var countryLink = $('<input class="form-control w-100" id="countryLink"/>');
        var countryPreview = $('<img id="countryPreview" src=""/>');
        leftCellDiv.append('<button id="saveCountry" type="submit" class="btn btn-primary">Save</button>');
        leftCellDiv.append('<button id="cancelCountry" type="submit" class="btn btn-warning">Cancel</button>');
        leftCellDiv.append('<br>');
        leftCellDiv.append('<label for="countryInput">Country name</label>');
        leftCellDiv.append(countryInput);
        leftCellDiv.append('<label for="countryLink">Country link</label>');
        leftCellDiv.append(countryLink);
        leftCellDiv.append('<br>');
        leftCellDiv.append(countryPreview);
    });

    $(document).on('click', '#cancelCountry', function (e) {
        getAllCountriesAndDisplay();
    });

    $(document).on('click', '#modifyCountry', function (e) {
        var countryToSave = new Object();
        countryToSave.id = Number(currentlyEditedCountryId);
        countryToSave.countryName = $("#countryInput").val();
        countryToSave.countryLink = $("#countryLink").val();
        $.ajax({
            async: false,
            type: "PUT",
            data: JSON.stringify(countryToSave),
            url: "/country/put/" + currentlyEditedCountryId,
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function (ooo) {
                $(successAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(successAlertHtml).slideUp(500);
                    getAllCountriesAndDisplay();
                });
            },
            error: function (ooo) {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500);
                    getAllCountriesAndDisplay();
                });

            },
        });
    });

    $(document).on('click', '#saveCountry', function (e) {
        var countryToSave = new Object();
        countryToSave.countryName = $("#countryInput").val();
        countryToSave.countryLink = $("#countryLink").val();
        $.ajax({
            async: false,
            type: "POST",
            data: JSON.stringify(countryToSave),
            url: "/country/post",
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function (ooo) {
                $(successAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(successAlertHtml).slideUp(500);
                    getAllCountriesAndDisplay();
                });
            },
            error: function (ooo) {
                $(failureAlertHtml).fadeTo(500, 500).slideUp(500, function () {
                    $(failureAlertHtml).slideUp(500);
                    getAllCountriesAndDisplay();
                });

            },
        });
    });

    $(document).on('focusout', '#countryLink', function (e) {
        var linkSrc = $(this).val();
        if (linkSrc.length > 0) {
            $("#countryPreview").attr("src", linkSrc);
        }
    });
});