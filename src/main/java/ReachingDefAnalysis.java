

import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.Stmt;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

import java.util.*;

class ReachingDefAnalysis
    extends ForwardFlowAnalysis<Unit, FlowSet<Unit>>
{

  public Map<Unit, Integer> node_num = new HashMap<Unit, Integer>();
  public Map<Integer, Unit> num_node = new HashMap<Integer, Unit>();
  public Map<String,Integer> variable_name_to_int = new HashMap<String, Integer>();
  public Map<Integer, Integer> node_variable_def =  new HashMap<Integer, Integer>();
  public Map<Integer, List<Integer>> node_child =  new HashMap<Integer, List<Integer>>();
  public List<Integer> entry = new ArrayList<Integer>(); //会有多个entry 吗？
  private FlowSet<Unit> emptySet;
  public UnitGraph graph;


  public ReachingDefAnalysis(DirectedGraph g) throws Exception {
    // First obligation
    super(g);
    // Create the emptyset
    emptySet = new ArraySparseSet<Unit>();
    graph =(UnitGraph) g;
    doAnalysis();
    get_node_num();
    get_adjmatrix();
    get_node_variable_def();


  }

  @Override
  protected void merge(FlowSet<Unit> inSet1,
                       FlowSet<Unit> inSet2,
                       FlowSet<Unit> outSet)
  {
    inSet1.union(inSet2, outSet);
  }


  @Override
  protected void copy(FlowSet<Unit> srcSet,
                      FlowSet<Unit> destSet)
  {
    srcSet.copy(destSet);
  }


  // Used to initialize the in and out sets for each node. In
  // our case we build up the sets as we go, so we initialize
  // with the empty set.
  @Override
  protected FlowSet<Unit> newInitialFlow() {
    return emptySet.clone();
  }


  // Returns FlowSet representing the initial set of the entry
  // node. In our case the entry node is the last node and it
  // should contain the empty set.
  @Override
  protected FlowSet<Unit> entryInitialFlow() {
    return emptySet.clone();
  }


  // Sets the outSet with the values that flow through the
  // node from the inSet based on reads/writes at the node
  // Set the outSet (entry) based on the inSet (exit)
  @Override
  protected void flowThrough(FlowSet<Unit> inSet, Unit node, FlowSet< Unit> outSet) {
    /*
     * If this path will not be taken return no path straightaway
     */
    System.out.println("Unit : "+node);
    System.out.println("S : "+node.toString());
    FlowSet<Unit> genSet = emptySet.clone();
    Set<Unit>the_gen_set = new HashSet<Unit>();
    if (node instanceof DefinitionStmt) {
      Value leftOp = ((DefinitionStmt) node).getLeftOp();
      if (leftOp instanceof Local) {
        // KILL any reaching defs of left
        Local left_local =  (Local) leftOp;
        kill( inSet, (Local) leftOp);
        // GEN
        gen( genSet , node);
      } // leftop is a local
    }
    inSet.union(genSet, outSet);

  }




  public void kill(FlowSet<Unit> in, Local redefined) {
    //    System.out.println("Enter kill : " + redefined);
    String redefinedLocalName = redefined.getName();
    // kill any previous localpairs which have the redefined Local in the
    // left i.e. previous definitions
    for (Iterator<Unit> listIt = in.iterator(); listIt.hasNext();) {
        Unit u =  listIt.next();

        if( u instanceof DefinitionStmt){
        DefinitionStmt tempStmt = (DefinitionStmt) u;
        Value leftOp = tempStmt.getLeftOp();
        if (leftOp instanceof Local) {
          String storedLocalName = ((Local) leftOp).getName();
          if (redefinedLocalName.compareTo(storedLocalName) == 0) {
            // need to kill this from the list
            listIt.remove();
          }
        }
      }
    }
  }



  public void gen(FlowSet<Unit>  out, Unit s) {
    out.add(s);
  }


  public  void get_node_num(){
    Iterator<Unit> unitIt = this.graph.iterator();
    int node_i=1;
    while (unitIt.hasNext()) {
      Unit node = unitIt.next();
      if (!node_num.containsKey(node)) {
        node_num.put(node, node_i);
        num_node.put(node_i,node);
      }
      node_i++;
    }
  }


  public  void get_node_variable_def() throws Exception {
    Iterator<Unit> unitIt = this.graph.iterator();
    int variable_i =1;
    while (unitIt.hasNext()) {
      Unit node = unitIt.next();
      List<ValueBox> l =node.getDefBoxes();
      if(l.size()>1)  throw new Exception("Method get_variable_int : def size > 1");
      for (ValueBox def: node.getDefBoxes()) {
        if (def.getValue() instanceof Local) {
          String name = ((Local) def.getValue()).getName();
          if(!variable_name_to_int.containsKey(name)){
            variable_name_to_int.put(name,variable_i); // 我们只关心left
            node_variable_def.put(node_num.get(node),variable_i);
            variable_i++;
          } else{
            node_variable_def.put(node_num.get(node),variable_name_to_int.get(name));
          }
        }
      }
    }
  }

  public  void get_adjmatrix(){
    Iterator<Unit> unitIt = this.graph.iterator();
    while (unitIt.hasNext()) {
      Unit node = unitIt.next();
      List<Unit> successors =this.graph.getSuccsOf(node); //会不会有自环呢
      List<Integer> successors_int = new ArrayList<Integer>();
      for(Unit child : successors){
        successors_int.add(node_num.get(child));
      }
      node_child.put(node_num.get(node), successors_int);
    }
  }
}

