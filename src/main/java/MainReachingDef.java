import soot.*;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import java.util.*;
public class MainReachingDef {
  private static int file_num =0;

  //  static int no =9;
  public static void main(String[] args) {

    // if needed add path to rt.jar (or classes.jar)
//    String classPath="/Users/yiwu/Documents/Senior/UCInspire/dataset/any/target/classes";
    //    String classPath = "/Users/yiwu/Documents/Senior/UCInspire/dataset/commons-lang/target/classes";
    //    String classPath = "/Users/yiwu/Documents/Senior/UCInspire/dataset/jsoup/target/classes";
    //    String classPath ="/Users/yiwu/Documents/Senior/UCInspire/dataset/jackson-core/target/classes";
        String classPath = "/Users/yiwu/Documents/Senior/UCInspire/dataset/jfreechart/target/classes";
    //		String classPath = ".:/Library/Java/JavaVirtualMachines/jdk1.8.0_231.jdk/Contents/Home/jre/lib/rt.jar";

    String output = "/Users/yiwu/Documents/Senior/SE/soot/src/data/reaching_def/";
    String[] sootArgs = {
        "-p","jb","use-original-names:true", // keep the original variable name. Use javac -g to compile the java file
        "-cp", classPath,
        "-pp",
        "-process-dir", classPath,
        "-w", 						// Whole program analysis, necessary for using Transformer
        "-src-prec", "class",		// Specify type of source file
        "-d", output,
        "-f", "J"					// Specify type of output file
    };
    System.out.println("Start reaching definition analysis.");

    PackManager.v().getPack("jtp").add(
        new Transform("jtp.myTransform", new BodyTransformer() {
          protected void internalTransform(Body body, String phase, Map options) {
            DirectedGraph g = new BriefUnitGraph(body);
            try {
              ReachingDefAnalysis analysis = new ReachingDefAnalysis(new BriefUnitGraph(body));
              if (analysis.staticAnalysisRecorder.node_num.size()>3) { //skip simple graph
                Writer writer = new Writer(analysis, analysis.staticAnalysisRecorder, output + "p" + file_num++);
                writer.write_graph();
                writer.write_node_def();
                writer.write_target_reaching_definition(g);
              }
            } catch (Exception e) {
              e.printStackTrace();
            }

          }
        }));

    soot.Main.main(sootArgs);
    System.out.println("Create "+file_num+" graph in total.");
  }
  }
