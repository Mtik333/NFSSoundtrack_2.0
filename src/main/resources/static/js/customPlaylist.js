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

$(document).on("click", "#customPlaylistStuff thead tr th:not(.no-sort)", function () {
	var table = $(this).parents("#customPlaylistStuff");
	var rows = $(this).parents("#customPlaylistStuff").find("tbody tr").toArray().sort(TableComparer($(this).index()));
	var dir = ($(this).hasClass("sort-asc")) ? "desc" : "asc";
	if (dir == "desc") {
		rows = rows.reverse();
	}
	for (var i = 0; i < rows.length; i++) {
		table.append(rows[i]);
	}
	table.find("thead tr th").removeClass("sort-asc").removeClass("sort-desc");
	$(this).removeClass("sort-asc").removeClass("sort-desc").addClass("sort-" + dir);
});

$(document).on("click", "img.remove-from-playlist", function () {
	$(this).tooltip('dispose');
	var customPlaylistArray = JSON.parse(localStorage.getItem("custom-playlist"));
	var relatedTr = $(this).parent().parent();
	var tbody = relatedTr.parent();
	var songSubgroupId = $(relatedTr).attr("data-songSubgroup-id");
	var songToRemoveIndex = customPlaylistArray.indexOf(songSubgroupId);
	customPlaylistArray.splice(songToRemoveIndex, 1);
	var jsonEdArray = JSON.stringify(customPlaylistArray);
	localStorage.setItem("custom-playlist", jsonEdArray);
	$(relatedTr).remove();
	var remainingTrs = $(tbody).find("tr");
	for (let i = 0; i < remainingTrs.length; i++) {
		var tdToReindex = remainingTrs[i].children[0];
		$(tdToReindex).html((i + 1));
	}
	$("#successRemoveFromCustomPlaylist").parent().fadeIn(500, function () {
		setTimeout(function () {
			$("#successRemoveFromCustomPlaylist").parent().fadeOut(500);
		}, 3000);
	});
});

$(document).on("click", "#truncate_playlist", function () {
	localStorage.removeItem("custom-playlist");
	$(document).find("tbody").empty();
	$("#playlistTruncated").parent().fadeIn(500, function () {
		setTimeout(function () {
			$("#playlistTruncated").parent().fadeOut(500, function () {
				window.location.href = window.location.href.replace("custom/playlist", "");
			});
		}, 2000);
	});
});

$('#playlistTruncated').on('hidden.bs.modal', function (e) {
	$("#spotifyVideo").attr('src', '');
});
