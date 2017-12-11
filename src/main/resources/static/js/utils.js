
function searchQuery(){
    var query = $("#queryText");
    console.log(query.val());
    $.post("/api/query", {
        query: query.val()
    }).done(success).fail(failed)
}

function success(data) {
    $("#init-content-window").hide();
    dust.render('results', data, function(err,out) {
        $("#resultsSpace").html(out);
    });
}

function failed(data) {
    $("#init-content-window").hide();
    dust.render('results', null, function(err,out) {
        $("#resultsSpace").html(out);
    });
}

document.addEventListener("keydown", function(e) {
    var keyCode = e.keyCode;
    if(keyCode==13) {
        searchQuery();
    }
}, false);
