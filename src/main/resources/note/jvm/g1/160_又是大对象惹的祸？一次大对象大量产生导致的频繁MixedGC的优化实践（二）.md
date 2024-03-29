这节课我们继续来讲这个案例，因为案例比较长，所以我们还是把上节课案例的一些被背景信息，查错的过程继续放在这里。

1、案例背景

业务背景：一个小时购电商系统。我们知道，电商平台一般都有几种模式，淘宝这种属于长距离电商，买的东西可能来自于全国各地。京东属于中距离电商平台，有自己的仓储物流系统，买东西基本上是走最近的仓库发货 ，速度上相对会快一点。而在这个基础之上还有近距离电商，比如天猫超市、京东到家、京东小时购等等这种类型的业务，购买之后，立马会有物流系统去接单，配送的。

 

这个系统的特点就是，需要使用大量的缓存，包括一些接入到我们平台的店铺信息，以及店铺的商品信息，平台自营的一些商品、店铺信息等等。并且在商品中心里面，商品更新、新商品上家这些操作会发送mq，我们这边儿的系统，一批一批的去取缓存数据，消费数据，更新redis缓存，一部分数据同步到本地缓存中。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/38255900_1646623928.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

然后分批消费的数据会在内存里进行解析，更新到redis之后，再同步一部分数据到本地缓存中。

另外一个缓存更新的业务点是，有很多商品数据是直接批量上架的，尤其是自营类型的商品，会直接批量上架一批次的商品，那么在商品更新之后，会发送一条通知消息给这个缓存系统，缓存系统拿到通知消息后，去生成一个job任务，然后执行这个job，去读取商品系统里面的一些数据，缓存到系统中去。

2、问题现场

整个这个过程，在平常系统正常运行的时候，是没什么问题的，因为商品本身上架的频率就不高，商品信息的一些数据更新也并不是很频繁，因此在日常的运行中这个系统运行起来没有任何问题。

其实从业务逻辑上来说，只要没有特殊情况，这个业务逻辑上也是没有任何问题的，因为只是一个缓存更新的逻辑而已。

然而在双十一前夕，商户运营那边儿反馈，有少量商品的缓存信息不显示，或者显示的很慢，要加载好几秒才能加载到，实时性比较差。虽然这个问题听起来不是很严重，但是毕竟是个问题，尤其是在双十一这种大促时间结点的前夕。

于是，我们团队开始去排查这个问题，具体的表现是在双十一前夕，商品中心的qps并不是很大，单台机器，在高峰期只有4-500的qps，总qps也不过就是5k+。然后再去观察redis集群的qps，总体也是在一个比较健康的范围内，不管是带宽使用率（我们带宽是100MB，当时高峰期查看只占用了20MB左右的带宽），还是CPU、内存，都是比较合理的值。那为什么还是会出现商品系统查询商品信息的时候加载慢的情况呢？这个时候我们只能去看看出现加载慢的时候的具体情况。查看商品系统的日志发现，在出现加载慢的时候，日志中有一条 “写入数据到缓存中……”log日志，在这个日志打印之后大概过了有四五秒中才继续往后打印日志。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/40123400_1646623928.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

那么这个日志有啥意义呢？其实很简单，因为我们在缓存没有命中的时候，是需要从数据库中查询数据，然后加载到缓存中去的，然后加载完成之后，会把查询到的数据返回给前端。如果缓存设置卡住的时间比较长，就会造成请求的整体响应时间比较长。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/40653900_1646623928.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

在QPS不高的情况下，出现查询超时，并且是经过数据库查询之后，设置数据到缓存中导致的超时。而redis本身又没有什么问题（刚刚提到的带宽，CPU，内存指标都是正常的）。于是没有办法，我们就只能继续针对redis本身来查问题。

 

3、redis缓存有啥问题？

这里给大家补充一个细节，就是，更新商品的缓存，我们是按照店铺这个来加锁的，因为这样做不至于产生大量的分布式锁在redis中。因为按照店铺的话，这个粒度其实已经够了，每个店铺，更新商品的频率哪怕是双十一前的准备，频率也不会很高。

言归正传，查redis的时候，因为我们其实已经知道缓存更新的时候是按照店铺的维度来加的锁，那么就直接查看一下这个锁相关的内容，结合redis分布式锁的日志发现，获取锁失败进入等待的时间比较长，导致没有直接把这个缓存设置到redis中，以至于，我们这个请求的接口等待的时间比较长，大家想想看，4-5s钟的等待时间，很多接口如果使用默认的超时时间，直接就判定超时了！所以这个问题还是非常严重的。

 

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/95477800_1646623928.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

知道这个问题之后，我们开始尝试排查解决，首先要找到的就是为什么会超时？谁在持有这把锁，导致更新的时候出现的这么长时间的等待？要查这个问题，其实相对容易一点儿，我们直接通过redis的客户端，来查看相关redis 分布式锁的lock key就可以了。这个其实也简单，大家了解redis分布式锁的都知道，其实加锁的本质就是先去获取一个key，这个key对应的value里面保存了一些加锁的信息。

很快我们查到了匹配这个商铺相关的分布式锁，此时，发现，加锁的机器，居然是我们上面提到的那个小时购平台的缓存同步系统的其中一台机器！

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/94393600_1646623928.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

到了这一步，很显然我们已经知道大概的原因了，那就是因为这个缓存同步服务里面的一些更新操作，获取到了锁，去更新这个商铺的商品缓存，然而更新的时间比较长，导致商品系统查询数据库->更新缓存这个操作被阻塞了，最终导致接口请求时间非常长。那么接下来我们就是要找到这个原因，为啥同步缓存会这么久？

 

4、缓存同步服务有啥问题？

缓存同步服务持有锁这么长时间，导致其他的机器获取锁等待时间过久这个问题是什么原因？

我们先来分析一下几种可能性：

（1）更新的商品数量太多，导致缓存服务压力过大

（2）代码出现了bug，导致系统出现了一些问题

（3）更新店铺更新的商品数量太多，导致加锁时间过长

（4）更新的总体数据量太大，导致系统经常出现GC，GC时间对更新操作的一些影响。

 

那么我们现在排除法来做。

（1）商品数量再怎么多，其实也不至于造成缓存服务压力过大的情况。大家思考一下就能知道，某宝，某东，某多，这些电商平台已经非常大的，他们的商品数量也就是几十万，最多上百万，而且即使双十一，更新的商品数量其实也不会太多，能占个10%已经算比较高的了。毕竟商品信息变更的情况是非常少的。我们算它双十一20w的商品更新数量，放到双十一前面的一两周时间里更新。分散到每天也不过就是很少量的数据罢了。所以说，压力过大这个排除。

（2）代码出现bug的情况，其实很容易找，直接日志看看就能看出来到底报错没有报错。如果不是异常这种错误，那么也可以结合日志，jvm监控等等可以看出来代码的问题。而我们本次的排查过程，只是出现了偶尔请求时间过长的情况，刷新之后还能正常显示，一般情况下不太可能是代码bug造成的。

（3）店铺的商品过多，导致加锁时间过长。这个其实是有可能的，因为更新商品缓存，如果一个店铺比较大的话，商品本身的数据信息，图片信息等等会有很多，有些商铺可能能达到几百，上千个商品同时出现一些信息的变更。所以这个我们暂时不排除

（4）更新的总体数据量太大，导致gc，这个问题其实可以结合上面的一点来看，也是完全有可能的。因为某个店铺更新量大，和整体更新量大都是有可能拖慢系统更新缓存的速度的。

所以我们就针对3 4两个点来做排查。当时排查的时候，发现店铺的更新的商品数量其实并不是很高，大概也就是在30个左右，虽然也有图片的更新（图片缓存不是直接放redis的，这点要注意），但是总体数据量不大。

这个时候，我们唯一的方向就是看看JVM相关的东西，是不是对整体造成了影响。排查了JVM的gc日志之后，发现确实在出现请求时间非常长的情况的时候，有大量的Mixed GC出现。并且Mixed GC持续的时间非常长，且频率非常高，基本上是10分钟就要进行一轮Mixed GC，每一轮，基本上都要跑满8次（我们线上环境设置的参数，就是8次）这就导致了超时的问题出现。

 

5、线上环境的参数设置及调优

-XX:InitialHeapSize=20G -XX:MaxHeapSize=20G -Xss1M -XX:+UseG1GC -XX:SurvivorRatio=8 -XX:MaxGCPauseMillis=300 -XX:G1HeapRegionSize=4M -XX:MaxTenuringThreshold=15 -XX:InitiatingHeapOccupancyPercent=30

 

这是一套我们当时线上环境的一些核心参数。

 

目前为止，我们已经定位出来，是因为MixedGC过于频繁，导致了一些更新缓存的操作比较慢。那到底发生了啥呢？这里有一个细节，再跟大家同步一下，就是job任务批量更新缓存是其中一部分。还有一部分是job做缓存不一致的对比补偿操作，当然这个具体规则，校验逻辑，数据筛选逻辑也都非常复杂，这里不做赘述。

我们看下面一张图：

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/95874200_1646623928.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

在平常的系统运行，之所以没有出现问题，是因为job定时更新缓存，频率不高，补偿job的频率，和数据量其实也不高，总体没有造成影响。

而双十一期间，虽然看起来商品数据量没有变化太大，但是，因为补偿任务和定时同步缓存的同时存在，导致其实我们有数倍于真实需要更新的（20w）商品数据的数据量需要获取到系统中，大概总数据量能达到200w以上，并且这个补偿的操作，还是一个定时执行的操作，也就是过一段时间就要搞大量的数据到缓存服务中。

这就出现了大量的补偿操作带来大量的数据，导致同步更新变更的操作的内存资源被大量占用。其实这个现象出现的时候，线上还经常出现机器的内存告警，只不过当时的运维认为这个告警过一会儿就自动消失，并且不影响系统的整体稳定性（因为不是主链路），就直接忽略了。直到我们检查了JVM的GC日志，分析了一下dump文件，才发现有大量的大对象，并且是缓存补偿的job持有的集合占用了大量的内存。

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/96168200_1646623928.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

继续观察GC日志发现，大对象分区占用的内存比例上升非常快。并且每次执行MixedGC的时候，基本上都是伴随着humongous allocation failer这种日志。所以，这个问题最终的原因其实也很明显，就是大量大对象产生，导致直接进入大对象分区（大对象分区占用的内存，在G1判定是否老年代占用率，决定是否mixedgc的时候，也是会算在老年代占用里面的）。最终导致频繁出发mixed gc。

 

到此其实还有一个疑问，那就是，补偿任务一般都是分批次去补偿的，分批次，一批一批处理，按道理不会有这么多大对象啊？

直到我们看了下线上的参数才明白了问题所在。

-XX:InitialHeapSize=20G -XX:MaxHeapSize=20G -Xss1M -XX:+UseG1GC -XX:SurvivorRatio=8 -XX:MaxGCPauseMillis=300 -XX:G1HeapRegionSize=4M -XX:MaxTenuringThreshold=15 -XX:InitiatingHeapOccupancyPercent=30

因为有个哥们儿，在这个缓存服务系统的性能优化，他首先想到的就是，如果能够让gc的时间更快，是不是能够提升整体的性能？所以他认为这个里面有一个平衡，太大的region，可能会导致分析追踪对象时间过长，并且，如果region比较大，那么每个region里面存活对象的数量太多，就会导致选择性价比高的region比较耗时，因为需要对region进行大量的存活对象统计。他基于这个考虑，把regionSize调到了4M。认为可以提升一部分性能。

但是实际上，这些过程造成的性能问题基本上是可以忽略的。这种操作属于是因小失大了。改成4M，在平常数据量小的时候没啥问题，但是如果数据量大的时候，即使分批次，也是按照几百几百这样的数据量去获取，我们系统限制的就是200，一个商品的数据信息对象，大概能达到十多K，刚刚好造成了这个问题，大量对象直接进入大对象分区，而因为补偿的操作，也需要花费一定的时间，数据量大的时候，源源不断的拉取数据，而处理对比数据的速率却跟不上，导致大量对象存活在大对象分区，最终导致频繁的mixed gc 的发生。因而造成正常的缓存同步操作，被阻塞在其中。造成商品系统那边儿的缓存写入操作拿不到锁而阻塞。

我们看下面这张图：

![picture.png](http://wechatapppro-1252524126.cdn.xiaoeknow.com/apppuKyPtrl1086/image/ueditor/96050000_1646623928.png?imageView2/2/q/80%7CimageMogr2/ignore-error/1)

耗时操作，导致大量数据进入大对象region，进而导致频繁MixedGC，而Mixed GC又清理不出来很多空间。处理速度下降，导致偶尔就会有一批同步操作被阻塞，始终无法执行完毕释放分布式锁。最终导致请求超时的问题。

 

如何调优？其实很简单，我们需要做三步，

第一步：就是把region调大，调回到16MB，至于为啥是16MB，这个其实是经过大量的压测，调试，最终确定的。

一个比较取巧的方法大家也可以尝试，就是找一个优秀的开源系统，采取他们的一些参数设置其实就完全可以。需要注意的是要按照系统的匹配程度。比如RocketMQ的broker参数设置region就设置的16MB，那结合RocketMQ运行的特点，和系统是否匹配，就可以考虑是否可以借鉴他们的参数设置。

第二步：修改代码，把job补偿的频率调的低一点儿，同时把job每次处理的数据量调小一点。

第三步：修改新生代初始比例。调整为25，默认情况下是5，也就是初始占用5%的堆内存。

为什么要修改这个参数？大家仔细观察一下我们上面的例子，其实不难发现，这个系统真正出问题的时候，还是来源于，大量数据需要处理的时候，处理不过来，造成大量数据积压。那么如果我们不调整新生代的大小，系统启动前期，还是会出现mixed gc频繁的情况，因为会有大量存活对象经过YGC后进入到老年代区域。所以调大新生代的初始比例，可以让系统在启动初期就能有比较高的吞吐