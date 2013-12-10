/*
Hugo Hromic <hugo.hromic@deri.org>
Unit for Information Mining and Retrieval (UIMR)
Digital Enterprise Research Institute (DERI)
NUI Galway, Ireland
*/

//*****************************************************************************
// Browser
(function( Browser, $, undefined ) {
    //*************************************************************************
    // Public properties
    Browser.version = "2.0";

    //*************************************************************************
    // Private properties

    // Constants
    var DEBUG = false;
    var REST_SERVICE = "rest/service.php";
    var NOSERIES_MSG = "Please create some series to begin";
    var USERMESSAGE_DELAY = 8000; // In milliseconds
    var ID_PREFIX = "Browser_";
    var ID_SERIESPANELS = ID_PREFIX + "seriespanels";
    var ID_SERIESPANEL = ID_SERIESPANELS + "-";
    var CLASS_PREFIX = ID_PREFIX;
    var BASE_SERIE = {
        panelid: null, title: "(untitled)",
        parameters: {
            dataset: null, community: null, feature: null,
            timestep: null, from: null, to: null },
        consolidation: { name: null, refvalue: null },
        rawdata: null, HSserie: null, plotted: false
    };

    // Internal variables
    var chartObject = null;
    var chartSeries = {};
    var newSeriePanelId = 1;
    var helpWindow = null;

    //*************************************************************************
    // Public methods

    // Start the browser program
    Browser.start = function(chartDiv,controlsDiv,helpDiv) {
        if(DEBUG) console.log("Browser starting");

        // Create the main Browser controls
        createChart(chartDiv);
        createControls(controlsDiv);

        // Initialize the help window
        helpWindow = $("#"+helpDiv).dialog({
            autoOpen: false, modal: true, hide: true,
            title: "Browser Help", width: 700, height: 500
        });
    }

    // Get the chartSeries for debugging
    Browser.getCS = function() { return chartSeries; }

    //*************************************************************************
    // Private methods

    // Create and initialize the HighCharts object
    function createChart(divName) {
        if(DEBUG) console.log("Creating chart object");

        // Base chart options
        var options = {
            global: {
                canvasToolsURL: "scripts/highcharts-canvas-tools.js"
            },
            chart: {
                renderTo: divName,
                defaultSeriesType: "line",
                zoomType: "x"
            },
            legend: { borderWidth: 0 },
            loading: { labelStyle: {
                position: "relative", top: "45%", 
                color: "#808080", fontSize: "250%"
            } },
            plotOptions: { line: { marker: { enabled: false } } },
            title: { text: "User Features over Time" },
            xAxis: { type: "datetime" },
            yAxis: { title: { text: "Feature Value" } }
        };
        chartObject = new Highcharts.Chart(options);
        chartObject.showLoading(NOSERIES_MSG);
    }

    // Create and initialize the control widgets
    function createControls(controlsDiv) {
        if(DEBUG) console.log("Creating series controls");

        var controls = $("#"+controlsDiv);

        // Create the series panel container
        controls.append($("<div/>",{id:ID_SERIESPANELS})
            .append($("<ul/>")));
        $("#"+ID_SERIESPANELS).tabs({
            add: function(event,ui){
                $(this).tabs("select","#"+ui.panel.id);
            },
            closable: true,
            closableClick: function(e,ui) {
                // Delete the chart structure and serie when closing tabs
                var panel = $(ui.panel).attr("id");
                var panelid = parseInt(panel.replace(ID_SERIESPANEL,""));
                if(DEBUG) console.log("Closing serie tab "+panelid)
                if( chartSeries[panelid].HSserie != null )
                    chartSeries[panelid].HSserie.remove();
                delete chartSeries[panelid];
                if( chartObject.series.length == 0 )
                    chartObject.showLoading(NOSERIES_MSG);
                return true;
            }
        });

        // Append the "Add a new serie" button
        controls.append($("<button/>").click(addNewSerie)
                .button({label:"Add a new serie"})
                .addClass(CLASS_PREFIX+"newserie_button")
        );

        // Append the "Show help" button
        controls.append($("<button/>").click(function(){
                if( helpWindow.dialog("isOpen") )
                    helpWindow.dialog("close");
                else helpWindow.dialog("open");
            })
            .button({label:"Help!"})
            .addClass(CLASS_PREFIX+"help_button")
        );

        // Append the user messages container
        controls.append($("<div/>",{id:ID_PREFIX+"usermessage"}).hide());
    }

    // Add a new serie to the series panel
    function addNewSerie() {
        // Create a new serie data structure
        var serie = $.extend( true, {}, BASE_SERIE );
        serie.panelid = newSeriePanelId;
        serie.title = "Serie "+newSeriePanelId;
        chartSeries[serie.panelid] = serie;
        newSeriePanelId++;
        if(DEBUG)
            console.log("Adding new serie "+serie.panelid+" to series panel");

        // Create a new serie panel
        var panel = {
            id: ID_SERIESPANEL + serie.panelid,
            title: serie.title, elements: []
        };
        $("#"+ID_SERIESPANELS).append($("<div/>", {id:panel.id}));
        $("#"+ID_SERIESPANELS).tabs("add", "#"+panel.id, panel.title);

        // Title parameter
        panel.elements.push( $("<input/>",
             { id:panel.id+"-title",value: panel.title })
             .keyup(function(){
                 if(DEBUG) console.log("Keyup in 'title' input box");
                 var panel = $(this).parent().attr("id");
                 var panelid = parseInt(panel.replace(ID_SERIESPANEL,""));
                 var title = $(this).val().trim();
                 chartSeries[panelid].title = title;
             })
             .addClass(CLASS_PREFIX+"parm_title")
        );

        // Dataset parameter
        panel.elements.push( $("<select/>",{id:panel.id+"-dataset"})
             .on("populate.Browser", function(e,data) {
                 if(DEBUG) console.log("Populating 'dataset' select box");
                 e.stopPropagation();
                 var sb = $(this).empty();
                 $.each(data, function() {
                     sb.append( $("<option/>")
                       .val(this.valueOf()).text(this.valueOf()) );
                 });
                 sb.selectmenu();
                 sb.change();
             })
             .change(function(){
                 if(DEBUG) console.log("Change in 'dataset' select box");
                 var panel = $(this).parent().attr("id");
                 var panelid = parseInt(panel.replace(ID_SERIESPANEL,""));
                 var dataset = $(this).selectmenu("value");
                 chartSeries[panelid].parameters.dataset = dataset;
                 loadParameterData( $("#"+panel+"-community"),
                                    "get_communities",
                                    chartSeries[panelid].parameters);
             })
             .addClass(CLASS_PREFIX+"parm_dataset")
        );

        // Community parameter
        panel.elements.push( $("<select/>",{id:panel.id+"-community"})
             .on("populate.Browser", function(e,data) {
                 if(DEBUG) console.log("Populating 'community' select box");
                 e.stopPropagation();
                 var sb = $(this).empty();
                 $.each(data, function() {
                     sb.append( $("<option/>")
                            .val(this.id).text(this.name));
                 });
                 sb.selectmenu();
                 sb.change();
             })
             .change(function(){
                 if(DEBUG) console.log("Change in 'community' select box");
                 var panel = $(this).parent().attr("id");
                 var panelid = parseInt(panel.replace(ID_SERIESPANEL,""));
                 var community = $(this).selectmenu("value");
                 chartSeries[panelid].parameters.community = community;
                 loadParameterData( $("#"+panel+"-feature"),
                                    "get_features",
                                    chartSeries[panelid].parameters);
             })
             .addClass(CLASS_PREFIX+"parm_community")
        );

        // Feature parameter
        panel.elements.push( $("<select/>",{id:panel.id+"-feature"})
             .on("populate.Browser", function(e,data) {
                 if(DEBUG) console.log("Populating 'feature' select box");
                 e.stopPropagation();
                 var sb = $(this).empty();
                 $.each(data, function() {
                     sb.append( $("<option/>")
                            .val(this.valueOf()).text(this.valueOf()) );
                 });
                 sb.selectmenu();
                 sb.change();
             })
             .change(function(){
                 if(DEBUG) console.log("Change in 'feature' select box");
                 var panel = $(this).parent().attr("id");
                 var panelid = parseInt(panel.replace(ID_SERIESPANEL,""));
                 var feature = $(this).selectmenu("value");
                 chartSeries[panelid].parameters.feature = feature;
                 loadParameterData( $("#"+panel+"-timestep"),
                                    "get_timeranges",
                                    chartSeries[panelid].parameters);
             })
             .addClass(CLASS_PREFIX+"parm_feature")
        );

        // Line break
        panel.elements.push( $("<br/>"));

        // Time Range parameters
        panel.elements.push( $("<select/>",{id:panel.id+"-timestep"})
             .on("populate.Browser", function(e,data) {
                 if(DEBUG) console.log("Populating 'timestep' select box");
                 e.stopPropagation();
                 var sb = $(this).empty();
                 $.each(data, function(timestep,ranges) {
                    sb.data(timestep,ranges);
                    if(ranges.from.length > 0 )
                        sb.append( $("<option/>")
                           .val(timestep).text(timestep) );
                 });
                 sb.selectmenu();
                 sb.change();
             })
             .change(function(){
                 if(DEBUG) console.log("Change in 'timestep' select box");
                 var panel = $(this).parent().attr("id");
                 var panelid = parseInt(panel.replace(ID_SERIESPANEL,""));
                 var timestep = $(this).selectmenu("value");
                 chartSeries[panelid].parameters.timestep = timestep;
                 $("#"+panel+"-from").trigger("populate.Browser",
                            [$(this).data(timestep).from]);
                 $("#"+panel+"-to").trigger("populate.Browser",
                            [$(this).data(timestep).to]);
             })
             .addClass(CLASS_PREFIX+"parm_timestep")
        );
        panel.elements.push( $("<select/>",{id:panel.id+"-from"})
             .on("populate.Browser",function(e,data) {
                 if(DEBUG) console.log("Populating 'from' select box");
                 e.stopPropagation();
                 var sb = $(this).empty();
                 $.each(data, function() {
                     sb.append( $("<option/>")
                       .val(this.valueOf()).text(this.valueOf()) );
                 });
                 sb.selectmenu();
                 sb.change();
             })
             .change(function(){
                 if(DEBUG) console.log("Change in 'from' select box");
                 var panel = $(this).parent().attr("id");
                 var panelid = parseInt(panel.replace(ID_SERIESPANEL,""));
                 var from = $(this).selectmenu("value");
                 var to = chartSeries[panelid].parameters.to;
                 if( dateGreater(from, to) ) {
                     $(this).selectmenu("value",
                                        chartSeries[panelid].parameters.from);
                     displayFlashMessage("Warning: 'To' date must be greater" +
                            " than 'From' to properly display data.","notice");
                 } else chartSeries[panelid].parameters.from = from;
             })
             .addClass(CLASS_PREFIX+"parm_from")
        );
        panel.elements.push( $("<select/>",{id:panel.id+"-to"})
             .on("populate.Browser",function(e,data) {
                 if(DEBUG) console.log("Populating 'to' select box");
                 e.stopPropagation();
                 var sb = $(this).empty();
                 $.each(data, function() {
                     sb.append( $("<option/>")
                       .val(this.valueOf()).text(this.valueOf()) );
                 });
                 sb.selectmenu();
                 sb.selectmenu("value",data[data.length-1]);
                 sb.change();
             })
             .change(function(){
                 if(DEBUG) console.log("Change in 'to' select box");
                 var panel = $(this).parent().attr("id");
                 var panelid = parseInt(panel.replace(ID_SERIESPANEL,""));
                 var from = chartSeries[panelid].parameters.from;
                 var to = $(this).selectmenu("value");
                 if( dateGreater(from, to) ) {
                     $(this).selectmenu("value",
                                        chartSeries[panelid].parameters.to);
                     displayFlashMessage("Warning: 'To' date must be greater" +
                            " than 'From' to properly display data.","notice");
                 } else chartSeries[panelid].parameters.to = to;
             })
             .addClass(CLASS_PREFIX+"parm_to")
        );

        // Consolidation parameters
        var consolidations = $("<select/>",{id:panel.id+"-consolidation"})
             .addClass(CLASS_PREFIX+"parm_consolidation")
             .change(function(){
                 if(DEBUG) console.log("Change in 'consolidation' select box");
                 var panel = $(this).parent().attr("id");
                 var panelid = parseInt(panel.replace(ID_SERIESPANEL,""));
                 var consolidation = $(this).selectmenu("value");
                 chartSeries[panelid].consolidation.name = consolidation;

                 // Need refvalue?
                 if( Consolidation[consolidation].reqRefValue ) {
                     $("#"+panel+"-refvalue").keyup();
                     $("#"+panel+"-refvalue").show();
                 } else {
                     chartSeries[panelid].consolidation.refvalue = null;
                     $("#"+panel+"-refvalue").hide();
                 }
             });
        $.each(Consolidation,function(consolidation){
            consolidations.append( $("<option/>").val(consolidation)
                          .text(consolidation));
        });
        panel.elements.push( consolidations );
        panel.elements.push( $("<input/>",{id:panel.id+"-refvalue",value:"0"})
             .keyup(function(){
                 if(DEBUG) console.log("Keyup in 'refvalue' input box");
                 var panel = $(this).parent().attr("id");
                 var panelid = parseInt(panel.replace(ID_SERIESPANEL,""));
                 var refvalue = parseFloat($(this).val().trim());
                 chartSeries[panelid].consolidation.refvalue = refvalue;
             })
             .addClass(CLASS_PREFIX+"parm_refvalue")
        );

        // Append all parameters to panel
        $.each(panel.elements,function(){ $("#"+panel.id).append(this);
            // Apply selectmenu() to select boxes
            if( this.is("select") ) this.selectmenu({
                    style: "dropdown", menuWidth: "auto", width:"auto",
                    wrapperElement: "<span/>"
                });
        });

        // Append the "Apply" button
        $("#"+panel.id).append($("<button/>").click(function(){
                        applyParameters( $(this).parent() )})
                       .button({label:"Apply"})
                       .addClass(CLASS_PREFIX+"apply_button"));

        // Load the dataset parameter (and begin the cascade!)
        $("#"+panel.id+"-consolidation").change();
        loadParameterData( $("#"+panel.id+"-dataset"), "get_datasets");
    }

    // Apply serie parameters to chart
    function applyParameters(panel) {
        // Update serie data structure with panel contents
        var panelid = parseInt(panel.attr("id").replace(ID_SERIESPANEL,""));
        if(DEBUG) console.log("Applying parameters for panel "+panelid);

        // Set tab title
        if( chartSeries[panelid].title == "" ) {
            chartSeries[panelid].title = "Serie " + panelid;
            $("#"+panel.attr("id")+"-title").val(chartSeries[panelid].title);
        }
        panel.parent().find("a[href=#"+panel.attr("id")+"]")
             .text(chartSeries[panelid].title);

        // Correct refvalue?
        if( chartSeries[panelid].consolidation.refvalue != null ) {
            if( isNaN(chartSeries[panelid].consolidation.refvalue) )
                chartSeries[panelid].consolidation.refvalue = 0;
            $("#"+panel.attr("id")+"-refvalue")
                .val(chartSeries[panelid].consolidation.refvalue);
        }

        // Load serie data from server and draw into chart
        loadSerieData(panelid);
    }

    // Compare if date 'd1' is greater (in time) that date 'd2'
    function dateGreater(d1,d2) {
        if( d1 == null || d2 == null ) return false;
        var d1 = new Date(d1);
        var d2 = new Date(d2);
        return d1 > d2;
    }

    // Parse a string date into a javascript date object
    function parseDate(date) {
        d = new Date(date);
        return Date.UTC(d.getFullYear(), d.getMonth(), d.getDate());
    }

    // Display a flash message to the user
    function displayFlashMessage(msg,type) {
        var div = $("#"+ID_PREFIX+"usermessage");
        div.removeClass("error notice success");
        div.addClass(type);
        div.text(msg);
        div.show();
        setTimeout(function(){
            $("#"+ID_PREFIX+"usermessage").fadeOut("slow");
        },USERMESSAGE_DELAY);
    }

    // Load parameter data into a select box
    function loadParameterData(selectbox,action,parms) {
        if(DEBUG) console.log("Loading parameter data");
        if( typeof(parms) == "undefined" ) parms = {};
        $.getJSON( REST_SERVICE, $.extend(true, {}, parms, {action:action}),
            function(response) {
                if(DEBUG) console.log("Received parameter data");
                selectbox.trigger("populate.Browser", [response]);
            });
    }

    // Load serie data
    function loadSerieData(serie) {
        if(DEBUG) console.log("Loading data for serie "+serie);

        $.getJSON( REST_SERVICE, $.extend(true, {},
            chartSeries[serie].parameters, {action:"get_data"}),
            function(response) {
                if(DEBUG) console.log("Received serie data");
                chartSeries[serie].rawdata = response;
                chartSeries[serie].plotted = false;
                plotChart();
            });
    }

    // Plot the chart using the chartSeries
    function plotChart() {
        if(DEBUG) console.log("Plotting the chart");
        if( chartObject == null ) {
            displayFlashMessage("No chart object initialized.","error");
            return;
        }

        // Build a consolidated array of values from rawdata
        function buildValues(rawdata,consolidation) {
            var values = [];
            $.each(rawdata, function(){
                values.push([ parseDate(this.from),
                    Consolidation[consolidation.name]
                        .compute(this, consolidation.refvalue) ]);
            });
            return values;
        }

        // Create new/update series as needed
        var chartChanged = false;
        $.each(chartSeries,function(idx,serie){
            // Serie needs plotting and have data?
            if( !serie.plotted && serie.rawdata != null ) {
                // Serie is new?
                if( serie.HSserie == null ) {
                    if(DEBUG) console.log("Plotting new serie "+serie.panelid);
                    var values = buildValues( serie.rawdata,
                        serie.consolidation);
                    serie.HSserie = chartObject.addSeries(
                        { name:serie.title, data:values }, false);
                } else {
                    if(DEBUG) console.log("Updating serie "+serie.panelid);
                    var values = buildValues( serie.rawdata,
                        serie.consolidation);
                    serie.HSserie.name = serie.title;
                    serie.HSserie.setData(values,false);
                }

                // Mark the serie as plotted
                serie.plotted = true; chartChanged = true;
            }
        })

        // Hide the "Loading" screen
        if( chartObject.series.length > 0 )
            chartObject.hideLoading();

        // Redraw the chart if needed
        if( chartChanged ) chartObject.redraw();

        // Fix bug on legend names redrawing [VERY DIRTY]
        $.each(chartObject.series,function(){
            $(this.legendItem.element).text(this.name);
        });
        chartObject.addSeries({data:[]},false).remove(true);
    }

}( window.Browser = window.Browser || {}, jQuery ));

//*****************************************************************************
// Start the browser
$(window).load(function(){
    Browser.start("chart","controls","help_msg");
});
