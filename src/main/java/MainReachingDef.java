import soot.*;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import java.util.*;
/**
 * Perform reaching definition analysis and generate the dataset.
 */
public class MainReachingDef {
  private static int file_num =0;
  public static void main(String[] args) {
    // The root directory that contains all the .class files you want to process
    String classPath = "/Users/yiwu/Documents/Senior/UCInspire/dataset/jfreechart/target/classes";
    // The output directory (end with "/")
    String output = "/Users/yiwu/Documents/Senior/SE/soot/src/data/reaching_def/";
    String[] sootArgs = {
        "-p","jb","use-original-names:true", // Keep the original variable name.
        "-cp", classPath, // Set the class path for Soot
        "-pp",
        "-process-dir", classPath,
        "-w", 						// Whole program analysis, necessary for using Transformer
        "-src-prec", "class",		// Specify type of source file
        "-d", output, // Specify the output directory
        "-f", "J"					// Specify type of output file
    };
    System.out.println("Start reaching definition analysis.");

    PackManager.v().getPack("jtp").add(
        new Transform("jtp.myTransform", new BodyTransformer() {
          protected void internalTransform(Body body, String phase, Map options) {
            DirectedGraph g = new BriefUnitGraph(body);
            try {
              ReachingDefAnalysis analysis = new ReachingDefAnalysis(new BriefUnitGraph(body));
              // Skip simple graphs whose node number <= 3
              if (analysis.staticAnalysisRecorder.node_num.size()>3) { //skip simple graph
                Writer writer = new Writer(analysis, analysis.staticAnalysisRecorder, output + "p" + file_num++);
                writer.write_graph();
                writer.write_node_def();
                writer.write_target_reaching_definition_only_def_node(g);
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
