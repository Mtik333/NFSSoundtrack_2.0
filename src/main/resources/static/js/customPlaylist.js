$(document).ready(function () {
	/**
	 * i got this function from somwhere, it's about sorting table by specific column
	 * @param {*} index 
	 * @returns result of filtering
	 */

	if ('ontouchstart' in window){
        $("td.song-instrumental").css("display","none");
        $("th.song-instrumental").css("display","none");
        $("col.song-instrumental").css("display","none");
		$("#mobileAddToPlaylist").css("display","none");
		$("#mobileRemoveFromPlaylist").css("display","");
    }

	function TableComparer(index) {
		return function (a, b) {
			var val_a = TableCellValue(a, index);
			var val_b = TableCellValue(b, index);
			var result = ($.isNumeric(val_a) && $.isNumeric(val_b)) ? val_a - val_b : val_a.toString().localeCompare(val_b);

			return result;
		};
	}
	function TableCellValue(row, index) {
		return $(row).children("td").eq(index).text();
	}

	/**
	 * when we click on header of row in playlist page
	 */
	$(document).on("click", "#customPlaylistStuff thead tr th:not(.no-sort)", function () {
		var table = $(this).parents("#customPlaylistStuff");
		//so we simply collect all rows from table more or less here
		var rows = $(this).parents("#customPlaylistStuff").find("tbody tr").toArray().sort(TableComparer($(this).index()));
		var dir = ($(this).hasClass("sort-asc")) ? "desc" : "asc";
		//and depending on sort type, we can reverse array too
		if (dir == "desc") {
			rows = rows.reverse();
		}
		//then we just reapply rows to the table
		for (var i = 0; i < rows.length; i++) {
			table.append(rows[i]);
		}
		//then we move any existing 'sort' marker from other column and put on the one we are using now
		table.find("thead tr th").removeClass("sort-asc").removeClass("sort-desc");
		$(this).removeClass("sort-asc").removeClass("sort-desc").addClass("sort-" + dir);
	});

	/**
	 * method to remove row from custom playlist table
	 */
	$(document).on("click", "img.remove-from-playlist", function () {
		//dispose tooltip to prevent it from staying visib
		$(this).tooltip('dispose');
		//we have to check local storage to see what's there now
		var relatedTr = $(this).parent().parent();
		var songSubgroupId = $(relatedTr).attr("data-songSubgroup-id");
		removeSongFromPlaylist(songSubgroupId);
	});

	function removeSongFromPlaylist(relatedTr, songSubgroupId){
		var tbody = relatedTr.parent();
		var customPlaylistArray = JSON.parse(localStorage.getItem("custom-playlist"));
		var songToRemoveIndex = customPlaylistArray.indexOf(songSubgroupId);
		//then we obviously remove from local array and push to the local storage
		customPlaylistArray.splice(songToRemoveIndex, 1);
		var jsonEdArray = JSON.stringify(customPlaylistArray);
		localStorage.setItem("custom-playlist", jsonEdArray);
		//and remove row from table
		$(relatedTr).remove();
		var remainingTrs = $(tbody).find("tr");
		//we also have to re-index ID value in table
		for (let i = 0; i < remainingTrs.length; i++) {
			var tdToReindex = remainingTrs[i].children[1];
			$(tdToReindex).html((i + 1));
		}
		//and show the alert that remove was successful
		$("#successRemoveFromCustomPlaylist").parent().fadeIn(500, function () {
			setTimeout(function () {
				$("#successRemoveFromCustomPlaylist").parent().fadeOut(500);
			}, 3000);
		});
	}

	/**
	 * method to wipe out whole table
	 */
	$(document).on("click", "#truncate_playlist", function () {
		//so we get rid of custom playlist info in local storage
		localStorage.removeItem("custom-playlist");
		//and make table empty
		$(document).find("tbody").empty();
		//and show alert that this operation was successful + redirect to home page
		$("#playlistTruncated").parent().fadeIn(500, function () {
			setTimeout(function () {
				$("#playlistTruncated").parent().fadeOut(500, function () {
					window.location.href = window.location.href.replace("custom/playlist", "");
				});
			}, 2000);
		});
	});

	/**
	 * not sure what this is doing here
	 */
	$('#playlistTruncated').on('hidden.bs.modal', function (e) {
		$("#spotifyVideo").attr('src', '');
	});

	$("#mobileRemoveFromPlaylist").on("click", function(e){
        var songSubgroupId = $(this).attr("data-songSubgroup-id");
		var relatedTr = $("tbody").find("tr[data-songSubgroup-id='"+songSubgroupId+"']");
		removeSongFromPlaylist(relatedTr,songSubgroupId);
        $("#mobile_context").removeClass("show").hide();
        $("#mobileLaunchSpotify").css("display", "");
        $("#mobileLaunchItunes").css("display", "");
        $("#mobileLaunchDeezer").css("display", "");
    });
});