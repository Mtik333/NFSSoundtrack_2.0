$(document).ready(function () {
	/**
	 * i got this function from somwhere, it's about sorting table by specific column
	 * @param {*} index 
	 * @returns result of filtering
	 */

	if ('ontouchstart' in window) {
		$("td.song-instrumental").css("display", "none");
		$("th.song-instrumental").css("display", "none");
		$("col.song-instrumental").css("display", "none");
		$("#mobileShowSongInfo").css("display", "none");
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
	 * when we click on header of row in song page, see documentation in customPlaylist.js
	 */
	$(document).on("click", "#songTable thead tr th:not(.no-sort)", function () {
		var table = $(this).parents("#songTable");
		var rows = $(this).parents("#songTable").find("tbody tr").toArray().sort(TableComparer($(this).index()));
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

	$(document).find("th.id").click();
});