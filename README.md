# Static Analysis
- This repo is for generating dataset for GNN. 
- Data-flow analysis using Soot.
- Output: A .jsonl file for a kind of static analysis
    - [reaching_def_data.jsonl](https://github.com/WuYff/Gated-Graph-Nerual-Network/blob/master/dataset/reaching_def_data.jsonl) 
    - [live_variable_data.jsonl](https://github.com/WuYff/Gated-Graph-Nerual-Network/blob/master/dataset/live_variable_data.jsonl) 

## Reaching Definition Analysis

- Run [MainReachingDef.java](https://github.com/WuYff/Static-Analysis/blob/master/src/main/java/MainReachingDef.java) 
- Referrence: See reaching definition analysis in this  [slide](https://pascal-group.bitbucket.io/lectures/static-program-analysis-3-4.pdf#page=41) from p41 to p121

## Live Variable Analysis

- Run [MainLiveVariable.java](https://github.com/WuYff/Static-Analysis/blob/master/src/main/java/MainLiveVariable.java)
- Referrence: See live variable analysis in this [slide](https://pascal-group.bitbucket.io/lectures/static-program-analysis-3-4.pdf#page=137) from p137 to p228

## Configuration
```java
    int number_of_node_upper_bound = 60; // the max number of nodes for each graph
    int number_of_node_lower_bound = 3; // the min number of nodes for each graph
    // The root directory that contains all the .class files you want to process
    String classPath ="/Users/yiwu/Documents/Senior/UCInspire/dataset/jfreechart/target/classes";
    // The path of .json file to store the generated dataset
    String filePath = "/Users/yiwu/Documents/Senior/SE/soot/src/data/reaching_def_data.jsonl";
    // The output directory (end with "/") for jimple file
    String output = "/Users/yiwu/Documents/Senior/SE/soot/src/data/reaching/";
```

- Because this is intra-procedure static data-flow analysis, we generate a graph for each method. As a result, most graphes are not large. About 56% of graphs has less than 20 nodes and about 94% of graphs with less than 40 nodes.Thus we have `number_of_node_lower_bound` and `number_of_node_lower_bound` to limit the node range and discard very few graph with large number of nodes. You can `run count.py` to get the distribution of the dataset.
## Open-source Java Project
I used the following java project to generate data:
- [jfreechart](https://github.com/jfree/jfreechart)
- [jsoup](https://github.com/jhy/jsoup)
- [jackson-core](https://github.com/FasterXML/jackson-core)
- [commons-lang](https://github.com/apache/commons-lang)
- [Leetcode](https://github.com/fishercoder1534/Leetcode)
- [Algorithms](https://github.com/williamfiset/Algorithms)

## Example
1. 
    ```shell
    git clone https://github.com/jfree/jfreechart.git
    cd ./jfreechart
    mvn clean compile  #generate .class files of jfreechart project
    ```
2. Set ` String classPath = /Path/jfreechart/target/classes` in `MainReachingDef.java`
3. Run `main` method in `MainReachingDef.java`

## Issue of Soot
- It would be better to collect more data but could meet this [issue](https://github.com/soot-oss/soot/issues/505) of soot for some code repo.

