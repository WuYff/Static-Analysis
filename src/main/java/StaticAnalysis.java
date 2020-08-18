import soot.Local;
import soot.Unit;
import soot.ValueBox;
import soot.toolkits.graph.UnitGraph;

import java.util.*;

public interface StaticAnalysis {
  Map<Unit, Integer> node_num = new HashMap<Unit, Integer>();
  Map<Integer, List<Integer>> node_child =  new HashMap<Integer, List<Integer>>();
  Map<String,Integer> variable_name_to_int = new HashMap<String, Integer>();
  Map<Integer, Integer> node_variable_def =  new HashMap<Integer, Integer>();
  Map<Integer, List<Integer>> node_variable_use =  new HashMap<Integer, List<Integer>>();

   default void get_node_num(UnitGraph graph){
    Iterator<Unit> unitIt = graph.iterator();
    int node_i=1;
    while (unitIt.hasNext()) {
      Unit node = unitIt.next();
      if (!node_num.containsKey(node)) {
        node_num.put(node, node_i);
      }
      node_i++;
    }
  }

  default void get_adjmatrix(UnitGraph graph){
    Iterator<Unit> unitIt = graph.iterator();
    while (unitIt.hasNext()) {
      Unit node = unitIt.next();
      List<Unit> succ =graph.getSuccsOf(node); //会不会有自环呢
      List<Integer> preds_int = new ArrayList<Integer>();
      for(Unit s : succ){
        preds_int.add(node_num.get(s));
      }
      node_child.put(node_num.get(node), preds_int);

    }
  }

    default void get_node_variable_def(UnitGraph graph) throws Exception {

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



  default void get_node_variable_use(UnitGraph graph) throws Exception {
    Map<Integer, List<Integer>> node_variable_use = new HashMap<Integer, List<Integer>>();
    Iterator<Unit> unitIt = graph.iterator();
    while (unitIt.hasNext()) {
      Unit node = unitIt.next();
      for (ValueBox use : node.getUseBoxes()) {
        if (use.getValue() instanceof Local) {
          String name = ((Local) use.getValue()).getName();
          if (!variable_name_to_int.containsKey(name)) {
            throw new Exception("Something Wrong");
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
