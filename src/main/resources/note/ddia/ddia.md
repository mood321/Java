## 设计数据密集型应用 概要阅读笔记
                        

<img src="src/main/resources/note/ddia/img.png">

#第一部分：数据系统基础
   
## 第一章：可靠性、可伸缩性和可维护性

<img src="src/main/resources/note/ddia/img_1.png">

系统的分类:
+ 数据密集型（data-intensive） ,更多的是数据量、数据复杂性、以及数据的变更速度
+ 计算密集型（compute-intensive）,集中在CPU的运算

数据密集系统的通用功能:
+ 存储数据，以便自己或其他应用程序之后能再次找到 （数据库，即 databases）
+ 记住开销昂贵操作的结果，加快读取速度（缓存，即 caches）
+ 允许用户按关键字搜索数据，或以各种方式对数据进行过滤（搜索索引，即 search indexes）
+ 向其他进程发送消息，进行异步处理（流处理，即 stream processing）
+ 定期处理累积的大批量数据（批处理，即 batch processing）


<img src="src/main/resources/note/ddia/img_2.png">

流式处理：关注实时性。kafka，storm，spark streaming，flink等
批处理：关注处理性能，不太要求实时性。MapReduce，spark.

大多数软件系统中都很重要的问题：

+ 可靠性（Reliability）系统在, 困境（adversity，比如硬件故障、软件故障、人为错误）中仍可正常工作（正确完成功能，并能达到期望的性能水准）。
+ 可伸缩性（Scalability）,有合理的办法应对系统的增长（数据量、流量、复杂性）
+ 可维护性（Maintainability）许多不同的人（工程师、运维）在不同的生命周期，都能高效地在系统上工作（使系统保持现有行为，并适应新的应用场景）


###  可靠性