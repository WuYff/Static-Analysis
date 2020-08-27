# Static Analysis
- This repo is for generating dataset for GNN. Data flow analysis using Soot.
- Output: A .jsonl file for each kind of analysis
    - [reaching_def_data.jsonl](https://github.com/WuYff/Gated-Graph-Nerual-Network/blob/master/dataset/reaching_def_data.jsonl) 
    - [live_variable_data.jsonl](https://github.com/WuYff/Gated-Graph-Nerual-Network/blob/master/dataset/live_variable_data.jsonl) 

## Reaching Definition Analysis

- Run [MainReachingDef.java](https://github.com/WuYff/Static-Analysis/blob/master/src/main/java/MainReachingDef.java) 
- Referrence: See reaching definition analysis in this  [slide](https://pascal-group.bitbucket.io/lectures/static-program-analysis-3-4.pdf#page=41) from p41 to p121

## Live Variable Analysis

- Run [MainLiveVariable.java](https://github.com/WuYff/Static-Analysis/blob/master/src/main/java/MainLiveVariable.java)
- Referrence: See live variable analysis in this [slide](https://pascal-group.bitbucket.io/lectures/static-program-analysis-3-4.pdf#page=137) from p137 to p228

## Open-source Java Project
Use the following java project to generate data:
- [jfreechart](https://github.com/apache/commons-compress.git)
- [jsoup](https://github.com/jhy/jsoup)
- [jackson-core](https://github.com/FasterXML/jackson-core)
- [commons-lang](https://github.com/jhy/jsoup)
- [Leetcode](https://github.com/fishercoder1534/Leetcode)
- [Algorithms](https://github.com/williamfiset/Algorithms)


