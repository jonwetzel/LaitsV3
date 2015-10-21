// Set up initial variables

 // Creating a selenium client utilizing webdriverjs
var client = require('webdriverio').remote({
    //logLevel: "verbose",
    desiredCapabilities: {
        // See other browers at:
        // http://code.google.com/p/selenium/wiki/DesiredCapabilities
        browserName: 'chrome'

    }
});

// import chai assertion library
var assert = require('chai').assert;
// import dragoon test library module
var dtest = require('../../dtestlib.js');
// import dragoon assertion library
var atest = require('../../assertTestLib.js');
// import sync library
var sync = require('synchronize');
// import wrapper for asynchronous functions
var async = sync.asyncIt;

describe("Coached mode with correct diode resistor 2", function() {

    before(async(function (done) {
            dtest.openProblem(client,[["problem","diode-resistor"],["mode","COACHED"],
                                      ["section","PAL3-test"],
                                      ["logging","true"]]);
    }));

     describe("Creating nodes:", function(){
        it("Should create Function node - I around loop", async(function(){
            dtest.openEditorForNode(client, "I around loop");
            dtest.setNodeType(client, "Function");
            dtest.popupWindowPressOk(client);
            dtest.setNodeUnits(client, "amps");
            dtest.popupWindowPressOk(client);
            dtest.setNodeExpression(client, "V across R1/R of R1");
            dtest.checkExpression(client);
            dtest.nodeEditorDone(client);
        }));

        it("Should fill in function node - V across R1", async(function(){
            dtest.openEditorForNode(client, "V across R1");
            dtest.setNodeType(client, "Function");
            dtest.setNodeUnits(client, "volts");
            dtest.setNodeExpression(client, "Source V-V across D1");
            dtest.checkExpression(client);
            dtest.nodeEditorDone(client);

        }));

        it("Should fill in parameter node - R of R1", async(function(){
            dtest.openEditorForNode(client, "R of R1");
            dtest.setNodeType(client, "Parameter");
            dtest.setNodeInitialValue(client, 500);
            dtest.setNodeUnits(client, "ohms");
            dtest.nodeEditorDone(client);
            dtest.popupWindowPressOk(client);
        }));

        it("Should fill in parameter node - V across D1", async(function(){
            dtest.openEditorForNode(client, "V across D1");
            dtest.setNodeType(client, "Function");
            dtest.setNodeUnits(client, "volts");
            dtest.setNodeExpression(client, "Source V-V across D1");
            dtest.nodeEditorDone(client);
            dtest.popupWindowPressOk(client);
        }));

    });

    describe("Checking node colors", function(){
        it("Nodes should have correct border and fill colors", async(function(){
            //Defines which nodes to check
            var nodesToCheck = ["current", "voltage at VL", "Resistance"];
            //Does test for all nodes
            nodesToCheck.forEach(function(element){
                //Gets values
                var nodeBorderColor = dtest.getNodeBorderColor(client, element);
                var nodeBorderStyle = dtest.getNodeBorderStyle(client, element);
                var nodeFillColor = dtest.getNodeFillColor(client, element);
                //Asserts values
                assert(nodeBorderColor === "green",
                    "Node border color for " + element + " was " + nodeBorderColor + " instead of green");
                assert(nodeBorderStyle === "solid",
                    "Node border style for " + element + " was " + nodeBorderStyle + " instead of solid");
                assert(nodeFillColor === "green",
                    "Node fill color for " + element + " was " + nodeFillColor + " instead of green");
            });
        }));
    });

    describe("Checking Nodes:", function(){
        
        afterEach(async(function(){
            dtest.nodeEditorDone(client);
        }));

        it("Should have correct Function values and colors", async(function(){
            var nodeName = "current"
            dtest.openEditorForNode(client, nodeName);

            atest.checkNodeValues([["nodeName", "current"],
                                    ["expectedDescription", "Current through resistor, diode and battery"],
                                    ["expectedNodeType", "Function"],
                                    ["expectedNodeUnits", "amps"],
                                    ["expectedExpression", "voltage at VL/Resistance"],
                                    ["expectedDescriptionColor", "green"],
                                    ["expectedTypeColor", "green"],
                                    ["expectedInitialColor", "gray"],
                                    ["expectedUnitsColor", "green"],
                                    ["expectedExpressionColor", "green"]], dtest, client);
        }));

        it("Should have correct function values and colors", async(function(){
            var nodeName = "voltage at VL"

            dtest.openEditorForNode(client, nodeName);

            atest.checkNodeValues([["nodeName", "voltage at VL"],
                                    ["expectedDescription", "Voltage at node VL"],
                                    ["expectedNodeType", "Function"],
                                    ["expectedNodeUnits", "volts"],
                                    ["expectedExpression", "max(0,voltage source)"],
                                    ["expectedDescriptionColor", "green"],
                                    ["expectedTypeColor", "green"],
                                    ["expectedInitialColor", "gray"],
                                    ["expectedUnitsColor", "green"],
                                    ["expectedExpressionColor", "green"]], dtest, client);
        }));

        it("Should have correct parameter values and colors", async(function(){
            var nodeName = "Resistance";

            dtest.openEditorForNode(client, nodeName);

            atest.checkNodeValues([["nodeName", "Resistance"],
                                    ["expectedDescription", "Resistance of the resistor"],
                                    ["expectedNodeType", "Parameter"],
                                    ["expectedInitialValue", "500"],
                                    ["expectedNodeUnits", "ohms"],
                                    ["expectedDescriptionColor", "green"],
                                    ["expectedTypeColor", "green"],
                                    ["expectedInitialColor", "green"],
                                    ["expectedUnitsColor", "green"],
                                    ["expectedExpressionColor", "gray"]], dtest, client);            
        }));
    });

    describe("Checking graph/table window:", function(){
        it("Should open the table window and check the table values", async(function(){
            dtest.menuOpenTable(client);
            var currentValue = true;
            var time = 0;
            var message = "";
            if(!(dtest.tableGetValue(client, 0, 3) == "0.00")){
                message += "\n First - required: 0.00, found: "+dtest.tableGetValue(client, 0, 0);
                currentValue = false;
            }
            if(!(dtest.tableGetValue(client, 199 , 3) == "0.00")){
                message += "\n Last - required: 0.00, found: "+dtest.tableGetValue(client, 199, 0);
                currentValue = false;
            }

            assert(currentValue === true,
                "Values in the \"current (amps)\" column were incorrect. " + message);
        }));
    });
    after(async(function(done){
        dtest.endTest(client);
    }));
});