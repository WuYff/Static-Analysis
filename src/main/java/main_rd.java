import soot.*;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.FlowSet;

import java.io.*;
import java.util.*;
public class main_rd {
  private static int file_num =0;
  private static String output ="/Users/yiwu/Documents/Senior/SE/soot/src/data/reaching_def";

  //  static int no =9;
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
        "-process-dir", classPath,
        "-w", 						// Whole program analysis, necessary for using Transformer
        "-src-prec", "class",		// Specify type of source file
        "-d",output,
        //        "-main-class", mainClass,	// Specify the main class
        "-f", "J"					// fiSpecify type of output file
    };
    System.out.println("Start reaching definition  analysis.");
    PackManager.v().getPack("jtp").add(
        new Transform("jtp.myTransform", new BodyTransformer() {
          protected void internalTransform(Body body, String phase, Map options) {
            DirectedGraph g = new BriefUnitGraph(body);
            try {
              ReachingDefAnalysis2 analysis = new ReachingDefAnalysis2(new BriefUnitGraph(body));
              write(analysis,g, file_num++);
            } catch (Exception e) {
              e.printStackTrace();
            }

          }
        }));

    soot.Main.main(sootArgs);
    System.out.println("Create "+file_num+" graph in total.");
  }





  public static void write(ReachingDefAnalysis2 analysis, DirectedGraph graph, int n ) throws IOException {
    Set<Integer > ck =  analysis.node_child.keySet();
    if(ck.size() <= 3){
      // we skip such simple graph
      return;
    }
    String name = output+"/jfree_" +n;
    BufferedWriter out = new BufferedWriter(new FileWriter(name + "_graph" + ".txt"));
    StringBuilder w = new StringBuilder();
    for(Integer k: ck){
      w = new StringBuilder(k + " ");
      List<Integer> child = analysis.node_child.get(k);
      for(Integer i: child){
        w.append(i).append(" ");
      }
      w.append("\n");
      out.write(w.toString());
    }
    out.close();



    BufferedWriter out2 = new BufferedWriter(new FileWriter(name+"_node_label"+ ".txt"));
    Set<Integer > rk =  analysis.node_variable_def.keySet();
    String r = "";
    for(Integer k: rk){
      w = new StringBuilder(k + " ");
      Integer v = analysis.node_variable_def.get(k);
      w.append(v).append(" ");
      w.append("\n");
      out2.write(w.toString());
    }
    out2.close();


    BufferedWriter out3 = new BufferedWriter(new FileWriter(name+"_target"+ ".txt"));
    Iterator<Unit> unitIt = graph.iterator();

    while (unitIt.hasNext()) {
      Unit s = unitIt.next();
      FlowSet<Unit> set = analysis.getFlowAfter(s);
      int node_num = analysis.node_num.get(s);
      StringBuilder rs= new StringBuilder();
      rs.append(node_num);
      for (Unit node: set) {
        rs.append(" ").append(analysis.node_num.get(node));
      }
      rs.append(" \n");
      out3.write(rs.toString());
    }
    out3.close();

  }

  }
