import soot.Local;
import soot.Unit;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.FlowAnalysis;
import soot.toolkits.scalar.FlowSet;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class Writer {
  FlowAnalysis analysis;
  StaticAnalysisRecorder analysisRecorder;
  String prefix;
  public Writer(FlowAnalysis analysis, StaticAnalysisRecorder analysisRecorder,String prefix){
    this.analysisRecorder= analysisRecorder;
    this.prefix = prefix;
    this.analysis=analysis;
  }

  public  void write_graph() throws IOException {
    BufferedWriter out = new BufferedWriter(new FileWriter(prefix+ "_graph" + ".txt"));
    StringBuilder w ;
    for(Integer k: analysisRecorder.node_child.keySet()){
      w = new StringBuilder(k + " ");
      List<Integer> child = analysisRecorder.node_child.get(k);
      for(Integer i: child){
        w.append(i).append(" ");
      }
      w.append("\n");
      out.write(w.toString());
    }
    out.close();
  }


  public void write_target_reaching_definition( DirectedGraph graph  ) throws IOException {
    BufferedWriter out = new BufferedWriter(new FileWriter(prefix+"_target"+ ".txt"));
    for (Unit s : (Iterable<Unit>) graph) {
      FlowSet<Unit> set = (FlowSet<Unit>) analysis.getFlowAfter(s);
      StringBuilder rs = new StringBuilder();
      rs.append(analysisRecorder.node_num.get(s));
      for (Unit node : set) {
        rs.append(" ").append(analysisRecorder.node_num.get(node));
      }
      rs.append(" \n");
      out.write(rs.toString());
    }
    out.close();
  }
  public void write_target_live_variable( DirectedGraph graph  ) throws IOException {
    BufferedWriter out = new BufferedWriter(new FileWriter(prefix+"_target"+ ".txt"));

    for (Unit s : (Iterable<Unit>) graph) {
      FlowSet<Local> set = (FlowSet<Local>) analysis.getFlowBefore(s); // The flow set here is for the non-reverse graph, equals OutSet
      int node_num = analysisRecorder.node_num.get(s);
      StringBuilder rs = new StringBuilder();
      rs.append(node_num);
      for (Local local : set) {
        rs.append(" ").append(analysisRecorder.variable_name_to_int.get(local.getName()));
      }
      rs.append(" \n");
      out.write(rs.toString());
    }
    out.close();
  }


  public  void write_node_def( ) throws IOException {
    BufferedWriter out = new BufferedWriter(new FileWriter(prefix+"_node_def"+ ".txt"));
    StringBuilder w ;
    for(Integer key:  analysisRecorder.node_variable_def.keySet()){
      w = new StringBuilder(key + " ");
      Integer v = analysisRecorder.node_variable_def.get(key);
      w.append(v).append(" ");
      w.append("\n");
      out.write(w.toString());
    }
    out.close();
  }


  public void write_node_use( ) throws IOException {
    BufferedWriter out = new BufferedWriter(new FileWriter(prefix+"_node_use"+ ".txt"));
    for (Integer key : analysisRecorder. node_variable_use.keySet()){
      StringBuilder hh = new StringBuilder();
      hh.append(key);
      for (Integer i : analysisRecorder. node_variable_use.get(key)){
        hh.append(" ").append(i);
      }
      hh.append(" \n");
      out.write(hh.toString());
    }
    out.close();

  }
}
