$(document).ready(function () {
    $("#success-alert").hide();
    function getSubgroupsFromGame() {
        $.ajax({
            async: false,
            type: "GET",
            url: "/subgroup/read/" + gameId,
            success: function (ooo) {
                fullScopeOfEdit = JSON.parse(ooo);
                var divToAppend = $('#nfs-content');
                divToAppend.empty();
                var dropdownDiv = $('<div id="selectSubgroup" class="dropdown">')
                dropdownDiv.append('<button class="btn btn-secondary dropdown-toggle" type="button" id="subgroupsDropdown" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">All</button>');
                var allSubgroupSongs;
                if (fullScopeOfEdit.length > 0) {
                    var dropdownMenuDiv = $('<div class="dropdown-menu"');
                    for (let i = 0; i < fullScopeOfEdit.length; i++) {
                        var subgroup = fullScopeOfEdit[i];
                        dropdownMenuDiv.append('<a class="dropdown-item subgroupItem" href="#" data-subgroupId="'+fullScopeOfEdit.id+'">'+subgroup.subgroupName+'</a>');
                        if (subgroup.subgroupName=="All"){
                            displayAllSongs(fullScopeOfEdit.songSubgroupList);
                        }
                    }
                    dropdownDiv.append(dropdownMenuDiv);
                }
                divToAppend.append(dropdownDiv);
                $(divToAppend).find("a").first().click();
            }
        });
    }

    function displayAllSongs(songs){
        var tableToFill = $('<table class="table"');
        for (let i = 0; i < fullScopeOfEdit.length; i++) {
            var tr=$("<tr>");
        }
    }
    $("a.manage-subgroups").click(function (e) {
        gameId = e.target.attributes["data-gameid"].value;
        getSubgroupsFromGame();
    });

    $(document).on('click', 'a.subgroupItem', function (e) {
        var dropdownDiv = $('#selectSubgroup');
        

    });
});