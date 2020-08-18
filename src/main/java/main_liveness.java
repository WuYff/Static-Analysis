import soot.*;
import soot.jimple.toolkits.pointer.LocalMustNotAliasAnalysis;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.FlowSet;

import java.io.*;
import java.util.*;
public class main_liveness {
  private static int file_num =0;
  private static String output ="/Users/yiwu/Documents/Senior/SE/soot/src/data/liveness";

  public static void main(String[] args) {

    // if needed add path to rt.jar (or classes.jar)
    String classPath="/Users/yiwu/Documents/Senior/UCInspire/dataset/any/target/classes";
//    String classPath = "/Users/yiwu/Documents/Senior/UCInspire/dataset/commons-lang/target/classes";
//    String classPath = "/Users/yiwu/Documents/Senior/UCInspire/dataset/jsoup/target/classes";
//    String classPath ="/Users/yiwu/Documents/Senior/UCInspire/dataset/jackson-core/target/classes";
//    String classPath = "/Users/yiwu/Documents/Senior/UCInspire/dataset/jfreechart/target/classes";
    //		String classPath = ".:/Library/Java/JavaVirtualMachines/jdk1.8.0_231.jdk/Contents/Home/jre/lib/rt.jar";
    String[] sootArgs = {
        "-p","jb","use-original-names:true", // keep the original variable name. Use javac -g to compile the java file
        "-cp", classPath,
        "-pp",// sets the class path for Soot
        "-process-dir", classPath, // process the whole directory
        "-w", 	// Whole program analysis, necessary for using Transformer
        "-src-prec", "class",		// Specify type of source file
        "-d",output,
        "-f", "J"					// Specify type of output file
    };
    System.out.println("Start liveness analysis.");

    PackManager.v().getPack("jtp").add(
        new Transform("jtp.myTransform", new BodyTransformer() {
          protected void internalTransform(Body body, String phase, Map options) {
              DirectedGraph g = new BriefUnitGraph(body);
//            System.out.println("Graph!!!!!!!!!!!");
//               System.out.println(g);
              LiveVariableAnalysis  analysis = null;
              try {
                analysis = new LiveVariableAnalysis (g);
              } catch (Exception e) {
                e.printStackTrace();
              }
              try {
//                assert analysis != null;
                write(analysis, g, file_num++);
              } catch (IOException e) {
                e.printStackTrace();
              }

          }
        }));
    soot.Main.main(sootArgs);
    System.out.println("Create "+file_num+" graph in total.");
  }





  public static void write(LiveVariableAnalysis  analysis,DirectedGraph graph, int n ) throws IOException {


    Set<Integer > nc =  analysis.node_child.keySet();
    if (nc.size() <=3 ) {
      // we skip such simple graph
      return;
    }
    String name = output+"/lang" +n;
    BufferedWriter out = new BufferedWriter(new FileWriter(name + "_graph" + ".txt"));
    StringBuilder w = new StringBuilder();
    for(Integer k: nc){
      w = new StringBuilder(k + " ");
      List<Integer> child = analysis.node_child.get(k);
      for(Integer i: child){
        w.append(i).append(" ");

      }
      w.append("\n");
      out.write(w.toString());
    }
    out.close();


    BufferedWriter out2 = new BufferedWriter(new FileWriter(name+"_target"+ ".txt"));
    Iterator<Unit> unitIt = graph.iterator();

    while (unitIt.hasNext()) {
//      System.out.println("-----------------------------------------------------------");
      Unit s = unitIt.next();
//      System.out.println("Unit :"+s);
      FlowSet<Local>  set = analysis.getFlowBefore(s);
//      System.out.println("FlowAfter :"+set);
      int node_num = analysis.node_num.get(s);
      StringBuilder rs= new StringBuilder();
      rs.append(node_num);
      for (Local local: set) {
        rs.append(" ").append(analysis.variable_name_to_int.get(local.getName()));
      }
      rs.append(" \n");
      out2.write(rs.toString());
    }
    out2.close();

    BufferedWriter out3 = new BufferedWriter(new FileWriter(name+"_node_use"+ ".txt"));

    for (Integer key : analysis. node_variable_use.keySet()){
      StringBuilder hh = new StringBuilder();
      hh.append(key);
      for (Integer i : analysis. node_variable_use.get(key)){
        hh.append(" ").append(i);
      }

      hh.append(" \n");
      out3.write(hh.toString());
    }
    out3.close();



    BufferedWriter out4 = new BufferedWriter(new FileWriter(name+"_node_label"+ ".txt"));
    Set<Integer > rk =  analysis.node_variable_def.keySet();
    String r = "";
    for(Integer k: rk){
      w = new StringBuilder(k + " ");
      Integer v = analysis.node_variable_def.get(k);
      w.append(v).append(" ");
      w.append("\n");
      out4.write(w.toString());
    }
    out4.close();


  }


}