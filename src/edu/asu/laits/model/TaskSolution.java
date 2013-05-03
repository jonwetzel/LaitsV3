/**
 * LAITS Project
 * Arizona State University
 */

package edu.asu.laits.model;

import edu.asu.laits.gui.MainWindow;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import org.apache.log4j.Logger;
import org.jgraph.graph.DefaultPort;

/**
 *
 * @author ramayantiwari
 */
public class TaskSolution {
    private String phase;
    private String taskType;
    private String taskDescription;
    private String imageURL;
    private int startTime;
    private int endTime;
    private String graphUnits;
    private int nodeCount;
    private List<SolutionNode> solutionNodes;
    private List<SolutionNode> givenNodes;
    private List<String> correctNodeNames;
    
    private SolutionDTreeNode dTreeNode;
    private Graph solutionGraph = null;

    private static Logger logs = Logger.getLogger("DevLogs");
    
    public TaskSolution(){
        solutionNodes = new ArrayList<SolutionNode> ();
        givenNodes = new ArrayList<SolutionNode> ();
        correctNodeNames = new ArrayList<String>();
    }
    
    /**
     * @return the phase
     */
    public String getPhase() {
        return phase;
    }

    /**
     * @param phase the phase to set
     */
    public void setPhase(String phase) {
        this.phase = phase;
    }

    /**
     * @return the phase
     */
    public String getTaskType() {
        return taskType;
    }

    /**
     * @param phase the phase to set
     */
    public void setTaskType(String type) {
        this.taskType = type;
    }
    
    /**
     * @return the taskDescription
     */
    public String getTaskDescription() {
        return taskDescription;
    }

    /**
     * @param taskDescription the taskDescription to set
     */
    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    /**
     * @return the imageURL
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * @param imageURL the imageURL to set
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * @return the startTime
     */
    public int getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public int getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the graphUnits
     */
    public String getGraphUnits() {
        return graphUnits;
    }

    /**
     * @param graphUnits the graphUnits to set
     */
    public void setGraphUnits(String graphUnits) {
        this.graphUnits = graphUnits;
    }

    /**
     * @return the nodeCount
     */
    public int getNodeCount() {
        return nodeCount;
    }

    /**
     * @param nodeCount the nodeCount to set
     */
    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    /**
     * @return the solutionNodes
     */
    public List<SolutionNode> getSolutionNodes() {
        return solutionNodes;
    }

    /**
     * @param solutionNodes the solutionNodes to set
     */
    public void setSolutionNodes(List<SolutionNode> solutionNodes) {
        this.solutionNodes = solutionNodes;
    }
    
    /**
     * @return the solutionNodes
     */
    public List<String> getCorrectNodeNames() {
        return correctNodeNames;
    }

    /**
     * @param solutionNodes the solutionNodes to set
     */
    public void setCorrectNodeNames(List<String> solutionNodeNames) {
        this.correctNodeNames = solutionNodeNames;
    }
    
    /**
     * @return the solutionNodes
     */
    public List<SolutionNode> getGivenNodes() {
        return givenNodes;
    }

    /**
     * @param solutionNodes the solutionNodes to set
     */
    public void setGivenNodes(List<SolutionNode> givenNodes) {
        this.givenNodes = givenNodes;
    }

    /**
     * @return the dTreeNode
     */
    public SolutionDTreeNode getdTreeNode() {
        return dTreeNode;
    }

    /**
     * @param dTreeNode the dTreeNode to set
     */
    public void setdTreeNode(SolutionDTreeNode dTreeNode) {
        this.dTreeNode = dTreeNode;
    }
    
    public boolean checkNodeName(String nodeName){
        if(getNodeByName(nodeName) != null)
            return true;
        else 
            return false;
    }
    
    public boolean checkNodePlan(String nodeName, Vertex.Plan plan){
        if(getNodeByName(nodeName).getNodePlan().compareTo(plan) == 0)
            return true;
        else
            return false;
    }
    
    public boolean checkNodeInputs(String nodeName, List<String> inputs){
        logs.debug("Checking Inputs for Node "+nodeName);
        
        SolutionNode correctNode = getNodeByName(nodeName);
        if(correctNode.getNodeType().equals(Vertex.VertexType.CONSTANT)){
            if(inputs == null)
                return true;
            
            return false;
        }
        
        else{
            List<String> correctInputs = correctNode.getInputNodes();
            logs.debug("Correct Inputs : "+correctInputs);
            logs.debug("User Inputs : "+inputs);
            if(correctInputs.size() != inputs.size())
                return false;
            else if(correctInputs.containsAll(inputs))
                return true; 
            else 
                return false;
        }
        
    }
    
    public boolean checkNodeCalculations(Vertex studentNode){
        logs.debug("Checking Calculations for Node "+studentNode.getName());
        
        SolutionNode correctNode = getNodeByName(studentNode.getName());
        
        // Check if Type of Nodes are Same
        if(!correctNode.getNodeType().equals(studentNode.getVertexType()))
            return false;
        
        // If Node is Constant or Accumulator check if Initial Values are same
        if(correctNode.getNodeType().equals(Vertex.VertexType.CONSTANT) ||
                correctNode.getNodeType().equals(Vertex.VertexType.STOCK)){
            
            if(!(correctNode.getInitialValue() == studentNode.getInitialValue()))
                return false;
            
            if(correctNode.getNodeType().equals(Vertex.VertexType.STOCK))
                return checkNodeEquation(correctNode.getNodeEquation(), studentNode.getEquation());
            
        }
        
        else if(correctNode.getNodeType().equals(Vertex.VertexType.FLOW)){
            return checkNodeEquation(correctNode.getNodeEquation().trim(), studentNode.getEquation().trim());
        }
        
        return true;
    }
    
    private boolean checkNodeEquation(String correctEquation, String studentEquation){
        logs.debug("Check Node Equation ");
        
        Evaluator evalStudent = new Evaluator();
        Evaluator evalCorrect = new Evaluator();
        
        try{
            evalStudent.parse(studentEquation);
            evalCorrect.parse(correctEquation);
            
            List<String> studentVariables = evalStudent.getAllVariables();
            List<String> correctVariables = evalCorrect.getAllVariables();
            
            if(studentVariables.size() != correctVariables.size())
                return false;
            
            if(!studentVariables.containsAll(correctVariables))
                return false;
            
            for(int i=0; i<5; i++){
            
                for(String var : studentVariables){
                    Random rand = new Random(123);
                    int r = rand.nextInt(100);
                    
                    double value = (r +rand.nextDouble()) % 150;
                    evalStudent.putVariable(var, String.valueOf(value));
                    evalCorrect.putVariable(var, String.valueOf(value));
                }
            
                String studentResult = evalStudent.evaluate();
                String correctResult = evalCorrect.evaluate();
                
                logs.debug("Result "+(i+1)+"  S: "+studentResult+"  C: "+correctResult);
                
                if(!studentResult.equals(correctResult)){
                    logs.debug("Student and Author Equation Results did not match");
                    return false;
                }
            }
            
        }catch(EvaluationException ex){
            logs.debug("Error in Evaluating Student's Equation");
            return false;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }    
        
        return true;
    }
    
    public SolutionNode getNodeByName(String name){
        for(SolutionNode currentNode : solutionNodes){
            if(currentNode.getNodeName().equalsIgnoreCase(name)){
                return currentNode;
            }
        }
        return null;
    }
    
    public boolean checkNodeGraph(Vertex studentVertex){
        if(solutionGraph == null){
               createSolutionGraph();
               ModelEvaluator evaluator = new ModelEvaluator(solutionGraph);
               
               try{
                evaluator.run();
               }catch(ModelEvaluationException ex){
                   logs.fatal("Error in Evaluating Correct Solution Graph.");
                   JOptionPane.showMessageDialog(MainWindow.getFrames()[0], 
                           "Internal LAITS Error in Solution File - System will exit.", 
                           "Fatal Error", JOptionPane.ERROR_MESSAGE);
                   System.exit(1);
               }
        }
        
        Vertex correctVertex = solutionGraph.getVertexByName(studentVertex.getName());
        
        // Check if Student Model was executed with correct start and end time
        if(correctVertex.getCorrectValues().size() != studentVertex.getCorrectValues().size()){
            logs.debug("Student Solution and Correct Solution size for Vertex "+
                    studentVertex.getName()+" are different");
            logs.debug("Correct Solution "+correctVertex.getCorrectValues().size()+"  Student Values Size: "+
                    studentVertex.getCorrectValues().size());
            return false;
        }
        
        
        Iterator<Double> correctValues = correctVertex.getCorrectValues().iterator();
        Iterator<Double> studentValues = studentVertex.getCorrectValues().iterator();
        
        while(correctValues.hasNext()){
            //double difference = 
            if(Math.abs(correctValues.next().doubleValue() - studentValues.next().doubleValue()) > 0.005 ){
                logs.debug("Graph Values for Vertex "+studentVertex.getName()+" differ with more than 0.001");
                return false;
            }    
        }
        
        return true;
    }
    
    private void createSolutionGraph(){
        solutionGraph = new Graph<Vertex, Edge>(Edge.class);
        
        // Add all the vertices in the Graph
        for(SolutionNode node : solutionNodes){
            if(correctNodeNames.contains(node.getNodeName())){
                Vertex v = new Vertex();
                v.setName(node.getNodeName());
                v.setEquation(node.getNodeEquation());
                v.setInitialValue(node.getInitialValue());

                v.setVertexType(node.getNodeType());

                v.setInputsStatus(Vertex.InputsStatus.CORRECT);
                v.setCalculationsStatus(Vertex.CalculationsStatus.CORRECT);

                solutionGraph.addVertex(v);
            }
        }
        
        // Add all the Edges in the Graph
        for(SolutionNode node : solutionNodes){
            List<String> inputVertices = node.getInputNodes();
            for(String vertexName : inputVertices){
                solutionGraph.addEdge(solutionGraph.getVertexByName(vertexName), 
                        solutionGraph.getVertexByName(node.getNodeName()));
            }            
        }
        
         
        logs.debug("Solution Graph Created with "+solutionGraph.vertexSet().size()+
                " Vertices, and "+solutionGraph.edgeSet().size()+" edges.");
    }
    
    public Graph getSolutionGraph(){
        return solutionGraph;
    }
}