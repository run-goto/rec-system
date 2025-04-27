# todo
* 图开始节点没有处理好

---
1） 数据集
在 DAG 图中流转的数据统一封装为 DataSet 数据集，数据集是结构化多行二维数据的封装，在数据集上封装便利的基础算子操作。
数据集上一系列处理操作基于 Java 的 Stream API 来处理，以此达到集合处理的最好性能，将非 Action 操作延迟到最后数据处理时运行。

2） 数据源
将能够返回数据或者数据交互的二方服务封装为通用数据源，所有业务算子围绕数据源的数据进行业务开发，通用数据源包括召回数据集、在线算法需要的辅助数据集（如存放在 KV 内存存储的旁路召回数据、特征等数据）、打分预估结果集、内存数据源等。
数据源的封装通过动态参数配置方式实现通用性和可扩展性。数据查询只需要修改配置即可实现数据获取，不需要开发代码。

3） 基础算子
在 DataSet 数据集上封装的基本操作作为基础算子，比如 Join、Union、Filter、Sort、Map、Collect 等流式操作。在 DataSet 上重新封装 Stream 相关 API，便于对 DataSet 进行流式处理。

4) 业务算子
召回、预估、合并、打散、过滤等业务操作封装为业务算子，在业务算子中可以查询数据源，返回数据集后通过基础算子计算得到结果。



5) yaml 配置


## 子图(定位为逻辑模版, 包含: 若干个算子及其依赖关系, 子图的配置及其默认值
## Note: 子图的配置实际为算子的配置, 在算子中引用
name: 'Recall子图1' ## 场景全局唯一
type: 'subgraph' ## 标记图为"子图"
configs: ## 子图包含配置项( 指定默认值 )
  - name: 'configKey1' ## 
    value: '默认值Value, 可为string, json等, xx'
  # - 其他配置及其默认值
  # ...
nodes: ## 子图包含的所有算子, 通过dpends指定依赖.
  ## 比如一路召回
  - name: 'fistRecallOp1'
    op: 'com.dag.demo.recrecall.FirstRecallOP'
    depends: []
    # 指定子图中该算子的默认值
    configs:
    - name: 'configKey1'
      value: 'fistRecallOp1s value'
  - name: 'otherRecall1'
    op: 'com.dag.demo.recrecall.OtherRecallOP'
    depends: ['fistRecallOp1']

    
