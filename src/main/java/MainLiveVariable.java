import soot.*;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import java.util.*;
public class MainLiveVariable {

  private static int file_num =0;

  public static void main(String[] args) {

    // if needed add path to rt.jar (or classes.jar)
//    String classPath="/Users/yiwu/Documents/Senior/UCInspire/dataset/any/target/classes";
//    String classPath = "/Users/yiwu/Documents/Senior/UCInspire/dataset/commons-lang/target/classes";
//    String classPath = "/Users/yiwu/Documents/Senior/UCInspire/dataset/jsoup/target/classes";
//    String classPath ="/Users/yiwu/Documents/Senior/UCInspire/dataset/jackson-core/target/classes";
    String classPath = "/Users/yiwu/Documents/Senior/UCInspire/dataset/jfreechart/target/classes";
    //		String classPath = ".:/Library/Java/JavaVirtualMachines/jdk1.8.0_231.jdk/Contents/Home/jre/lib/rt.jar";
    String output = "/Users/yiwu/Documents/Senior/SE/soot/src/data/liveness/";
    String[] sootArgs = {
        "-p","jb","use-original-names:true", // keep the original variable name. Use javac -g to compile the java file
        "-cp", classPath,
        "-pp",// sets the class path for Soot
        "-process-dir", classPath, // process the whole directory
        "-w", 	// Whole program analysis, necessary for using Transformer
        "-src-prec", "class",		// Specify type of source file
        "-d", output,
        "-f", "J"					// Specify type of output file
    };
    System.out.println("Start liveness analysis.");

    PackManager.v().getPack("jtp").add(
        new Transform("jtp.myTransform", new BodyTransformer() {
          protected void internalTransform(Body body, String phase, Map options) {
              DirectedGraph g = new BriefUnitGraph(body);
              try {
                LiveVariableAnalysis analysis = new LiveVariableAnalysis(g);
                if (analysis.staticAnalysisRecorder.node_num.size()>3) {
                  Writer writer = new Writer(analysis, analysis.staticAnalysisRecorder, output + "p" + file_num++);
                  writer.write_graph();
                  writer.write_target_live_variable(g);
                  writer.write_node_def();
                  writer.write_node_use();
                }
              }  catch (Exception e) {
                e.printStackTrace();
              }

          }
        }));
    soot.Main.main(sootArgs);
    System.out.println("Create "+file_num+" graph in total.");
  }
}