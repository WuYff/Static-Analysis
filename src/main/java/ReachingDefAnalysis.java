

import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

import java.util.*;

class ReachingDefAnalysis
    extends ForwardFlowAnalysis<Unit, FlowSet<Unit>>
{


  private FlowSet<Unit> emptySet;
  public UnitGraph graph;
  StaticAnalysisRecorder staticAnalysisRecorder;


  public ReachingDefAnalysis(DirectedGraph g) throws Exception {
    // First obligation
    super(g);
    // Create the emptyset
    emptySet = new ArraySparseSet<Unit>();
    graph =(UnitGraph) g;
    staticAnalysisRecorder = new StaticAnalysisRecorder(graph);
    staticAnalysisRecorder.get_node_num();
    staticAnalysisRecorder.get_def_node_num();
    staticAnalysisRecorder.get_node_successor();
    staticAnalysisRecorder.get_node_variable_def();
    doAnalysis();

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


}

