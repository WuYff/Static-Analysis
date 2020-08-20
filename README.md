# Static Analysis
This repo is for generating dataset for GNN. 
Data flow analysis using Soot.  
## Reaching Definition Analysis

- Run [MainReachingDef.java](https://github.com/WuYff/Static-Analysis/blob/master/src/main/java/MainReachingDef.java) 
- The program generates 3 .txt files for each method:
    - _graph.txt
    - _target.txt
    - _node_def.txt

- Referrence: See reaching definition analysis in this  [slide](https://pascal-group.bitbucket.io/lectures/static-program-analysis-3-4.pdf#page=41) from p41 to p121

## Live Variable Analysis

- Run [MainLiveVariable.java](https://github.com/WuYff/Static-Analysis/blob/master/src/main/java/MainLiveVariable.java)
- The program generates 4 .txt files for each method:
    - _graph.txt
    - _target.txt
    - _node_def.txt
    - _node_use.txt

- Referrence: See live variable analysis in this [slide](https://pascal-group.bitbucket.io/lectures/static-program-analysis-3-4.pdf#page=137) from p137 to p228
