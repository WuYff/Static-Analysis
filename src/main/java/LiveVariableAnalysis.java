import java.util.*;

import soot.Local;
import soot.Unit;
import soot.UnitBox;
import soot.ValueBox;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.BackwardFlowAnalysis;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ArraySparseSet;

class LiveVariableAnalysis 
	extends BackwardFlowAnalysis<Unit, FlowSet<Local>> 
{
	public Map<Unit, Integer> node_num = new HashMap<Unit, Integer>();
	public Map<Integer, Unit> num_node = new HashMap<Integer, Unit>();
	public Map<Integer, List<Integer>> node_child =  new HashMap<Integer, List<Integer>>();
	public Map<String,Integer> variable_name_to_int = new HashMap<String, Integer>();
	public Map<Integer, Integer> node_variable_def =  new HashMap<Integer, Integer>();
	public Map<Integer, List<Integer>> node_variable_use =  new HashMap<Integer, List<Integer>>();
	private FlowSet<Local> emptySet;


	public LiveVariableAnalysis(DirectedGraph g) throws Exception {
		super(g);

		emptySet = new ArraySparseSet<Local>();
		get_node_num((UnitGraph) graph);
		get_adjmatrix((UnitGraph) graph);
		get_variable_int((UnitGraph) graph);
		get_node_variable_use((UnitGraph) graph);
		doAnalysis();

	}
	

	// This method performs the joining of successor nodes
	// Since live variables is a may analysis we join by union 
	@Override
	protected void merge(FlowSet<Local> inSet1, 
		FlowSet<Local> inSet2, 
		FlowSet<Local> outSet) 
	{
		inSet1.union(inSet2, outSet);
	}


	@Override
	protected void copy(FlowSet<Local> srcSet, 
		FlowSet<Local> destSet) 
	{
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

		// outSet is the set at enrty of the node
		// inSet is the set at exit of the node
		// out <- (in - write(node)) union read(node)

		// out <- (in - write(node))

		FlowSet writes = (FlowSet)emptySet.clone();

		for (ValueBox def: node.getDefBoxes()) {

			if (def.getValue() instanceof Local) {

				writes.add(def.getValue());

			}
		}

		inSet.difference(writes, outSet);

		for (ValueBox use: node.getUseBoxes()) {

			if (use.getValue() instanceof Local) {

				outSet.add((Local) use.getValue());

			}
		}

	}

	public  void get_node_num(UnitGraph graph){
		Iterator<Unit> unitIt = graph.iterator();
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

	public  void get_adjmatrix(UnitGraph graph){

		Iterator<Unit> unitIt = graph.iterator();
		while (unitIt.hasNext()) {
			Unit node = unitIt.next();
			List<Unit> preds =graph.getPredsOf(node); //会不会有自环呢
			List<Integer> preds_int = new ArrayList<Integer>();
			for(Unit p : preds){
				preds_int.add(node_num.get(p));
			}
			node_child.put(node_num.get(node), preds_int);

		}

	}

	public  void get_variable_int(UnitGraph graph) throws Exception {

		Iterator<Unit> unitIt = graph.iterator();
		int varible_i =1;
		while (unitIt.hasNext()) {
			Unit node = unitIt.next();
			List<ValueBox> l =node.getDefBoxes();

			if(l.size()>1){
				throw new Exception("def size > 1");
			}
			for (ValueBox def: node.getDefBoxes()) {
				if (def.getValue() instanceof Local) {
					String name = ((Local) def.getValue()).getName();
					if(!variable_name_to_int.containsKey(name)){
						variable_name_to_int.put(name,varible_i); // 我们只关心left
						node_variable_def.put(node_num.get(node),varible_i);
						varible_i++;
					} else{
						node_variable_def.put(node_num.get(node),variable_name_to_int.get(name));
					}
				}
			}
		}

	}



	public  void get_node_variable_use(UnitGraph graph) throws Exception {

		Iterator<Unit> unitIt = graph.iterator();
		while (unitIt.hasNext()) {
			Unit node = unitIt.next();
			for (ValueBox use: node.getUseBoxes()) {
				if (use.getValue() instanceof Local) {
					String name = ((Local) use.getValue()).getName();
					if(!variable_name_to_int.containsKey(name)){
						throw new Exception("Something Wrong");
					} else{
						if(!node_variable_use.containsKey(node_num.get(node))){
							node_variable_use.put(node_num.get(node),new ArrayList<Integer>());
							node_variable_use.get(node_num.get(node)).add(variable_name_to_int.get(name));
						}else{
						node_variable_use.get(node_num.get(node)).add(variable_name_to_int.get(name)); }
					}

				}
			}
		}
	}
}


