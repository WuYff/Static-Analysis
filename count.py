import jsonlines

count ={5:0,10:0,20:0,30:0,40:0,50:0,60:0}

with open("/Users/yiwu/Documents/Senior/SE/soot/src/data/reaching_def_data.jsonl", "r+", encoding="utf8") as f:
    sum =0
    for item in jsonlines.Reader(f):
        sum +=1
        graph = eval(item["graph"])
        target = eval(item["target"])
        node_def = eval(item["node_def"])
        max_node_id_of_one_graph = item["max_node_id_of_one_graph"]
        max_def_id_of_one_graph = item["max_def_id_of_one_graph"]
        for i in count:
            if max_node_id_of_one_graph <= i:
                count[i]+=1
                break
print(count)
print(sum)
for i in count:
    count[i] = count[i]/sum*100
print(count)
            