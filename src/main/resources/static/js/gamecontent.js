$(document).ready(function () {

    $('#spotifyModal').on('show.bs.modal', function (e) {
        // set the video src to autoplay and not to show related video. Youtube related video is like a box of chocolates... you never know what you're gonna get
        $("#spotifyVideo").attr('src', "https://open.spotify.com/embed/playlist/" + $("#spotifyLink").attr('data-tagVideo'));
        $("#spotify-ext").attr('href', "spotify:playlist:" + $("#spotifyLink").attr('data-tagVideo'));
    });

    $('#spotifyModal').on('hide.bs.modal', function (e) {
        $("#spotifyVideo").attr('src', '');
    });

    $('#soundcloudModal').on('show.bs.modal', function (e) {
        // set the video src to autoplay and not to show related video. Youtube related video is like a box of chocolates... you never know what you're gonna get
        $("#soundcloudVideo").attr('src', "https://w.soundcloud.com/player/?url=https%253A//api.soundcloud.com/playlists/" + $("#soundcloudLink").attr('data-tagVideo') + "&auto_play=false&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true&visual=true");
    });

    $('#soundcloudModal').on('hide.bs.modal', function (e) {
        $("#spotifyVideo").attr('src', '');
    });

    $('#tidalModal').on('show.bs.modal', function (e) {
        // set the video src to autoplay and not to show related video. Youtube related video is like a box of chocolates... you never know what you're gonna get
        $("#tidalVideo").attr('src', "https://embed.tidal.com/playlists/" + $("#tidalLink").attr('data-tagVideo'));
        $("#tidal-ext").attr('href', "https://listen.tidal.com/playlist/" + $("#tidalLink").attr('data-tagVideo'));
    });

    $('#tidalModal').on('hide.bs.modal', function (e) {
        $("#tidalVideo").attr('src', '');
    });

    $('#deezerModal').on('show.bs.modal', function (e) {
        // set the video src to autoplay and not to show related video. Youtube related video is like a box of chocolates... you never know what you're gonna get
        $("#deezerVideo").attr('src', "https://widget.deezer.com/widget/auto/playlist/" + $("#deezerLink").attr('data-tagVideo'));
        $("#deezer-ext").attr('href', "deezer://www.deezer.com/playlist/" + $("#deezerLink").attr('data-tagVideo'));
    });

    $('#deezerModal').on('hide.bs.modal', function (e) {
        $("#deezerVideo").attr('src', '');
    });

    $(document).on('click', 'button.subgroup', function (e) {
        //handling clicking on subgroup
        e.preventDefault();
        var groupId = $(this).attr('data-group_id');
        if ($(this).text() == "All") {
            $('.subgroup.active').each(function () {
                $(this).removeClass('active');
                //we again remove active mark from any active subgroup
            });
            if ($(this).attr("data-gameGroupTxt") == "All") {
                $(document).find('tr').each(function () {
                    $(this).removeClass('visually-hidden');
                    //first we hide all rows
                });
            } else {
                if (groupId != undefined) {
                    $(document).find('tr:has(td):not([data-group_id="' + groupId + '"])').each(function () {
                        $(this).addClass('visually-hidden');
                        //first we hide all rows
                    });
                    $(document).find('tr:has(td)[data-group_id="' + groupId + '"]').each(function () {
                        $(this).removeClass('visually-hidden');
                        //first we hide all rows
                    });
                }
            }
            activeSubgroups = 1;
        } else {
            var aId = $(this).attr('id');
            var allSubgroup = $(this).parent().parent().find("button[data-subgroupTxt='All']").first();
            if (allSubgroup.hasClass('active')) {
                allSubgroup.removeClass('active');
                $(document).find('tr:has(td):not([data-el_id="' + aId + '"])').each(function () {
                    $(this).addClass('visually-hidden');
                    //first we hide all rows
                });
                activeSubgroups--;
            }
            if ($(this).hasClass("active")) {
                $(this).removeClass('active');
                $(document).find('tr:has(td)[data-el_id="' + aId + '"]').each(function () {
                    $(this).addClass('visually-hidden');
                });
                activeSubgroups--;
            } else {
                $(this).addClass('active');
                $(document).find('tr:has(td)[data-el_id="' + aId + '"]').each(function () {
                    $(this).removeClass('visually-hidden');
                });
                activeSubgroups++;
            }
            if (activeSubgroups == 0) {
                allSubgroup = $(this).parent().parent().find("button[data-subgroupTxt='All']").first();
                $(allSubgroup).click();
                activeSubgroups = 1;
                return;
            }
        }
        if ($(e.target).text() == "All" && $(e.target).attr("data-gameGroupTxt") == "All") {
            $(this).addClass('active');
        }
        if ($(e.target).text() == "All" && $(e.target).attr("data-gameGroupTxt") != "All") {
            //we are in not-root subgroup as i call it
            $(document).find('tr:has(td)').not('.subgroup-separator').each(function () {
                $(this).addClass('visually-hidden');
                //first we hide all rows
            });
            $(e.target.parentElement.parentElement).find("button").each(function () {
                //then for each subgroup we remove this hidden class
                var aId = $(this).attr('id');
                $(document).find('tr[data-el_id="' + aId + '"]').each(function () {
                    $(this).removeClass('visually-hidden');
                });
                var aText = $(this).text();

            });
            $(this).addClass('active');
        }
    });

    $(document).on('click', 'a.gamegroup', function (e) {
        e.preventDefault();
        $('.gamegroup.active').each(function () {
            $(this).removeClass('active');
            //we get rid of any previously active groups
        });
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
});