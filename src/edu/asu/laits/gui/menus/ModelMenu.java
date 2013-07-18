/**
 * (c) 2013, Arizona Board of Regents for and on behalf of Arizona State
 * University. This file is part of LAITS.
 *
 * LAITS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * LAITS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with LAITS. If not, see <http://www.gnu.org/licenses/>.
 */
package edu.asu.laits.gui.menus;

import edu.asu.laits.editor.ApplicationContext;
import edu.asu.laits.editor.GraphEditorPane;
import edu.asu.laits.editor.GraphRangeEditor;
import edu.asu.laits.gui.GraphView;
import edu.asu.laits.gui.MainWindow;
import edu.asu.laits.gui.nodeeditor.NodeEditor;
import edu.asu.laits.model.Graph;
import edu.asu.laits.model.ModelEvaluationException;
import edu.asu.laits.model.ModelEvaluator;
import edu.asu.laits.model.SolutionNode;
import edu.asu.laits.model.TaskSolution;
import edu.asu.laits.model.TaskSolutionReader;
import edu.asu.laits.model.Vertex;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;

/**
 * Menu for all Model Functionalities. Contains Menu Items for Run Model,
 * TimeRangeEditor and Solution File Generation.
 *
 * @author rptiwari
 *
 */
public class ModelMenu extends JMenu {

    private JMenuItem addNodeMenuItem = null;
    private JMenuItem runModelMenuItem = null;
    private JMenuItem editTimeRangeMenuItem = null;
    private JMenuItem exportSolutionMenuItem = null;
    private JMenu deleteNodeMenu = null;
    private JMenuItem showGraphMenuItem = null;
    private GraphEditorPane graphPane;
    private MainWindow mainWindow;
    private static Logger logs = Logger.getLogger("DevLogs");
    private static Logger activityLogs = Logger.getLogger("ActivityLogs");
    private HashMap<String, JMenuItem> menuMap = new HashMap<String, JMenuItem>();

    /**
     * This method initializes
     *
     */
    public ModelMenu(GraphEditorPane pane, MainWindow main) {
        super();
        graphPane = pane;
        mainWindow = main;
        initialize();
    }

    /**
     * This method initializes this
     *
     */
    private void initialize() {
        this.setText("Model");
        this.setMnemonic(KeyEvent.VK_M);
        this.add(getAddNodeMenuItem());
        this.add(getDeleteNodeMenu());
        this.add(getRunModelMenuItem());
        this.add(getEditTimeRangeMenuItem());
        this.add(getExportSolutionMenuItem());
        this.add(getshowGraphMenuItem());
        
        disableShowGraphMenu();
        disableDeleteNodeMenu();
    }

    /**
     * This method initializes addNodeMenuItem
     *
     */
    private JMenuItem getAddNodeMenuItem() {
        if (addNodeMenuItem == null) {
            addNodeMenuItem = new JMenuItem();
            addNodeMenuItem.setText("Create Node ");

            addNodeMenuItem
                    .addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    newNodeAction();
                }
            });
        }
        return addNodeMenuItem;
    }

    private JMenu getDeleteNodeMenu() {
        if (deleteNodeMenu == null) {
            deleteNodeMenu = new JMenu("Delete Node");

        }

        return deleteNodeMenu;
    }

    /**
     * This method initializes runModelMenuItem
     *
     */
    private JMenuItem getRunModelMenuItem() {
        if (runModelMenuItem == null) {
            runModelMenuItem = new JMenuItem();
            runModelMenuItem.setText("Show Graph");
            runModelMenuItem
                    .addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    activityLogs.debug("User pressed Run Model Button.");
                    showNodeGraph();
                }
            });
        }
        return runModelMenuItem;
    }

    /**
     * This method initializes selectNeighbourSelectionMenuItem
     *
     */
    private JMenuItem getEditTimeRangeMenuItem() {
        if (editTimeRangeMenuItem == null) {
            editTimeRangeMenuItem = new JMenuItem();
            editTimeRangeMenuItem.setText("Edit Time Range ");
            editTimeRangeMenuItem
                    .addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    activityLogs.debug("User pressed Edit Time Range Button.");
                    editTimeRangeAction();
                }
            });
        }
        return editTimeRangeMenuItem;
    }

    /**
     * This method initializes selectOtherSelectionMenuItem
     */
    private JMenuItem getExportSolutionMenuItem() {
        if (exportSolutionMenuItem == null) {
            exportSolutionMenuItem = new JMenuItem();
            exportSolutionMenuItem.setText("Export Solution");
            exportSolutionMenuItem
                    .addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    activityLogs.debug("User pressed Export Solution Button.");
                }
            });
        }
        return exportSolutionMenuItem;
    }

    /**
     * This method initializes selectOtherSelectionMenuItem
     */
    private JMenuItem getshowGraphMenuItem() {
        if (showGraphMenuItem == null) {
            showGraphMenuItem = new JMenuItem();
            showGraphMenuItem.setText("Export Solution");
            showGraphMenuItem
                    .addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showNodeGraph();
                }
            });
        }
        return showGraphMenuItem;
    }

    public void showNodeGraph() {
        activityLogs.debug("User pressed Run Model button.");
        
        if(runModel()) {
            showChartDialog();
        } 
    }

    public boolean runModel() {
        // Check if Model has already been executed and is still valid
        if(isGraphPrepared())
            return true;
        
        ModelEvaluator me = new ModelEvaluator((Graph) graphPane.getModelGraph());
        MainWindow window = (MainWindow) graphPane.getMainFrame();
        if (me.isModelComplete()) {
            if (!me.hasExtraNodes()) {
                try {
                    me.run();

                    if (ApplicationContext.getAppMode().equals("STUDENT") || ApplicationContext.getAppMode().equals("COACHED")) {
                        me.validateStudentGraph();
                    }

                    window.getStatusBarPanel().setStatusMessage("", true);
                    activityLogs.debug("Model ran successfully.");

                    // Enable Done Button
                    if (ApplicationContext.isProblemSolved()) {
                        mainWindow.getModelToolBar().enableDoneButton();
                    }

                } catch (ModelEvaluationException ex) {
                    window.getStatusBarPanel().setStatusMessage(ex.getMessage(), false);
                }
                graphPane.repaint();
            } else {
                activityLogs.debug("Model had extra nodes, so user could not run the model.");
                JOptionPane.showMessageDialog(window, "Model has extra nodes in it, please remove them before running the model.");
            }

            return true;
        } else {
            activityLogs.debug("Model was incomplete, so user could not run the model.");
            JOptionPane.showMessageDialog(window, "The model is incomplete, please complete all the nodes before running Model");
            window.getStatusBarPanel().setStatusMessage("Please complete all the nodes before running Model", false);
            return false;
        }
    }

    private boolean isGraphPrepared() {
        boolean isEnable = true;

        Iterator<Vertex> allVertices = graphPane.getModelGraph().vertexSet().iterator();
        while (allVertices.hasNext()) {
            Vertex v = allVertices.next();
            if (v.getGraphsStatus().equals(Vertex.GraphsStatus.UNDEFINED)) {
                isEnable = false;
                break;
            }
        }

        return isEnable;
    }
    
    private void showChartDialog(){
        JDialog graphValuesDialog = new JDialog(graphPane.getMainFrame(), true);
        GraphView gPanel = new GraphView(graphPane.getModelGraph(), graphValuesDialog);
        graphValuesDialog.setTitle("Model Graph");
        graphValuesDialog.setSize(610,510);
        graphValuesDialog.setLocationRelativeTo(null);

            graphValuesDialog.setResizable(false);
            graphValuesDialog.setVisible(true);
    }

    public void menuSelectionChanged(boolean value) {
        super.menuSelectionChanged(value);
        if (value) {
            Object[] selectedVertices = graphPane.getSelectionCells(graphPane
                    .getGraphLayoutCache().getCells(false, true, false, false));

            boolean verticesSelected = !((selectedVertices == null)
                    || (selectedVertices.length == 0));

            getEditTimeRangeMenuItem().setEnabled(true);
            getExportSolutionMenuItem().setEnabled(verticesSelected);
        }
    }

    public void newNodeAction() {
        activityLogs.debug("User Pressed Create Node Button");
        MainWindow window = (MainWindow) graphPane.getMainFrame();
        if (newNodeAllowed()) {
            activityLogs.debug("User is allowed to create a new node");
            Vertex v = new Vertex();
            v.setVertexIndex(graphPane.getModelGraph().getNextAvailableIndex());
            graphPane.addVertex(v);
            graphPane.getMainFrame().getModelToolBar().disableDeleteNodeButton();
            disableDeleteNodeMenu();

            if (graphPane.getMainFrame().isSituationSelected()) {
                logs.debug("Switing to Model Design Panel");
                graphPane.getMainFrame().switchTutorModelPanels(false);
            }

            graphPane.repaint();
            NodeEditor editor = new NodeEditor(graphPane, true);
        } else {
            activityLogs.debug("User was not allowed to create new node as all the nodes were already present");
            JOptionPane.showMessageDialog(window, "The model is already using all the correct nodes.");
        }

    }

    public void deleteNodeAction() {

        Object[] cells = graphPane.getSelectionCells();
        for (Object obj : cells) {
            DefaultGraphCell cell = (DefaultGraphCell) obj;
            Vertex v = (Vertex) cell.getUserObject();
            if (v != null) {
                activityLogs.debug("User pressed Delete button for Node " + v.getName());

                if (menuMap.get(v.getName()) != null) {
                    deleteNodeMenu.remove(menuMap.get(v.getName()));
                }

            }

        }

        graphPane.removeSelected();

        Iterator it = graphPane.getModelGraph().vertexSet().iterator();
        Vertex v;
        while (it.hasNext()) {
            v = (Vertex) it.next();
            v.getCorrectValues().clear();
            v.setGraphsStatus(Vertex.GraphsStatus.UNDEFINED);
        }

        activityLogs.debug("Closing NodeEditor because of Delete action.");


    }

    public void addDeleteNodeMenu() {
        DefaultGraphCell cell = (DefaultGraphCell) graphPane.getSelectionCell();
        Vertex currentVertex = (Vertex) cell.getUserObject();

        JMenuItem menu = new JMenuItem(currentVertex.getName());
        menuMap.put(currentVertex.getName(), menu);

        menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JMenuItem m = (JMenuItem) e.getSource();

                Object[] cells = graphPane.getGraphLayoutCache().getCells(true, true, true, true);
                for (Object obj : cells) {
                    DefaultGraphCell cell = (DefaultGraphCell) obj;
                    Vertex v = (Vertex) cell.getUserObject();

                    if (v != null) {
                        //JMenuItem m = (JMenuItem)e.getSource();
                        if (v.getName() == m.getText()) {
                            graphPane.setSelectionCell(obj);
                            graphPane.removeSelected();
                            deleteNodeMenu.remove(menuMap.get(v.getName()));
                        }

                    }
                }
            }
        });
        deleteNodeMenu.add(menu);

    }

    public void removeAllDeleteMenu() {
        deleteNodeMenu.removeAll();
    }

    public void enableDeleteNodeMenu() {
        deleteNodeMenu.setEnabled(true);
    }

    public void disableDeleteNodeMenu() {
        deleteNodeMenu.setEnabled(false);
    }
    
    public void enableShowGraphMenu() {
        showGraphMenuItem.setEnabled(true);
    }

    public void disableShowGraphMenu() {
        showGraphMenuItem.setEnabled(false);
    }

    public boolean newNodeAllowed() {
        if (ApplicationContext.getAppMode().equals("AUTHOR")) {
            return true;
        }

        TaskSolution solution = ApplicationContext.getCorrectSolution();
        if (graphPane.getModelGraph().vertexSet().size()
                < solution.getSolutionNodes().size()) {
            return true;
        }

        return false;
    }

    
    public void editTimeRangeAction() {
        activityLogs.debug("User pressed EditTimeRange Menu Item.");
        GraphRangeEditor ed = new GraphRangeEditor(graphPane, true);
        ed.setVisible(true);
    }

    public void doneButtonAction() {
        activityLogs.debug("User Pressed Done button with current task as " + ApplicationContext.getCurrentTaskID());
        writeResultToServer();
        System.exit(0);
    }

    private void createGivenModel(TaskSolution solution, GraphEditorPane editorPane) {
        List<SolutionNode> givenNodes = solution.getGivenNodes();

        for (SolutionNode node : givenNodes) {
            Vertex v = new Vertex();
            v.setVertexIndex(graphPane.getModelGraph().getNextAvailableIndex());

            v.setName(node.getNodeName());
            v.setCorrectDescription(node.getCorrectDescription());
            v.setPlan(node.getNodePlan());
            v.setDescriptionStatus(Vertex.DescriptionStatus.CORRECT);
            v.setPlanStatus(Vertex.PlanStatus.CORRECT);
            v.setEquation(node.getNodeEquation());
            v.setInitialValue(node.getInitialValue());

            v.setVertexType(node.getNodeType());

            if (solution.checkNodeInputs(node.getNodeName(), node.getInputNodes()) == 0) {
                v.setInputsStatus(Vertex.InputsStatus.CORRECT);
            } else {
                v.setInputsStatus(Vertex.InputsStatus.INCORRECT);
            }

            if (solution.checkNodeCalculations(v)) {
                v.setCalculationsStatus(Vertex.CalculationsStatus.CORRECT);
            } else {
                v.setCalculationsStatus(Vertex.CalculationsStatus.INCORRECT);
            }

            editorPane.addVertex(v);
        }

        for (SolutionNode node : givenNodes) {
            List<String> inputVertices = node.getInputNodes();
            for (String vertexName : inputVertices) {
                Vertex v1 = editorPane.getModelGraph().getVertexByName(node.getNodeName());
                Vertex v2 = editorPane.getModelGraph().getVertexByName(vertexName);

                DefaultPort p1 = editorPane.getJGraphTModelAdapter().getVertexPort(v1);
                DefaultPort p2 = editorPane.getJGraphTModelAdapter().getVertexPort(v2);

                editorPane.insertEdge(p2, p1);
            }
        }
    }

    private void writeResultToServer() {
        logs.debug("Student " + ApplicationContext.getUserID() + " Completed Task: " + ApplicationContext.getCurrentTaskID());
    }

    public MainWindow getMainWindow() {
        return mainWindow;
    }
}
