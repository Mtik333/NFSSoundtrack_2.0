$(document).ready(function(){function a(a){return function(c,d){var e=b(c,a),f=b(d,a),g=$.isNumeric(e)&&$.isNumeric(f)?e-f:e.toString().localeCompare(f);return g}}function b(a,b){return $(a).children("td").eq(b).text()}$(document).on("click","#genreTable thead tr th:not(.no-sort)",function(){var b=$(this).parents("#genreTable"),c=$(this).parents("#genreTable").find("tbody tr").toArray().sort(a($(this).index())),d=$(this).hasClass("sort-asc")?"desc":"asc";"desc"==d&&(c=c.reverse());for(var e=0;e<c.length;e++)b.append(c[e]);b.find("thead tr th").removeClass("sort-asc").removeClass("sort-desc"),$(this).removeClass("sort-asc").removeClass("sort-desc").addClass("sort-"+d)}),$(document).find("th.id").click()});