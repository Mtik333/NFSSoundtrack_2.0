$(document).ready(function () {
	/**
 * i got this function from somwhere, it's about sorting table by specific column
 * @param {*} index
 * @returns result of filtering
 */

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
	 * when we click on header of row in genre page - see info in 'customPlaylist.js'
	 */
	$(document).on("click", "#searchSongTitlesTable thead tr th:not(.no-sort)", function () {
		var table = $(this).parents("#searchSongTitlesTable");
		var rows = $(this).parents("#searchSongTitlesTable").find("tbody tr").toArray().sort(TableComparer($(this).index()));
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

	$(document).on("click", "#searchSongLyricsTable thead tr th:not(.no-sort)", function () {
		var table = $(this).parents("#searchSongLyricsTable");
		var rows = $(this).parents("#searchSongLyricsTable").find("tbody tr").toArray().sort(TableComparer($(this).index()));
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

	$(document).find("th.band").click();
});