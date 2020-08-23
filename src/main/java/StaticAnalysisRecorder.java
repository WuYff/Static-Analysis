import soot.Local;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.DefinitionStmt;
import soot.toolkits.graph.UnitGraph;
import java.util.*;

/**
 * Record program data with respect to static analysis
 * Each unit is a node in the graph.
 */
class StaticAnalysisRecorder {
  UnitGraph graph;
  // <node, node id>
  Map<Unit, Integer> node_num = new HashMap<Unit, Integer>();
  // node id when only considering definition stmt
  Map<Unit, Integer> def_node_num = new HashMap<Unit, Integer>();
  // node_successor is for building the adjacent matrix
  Map<Integer, List<Integer>> node_successor = new HashMap<Integer, List<Integer>>();
  // id number for each variable
  Map<String, Integer> variable_name_to_int = new HashMap<String, Integer>();
  // < node id, variable id>
  Map<Integer, Integer> node_variable_def = new HashMap<Integer, Integer>();
  // <node id,>
  Map<Integer, List<Integer>> node_variable_use = new HashMap<Integer, List<Integer>>();

  public StaticAnalysisRecorder(UnitGraph graph) {
    this.graph = graph;
  }

  /**
   * Map each <code>{@link Unit}</code> of the CFG to an id number
   */
  public void get_node_num() {
    Iterator<Unit> unitIt = graph.iterator();
    int node_id = 1;
    while (unitIt.hasNext()) {
      Unit node = unitIt.next();
      if (!node_num.containsKey(node)) {
        node_num.put(node, node_id);
      }
      node_id++;
    }
  }

  /**
   * Map each unit of type <code>{@link DefinitionStmt}</code> of the CFG to an id number
   */
  public void get_def_node_num() {
    Iterator<Unit> unitIt = graph.iterator();
    int def_node_id = 1;
    while (unitIt.hasNext()) {
      Unit node = unitIt.next();
      if (def_node_num.containsKey(node)){
        System.out.println("WRONG");
      }
      if ((node instanceof DefinitionStmt) && !def_node_num.containsKey(node)) {
        def_node_num.put(node, def_node_id);
        def_node_id++;
      }


    }
  }

  /**
   * Obtain the id number for each successor of a unit.
   */
  public void get_node_successor() {
    Iterator<Unit> unitIt = graph.iterator();
    while (unitIt.hasNext()) {
      Unit node = unitIt.next();
      List<Unit> successor = graph.getSuccsOf(node);
      List<Integer> successor_int = new ArrayList<Integer>();
      for (Unit s : successor) {
        successor_int.add(node_num.get(s));
      }
      node_successor.put(node_num.get(node), successor_int);

    }
  }

  /**
   * For each definition unit, obtain the id number for the variable defined in this unit .
   * (At most one variable can be defined in a unit.)
   */
  public void get_node_variable_def() throws Exception {
    Iterator<Unit> unitIt = graph.iterator();
    int varible_i = 1;
    while (unitIt.hasNext()) {
      Unit node = unitIt.next();
      if (node instanceof DefinitionStmt) {
        for (ValueBox def : node.getDefBoxes()) {
          if (def.getValue() instanceof Local) {
            String name = ((Local) def.getValue()).getName();
            if (!variable_name_to_int.containsKey(name)) {
              variable_name_to_int.put(name, varible_i); // 我们只关心left
              node_variable_def.put(node_num.get(node), varible_i);
              varible_i++;
            } else {
              node_variable_def.put(node_num.get(node), variable_name_to_int.get(name));
            }
          }
        }
      }
    }
  }

  /**
   * For each unit, obtain the id number for each variable used in this unit .
   */
  public void get_node_variable_use() throws Exception {
    Iterator<Unit> unitIt = graph.iterator();
    while (unitIt.hasNext()) {
      Unit node = unitIt.next();
      for (ValueBox use : node.getUseBoxes()) {
        if (use.getValue() instanceof Local) {
          String name = ((Local) use.getValue()).getName();
          if (!variable_name_to_int.containsKey(name)) {
            throw new Exception("Debug :  get_node_variable_use() ");
          } else {
            if (!node_variable_use.containsKey(node_num.get(node))) {
              node_variable_use.put(node_num.get(node), new ArrayList<Integer>());
              node_variable_use.get(node_num.get(node)).add(variable_name_to_int.get(name));
            } else {
              node_variable_use.get(node_num.get(node)).add(variable_name_to_int.get(name));
            }
          }

        }
      }
    }

  }
}
