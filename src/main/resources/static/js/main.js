$(document).ready(function () {
    //when loading list of songs, we want to trigger main group by default
    console.log("freeman");
    var firstGameGroup = $('a.gamegroup:first')[0];
    if (firstGameGroup != null) {
        $(firstGameGroup).click();
        //we have to hit first subgroup in main group to have stuff displayed
    }
    autoPlayYouTubeModal();

    $("a.gamegroup").click(function (e) {
        console.log("jesus");
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
        $(div).find("a").first().click();
    });
    $('a.subgroup').click(function (e) {
        //handling clicking on subgroup
        e.preventDefault();
        $('.subgroup.active').each(function () {
            $(this).removeClass('active');
            //we again remove active mark from any active subgroup
        });
        $(this).addClass('active');
        $(this).removeClass('visually-hidden');
        if (e.target.text == "All" && e.target.attributes.gameGroupTxt.value != "All") {
            //we are in not-root subgroup as i call it
            console.log('zonk');
            $(document).find('tr:has(td)').each(function () {
                $(this).addClass('visually-hidden');
                //first we hide all rows
            });
            $(e.target.parentElement.parentElement).find("a").each(function () {
                //then for each subgroup we remove this hidden class
                var aId = $(this).attr('id');
                $(document).find('tr[data-el_id="' + aId + '"]').each(function () {
                    $(this).removeClass('visually-hidden');
                });
                var aText = $(this).text();
                //and we append the subgroup separator so one can know which song belongs to what subgroup
                $('<tr><td colspan="5">' + aText + '</td></tr>').insertBefore(
                    $(document).find('tr[data-el_id="' + aId + '"]').first()
                );
            });
        } else {
            //we're in main group and main subgroup so we just make basically everything visible
            var aId = $(this).attr('id');
            $(document).find('tr[data-el_id="' + aId + '"]').each(function () {
                $(this).removeClass('visually-hidden');
            });
            $(document).find('tr:has(td):not([data-el_id=' + aId + '])').each(function () {
                $(this).addClass('visually-hidden');
            });
        }
    });

    function autoPlayYouTubeModal() {
        var triggerOpen = $('body').find('[data-tagVideo]');
        triggerOpen.click(function () {
            var theModal = $(this).data('bs-target'),
                videoSRC = $(this).attr('data-tagVideo'),
                videoSRCauto = 'https://www.youtube.com/embed/' + videoSRC + '?autoplay=1';
            $('body').css('overflow', 'inherit');
            $(theModal + ' iframe').attr('src', videoSRCauto);
            $(theModal + ' button.btn-close').click(function () {
                $(theModal + ' iframe').attr('src', videoSRC);
            });
            $('#videoModal').click(function (ev) {
                if (ev.originalEvent.target.className != "modal-body") {
                    $(theModal + ' iframe').attr('src', videoSRC);
                }
            });
        });
    }
});
