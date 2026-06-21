$(document).ready(function () {

    var youtubeIndex = 0, spotifyIndex = 0, tidalIndex = 0, deezerIndex = 0, soundcloudIndex = 0;

    function readPlaylists(containerId) {
        var result = [];
        $('#' + containerId + ' .playlist-entry').each(function () {
            result.push({url: $(this).attr('data-url'), label: $(this).attr('data-label') || ''});
        });
        return result;
    }

    function applyNav(prefix, index, playlists) {
        var total = playlists.length;
        var label = playlists[index].label;
        var navLabel = label + (total > 1 ? ' (' + (index + 1) + '/' + total + ')' : '');
        $('#' + prefix + '-nav-label').text(navLabel);
        $('#' + prefix + '-nav').css('display', 'flex');
        if (total > 1) {
            $('#' + prefix + '-prev').css('display', 'block').prop('disabled', index === 0);
            $('#' + prefix + '-next').css('display', 'block').prop('disabled', index === total - 1);
        }
    }

    $('#youtubeModal').on('show.bs.modal', function () {
        var playlists = readPlaylists('youtube-playlists');
        if (playlists.length > 0) {
            youtubeIndex = 0;
            $("#youtubePlaylist").attr('src', "https://www.youtube.com/embed/videoseries?list=" + playlists[0].url);
            applyNav('youtube', 0, playlists);
        } else {
            $("#youtubePlaylist").attr('src', "https://www.youtube.com/embed/videoseries?list=" + $("#youtubeLink").attr('data-tagVideo'));
        }
    });
    $('#youtubeModal').on('hide.bs.modal', function () { $("#youtubePlaylist").attr('src', ''); });
    $(document).on('click', '#youtube-prev', function () {
        var playlists = readPlaylists('youtube-playlists');
        if (youtubeIndex > 0) { youtubeIndex--; $("#youtubePlaylist").attr('src', "https://www.youtube.com/embed/videoseries?list=" + playlists[youtubeIndex].url); applyNav('youtube', youtubeIndex, playlists); }
    });
    $(document).on('click', '#youtube-next', function () {
        var playlists = readPlaylists('youtube-playlists');
        if (youtubeIndex < playlists.length - 1) { youtubeIndex++; $("#youtubePlaylist").attr('src', "https://www.youtube.com/embed/videoseries?list=" + playlists[youtubeIndex].url); applyNav('youtube', youtubeIndex, playlists); }
    });

    $('#spotifyModal').on('show.bs.modal', function () {
        var playlists = readPlaylists('spotify-playlists');
        if (playlists.length > 0) {
            spotifyIndex = 0;
            $("#spotifyVideo").attr('src', "https://open.spotify.com/embed/playlist/" + playlists[0].url);
            $("#spotify-ext").attr('href', "spotify:playlist:" + playlists[0].url);
            applyNav('spotify', 0, playlists);
        } else {
            var id = $("#spotifyLink").attr('data-tagVideo');
            $("#spotifyVideo").attr('src', "https://open.spotify.com/embed/playlist/" + id);
            $("#spotify-ext").attr('href', "spotify:playlist:" + id);
        }
    });
    $('#spotifyModal').on('hide.bs.modal', function () { $("#spotifyVideo").attr('src', ''); });
    $(document).on('click', '#spotify-prev', function () {
        var playlists = readPlaylists('spotify-playlists');
        if (spotifyIndex > 0) { spotifyIndex--; $("#spotifyVideo").attr('src', "https://open.spotify.com/embed/playlist/" + playlists[spotifyIndex].url); $("#spotify-ext").attr('href', "spotify:playlist:" + playlists[spotifyIndex].url); applyNav('spotify', spotifyIndex, playlists); }
    });
    $(document).on('click', '#spotify-next', function () {
        var playlists = readPlaylists('spotify-playlists');
        if (spotifyIndex < playlists.length - 1) { spotifyIndex++; $("#spotifyVideo").attr('src', "https://open.spotify.com/embed/playlist/" + playlists[spotifyIndex].url); $("#spotify-ext").attr('href', "spotify:playlist:" + playlists[spotifyIndex].url); applyNav('spotify', spotifyIndex, playlists); }
    });

    $('#soundcloudModal').on('show.bs.modal', function () {
        var playlists = readPlaylists('soundcloud-playlists');
        if (playlists.length > 0) {
            soundcloudIndex = 0;
            $("#soundcloudVideo").attr('src', "https://w.soundcloud.com/player/?url=https%253A//api.soundcloud.com/playlists/" + playlists[0].url + "&auto_play=false&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true&visual=true");
            applyNav('soundcloud', 0, playlists);
        } else {
            $("#soundcloudVideo").attr('src', "https://w.soundcloud.com/player/?url=https%253A//api.soundcloud.com/playlists/" + $("#soundcloudLink").attr('data-tagVideo') + "&auto_play=false&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true&visual=true");
        }
    });
    $('#soundcloudModal').on('hide.bs.modal', function () { $("#soundcloudVideo").attr('src', ''); });
    $(document).on('click', '#soundcloud-prev', function () {
        var playlists = readPlaylists('soundcloud-playlists');
        if (soundcloudIndex > 0) { soundcloudIndex--; $("#soundcloudVideo").attr('src', "https://w.soundcloud.com/player/?url=https%253A//api.soundcloud.com/playlists/" + playlists[soundcloudIndex].url + "&auto_play=false&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true&visual=true"); applyNav('soundcloud', soundcloudIndex, playlists); }
    });
    $(document).on('click', '#soundcloud-next', function () {
        var playlists = readPlaylists('soundcloud-playlists');
        if (soundcloudIndex < playlists.length - 1) { soundcloudIndex++; $("#soundcloudVideo").attr('src', "https://w.soundcloud.com/player/?url=https%253A//api.soundcloud.com/playlists/" + playlists[soundcloudIndex].url + "&auto_play=false&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true&visual=true"); applyNav('soundcloud', soundcloudIndex, playlists); }
    });

    $('#tidalModal').on('show.bs.modal', function () {
        var playlists = readPlaylists('tidal-playlists');
        if (playlists.length > 0) {
            tidalIndex = 0;
            $("#tidalVideo").attr('src', "https://embed.tidal.com/playlists/" + playlists[0].url);
            $("#tidal-ext").attr('href', "https://listen.tidal.com/playlist/" + playlists[0].url);
            applyNav('tidal', 0, playlists);
        } else {
            var id = $("#tidalLink").attr('data-tagVideo');
            $("#tidalVideo").attr('src', "https://embed.tidal.com/playlists/" + id);
            $("#tidal-ext").attr('href', "https://listen.tidal.com/playlist/" + id);
        }
    });
    $('#tidalModal').on('hide.bs.modal', function () { $("#tidalVideo").attr('src', ''); });
    $(document).on('click', '#tidal-prev', function () {
        var playlists = readPlaylists('tidal-playlists');
        if (tidalIndex > 0) { tidalIndex--; $("#tidalVideo").attr('src', "https://embed.tidal.com/playlists/" + playlists[tidalIndex].url); $("#tidal-ext").attr('href', "https://listen.tidal.com/playlist/" + playlists[tidalIndex].url); applyNav('tidal', tidalIndex, playlists); }
    });
    $(document).on('click', '#tidal-next', function () {
        var playlists = readPlaylists('tidal-playlists');
        if (tidalIndex < playlists.length - 1) { tidalIndex++; $("#tidalVideo").attr('src', "https://embed.tidal.com/playlists/" + playlists[tidalIndex].url); $("#tidal-ext").attr('href', "https://listen.tidal.com/playlist/" + playlists[tidalIndex].url); applyNav('tidal', tidalIndex, playlists); }
    });

    $('#deezerModal').on('show.bs.modal', function () {
        var playlists = readPlaylists('deezer-playlists');
        if (playlists.length > 0) {
            deezerIndex = 0;
            $("#deezerVideo").attr('src', "https://widget.deezer.com/widget/auto/playlist/" + playlists[0].url);
            $("#deezer-ext").attr('href', "deezer://www.deezer.com/playlist/" + playlists[0].url);
            applyNav('deezer', 0, playlists);
        } else {
            var id = $("#deezerLink").attr('data-tagVideo');
            $("#deezerVideo").attr('src', "https://widget.deezer.com/widget/auto/playlist/" + id);
            $("#deezer-ext").attr('href', "deezer://www.deezer.com/playlist/" + id);
        }
    });
    $('#deezerModal').on('hide.bs.modal', function () { $("#deezerVideo").attr('src', ''); });
    $(document).on('click', '#deezer-prev', function () {
        var playlists = readPlaylists('deezer-playlists');
        if (deezerIndex > 0) { deezerIndex--; $("#deezerVideo").attr('src', "https://widget.deezer.com/widget/auto/playlist/" + playlists[deezerIndex].url); $("#deezer-ext").attr('href', "deezer://www.deezer.com/playlist/" + playlists[deezerIndex].url); applyNav('deezer', deezerIndex, playlists); }
    });
    $(document).on('click', '#deezer-next', function () {
        var playlists = readPlaylists('deezer-playlists');
        if (deezerIndex < playlists.length - 1) { deezerIndex++; $("#deezerVideo").attr('src', "https://widget.deezer.com/widget/auto/playlist/" + playlists[deezerIndex].url); $("#deezer-ext").attr('href', "deezer://www.deezer.com/playlist/" + playlists[deezerIndex].url); applyNav('deezer', deezerIndex, playlists); }
    });

    $(document).on('keydown', function (e) {
        if (e.key !== 'ArrowLeft' && e.key !== 'ArrowRight') return;
        var openModal = $('#youtubeModal.show, #spotifyModal.show, #soundcloudModal.show, #tidalModal.show, #deezerModal.show');
        if (openModal.length === 0) return;
        e.preventDefault();
        var prefix = openModal.attr('id').replace('Modal', '');
        if (e.key === 'ArrowLeft') $('#' + prefix + '-prev').trigger('click');
        else $('#' + prefix + '-next').trigger('click');
    });

    /**
     * when we click on 'subgroup' button in game soundtrack view
     */
    $(document).on('click', 'button.subgroup', function (e) {
        /*do not do any stupid things by default*/
        e.preventDefault();
        var groupId = $(this).attr('data-group_id');
        /*need to check if we click on 'All' meta-subgroup*/
        if ($(this).text().localeCompare("All") == 0) {
            /*if yes, then we un-active all subgroups*/
            $('.subgroup.active').each(function () {
                $(this).removeClass('active');
            });
            /*now if the group is also "All" then we hide everything in table*/
            if ($(this).attr("data-gameGroupTxt").localeCompare("All") == 0) {
                $(document).find('tr').each(function () {
                    $(this).removeClass('visually-hidden');
                    $(this).attr("hidden", false);
                });
            } else {
                /*otherwise we check what groupid is assigned to subgroup button*/
                if (groupId != undefined) {
                    /*hide all rows that do not have groupid equal to the one currently active*/
                    $(document).find('tr:has(td):not([data-group_id="' + groupId + '"])').each(function () {
                        $(this).addClass('visually-hidden');
                        $(this).attr("hidden", true);
                    });
                    /*now show all rows that have groupid equal to the one currently active*/
                    $(document).find('tr:has(td)[data-group_id="' + groupId + '"]').each(function () {
                        $(this).removeClass('visually-hidden');
                        $(this).attr("hidden", false);
                        /*first we hide all rows*/
                    });
                }
            }
            activeSubgroups = 1;
        } else {
            /*otherwise we clicked on other subgroup*/
            var aId = $(this).attr('id');
            var allSubgroup = $(this).parent().parent().find("button[data-subgroupTxt='All']").first();
            /*let's check if 'all' subgroup is already active*/
            if (allSubgroup.hasClass('active')) {
                /*then we make it unactive and simply hide all rows that have different subgroup id than the one we clicked on*/
                allSubgroup.removeClass('active');
                $(document).find('tr:has(td):not([data-el_id="' + aId + '"])').each(function () {
                    $(this).addClass('visually-hidden');
                    $(this).attr("hidden", true);
                });
                activeSubgroups--;
                /* we enabled some subgroup and potentially music played while in All subgroup, 
                so if we now have only one subgroup that does not have this music, we need to stop the music */
                var existingTr = $("#listen-music");
                if (existingTr.length != 0) {
                    var playedMusicSubgroup = existingTr.attr("data-el_id");
                    if (playedMusicSubgroup.localeCompare(aId)!=0) {
                        existingTr.remove();
                    }
                }                    
            }
            /*if this subgroup was already active*/
            if ($(this).hasClass("active")) {
                $(this).removeClass('active');
                /*then we make it unactive and hide all rows associated with it*/
                $(document).find('tr:has(td)[data-el_id="' + aId + '"]').each(function () {
                    $(this).addClass('visually-hidden');
                    $(this).attr("hidden", true);
                });
                activeSubgroups--;
                /* we just disabled some subgroup (but some other still remain active), 
                so if it had music played, we need to remove it too */
                var existingTr = $("#listen-music");
                if (existingTr.length != 0 && activeSubgroups!=0) {
                    var playedMusicSubgroup = existingTr.attr("data-el_id");
                    if (playedMusicSubgroup.localeCompare(aId)==0) {
                        existingTr.remove();
                    }
                }                
            } else {
                /*otherwise we make subgroup active and show all rows associated with this*/
                $(this).addClass('active');
                $(document).find('tr:has(td)[data-el_id="' + aId + '"]').each(function () {
                    $(this).removeClass('visually-hidden');
                    $(this).attr("hidden", false);
                });
                activeSubgroups++;
            }
            /*if there are no active subgroups right now, we click meta-subgroup 'all'*/
            if (activeSubgroups == 0) {
                allSubgroup = $(this).parent().parent().find("button[data-subgroupTxt='All']").first();
                $(allSubgroup).click();
                activeSubgroups = 1;
                return;
            }
        }
        /*if we are on 'all' group and 'all' subgroup then we make 'all' subgroup active*/
        if ($(e.target).text().localeCompare("All") == 0 && $(e.target).attr("data-gameGroupTxt").localeCompare("All") == 0) {
            $(this).addClass('active');
        }
        if ($(e.target).text().localeCompare("All") == 0 && $(e.target).attr("data-gameGroupTxt").localeCompare("All") != 0) {
            /*we are in not-'all' group but in 'all' subgroup so we have to hide all trs at the moment*/
            $(document).find('tr:has(td)').not('.subgroup-separator').each(function () {
                $(this).addClass('visually-hidden');
                $(this).attr("hidden", true);
            });
            $(e.target.parentElement.parentElement).find("button").each(function () {
                /*then for each subgroup related row we remove this hidden class*/
                var aId = $(this).attr('id');
                $(document).find('tr[data-el_id="' + aId + '"]').each(function () {
                    $(this).removeClass('visually-hidden');
                    $(this).attr("hidden", false);
                });
            });
            $(this).addClass('active');
        }
    });

    /**
     * method when clicking on 'group' (this upper tab) in game soundtrack table
     */
    $(document).on('click', 'a.gamegroup', function (e) {
        /*preventing stupid redirects*/
        e.preventDefault();               
        $('.gamegroup.active').each(function () {
            $(this).removeClass('active');
            /*we get rid of any previously active groups*/
        });
        /*making group active and fetching its id*/
        $(this).addClass('active');
        var aId = $(this).attr('data-el_id');
        var justGroupId = aId.replace("gamegroupid","");
        /* if we switch to group where music is not present, we have to remove played song */
        var existingTr = $("#listen-music");
        if (existingTr.length != 0) {
            var playedMusicGroup = existingTr.attr("data-group_id");
            /* of course if we swich from current group to 'all', then we don't stop the music */
            if ($(this).text().localeCompare("All")!=0 && playedMusicGroup.localeCompare(justGroupId)!=0){
                existingTr.remove();
            }
        } 
        $(document).find('ul.subgroup[data-el_id="' + aId + '"]').each(function () {
            $(this).removeClass('visually-hidden');
            $(this).attr("hidden", false);
            /*we remove visually-hidden class from subgroups that belong to this group*/
        });
        $(document).find('ul.subgroup:not([data-el_id="' + aId + '"])').each(function () {
            $(this).addClass('visually-hidden');
            $(this).attr("hidden", true);
            /*and we hide subgroups that belong to other groups*/
        });
        $(this).removeClass('visually-hidden');
        $(this).attr("hidden", false);
        var div = $($(this).attr('href'))[0];
        $(div).addClass('active');
        $(div).removeClass('visually-hidden');
        $(div).attr("hidden", false);
        $(div).find("button").first().click();
        activeSubgroups = 1;
    });

    /*when loading list of songs, we want to trigger main group by default*/
    var firstGameGroup = $('a.gamegroup:first')[0];
    if (firstGameGroup != null) {
        $(firstGameGroup).first().click();
        /*we have to hit first subgroup in main group to have stuff displayed*/
    }

    $(document).on('click', '#dynamicBackground', function (e) {
        var gameId = Number($("#playlistsDiv").attr("data-el_id"));
        if ($("body").css("background-image").indexOf("url") == 0) {
            changeStuffForDarkMode();
            changeStuffForDynamicTheme();
        } else {
            $.ajax({
                async: false,
                type: "GET",
                url: "/customtheme/" + gameId,
                success: function (ooo) {
                    var theme = JSON.parse(ooo);
                    changeStuffForDarkMode(theme.nightMode);
                    changeStuffForDynamicTheme(theme);
                },
            });
        }
    });

    $(document).on('click', '.warn-info', function (e) {
        if (!$(this).attr('note-clicked')) {
            $(this).tooltip('dispose');
            $(this).tooltip({ 'delay': { show: 0, hide: 5000 } });
            $(this).tooltip('show');
            $(this).attr("note-clicked", true);
        } else {
            $(this).tooltip('dispose');
            $(this).tooltip({ 'delay': { show: 0, hide: 0 } });
            $(this).removeAttr("note-clicked");
        }
    });

    $(document).on('hide.bs.tooltip', '.warn-info', function (e) {
        if ($(this).attr("note-clicked")) {
            $(this).tooltip('dispose');
            $(this).tooltip({ 'delay': { show: 0, hide: 0 } });
            $(this).removeAttr("note-clicked");
        }
    });
});