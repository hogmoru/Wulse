function Overview(options) {
    var self = this;
    var project = options.project;
    var divID = options.divID;
    $('#'+divID).html('<span class="glyphicon glyphicon-refresh"></span>&nbsp;Loading...');
    $.getJSON("steps?pretty&project="+project, function(data, textStatus, jqXHR) {
        self.updateView(divID, data, textStatus, jqXHR);
    }).fail(function() {
        $('#'+divID).html('<div class="alert alert-danger"><strong>Oh snap!</string> Failed to load data...</div>');
    });
}

Overview.prototype.updateView = function(divID, data, textStatus, jqXHR) {
    var steps = data.steps;
    $('#'+divID).html('<ul class="list-group"></ul>');
    var selector = '#'+divID+" ul";
    for (var i=0; i<steps.length; i++) {
        var step = steps[i];
        $(selector).append(
            '<li class="list-group-item" style="background-color:'
             + step.color + '"><span class="badge">'
             + step.success + '/' + step.total + '</span>'
             + step.name + '</li>'
        );
    }
}