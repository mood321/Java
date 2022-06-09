_## 学习MIT 6.824 部分笔记

之前笔记找时间补上

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

### 7.5 日志快照（Log Snapshot）

Log压缩和快照解决的问题是：在系统长时间运行,日志追加,日志会变得和庞大,消耗磁盘的大量空间 如果还是用日志重放的方式去恢复数据,时间也会很漫长

对于大多数的应用程序来说，应用程序的状态远小于Log的大小,因为一条数据总是有多个版本,多条LOG
                                                

### 7.6 线性一致（Linearizability）

通常来说，线性一致等价于强一致。一个服务是线性一致的，那么它表现的就像只有一个服务器，并且服务器没有故障，这个服务器每次执行一个客户端请求，并且没什么奇怪的是事情发生。

线性一致对于这个顺序，有两个限制条件：

+ 如果一个操作在另一个操作开始前就结束了，那么这个操作必须在执行历史中出现在另一个操作前面。
+ 执行历史中，读操作，必须在相应的key的写操作之后。
                                            
## 8.1  线性一致（Linearizability）

线性一致性的定义就是,所有客户端的 读写请求历史记录 能构成线性,没有环

线性一致性大致等于强一致性, 但生产系统很少有线性一致性的,  

+ 快照一致性不是线性一致性,因为快照 它不会包括该快照之后的写入
+ zk也不是线性的,他的副本读,可能读到旧数据 

### 8.3 线性一致（Linearizability）（3）

对于读请求不允许返回旧的数据，只能返回最新的数据。或者说，对于读请求，线性一致系统只能返回最近一次完成的写请求写入的值

在一个实际的系统实现中，可能有任何原因导致这个结果，例如：
+ Leader在某个时间故障了
+ 这个客户端发送了一个读请求，但是这个请求丢包了因此Leader没有收到这个请求
+ Leader收到了这个读请求并且执行了它，但是回复的报文被网络丢包了
+ Leader收到了请求并开始执行，在完成执行之前故障了
+ Leader执行了这个请求，但是在返回响应的时候故障了

一般来说,执行失败,客户端可能有重试机制, 所以,服务端一定要有幂等,根据请求的唯一号或者其他的客户端信息来保存一个表

### 8.4 Zookeeper

Zookeeper，作为一个多副本系统，Zookeeper是一个容错的，通用的协调服务，它与其他系统一样，通过多副本来完成容错

但是他3台,5台,7台 ,并不能直接提升性能,因为zk的 leader会成为性能瓶颈 ,而且会降低,他需要将更多的操作日志发出去(只考虑写请求)

为此zk 放弃了线性一致,他是可以提供旧数据的,读请求就不用必须走leader了,提升了性能  (zk 自己管这种一致性叫<b> 顺序一致性</b>)

### 8.5 一致保证（Consistency Guarantees）

Zookeeper 会保证写请求的一致性, 表现的以某种顺序 ,一次执行一次写请求

另一个保证是，任何一个客户端的请求，都会按照客户端指定的顺序来执行，论文里称之为FIFO（First In First Out）客户端序列

对于读写混合的模式,  因为客户端会给每个请求打上顺序, 读之前有写, 读从副本节点读, 副本会判断他前面顺序的写请求有没有执行,

在执行之前,副本是不能返回结果给客户端的(可能是阻塞,可能是返回失败)

### 8.6 同步操作（sync）

Zookeeper有一个操作类型是sync，它本质上就是一个写请求 

我想读出Zookeeper中最新的数据。这个时候，我可以发送一个sync请求，它的效果相当于一个写请求，发送一个sync请求，之后再发送读请求。

这个读请求可以保证看到sync对应的状态，所以可以合理的认为是最新的 但这是一个有代价的操作

### 8.7 就绪文件（Ready file/znode）

所有的client 向zk发送操作的顺序，和这些操作被执行的顺序，是一致的

<img src="/src/main/resources/note/mit6.824/image.png"> 

保证 每个client的“写”的顺序在zk执行的是一致的 操作的原子性用“ready file”来实现

写的时候:

+ 大致的思想是，要操作到某个数据，先检查对于的标记“ready file”是否存在，存在才能操作
+ 在修改对应的数据的时候，会先删除这个“ready file”标记，修改完再create 这个"ready file"( 在)

读的时候:

判断"ready file"是否存在(在写读混合模式下)

+ 存在,说明之前的写操作已经执行,直接读
+ 不存在,这个客户端会在特定(zxid)上面建立watch事件监听,后续有操作,他再尝试读取

                                                                                                                                                                                                                              
### 9.1 Zookeeper API

Zookeeper的特点：
+ Zookeeper基于（类似于）Raft框架，所以我们可以认为它是，当然它的确是容错的，它在发生网络分区的时候，也能有正确的行为。
+ 当我们在分析各种Zookeeper的应用时，我们也需要记住Zookeeper有一些性能增强，使得读请求可以在任何副本被处理，因此，可能会返回旧数据。
+ 另一方面，Zookeeper可以确保一次只处理一个写请求，并且所有的副本都能看到一致的写请求顺序。这样，所有副本的状态才能保证是一致的（写请求会改变状态，一致的写请求顺序可以保证状态一致）。
+ 由一个客户端发出的所有读写请求会按照客户端发出的顺序执行。
+ 一个特定客户端的连续请求，后来的请求总是能看到相比较于前一个请求相同或者更晚的状态（详见8.5 FIFO客户端序列）

Zookeeper的目标是解决什么问题，或者期望用来解决什么问题？

+ 对于我来说，使用Zookeeper的一个主要原因是，它可以是一个VMware FT所需要的Test-and-Set服务（详见4.7）的实现。Test-and-Set服务在发生主备切换时是必须存在的，但是在VMware FT论文中对它的描述却又像个谜一样，论文里没有介绍：这个服务究竟是什么，它是容错的吗，它能容忍网络分区吗？Zookeeper实际的为我们提供工具来写一个容错的，完全满足VMware FT要求的Test-and-Set服务，并且可以在网络分区时，仍然有正确的行为。这是Zookeeper的核心功能之一。
+ 使用Zookeeper还可以做很多其他有用的事情。其中一件是，人们可以用它来发布其他服务器使用的配置信息。例如，向某些Worker节点发布当前Master的IP地址。
+ 另一个Zookeeper的经典应用是选举Master。当一个旧的Master节点故障时，哪怕说出现了网络分区，我们需要让所有的节点都认可同一个新的Master节点。
+ 如果新选举的Master需要将其状态保持到最新，比如说GFS的Master需要存储对于一个特定的Chunk的Primary节点在哪，现在GFS的Master节点可以将其存储在Zookeeper中，并且知道Zookeeper不会丢失这个信息。当旧的Master崩溃了，一个新的Master被选出来替代旧的Master，这个新的Master可以直接从Zookeeper中读出旧Master的状态。
+ 其他还有，对于一个类似于MapReduce的系统，Worker节点可以通过在Zookeeper中创建小文件来注册自己。
+ 同样还是类似于MapReduce这样的系统，你可以设想Master节点通过向Zookeeper写入具体的工作，之后Worker节点从Zookeeper中一个一个的取出工作，执行，完成之后再删除工作。

Zookeeper以RPC的方式暴露以下API。

+ CREATE(PATH，DATA，FLAG)。入参分别是文件的全路径名PATH，数据DATA，和表明znode类型的FLAG。这里有意思的是，CREATE的语义是排他的。也就是说，如果我向Zookeeper请求创建一个文件，如果我得到了yes的返回，那么说明这个文件之前不存在，我是第一个创建这个文件的客户端；如果我得到了no或者一个错误的返回，那么说明这个文件之前已经存在了。所以，客户端知道文件的创建是排他的。在后面有关锁的例子中，我们会看到，如果有多个客户端同时创建同一个文件，实际成功创建文件（获得了锁）的那个客户端是可以通过CREATE的返回知道的。
+ DELETE(PATH，VERSION)。入参分别是文件的全路径名PATH，和版本号VERSION。有一件事情我之前没有提到，每一个znode都有一个表示当前版本号的version，当znode有更新时，version也会随之增加。对于delete和一些其他的update操作，你可以增加一个version参数，表明当且仅当znode的当前版本号与传入的version相同，才执行操作。当存在多个客户端同时要做相同的操作时，这里的参数version会非常有帮助（并发操作不会被覆盖）。所以，对于delete，你可以传入一个version表明，只有当znode版本匹配时才删除。
+ EXIST(PATH，WATCH)。入参分别是文件的全路径名PATH，和一个有趣的额外参数WATCH。通过指定watch，你可以监听对应文件的变化。不论文件是否存在，你都可以设置watch为true，这样Zookeeper可以确保如果文件有任何变更，例如创建，删除，修改，都会通知到客户端。此外，判断文件是否存在和watch文件的变化，在Zookeeper内是原子操作。所以，当调用exist并传入watch为true时，不可能在Zookeeper实际判断文件是否存在，和建立watch通道之间，插入任何的创建文件的操作，这对于正确性来说非常重要。
+ GETDATA(PATH，WATCH)。入参分别是文件的全路径名PATH，和WATCH标志位。这里的watch监听的是文件的内容的变化。
+ SETDATA(PATH，DATA，VERSION)。入参分别是文件的全路径名PATH，数据DATA，和版本号VERSION。如果你传入了version，那么Zookeeper当且仅当文件的版本号与传入的version一致时，才会更新文件。
+ LIST(PATH)。入参是目录的路径名，返回的是路径下的所有文件。

### 9.2 使用Zookeeper实现计数器

zk 的多客户端,get->put 操作不是原子的
     
<pre>
WHILE TRUE:
    X, V = GETDATA("F")
    IF SETDATA("f", X + 1, V):
        BREAK
</pre>

这是通常写法, 但这种写法值在低负载的场景使用, 因为他重试的次数和客户端多少挂钩,复杂度是 O(n^2)

### 9.3 使用Zookeeper实现非扩展锁

<pre>
WHILE TRUE:
    IF CREATE("f", data, ephemeral=TRUE): RETURN
    IF EXIST("f", watch=TRUE):
        WAIT
</pre>

一般来说会尝试去创建:
+ 成功,加锁成功
+ 失败,会在节点上加watch 监听 ,直到之前成功的del

但监听会有和上面,累加的场景一样的问题 ,在del的时候 会有羊群效应,一般解决方案是,watch 序号节点,给他排队(见9.4)

### 9.4 使用Zookeeper实现可扩展锁
<pre>
CREATE("f", data, sequential=TRUE, ephemeral=TRUE)
WHILE TRUE:
    LIST("f*")
    IF NO LOWER #FILE: RETURN
    IF EXIST(NEXT LOWER #FILE, watch=TRUE):
        WAIT
</pre>

这有问题,就是 中间序号的客户端节点 如果挂了, 或者持有锁挂了, 这种可以依赖,zk的临时znode自动del的机制做



### 9.5 链复制（Chain Replication）(CRAQ)

+ 第一个是它通过复制实现了容错；
+ 第二是它通过以链复制API请求这种有趣的方式，提供了与Raft相比不一样的属性。

CRAQ是对于一个叫链式复制（Chain Replication）的旧方案的改进,有许多系统使用了他

，Zookeeper为了能够从任意副本执行读请求，不得不牺牲数据的实时性，因此也就不是线性一致的。CRAQ却可以从任意副本执行读请求，同时也保留线性一致性

<img src="/src/main/resources/note/mit6.824/img.png">

这里只是Chain Replication，并不是CRAQ。Chain Replication本身是线性一致的,在没有故障,他是一致的

从全局看,只有一个请求,tail 尾节点处理完了,才算commit,读请求才能读到,意味着链条上所有节点都成功

### 9.6 链复制的故障恢复（Fail Recover）

在这个模式下 只有两种可能

+ 一种没有故障,tail 处理完成,commit
+ 一种中间链表节点有一个出现故障, 链表后面的都没有

- 如果HEAD出现故障，作为最接近的服务器，下一个节点可以接手成为新的HEAD，并不需要做任何其他的操作。对于还在处理中的请求，可以分为两种情况：

  + 对于任何已经发送到了第二个节点的写请求，不会因为HEAD故障而停止转发，它会持续转发直到commit。
  + 如果写请求发送到HEAD，在HEAD转发这个写请求之前HEAD就故障了，那么这个写请求必然没有commit，也必然没有人知道这个写请求，我们也必然没有向发送这个写请求的客户端确认这个请求，因为写请求必然没能送到TAIL。所以，对于只送到了HEAD，并且在HEAD将其转发前HEAD就故障了的写请求，我们不必做任何事情。或许客户端会重发这个写请求，但是这并不是我们需要担心的问题。

- 如果TAIL出现故障，处理流程也非常相似，TAIL的前一个节点可以接手成为新的TAIL。所有TAIL知道的信息，TAIL的前一个节点必然都知道，因为TAIL的所有信息都是其前一个节点告知的
- 中间节点出现问题就去除故障节点

Chain Replication与Raft进行对比，有以下差别：
+ 从性能上看，raft的leader 需要处理所有副本,Chain Replication只需要处理后继节点 ,所有性能瓶颈来的更晚
+ raft 的读写都会从leader, CRAQ,写请求走head ,读请求是tail节点发的,所以压力分摊了
+ 故障恢复，Chain Replication也比Raft更加简单,这是主要动力

### 9.7 链复制的配置管理器（Configuration Manager）

CRAQ 并不能处理脑裂和网络分区 ,这意味它不能单独使用。

总是会有一个外部的权威（External Authority）来决定谁是活的，谁挂了，并确保所有参与者都认可由哪些节点组成一条链，这样在链的组成上就不会有分歧。这个外部的权威通常称为Configuration Manager。

有一个基于Raft或者Paxos的Configuration Manager，它是容错的，也不会受脑裂的影响。

之后，通过一系列的配置更新通知，Configuration Manager将数据中心内的服务器分成多个链。

比如说，Configuration Manager决定链A由服务器S1，S2，S3组成，链B由服务器S4，S5，S6组成。
### 10.1 Aurora 背景历史

### 10.2 故障可恢复事务（Crash Recoverable Transaction）

Aurora 实际上主要关注的是，如何实现一个故障可恢复事务（Crash Recoverable Transaction）。所以这一部分我们主要看的是事务（Transaction）和故障可恢复（Crash Recovery）

事务是指将多个操作打包成原子操作，并确保多个操作顺序执行  ,并在执行的时候 ,其他事务是不可以看到中间状态的 ,并在故障的时候保持原子

在单机数据库系统中, 用B-tree 存储在硬盘上, 用预写日志(WAL) 来保证系统容错

在数据库操作的时候 ,都会对最近从磁盘读取的page 有缓存

在写数据的时候,并不会直接写磁盘 还在本地缓存(change buffer) , 在事务提交前,都会写WAL (mysql redo log),日志落盘,顺序写入一般

<img src="src/main/resources/note/mit6.824/mysqlupdate.png">
 
<pre>
ps:   mysql 实现,非唯一索引,在操作的的时候回存到 change buffer( change buffer会落盘), 之后merge 进磁盘, 维护了二级索引

redo日志属于物理日志, 只是记录一下事务对数据库做了哪些修改 
undo log 是逻辑日志,他实际是跟在 mvcc的多版本控制链表上的


物理日志VS逻辑日志
物理日志: 记录的是每一个page页中具体存储的值是多少，在这个数据页上做了什么修改.  比如: 某个事物将系统表空间中的第100个页面中偏移量为1000处的那个字节的值1改为2.
逻辑日志: 记录的是每一个page页面中具体数据是怎么变动的，它会记录一个变动的过程或SQL语句的逻辑, 比如: 把一个page页中的一个数据从1改为2，再从2改为3,逻辑日志就会记录1->2,2->3这个数据变化的过程
</pre>

### 10.3 关系型数据库（Amazon RDS）

Amazon RDS用EC2实例作为数据库,它的data page和WAL Log存储在EBS，而不是对应服务器的本地硬盘。(我猜polarDB 在存储上的思路也差不多,存算分离 )

但这种方式,日志会很大,副本备份 网络传输很吃心性能

### 10.4 Aurora 初探

Aurora 有多个副本 在不同数据中心(AZ), 在同步的时候只同步日志, 每条日志 ,只记新值,旧值  实际上比数据大小更小

ps: 因为之前笔记还没补,所以这儿ps下, 复制的两种方式: 状态转移和 复制状态机(一般实现 就是快照复制, 日志回放,优缺点可以类比redis两种备份)

只存日志 ,会导致,系统不再通用,只用于数据库日志

他写是采用 Quorum 方式,全部响应 (分布式事务: 两段式原子提交 和 共识算法,之后有时间谢谢ddia的笔记)

### 10.5 Aurora存储服务器的容错目标（Fault-Tolerant Goals）

Aurora的容错目标是什么？

+ 一个AZ挂了之后 ,写不受影响
+ 对于读操作，当一个AZ和一个其他AZ的服务器挂了之后，读操作不受影响,对于Aurora 读,他们可以容忍 额外的一个AZ挂掉
+ Aurora期望能够容忍暂时的慢副本
+ 如果一个服务器看起来永久故障了，我们期望能够尽可能快的根据剩下的副本，生成一个新的副本。(因为一个出来故障,那意味着很多也会出现故障)

这里只针对他的存储服务器和 容错

### 10.6 Quorum 复制机制（Quorum Replication）

假设有N个副本。为了能够执行写请求，必须要确保写操作被W个副本确认，W小于N。所以你需要将写请求发送到这W个副本。
如果要执行读请求，那么至少需要从R个副本得到所读取的信息。这里的W对应的数字称为Write Quorum，R对应的数字称为Read Quorum。
这是一个典型的Quorum配置

要点:R加上W必须大于N（ 至少满足R + W = N + 1 ），这样任意W个服务器至少与任意R个服务器有一个重合。

这说明你读都是能读到正确值的(但不一定一定对)

这时候的读 就可能读到错的,读取你投票也是没用的 ,所以一般方案是 写的时候加版本,读选一个版本号最高的 返回

你如果不能从 quorum数量的节点,那你结果是不能保证的 ,会重试或者返回错误

和CRAQ 相比,有点就是剔除了耗时久的节点 ,不用全部执行一遍 ,能动态临时修改数量 提升性能

### 10.7 Aurora读写存储服务器

Aurora的写,并不会更新数据 ,而是增加版本 追加日志(这点类似es所用的lsm日志) ,写的时候 Quorum 共识方案,  

而且先只写日志 惰性合并,后面读取最新数据才会合并

在普通操作中,是可以避免 Read Quorum的, 每一个读序号,你只需要读 副本存在最新的就行(类似kafka的 ISR机制), 这样就不用去读Quorum数量 ,提升课性能

但是，数据库服务器有时也会使用Quorum Read。 硬件故障,挂掉了一台,基础设施 会开一个新的节点, 新的数据库 需要同步之前的数据, 他是需要Quorum的

我们可能会有这样一种场景，第一个副本有第101个Log条目，第二个副本有第102个Log条目，第三个副本有第104个Log条目，但是没有一个副本持有第103个Log条目。

### 10.8 数据分片（Protection Group）

为了能支持超过10TB数据的大型数据库. 数据库的数据，分割存储到多组存储服务器上,每组6个副本 

但分片之后,log 的存储就不那么直观了,Aurora 的做法是,data page刚在哪,log放在哪 ,实际上常见的 分库分表的 根据具体属性计算出来 , hdfs 的nameNode 的存到内存管理都行

他的副本机制 意味着 ，如果一个服务器挂了，它可以并行的，快速的在数百台服务器上恢复, 挂的服务器太多可能有问题


### 10.9 只读数据库（Read-only Database）

Aurora不仅有主数据库实例，同时多个数据库的副本 

对于写请求，可以只发送给一个数据库,因为无主分布式事务是有点难搞的 (facebook的 Cassandra 是无主的,有兴趣可以看看)_

对于读请求, 不用只给主了 ,只读数据库需要弄清楚它需要哪些data page来处理这个读请求，之后直接从存储服务器读取这些data page，

然后缓存起来 , 但有修改,主节点会吧log 日志给只读数据库,但这意味着 主从是有延时的(polarDB 有这个问题,具体是不是如果有大佬知道 请告知)

其实还有一些问题

+ 我们不想只读数据库看到未commit的数据。在主数据库发给只读数据库的Log流中，主数据库需要指出，哪些事务commit了，
而只读数据库需要小心的不要应用未commit的事务到自己的缓存中，它们需要等到事务commit了再应用对应的Log
+ 数据库的b-tree 非常复杂, 是需要经常rebalance,来调整树上的节点 ,是原子的, 但他是有中间状态的 ,只读数据库 直接从存储服务器上读数据,
他是能看到中间状态数据的
  
论文中讨论了微事务（Mini-Transaction）和VDL/VCL。这部分实际讨论的就是，数据库服务器可以通知存储服务器说，这部分复杂的Log序列只能以原子性向只读数据库展示，也就是要么全展示，要么不展示。这就是微事务（Mini-Transaction）和VDL。所以当一个只读数据库需要向存储服务器查看一个data page时，存储服务器会小心的，要么展示微事务之前的状态，要么展示微事务之后的状态，但是绝不会展示中间状态。

总结:

+ 事务型数据库是如何工作的，并且知道事务型数据库与后端存储之间交互带来的影响。这里涉及了性能，故障修复，以及运行一个数据库的复杂度
+ Quorum思想。通过读写Quorum的重合，可以确保总是能看见最新的数据，但是又具备容错性。这种思想在Raft中也有体现，Raft可以认为是一种强Quorum的实现（读写操作都要过半服务器认可）
+ 数据库和存储系统基本是一起开发出来的，数据库和存储系统以一种有趣的方式集成在了一起。通常我们设计系统时，需要有好的隔离解耦来区分上层服务和底层的基础架构。所以通常来说，存储系统是非常通用的，并不会为某个特定的应用程序定制。因为一个通用的设计可以被大量服务使用。但是在Aurora面临的问题中，性能问题是非常严重的，它不得不通过模糊服务和底层基础架构的边界来获得35倍的性能提升，这是个巨大的成功。
+ 最后一件有意思的事情是，论文中的一些有关云基础架构中什么更重要的隐含信息。例如：
  - 需要担心整个AZ会出现故障；
  - 需要担心短暂的慢副本，这是经常会出现的问题；
  - 网络是主要的瓶颈，毕竟Aurora通过网络发送的是极短的数据，但是相应的，存储服务器需要做更多的工作（应用Log），
  因为有6个副本，所以有6个CPU在复制执行这些redo Log条目，明显，从Amazon看来，网络容量相比CPU要重要的多。
  ps: 时代发展,现在架构中几乎不把网络当成主要瓶颈了, 分布式数据库 都是这个概念





