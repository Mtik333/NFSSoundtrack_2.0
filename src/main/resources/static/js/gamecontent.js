$(document).ready(function () {

    /**
     * method to show spotify modal
     */
    $('#spotifyModal').on('show.bs.modal', function (e) {
        $("#spotifyVideo").attr('src', "https://open.spotify.com/embed/playlist/" + $("#spotifyLink").attr('data-tagVideo'));
        $("#spotify-ext").attr('href', "spotify:playlist:" + $("#spotifyLink").attr('data-tagVideo'));
    });

    /**
     * method to unload spotify iframe when modal closed
     */
    $('#spotifyModal').on('hide.bs.modal', function (e) {
        $("#spotifyVideo").attr('src', '');
    });

    /**
     * method to show soundcloud modal
     */
    $('#soundcloudModal').on('show.bs.modal', function (e) {
        // set the video src to autoplay and not to show related video. Youtube related video is like a box of chocolates... you never know what you're gonna get
        $("#soundcloudVideo").attr('src', "https://w.soundcloud.com/player/?url=https%253A//api.soundcloud.com/playlists/" + $("#soundcloudLink").attr('data-tagVideo') + "&auto_play=false&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true&visual=true");
    });

    /**
     * method to unload soundcloud iframe when modal closed
     */
    $('#soundcloudModal').on('hide.bs.modal', function (e) {
        $("#soundcloudVideo").attr('src', '');
    });

    /**
     * method to show tidal modal
     */
    $('#tidalModal').on('show.bs.modal', function (e) {
        // set the video src to autoplay and not to show related video. Youtube related video is like a box of chocolates... you never know what you're gonna get
        $("#tidalVideo").attr('src', "https://embed.tidal.com/playlists/" + $("#tidalLink").attr('data-tagVideo'));
        $("#tidal-ext").attr('href', "https://listen.tidal.com/playlist/" + $("#tidalLink").attr('data-tagVideo'));
    });

    /**
     * method to unload tidal iframe when modal closed
     */
    $('#tidalModal').on('hide.bs.modal', function (e) {
        $("#tidalVideo").attr('src', '');
    });

    /**
     * method to show deezer modal
     */
    $('#deezerModal').on('show.bs.modal', function (e) {
        // set the video src to autoplay and not to show related video. Youtube related video is like a box of chocolates... you never know what you're gonna get
        $("#deezerVideo").attr('src', "https://widget.deezer.com/widget/auto/playlist/" + $("#deezerLink").attr('data-tagVideo'));
        $("#deezer-ext").attr('href', "deezer://www.deezer.com/playlist/" + $("#deezerLink").attr('data-tagVideo'));
    });

    /**
     * method to unload deezer iframe when modal closed
     */
    $('#deezerModal').on('hide.bs.modal', function (e) {
        $("#deezerVideo").attr('src', '');
    });

    /**
     * when we click on 'subgroup' button in game soundtrack view
     */
    $(document).on('click', 'button.subgroup', function (e) {
        //do not do any stupid things by default
        e.preventDefault();
        var groupId = $(this).attr('data-group_id');
        //need to check if we click on 'All' meta-subgroup
        if ($(this).text() == "All") {
            //if yes, then we un-active all subgroups
            $('.subgroup.active').each(function () {
                $(this).removeClass('active');
            });
            //now if the group is also "All" then we hide everything in table
            if ($(this).attr("data-gameGroupTxt") == "All") {
                $(document).find('tr').each(function () {
                    $(this).removeClass('visually-hidden');
                });
            } else {
                //otherwise we check what groupid is assigned to subgroup button
                if (groupId != undefined) {
                    //hide all rows that do not have groupid equal to the one currently active
                    $(document).find('tr:has(td):not([data-group_id="' + groupId + '"])').each(function () {
                        $(this).addClass('visually-hidden');
                    });
                    //now show all rows that have groupid equal to the one currently active
                    $(document).find('tr:has(td)[data-group_id="' + groupId + '"]').each(function () {
                        $(this).removeClass('visually-hidden');
                        //first we hide all rows
                    });
                }
            }
            activeSubgroups = 1;
        } else {
            //otherwise we clicked on other subgroup
            var aId = $(this).attr('id');
            var allSubgroup = $(this).parent().parent().find("button[data-subgroupTxt='All']").first();
            //let's check if 'all' subgroup is already active
            if (allSubgroup.hasClass('active')) {
                //then we make it unactive and simply hide all rows that have different subgroup id than the one we clicked on
                allSubgroup.removeClass('active');
                $(document).find('tr:has(td):not([data-el_id="' + aId + '"])').each(function () {
                    $(this).addClass('visually-hidden');
                });
                activeSubgroups--;
            }
            //if this subgroup was already active
            if ($(this).hasClass("active")) {
                $(this).removeClass('active');
                //then we make it unactive and hide all rows associated with it
                $(document).find('tr:has(td)[data-el_id="' + aId + '"]').each(function () {
                    $(this).addClass('visually-hidden');
                });
                activeSubgroups--;
            } else {
                //otherwise we make subgroup active and show all rows associated with this 
                $(this).addClass('active');
                $(document).find('tr:has(td)[data-el_id="' + aId + '"]').each(function () {
                    $(this).removeClass('visually-hidden');
                });
                activeSubgroups++;
            }
            //if there are no active subgroups right now, we click meta-subgroup 'all'
            if (activeSubgroups == 0) {
                allSubgroup = $(this).parent().parent().find("button[data-subgroupTxt='All']").first();
                $(allSubgroup).click();
                activeSubgroups = 1;
                return;
            }
        }
        //if we are on 'all' group and 'all' subgroup then we make 'all' subgroup active
        if ($(e.target).text() == "All" && $(e.target).attr("data-gameGroupTxt") == "All") {
            $(this).addClass('active');
        }
        if ($(e.target).text() == "All" && $(e.target).attr("data-gameGroupTxt") != "All") {
            //we are in not-'all' group but in 'all' subgroup so we have to hide all trs at the moment
            $(document).find('tr:has(td)').not('.subgroup-separator').each(function () {
                $(this).addClass('visually-hidden');
            });
            $(e.target.parentElement.parentElement).find("button").each(function () {
                //then for each subgroup related row we remove this hidden class
                var aId = $(this).attr('id');
                $(document).find('tr[data-el_id="' + aId + '"]').each(function () {
                    $(this).removeClass('visually-hidden');
                });
            });
            $(this).addClass('active');
        }
    });

    /**
     * method when clicking on 'group' (this upper tab) in game soundtrack table
     */
    $(document).on('click', 'a.gamegroup', function (e) {
        //preventing stupid redirects
        e.preventDefault();
        $('.gamegroup.active').each(function () {
            $(this).removeClass('active');
            //we get rid of any previously active groups
        });
        //making group active and fetching its id
        $(this).addClass('active');
        var aId = $(this).attr('data-el_id');
        $(document).find('ul.subgroup[data-el_id="' + aId + '"]').each(function () {
            $(this).removeClass('visually-hidden');
            //we remove visually-hidden class from subgroups that belong to this group
        });
        $(document).find('ul.subgroup:not([data-el_id="' + aId + '"])').each(function () {
            $(this).addClass('visually-hidden');
            //and we hide subgroups that belong to other groups
        });
        $(this).removeClass('visually-hidden');
        var div = $($(this).attr('href'))[0];
        $(div).addClass('active');
        $(div).removeClass('visually-hidden');
        $(div).find("button").first().click();
        activeSubgroups = 1;
    });

    //when loading list of songs, we want to trigger main group by default
    var firstGameGroup = $('a.gamegroup:first')[0];
    if (firstGameGroup != null) {
        $(firstGameGroup).first().click();
        //we have to hit first subgroup in main group to have stuff displayed
    }
});