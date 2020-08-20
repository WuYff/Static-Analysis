import soot.Local;
import soot.Unit;
import soot.ValueBox;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.BackwardFlowAnalysis;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ArraySparseSet;

class LiveVariableAnalysis
    extends BackwardFlowAnalysis<Unit, FlowSet<Local>> {

  private FlowSet<Local> emptySet;
  StaticAnalysisRecorder staticAnalysisRecorder;
  public UnitGraph graph;

  public LiveVariableAnalysis(DirectedGraph g) throws Exception {
    super(g);
    graph = (UnitGraph) g;
    emptySet = new ArraySparseSet<Local>();
    staticAnalysisRecorder = new StaticAnalysisRecorder(graph);
    staticAnalysisRecorder.get_node_num();
    staticAnalysisRecorder.get_node_successor();
    staticAnalysisRecorder.get_node_variable_def();
    staticAnalysisRecorder.get_node_variable_use();
    doAnalysis();

  }

  @Override
  protected void merge(FlowSet<Local> inSet1,
                       FlowSet<Local> inSet2,
                       FlowSet<Local> outSet) {
    inSet1.union(inSet2, outSet);
  }

  @Override
  protected void copy(FlowSet<Local> srcSet,
                      FlowSet<Local> destSet) {
    srcSet.copy(destSet);
  }

  // Used to initialize the in and out sets for each node. In
  // our case we build up the sets as we go, so we initialize
  // with the empty set.
  @Override
  protected FlowSet<Local> newInitialFlow() {
    return emptySet.clone();
  }

  // Returns FlowSet representing the initial set of the entry
  // node. In our case the entry node is the last node and it
  // should contain the empty set.
  @Override
  protected FlowSet<Local> entryInitialFlow() {
    return emptySet.clone();
  }

  // Sets the outSet with the values that flow through the
  // node from the inSet based on reads/writes at the node
  // Set the outSet (entry) based on the inSet (exit)
  @Override
  protected void flowThrough(FlowSet<Local> inSet,
                             Unit node, FlowSet<Local> outSet) {
    // Regard the CFG as reversed graph
    // outSet is the set at enrty of the node
    // inSet is the set at exit of the node
    // out <- (in - write(node)) union read(node)

    // out <- (in - write(node))
    FlowSet writes = (FlowSet) emptySet.clone();

    for (ValueBox def : node.getDefBoxes()) {

      if (def.getValue() instanceof Local) {

        writes.add(def.getValue());

      }
    }

    inSet.difference(writes, outSet);

    for (ValueBox use : node.getUseBoxes()) {

      if (use.getValue() instanceof Local) {

        outSet.add((Local) use.getValue());

      }
    }
  }

}
