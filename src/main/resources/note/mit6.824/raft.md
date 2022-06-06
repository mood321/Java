## 学习MIT 6.824 部分笔记


### raft

#### 7.1 日志恢复（Log Backup）

#### 7.2 Raft选举约束
              
经典场景
     
<img src="https://906337931-files.gitbook.io/~/files/v0/b/gitbook-legacy-files/o/assets%2F-MAkokVMtbC7djI1pgSw%2F-MBU-cLGzrm5ZAw-M4Lb%2F-MBWdJ_lN3OZSailEx6z%2Fimage.png?alt=media&token=f0282299-a149-4720-acb2-b6a40c248c0b">


节点只能向满足下面条件之一的候选人投出赞成票：

+ 候选人最后一条Log条目的任期号大于本地最后一条Log条目的任期号；

+ 或者，候选人最后一条Log条目的任期号等于本地最后一条Log条目的任期号，且候选人的Log记录长度大于等于本地Log记录的长度


### 7.3 快速恢复（Fast Backup）

在日志恢复的时候,如果每一次值恢复一条,一台机器长时间掉线是很耗时的,

可以让Follower在回复Leader的AppendEntries消息中，携带3个额外的信息，来加速日志的恢复。这里的回复是指，Follower因为Log信息不匹配，拒绝了Leader的AppendEntries之后的回复。这里的三个信息是指：

+ XTerm：这个是Follower中与Leader冲突的Log对应的任期号。在之前（7.1）有介绍Leader会在prevLogTerm中带上本地Log记录中，前一条Log的任期号。如果Follower在对应位置的任期号不匹配，它会拒绝Leader的AppendEntries消息，并将自己的任期号放在XTerm中。如果Follower在对应位置没有Log，那么这里会返回 -1。
+ XIndex：这个是Follower中，对应任期号为XTerm的第一条Log条目的槽位号。
+ XLen：如果Follower在对应位置没有Log，那么XTerm会返回-1，XLen表示空白的Log槽位数。

### 7.4 持久化（Persistence）

为了解决机器故障/断点等情况, 需要对一些数据持久化,便于重启或迁移

有且仅有三个数据是需要持久化存储的。它们分别是Log、currentTerm、votedFor

+ Log需要被持久化存储的原因是，这是唯一记录了应用程序状态的地方。所以当服务器重启时，唯一能用来重建应用程序状态的信息就是存储在Log中的一系列操作，所以Log必须要被持久化存储。
+ currentTerm和votedFor都是用来确保每个任期只有最多一个Leader。votedFor 保证投完票,重启完,知道这轮 自己是否投过票
+ currentTerm要更微妙一些，但是实际上还是为了实现一个任期内最多只有一个Leader，重启之后我们不知道任期号是什么，很难确保一个任期内只有一个Leader。 

向磁盘写数据是一个代价很高的操作。一个机械硬盘，我们通过写文件的方式来持久化存储，向磁盘写入任何数据都需要花费大概10毫秒时间。

为了提高效率,这里有很多选择 ,使用ssd ,或者闪存,ssd 可以0.1毫秒完成一次写操作,提升了100倍 ,使用电池供电的DRAM的,在电池的可供电时间内重启都不会丢数据,如果资金充足，且不怕复杂的话，这种方式的优点是，你可以每秒写DRAM数百万次，那么持久化存储就不再会是一个性能瓶颈

另一个常见方法是，批量执行操作, 限定时间,数量批量持久化

为什么服务器重启时，commitIndex、lastApplied、nextIndex、matchIndex，可以被丢弃？

是因为Leader可以通过检查自己的Log和发送给Followers的AppendEntries的结果来对比出来commit,所以不持久化,也能比出来




