import soot.Local;
import soot.Unit;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.FlowAnalysis;
import soot.toolkits.scalar.FlowSet;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;

/**
 * Generates a .jsonl file as the dataset. Writes the static analysis results and information about the program.
 *
 */
public class JsonWriter {
  FlowAnalysis analysis;
  StaticAnalysisRecorder analysisRecorder;
  DirectedGraph graph;
  String filePath;

  public JsonWriter(FlowAnalysis analysis, StaticAnalysisRecorder analysisRecorder, DirectedGraph graph, String filePath) {
    this.analysisRecorder = analysisRecorder;
    this.analysis = analysis;
    this.graph = graph;
    this.filePath = filePath;
  }

  public void write() throws IOException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("graph", JSON.toJSONString(analysisRecorder.node_successor));
    jsonObject.put("node_def", JSON.toJSONString(analysisRecorder.node_variable_def));
    jsonObject.put("max_node_id_of_one_graph", get_max_node_id_of_one_graph());
    jsonObject.put("max_def_id_of_one_graph", get_max_def_id_one_graph());
    if (this.analysis instanceof ReachingDefAnalysis) {
      jsonObject.put("target", JSON.toJSONString(get_target_reaching_definition_only_def_node(graph)));
    } else if (this.analysis instanceof LiveVariableAnalysis) {
      jsonObject.put("target", JSON.toJSONString(get_target_live_variable(graph)));
      jsonObject.put("node_use", JSON.toJSONString(analysisRecorder.node_variable_use));
    }

    try (FileWriter fw = new FileWriter(filePath, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw)) {
      out.println(jsonObject.toJSONString());
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public Map<Integer, List<Integer>> get_target_reaching_definition_only_def_node(DirectedGraph graph) throws IOException {
    Map<Integer, List<Integer>> target = new HashMap<Integer, List<Integer>>();
    for (Unit node : (Iterable<Unit>) graph) {
      FlowSet<Unit> set = (FlowSet<Unit>) analysis.getFlowAfter(node);
      int node_num = analysisRecorder.node_num.get(node);
      List<Integer> value = new ArrayList<>();
      for (Unit s : set) {
        value.add(analysisRecorder.def_node_num.get(s));
      }
      target.put(node_num, value);
    }
    return target;
  }

  public Map<Integer, List<Integer>> get_target_live_variable(DirectedGraph graph) {
    Map<Integer, List<Integer>> target = new HashMap<Integer, List<Integer>>();

    for (Unit s : (Iterable<Unit>) graph) {
      FlowSet<Local> set = (FlowSet<Local>) analysis.getFlowBefore(s); // The flow set here is for the non-reverse graph, equals
      int node_num = analysisRecorder.node_num.get(s);
      List<Integer> value = new ArrayList<>();
      for (Local local : set) {
        value.add(analysisRecorder.variable_name_to_int.get(local.getName()));
      }
      target.put(node_num, value);
    }
    return target;
  }

  public int get_max_node_id_of_one_graph() {
    int max_node_id_of_one_graph = -1;
    for (Unit node : analysisRecorder.node_num.keySet()) {
      int node_num = analysisRecorder.node_num.get(node);
      if (node_num > max_node_id_of_one_graph) max_node_id_of_one_graph = node_num;
    }
    return max_node_id_of_one_graph;
  }

  public int get_max_def_id_one_graph() {
    int max_def_id_of_one_graph = -1;
    for (int key : analysisRecorder.node_variable_def.keySet()) {
      int def = analysisRecorder.node_variable_def.get(key);
      if (def > max_def_id_of_one_graph) max_def_id_of_one_graph = def;
    }
    return max_def_id_of_one_graph;
  }
}
