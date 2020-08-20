import soot.Local;
import soot.Unit;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.FlowAnalysis;
import soot.toolkits.scalar.FlowSet;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Generates .txt files as the dataset. Writes the static analysis results
 * and information about the program (e.g. CFG) to corresponding .txt files.
 *
 */
public class Writer {
  FlowAnalysis analysis;
  StaticAnalysisRecorder analysisRecorder;
  String prefix;
  public Writer(FlowAnalysis analysis, StaticAnalysisRecorder analysisRecorder,String prefix){
    this.analysisRecorder= analysisRecorder;
    this.prefix = prefix;
    this.analysis=analysis;
  }


  /**
   * Generates _graph.txt files
   *
   * If CFG edges :
   *    (src1 -> trg1), (src2 -> trg1),  (src2 -> trg2)
   * then write as:
   *    src1 trg1
   *    src2 trg1 trg2
   */
  public void write_graph() throws IOException {
    BufferedWriter out = new BufferedWriter(new FileWriter(prefix+ "_graph" + ".txt"));
    StringBuilder w ;
    for(Integer k: analysisRecorder.node_successor.keySet()){
      w = new StringBuilder(k + " ");
      List<Integer> child = analysisRecorder.node_successor.get(k);
      for(Integer i: child){
        w.append(i).append(" ");
      }
      w.append("\n");
      out.write(w.toString());
    }
    out.close();
  }

  @Deprecated
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

  /**
   * Generates _target.txt files for reaching definition analysis
   *
   * If for stmt C, the definition stmts that can reach it are A and B,
   * then write as:
   *    C A B
   */
  public void write_target_reaching_definition_only_def_node( DirectedGraph graph  ) throws IOException {
    BufferedWriter out = new BufferedWriter(new FileWriter(prefix+"_target"+ ".txt"));
    for (Unit s : (Iterable<Unit>) graph) {
      FlowSet<Unit> set = (FlowSet<Unit>) analysis.getFlowAfter(s);
      StringBuilder rs = new StringBuilder();
      rs.append(analysisRecorder.node_num.get(s));
      for (Unit node : set) {
        rs.append(" ").append(analysisRecorder.def_node_num.get(node));
      }
      rs.append(" \n");
      out.write(rs.toString());
    }
    out.close();
  }

  /**
   * Generates _target.txt files for live variable analysis
   *
   * If for stmt C, the variable x and y are alive before this stmt,
   * then write as:
   *    C x y
   */
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

  /**
   * Generates _node_def.txt files
   *
   * If definition stmt A is b = 3 and stmt B is c = b+1,
   * then write as :
   *   A b
   *   B c
   */
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

  /**
   * Generates _node_use.txt files
   *
   * If stmt A is func(b,d) and stmt B is c = b+1
   * then write as:
   *   A b d
   *   B b
   */
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
