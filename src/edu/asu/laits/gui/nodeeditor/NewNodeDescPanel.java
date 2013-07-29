/**
 * LAITS Project
 * Arizona State University
 * (c) 2013, Arizona Board of Regents for and on behalf of Arizona State University.
 * This file is part of LAITS.
 *
 * LAITS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LAITS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with LAITS.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package edu.asu.laits.gui.nodeeditor;

import edu.asu.laits.editor.ApplicationContext;
import edu.asu.laits.editor.GraphEditorPane;
import edu.asu.laits.gui.BlockingToolTip;
import edu.asu.laits.model.Graph;
import edu.asu.laits.model.HelpBubble;
import edu.asu.laits.model.SolutionDTreeNode;
import edu.asu.laits.model.SolutionNode;
import edu.asu.laits.model.TaskSolution;
import edu.asu.laits.model.Vertex;
import java.awt.Color;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.*;

import org.apache.log4j.Logger;


/**
 *
 * @author ramayantiwari
 */
public class NewNodeDescPanel extends JPanel{

  TreePath[] decisionTreePaths;
  
  SolutionDTreeNode root = null;
  DefaultTreeModel model = null;
  private boolean triedDuplicate = false;
  private static NewNodeDescPanel descView;
  private CreateNewNodeDialog nodeEditor;
  
  private static Logger logs = Logger.getLogger("DevLogs");
  private static Logger activityLogs = Logger.getLogger("ActivityLogs");
  private Vertex currentVertex;
  
  
  String getNodeName()
  {
      return this.nodeNameTextField.getText();
  }
  
  String getNodeDesc()
  {
      return this.quantityDescriptionTextField.getText();
  }
  
  
  public NewNodeDescPanel(CreateNewNodeDialog ne, Vertex v){
    initComponents();
    nodeEditor = ne;
    currentVertex = v;
    initPanel();  
    addHelpBalloon(ApplicationContext.getFirstNextNode(), "onLoad", "InputNewNode");
  }
  
  public void initPanel(){
      if(ApplicationContext.getAppMode().equals("STUDENT") || ApplicationContext.getAppMode().equals("COACHED")){
          this.nodeNameTextField.setEditable(false);
          this.quantityDescriptionTextField.setEditable(false);
          initTree();
      }else{
          decisionTree.setVisible(false);
      }
      
      this.nodeNameTextField.setText(currentVertex.getName());
      this.quantityDescriptionTextField.setText(currentVertex.getCorrectDescription());
      
  }
  
  private void initTree() {
    root = ApplicationContext.getCorrectSolution().getdTreeNode();
    model = new DefaultTreeModel(root);
    decisionTree.setModel(model);

    jScrollPane2.setViewportView(decisionTree);

  }

  
  
  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        contentPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        decisionTree = new javax.swing.JTree();
        evenMorePreciseLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        quantityDescriptionTextField = new javax.swing.JTextArea();
        referencesLabel = new javax.swing.JLabel();
        nodeNameTextField = new javax.swing.JTextField();
        NodeNameLabel = new javax.swing.JLabel();

        contentPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        decisionTree.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("A count of");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("rabbits in the population");
        javax.swing.tree.DefaultMutableTreeNode treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("at the beginning of the year");
        javax.swing.tree.DefaultMutableTreeNode treeNode5 = new javax.swing.tree.DefaultMutableTreeNode("and it is constant from year to year");
        treeNode4.add(treeNode5);
        treeNode5 = new javax.swing.tree.DefaultMutableTreeNode("and it varies from year to year");
        treeNode4.add(treeNode5);
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("totaled up across all years");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("averaged across all years");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("rabbits born into the population");
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("during a year");
        treeNode5 = new javax.swing.tree.DefaultMutableTreeNode("and it is constant from year to year");
        treeNode4.add(treeNode5);
        treeNode5 = new javax.swing.tree.DefaultMutableTreeNode("and it varies from year to year");
        treeNode4.add(treeNode5);
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("across all years");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("per year on average");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("A ratio of");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("the number of rabbits in the population at the beginning of the year, divided by");
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("the number of rabbits added to the population during that same year");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("the total number of rabbits added up across all years");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("the average number of rabbits across all years");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("the number of rabbits added to the population during the year, divided by");
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("the number of rabbits in the population at the beginning of that same year");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("the total number of rabbits added to the population across all years");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("the average number of rabbits added to the population each year");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        decisionTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        decisionTree.setEditable(true);
        decisionTree.setRowHeight(23);
        decisionTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                decisionTreeValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(decisionTree);

        contentPanel.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 28, 595, 285));

        evenMorePreciseLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        evenMorePreciseLabel.setText("Description Tree of the Problem");
        contentPanel.add(evenMorePreciseLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 6, 363, -1));

        quantityDescriptionTextField.setWrapStyleWord(true);
        quantityDescriptionTextField.setColumns(20);
        quantityDescriptionTextField.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        quantityDescriptionTextField.setLineWrap(true);
        quantityDescriptionTextField.setRows(2);
        quantityDescriptionTextField.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        quantityDescriptionTextField.setMargin(new java.awt.Insets(2, 3, 2, 3));
        jScrollPane1.setViewportView(quantityDescriptionTextField);

        contentPanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 457, 595, -1));

        referencesLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        referencesLabel.setText("Precise description of the quantity:");
        contentPanel.add(referencesLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 435, 572, -1));

        nodeNameTextField.setDisabledTextColor(new java.awt.Color(102, 102, 102));
        contentPanel.add(nodeNameTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 389, 496, -1));

        NodeNameLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        NodeNameLabel.setText("Node Name:");
        contentPanel.add(NodeNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 395, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void decisionTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_decisionTreeValueChanged
       resetTextFieldBackground();
       SolutionDTreeNode node = (SolutionDTreeNode) decisionTree.getLastSelectedPathComponent();
       TreeNode[] treeNodes;
       StringBuilder sb = new StringBuilder();
       
       if(node.isLeaf()){
           treeNodes = node.getPath();
           
           for(int i=1; i<treeNodes.length; i++){
               sb.append(treeNodes[i].toString().trim());
               sb.append(" ");
           }
           
           this.quantityDescriptionTextField.setText(sb.toString().trim());
           this.nodeNameTextField.setText(node.getNodeName());
           this.repaint();
       addHelpBalloon(ApplicationContext.getFirstNextNode(), "descFilled", "InputNewNode");
       }
       
    }//GEN-LAST:event_decisionTreeValueChanged

  // returns the value held by triedDuplicate
  public boolean getTriedDuplicate() {
    return triedDuplicate;
  }

  

  public void collapseAll(javax.swing.JTree tree) {
    int row = tree.getRowCount() - 1;
    while (row >= 1) {
      tree.collapseRow(row);
      row--;
    }
  }

  public void expandTreePath(javax.swing.JTree tree, TreePath treepath) {
    collapseAll(tree);
    decisionTree.scrollPathToVisible(treepath);
    decisionTree.setSelectionPath(treepath);
  }
 protected static ImageIcon createImageIcon(String path) {
    java.net.URL imgURL = NewNodeDescPanel.class.getResource(path);
    if (imgURL != null) {
      return new ImageIcon(imgURL);
    } else {
      System.err.println("Couldn't find file: " + path);
      return null;
    }
  }

  
  public boolean processDescriptionPanel(){
      if(getNodeName().trim().length() == 0){
        nodeEditor.setEditorMessage("Node Name can not be empty.", true);
        return false;
      }
      
      if (!currentVertex.getName().equals(getNodeName())) {
         
          if (!duplicatedNode(getNodeName())) 
          {
              try {
                  currentVertex.setName(getNodeName().trim());
              } catch (Exception ex) {
                  nodeEditor.setEditorMessage(ex.getMessage(), true);
                  setTextFieldBackground(Color.RED);
                  activityLogs.debug(ex.getMessage());
                  return false;
              }
          } else {
              nodeEditor.setEditorMessage("The node name is already used by another node. Please choose a new name for this node.", true);
              setTextFieldBackground(Color.RED);
              activityLogs.debug("User entered duplicate node name");
              return false;
          }
      }

      if (getNodeDesc().trim().isEmpty()) {
          nodeEditor.setEditorMessage("Please provide correct description for this node.", true);
          setTextFieldBackground(Color.RED);
          activityLogs.debug("User entered incorrect description");
          return false;
      }

      currentVertex.setCorrectDescription(getNodeDesc().trim());
      return true;
  }
  
  private boolean duplicatedNode(String nodeName) {
      
      Graph graph = nodeEditor.getGraphPane().getModelGraph();
      
      if(graph.getVertexByName(nodeName)!=null && currentVertex.getName()!=nodeName)
          return true;
      else
          return false;
  }
  
  public void setTextFieldBackground(Color c){
      nodeNameTextField.setBackground(c);
      quantityDescriptionTextField.setBackground(c);
  }
  
  public void resetTextFieldBackground(){
      nodeNameTextField.setBackground(Color.white);
      quantityDescriptionTextField.setBackground(Color.white);
  }
  
  public void giveUpDescriptionPanel(){
      // Get a correct Node Name
      TaskSolution solution = ApplicationContext.getCorrectSolution();
      //List<String> correctNodeNames = solution.getCorrectNodeNames();
      List<SolutionNode> correctNodeNames = solution.getSolutionNodes();
      
      String giveupNode = null;
      if(ApplicationContext.getAppMode().equalsIgnoreCase("COACHED")){
          for(SolutionNode name : correctNodeNames){
             if(name.getNodeName().equalsIgnoreCase(ApplicationContext.getFirstNextNode())){
                  giveupNode = name.getNodeName();
                  ApplicationContext.nextCurrentOrder();
                  ApplicationContext.removeNextNodes(name.getNodeName());
                  ApplicationContext.setNextNodes(name.getNodeName());
                  break;
             } 
          }
      }  else {
           for(SolutionNode name : correctNodeNames){
            if(nodeEditor.getGraphPane().getModelGraph().getVertexByName(name.getNodeName()) == null){
                 giveupNode = name.getNodeName();
                 break;
              }
         }
      }
      if(giveupNode == null){
          nodeEditor.setEditorMessage("All Nodes are already being used in the Model.", true);
          return ;
      }
      
      logs.debug("Found Giveup Node as : "+giveupNode);
      
      setDescriptionTreeNode(giveupNode);
      setTextFieldBackground(Color.YELLOW);
  }
  
  private void setDescriptionTreeNode(String nodeName){
      Enumeration<SolutionDTreeNode> allNodes = root.breadthFirstEnumeration();
      while(allNodes.hasMoreElements()){
          SolutionDTreeNode node = allNodes.nextElement();
          
          if(node.isLeaf() && node.getNodeName().equals(nodeName)){
              TreePath path = new TreePath(node.getPath());
              decisionTree.setSelectionPath(path);
          }
      }
  }
  
  public String printDescriptionPanelDetails(){
      StringBuilder sb = new StringBuilder();
      sb.append("Node Name = '");
      sb.append(nodeNameTextField.getText()+"'");
      sb.append("  Description = '");
      sb.append(quantityDescriptionTextField.getText()+"'");
      return sb.toString();
  }
  
  
public void setEditableTree(boolean b){
      decisionTree.setEditable(b);
      decisionTree.setEnabled(b);
}


public JComponent getLabel(String label){
 
    Map<String, JComponent> map = new HashMap<String, JComponent>();
    map.put("evenMorePreciseLabel", evenMorePreciseLabel);
    map.put("referencesLabel", referencesLabel);
    map.put("NodeNameLabel", NodeNameLabel);
    map.put("jScrollPane1", jScrollPane1);
    map.put("jScrollPane2", jScrollPane2);
    if(map.containsKey(label)){
        return map.get(label);
    }
    else {
        return null;
    }
}

   private void addHelpBalloon(String name, String timing, String panel) {
        if (ApplicationContext.getAppMode().equals("COACHED")) {
            System.out.println("addhelpballoon passing in " + name);
            HelpBubble bubble = ApplicationContext.getHelp(name, panel, timing);

            if (bubble != null) {
                System.out.println("help was not null");
                        new BlockingToolTip(this.nodeEditor, bubble.getMessage(), getLabel(bubble.getAttachedTo()), 0, 0);
          //      new BlockingToolTip(this, bubble.getMessage(), dPanel.getLabel(bubble.getAttachedTo()), 0, 0);
            } else {
                     System.out.println("help was null");
            }
        }
   }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel NodeNameLabel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JTree decisionTree;
    private javax.swing.JLabel evenMorePreciseLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField nodeNameTextField;
    private javax.swing.JTextArea quantityDescriptionTextField;
    private javax.swing.JLabel referencesLabel;
    // End of variables declaration//GEN-END:variables

    
  
}

