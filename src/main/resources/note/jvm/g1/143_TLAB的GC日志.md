前面我们学了很多的理论知识，而程序在运行过程中，需要做什么，参数怎么设置，程序运行中会发生什么，我们还是需要去模拟验证、实践。

 

所以本节课开始，会花一周的时间去写模拟代码，模拟G1回收器的使用，并尝试读懂G1的GC日志。

TLAB相关的日志。

1、参数设置

首先参数上要有一套基本的设置：

-XX:InitialHeapSize=128M -XX:MaxHeapSize=128M -XX:+UseG1GC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintTLAB -XX:+UnlockExperimentalVMOptions -XX:G1LogLevel=finest -XX:MaxGCPauseMillis=20 -Xloggc:gc.log

堆内存的大小设置为128M是因为，G1本身的region都要最小1M，总共就没有几个region，如果设置成10MB这种值，不好做实验。

 

注意:-XX:+UnlockExperimentalVMOptions 这个参数是一个实验选项，打开以后才可以使用-XX:G1LogLevel=finest这个参数，来设置日志的输出级别。

 

2、针对这个参数设置，我们写一套简单的demo代码

public class TLABDemo {

  private static final ArrayList<String> strs = new ArrayList<String>();

  public static void main(String[] args) throws Exception{

​    for (int n = 0 ;;n++){

​      // 外层循环100次，每次内层循环创建10个字符串

​      for (int i = 0; i<100; i++){

​        // 内层循环10次

​        for (int j = 0; j < 10; j++){

​          strs.add(new String("NO." + j + "Str" ));

​        }

​      }

​      // 无限循环，每次循环间隔0.1s

​      System.out.println("第" + n + "次循环");

​      Thread.sleep(100);

​    }

  }

}

 

这段代码的意思其实也很简单，无限循环，去创建字符串儿，放到一个list去，每次循环会执行1000次创建字符串，并把字符串加入到list的操作。通过这段代码，我们先看看TLAB相关的GC日志信息。

 

3、代码运行参数设置

在运行上面的代码之前，我们需要先对参数进行一个设置。首先要找到主类：

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/25038600_1641819508.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

然后点击主类的编辑按钮：

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/20138000_1641819508.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

进入如下界面：

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/18148300_1641819508.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

在VM options中，复制我们上面提供的参数，点击Apply，然后就可以运行代码了。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/18949200_1641819508.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

接下来运行代码。代码运行起来之后，会在项目根目录输出一份儿gc.log日志。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/image/ueditor/24078200_1641819508.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

打开这个日志文件，就能看到所有G1运行过程中的日志情况了。

 

4、TLAB相关的运行日志

// 这一部分是我们设置的一些参数信息此处就不再赘述。

Java HotSpot(TM) 64-Bit Server VM (25.162-b12) for windows-amd64 JRE (1.8.0_162-b12), built on Dec 19 2017 20:00:03 by "java_re" with MS VC++ 10.0 (VS2010)

Memory: 4k page, physical 16629016k(9165564k free), swap 20823320k(7957616k free)

CommandLine flags: -XX:G1LogLevel=finest -XX:InitialHeapSize=134217728 -XX:MaxGCPauseMillis=20 -XX:MaxHeapSize=134217728 -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintTLAB -XX:+UnlockExperimentalVMOptions -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseG1GC -XX:-UseLargePagesIndividualAllocation

2.236: [GC pause (G1 Evacuation Pause) (young)

TLAB: gc thread: 0x00000000181fd800 [id: 8648] desired_size: 122KB slow allocs: 0 refill waste: 1960B alloc: 0.99996   6144KB refills: 1 waste 1.4% gc: 1704B slow: 0B fast: 0B

TLAB: gc thread: 0x00000000181fa000 [id: 3280] desired_size: 122KB slow allocs: 2 refill waste: 1960B alloc: 0.99996   6144KB refills: 3 waste 25.0% gc: 91168B slow: 3104B fast: 0B

TLAB: gc thread: 0x00000000181bb000 [id: 10280] desired_size: 122KB slow allocs: 2 refill waste: 1960B alloc: 0.99996   6144KB refills: 7 waste 2.0% gc: 13992B slow: 3624B fast: 0B

TLAB: gc thread: 0x0000000003683800 [id: 12164] desired_size: 122KB slow allocs: 6 refill waste: 1960B alloc: 0.99996   6144KB refills: 41 waste 0.1% gc: 0B slow: 7584B fast: 0B

TLAB totals: thrds: 4 refills: 52 max: 41 slow allocs: 10 max 6 waste: 1.9% gc: 106864B max: 91168B slow: 14312B max: 7584B fast: 0B max: 0B

, 0.0025449 secs]

 

// 这一部分是我们设置的一些参数信息此处就不再赘述。

Java HotSpot(TM) 64-Bit Server VM (25.162-b12) for windows-amd64 JRE (1.8.0_162-b12), built on Dec 19 2017 20:00:03 by "java_re" with MS VC++ 10.0 (VS2010)

Memory: 4k page, physical 16629016k(9165564k free), swap 20823320k(7957616k free)

CommandLine flags: -XX:G1LogLevel=finest -XX:InitialHeapSize=134217728 -XX:MaxGCPauseMillis=20 -XX:MaxHeapSize=134217728 -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintTLAB -XX:+UnlockExperimentalVMOptions -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseG1GC -XX:-UseLargePagesIndividualAllocation

 

// 这一部分，我们看到，GC pause，很关键，这个其实就是我们的停顿时间。后面跟的young就指的是新生代。

2.236: [GC pause (G1 Evacuation Pause) (young)

 

// 注意看TLAB后跟的这一大段，其中有几个关键点需要我们注意。

TLAB: gc thread: 0x00000000181fd800 [id: 8648] desired_size: 122KB slow allocs: 0 refill waste: 1960B alloc: 0.99996   6144KB refills: 1 waste 1.4% gc: 1704B slow: 0B fast: 0B

 

gc thread表示是哪个线程的TLAB。

desired_size: 122KB指的是期望分配的TLAB的大小。这个值，其实就是我们前面讲解TLAB计算公式的时候，计算出来的值。TLABSize = Eden * 2 * 1%/线程个数

slow allocs: 0 为慢速分配的次数，0说明每次都使用了TLAB快速分配。没有直接使用堆内存去分配。

refill waste: 1960B代表可浪费的内存，也是重新申请一个TLAB的阈值。

alloc: 0.99996表示当前这个线程在一个分区中分配对象占用的比例。Region使用的比例

refills: 1 代表，出现了多少次废弃TLAB（即填充一个dummy对象），重新申请一个TLAB的次数，次数为1，只做了一次。注意，这个地方只是代表refills的次数也就是填充TLAB的次数，也可以理解为申请新的TLAB的次数。

waste 1.4% gc: 1704B slow: 0B fast: 0B代表的是浪费的空间，这个浪费的空间分为三个层面。

gc表示，GC时还没有使用的TLAB的空间。现在正处于GC的状态中，TLAB还剩下多少空间没有使用

slow表示，申请新的TLAB时，旧的TLAB浪费的空间，这里就是0B，因为只产生了1次

fast表示，在出现需要调整TLAB的大小的时候，即refill_waste不合理的时候，旧的TLAB浪费的空间。dummy对象造成的浪费。

2.236: [GC pause (G1 Evacuation Pause) (young)TLAB: gc thread: 0x00000000181fd800 [id: 8648] desired_size: 122KB slow allocs: 0 refill waste: 1960B alloc: 0.99996   6144KB refills: 1 waste 1.4% gc: 1704B slow: 0B fast: 0B

 

后面的日志，是多个线程持有的不同的TLAB的具体情况。大家可以自行分析一下。观察这个TLAB状态，refill次数还是非常有用的，可以帮助我们调整TLAB相关参数的设置。有助于优化性能。比如，经过压测，我们观察了gc日志，发现当TLAB的大小设置成 1/4regionSize会出现更少的TLAB动态调整的情况，并且这个值能够保证大多数对象能够直接走TLAB分配，那么我们就可以手动设置TLAB的大小，不使用G1自动推断的值，避免自动调整这个过程。（自动调整是需要代价的，每次自动调整都会有一定的性能损耗）