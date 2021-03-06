### linux 学习笔记


### 基础操作
<p> passwd
<p> 修改密码    /etc/passwd 文件里
<p>  useradd cliu8
<p>添加用户   /root 和 /home/cliu8 
<p>/bin/bash 的位置是用于配置登录后的默认交互命令行的，不像 Windows，登录进去是界面，其实就是 explorer.exe。而 Linux 登录后的交互命令行是一个解析脚本的程序，这里配置的是 /bin/bash

<h4>文件
<p> ls -l 
<p>文件权限
<p>
<p>第一个字段剩下的 9 个字符是模式，其实就是权限位（access permission bits）。3 个一组，每一组 rwx 表示“读（read）”“写（write）”“执行（execute）”。如果是字母，就说明有这个权限；如果是横线，就是没有这个权限

<h4> 软件
<p> CentOS 下面使用rpm -i jdk-XXX_linux-x64_bin.rpm进行安装，Ubuntu 下面使用dpkg -i jdk-XXX_linux-x64_bin.deb。其中 -i 就是 install 的意思。
<p>删除，可以用rpm -e和dpkg -r。-e 就是 erase，-r 就是 remove。
<p> CentOS 来讲，配置文件在/etc/yum.repos.d/CentOS-Base.repo里
<pre><code>
[base]
name=CentOS-$releasever - Base - 163.com
baseurl=http://mirrors.163.com/centos/$releasever/os/$basearch/
gpgcheck=1
gpgkey=http://mirrors.163.com/centos/RPM-GPG-KEY-CentOS-7
</code></pre>
<p>主执行文件会放在 /usr/bin 或者 /usr/sbin 下面，其他的库文件会放在 /var 下面，配置文件会放在 /etc 下面。
<p>Linux 上面有一个工具 wget，后面加上链接，就能从网上下载了
<p>进行解压缩了。Windows 下可以有 winzip 之类的解压缩程序，Linux 下面默认会有 tar 程序。如果是解压缩 zip 包，就需要另行安装
<pre><code>
yum install zip.x86_64 unzip.x86_64
apt-get install zip unzip
</code></pre>
<p> tar.gz 这种格式的，通过 tar xvzf jdk-XXX_linux-x64_bin.tar.gz 就可以解压缩了

<h4>环境变量
<p> tar 解压缩之后，也需要配置环境变量，可以通过 export 命令来配置。
<pre><code>
            export JAVA_HOME=/root/jdk-XXX_linux-x64
            export PATH=$JAVA_HOME/bin:$PATH
</code></pre>

<p>export 命令仅在当前命令行的会话中管用，一旦退出重新登录进来，就不管用了

<p> /root 或者 /home/cliu8 下面，有一个.bashrc 文件，这个文件是以点开头的，这个文件默认看不到，需要 ls -la 才能看到，a 就是 all。每次登录的时候，这个文件都会运行，因而把它放在这里。这样登录进来就会自动执行。当然也可以通过 source .bashrc 手动执行

<h4>后台运行。
<p> nohup命令。这个命令的意思是 no hang up（不挂起），也就是说，当前交互命令行退出的时候，程序还要在

<h4> 关闭
<p> ps -ef |grep 关键字  |awk '{print $2}'|xargs kill -9

<h4> 服务
<p>通过命令yum install mariadb-server mariadb进行安装，命令systemctl start mariadb启动，命令systemctl enable mariadb设置开机启动。同理，会在 /usr/lib/systemd/system 目录下，创建一个 XXX.service 的配置文件，从而成为一个服务。

<p>shutdown -h now是现在就关机，reboot就是重启。

<h3> 启动
<h4> 立项服务与进程管理
<p>创建一个新的进程，需要一个老的进程调用 fork 来实现，其中老的进程叫作父进程（Parent Process），新的进程叫作子进程（Child Process）

<h4> 计算机的工作模式
<p> 最核心的就是CPU
<p> CPU 和其他设备连接，要靠一种叫作总线（Bus）的东西，其实就是主板上密密麻麻的集成电路，这些东西组成了 CPU 和其他设备的高速通道
<p>这些设备中，最重要的是内存（Memory）


<h4> CPU 包括三个部分，运算单元、数据单元和控制单元。
<p>运算单元只管算，例如做加法、做位移等等
<p> 运算单元计算的数据如果每次都要经过总线，到内存里面现拿，这样就太慢了，所以就有了数据单元。数据单元包括 CPU 内部的缓存和寄存器组，空间很小，但是速度飞快，可以暂时存放数据和运算结果。
<p>控制单元。控制单元是一个统一的指挥中心，它可以获得下一条指令，然后执行这条指令

<p>CPU 的控制单元里面，有一个指令指针寄存器，它里面存放的是下一条指令在内存中的地址。控制单元会不停地将代码段的指令拿进来，先放入指令寄存器。
<p>  当前的指令分两部分，一部分是做什么操作，例如是加法还是位移；一部分是操作哪些数据。
<p>   要执行这条指令，就要把第一部分交给运算单元，第二部分交给数据单元

<p>  多个指针寄存器切换 就是进程切换 这个概念和jvm的程序计数器是一样的

<p>CPU 和内存来来回回传数据，靠的都是总线。其实总线上主要有两类数据，一个是地址数据，也就是我想拿内存中哪个位置的数据，这类总线叫地址总线（Address Bus）；另一类是真正的数据，这类总线叫数据总线（Data Bus

<h4> x86  的总线规则
<img src="https://static001.geekbang.org/resource/image/2d/1c/2dc8237e996e699a0361a6b5ffd4871c.jpeg" >

<p>1 数据单元 -通用寄存器
<p>8086 处理器内部有 8 个 16 位的通用寄存器，也就是刚才说的 CPU 内部的数据单元，分别是 AX、BX、CX、DX、SP、BP、SI、DI。这些寄存器主要用于在计算过程中暂存数据。

<p>2 控制单元
<p>IP 寄存器就是指令指针寄存器（Instruction Pointer Register)，指向代码段中下一条指令的位置

<p> 切换进程
<p>每个进程都分代码段和数据段，为了指向不同进程的地址空间，有四个 16 位的段寄存器，分别是 CS、DS、SS、ES。
<p>   其中，CS 就是代码段寄存器（Code Segment Register），通过它可以找到代码在内存中的位置；DS 是数据段的寄存器，通过它可以找到数据在内存中的位置。
<p>   SS 是栈寄存器（Stack Register）

<h4>  32 位处理器 的兼容
<p>因为原来的模式其实有点不伦不类，因为它没有把 16 位当成一个段的起始地址，也没有按 8 位或者 16 位扩展的形式，而是根据当时的硬件，弄了一个不上不下的 20 位的地址
<p> 当系统刚刚启动的时候, CPU 是处于实模式的，这个时候和原来的模式是兼容的。也就是说，哪怕你买了 32 位的 CPU，也支持在原来的模式下运行，只不过快了一点而已。
<p>     当需要更多内存的时候，你可以遵循一定的规则，进行一系列的操作，然后切换到保护模式，就能够用到 32 位 CPU 更强大的能力。
<p>    这也就是说，不能无缝兼容，但是通过切换模式兼容，也是可以接受的。

<h3> BIOS 到bootloader

<p>在主板上，有一个东西叫ROM（Read Only Memory，只读存储器）。这和咱们平常说的内存RAM（Random Access Memory，随机存取存储器）不同。 只读

<p>第一条，BIOS 要检查一下系统的硬件是不是都好着呢。
<p>第二条，要有个办事大厅，只不过自己就是办事员。这个时期你能提供的服务很简单，但也会有零星的客户来提要求。

<h4>bootloader
<p>在 Linux 里面有一个工具，叫Grub2，全称 Grand Unified Bootloader Version 2。顾名思义，就是搞系统启动的
<img src="https://static001.geekbang.org/resource/image/2b/6a/2b8573bbbf31fc0cb0420e32d07b196a.jpeg" >
<p>BIOS 完成任务后，会将 boot.img 从硬盘加载到内存中的 0x7c00 来运行。
<p>由于 512 个字节实在有限，boot.img 做不了太多的事情。它能做的最重要的一个事情就是加载 grub2 的另一个镜像 core.img。
<p>引导扇区就是你找到的门卫，虽然他看着档案库的大门，但是知道的事情很少。他不知道你的宝典在哪里，但是，他知道应该问谁。门卫说，档案库入口处有个管理处，然后把你领到门口。
<p>core.img 就是管理处，它们知道的和能做的事情就多了一些。core.img 由 lzma_decompress.img、diskboot.img、kernel.img 和一系列的模块组成，功能比较丰富，能做很多事情。
<p>boot.img 先加载的是 core.img 的第一个扇区。如果从硬盘启动的话，这个扇区里面是 diskboot.img，对应的代码是 diskboot.S。
<p>boot.img 将控制权交给 diskboot.img 后，diskboot.img 的任务就是将 core.img 的其他部分加载进来，先是解压缩程序 lzma_decompress.img，再往下是 kernel.img，最后是各个模块 module 对应的映像。这里需要注意，它不是 Linux 的内核，而是 grub 的内核。
<p>lzma_decompress.img 对应的代码是 startup_raw.S，本来 kernel.img 是压缩过的，现在执行的时候，需要解压缩。
<p>在这之前，我们所有遇到过的程序都非常非常小，完全可以在实模式下运行，但是随着我们加载的东西越来越大，实模式这 1M 的地址空间实在放不下了，所以在真正的解压缩之前，lzma_decompress.img 做了一个重要的决定，就是调用 real_to_prot，切换到保护模式，这样就能在更大的寻址空间里面，加载更多的东西

<h4> 从实模式切换到保护模式
<p>切换到保护模式要干很多工作，大部分工作都与内存的访问方式有关。
<p>第一项是启用分段，就是在内存里面建立段描述符表，将寄存器里面的段寄存器变成段选择子，指向某个段描述符，这样就能实现不同进程的切换了。第二项是启动分页。能够管理的内存变大了，就需要将内存分成相等大小的块 
<p>一旦，你选定了某个宝典，启动某个操作系统，就要开始调用 grub_menu_execute_entry() ，开始解析并执行你选择的那一项。接下来你的经营企业之路就此打开了


<img src="https://static001.geekbang.org/resource/image/0a/6b/0a29c1d3e1a53b2523d2dcab3a59886b.jpeg" >


### 内核初始化

<img src="https://static001.geekbang.org/resource/image/cd/01/cdfc33db2fe1e07b6acf8faa3959cb01.jpeg" >

<h5>首先是项目管理部门
<p>在操作系统里面，先要有个创始进程，有一行指令 set_task_stack_end_magic(&init_task)。这里面有一个参数 init_task，它的定义是 struct task_struct init_task = INIT_TASK(init_task)。它是系统创建的第一个进程，我们称为0 号进程。这是唯一一个没有通过 fork 或者 kernel_thread 产生的进程，是进程列表的第一个。
<p> 所谓进程列表（Procese List），就是咱们前面说的项目管理工具，里面列着我们所有接的项目。

<h5> 办事大厅
<p>有了办事大厅，我们就可以响应客户的需求。
<p>  这里面对应的函数是 trap_init()，里面设置了很多中断门（Interrupt Gate），用于处理各种中断。其中有一个 set_system_intr_gate(IA32_SYSCALL_VECTOR, entry_INT80_32)，这是系统调用的中断门。系统调用也是通过发送中断的方式进行的。当然，64 位的有另外的系统调用方法，这一点我们放到后面的系统调用章节详细谈

<h5> 会议室管理系统
<p>mm_init() 就是用来初始化内存管理模块。
<p>项目需要项目管理进行调度，需要执行一定的调度策略。sched_init() 就是用于初始化调度模块。
<p>vfs_caches_init() 会用来初始化基于内存的文件系统 rootfs。在这个函数里面，会调用 mnt_init()->init_rootfs()。这里面有一行代码，register_filesystem(&rootfs_fs_type)。在 VFS 虚拟文件系统里面注册了一种类型，我们定义为 struct file_system_type rootfs_fs_type
<p>最后，start_kernel() 调用的是 rest_init()，用来做其他方面的初始化，这里面做了好多的工作

<h4>初始化 1 号进程
<p>rest_init 的第一大工作是，用 kernel_thread(kernel_init, NULL, CLONE_FS) 创建第二个进程，这个是1 号进程。
<p> 进程多起来 就需要权限分层  权限机制，把区域分成了四个 Ring，越往里权限越高，越往外权限越低。
<img src="https://static001.geekbang.org/resource/image/2b/42/2b53b470673cde8f9d8e2573f7d07242.jpg">

<p>操作系统很好地利用了这个机制，将能够访问关键资源的代码放在 Ring0，我们称为内核态（Kernel Mode）；将普通的程序代码放在 Ring3，我们称为用户态（User Mode）

<p> 用户态的进程调用系统调用 进程是需要暂停的  流程:用户态 - 系统调用 - 保存寄存器 - 内核态执行系统调用 - 恢复寄存器 - 返回用户态，然后接着运行
<img src="https://static001.geekbang.org/resource/image/d2/14/d2fce8af88dd278670395ce1ca6d4d14.jpg" >

<p> 从内核态到用户态
<p> 1号进程完成了 内核态的init 
<h5> ramdisk 
<p>init 终于从内核到用户态了。一开始到用户态的是 ramdisk 的 init，后来会启动真正根文件系统上的 init，成为所有用户态进程的祖先
<p>一个基于内存的文件系统。内存访问是不需要驱动的，这个就是 ramdisk。这个时候，ramdisk 是根文件系统。
<p> 然后，我们开始运行 ramdisk 上的 /init。等它运行完了就已经在用户态了。/init 这个程序会先根据存储系统的类型加载驱动，有了驱动就可以设置真正的根文件系统了。有了真正的根文件系统，ramdisk 上的 /init 会启动文件系统上的 init
<p>rest_init 的第一个大事情才完成。我们仅仅形成了用户态所有进程的祖先。

<h4> 创建 2 号进程
<p>rest_init 第二大事情就是第三个进程，就是 2 号进程。
<p>kernel_thread(kthreadd, NULL, CLONE_FS | CLONE_FILES) 又一次使用 kernel_thread 函数创建进程。这里需要指出一点，函数名 thread 可以翻译成“线程”，这也是操作系统很重要的一个概念。它和进程有什么区别呢？为什么这里创建的是进程，函数名却是线程呢？
<p>   从用户态来看，创建进程其实就是立项，也就是启动一个项目。这个项目包含很多资源，例如会议室、资料库等。这些东西都属于这个项目，但是这个项目需要人去执行。有多个人并行执行不同的部分，这就叫多线程（Multithreading）。如果只有一个人，那它就是这个项目的主线程。
<p>   但是从内核态来看，无论是进程，还是线程，我们都可以统称为任务（Task），都使用相同的数据结构


<h4> 系统调用

<img  src="https://static001.geekbang.org/resource/image/86/a5/868db3f559ad08659ddc74db07a9a0a5.jpg" >

<p> glibc 对系统调用的封装 
<p>内核态和用户态的模式切换
<img src="https://static001.geekbang.org/resource/image/86/a5/868db3f559ad08659ddc74db07a9a0a5.jpg" >


### 3   进程管理
<p> 文件编译过程，生成 so 文件和可执行文件，放在硬盘上。下图左边的用户态的进程 A 执行 fork，创建进程 B，在进程 B 的处理逻辑中，执行 exec 系列系统调用。这个系统调用会通过 load_elf_binary 方法，将刚才生成的可执行文件，加载到进程 B 的内存中执行。
<img src="https://static001.geekbang.org/resource/image/db/a9/dbd8785da6c3ce3fe1abb7bb5934b7a9.jpeg" >

<p> 创建线程的套路、mutex 使用的套路、条件变量使用的套路。
<img src="https://static001.geekbang.org/resource/image/02/58/02a774d7c0f83bb69fec4662622d6d58.png">

<p> 进程管理复杂的数据结构，我还是画一个图总结一下。这个图是进程管理 task_struct 的的结构图
<img src="https://static001.geekbang.org/resource/image/01/e8/016ae7fb63f8b3fd0ca072cb9964e3e8.jpeg">


<li>进程亲缘关系维护的数据结构，是一种很有参考价值的实现方式，在内核中会多个地方出现类似的结构；
<li>    进程权限中 setuid 的原理，这一点比较难理解，但是很重要，面试经常会考。
<h5> 内核栈
<p>内核栈，但是内容更加重要。如果说 task_struct 的其他成员变量都是和进程管理有关的，内核栈是和进程运行有关系的。
<li>   我这里画了一张图总结一下 32 位和 64 位的工作模式，左边是 32 位的，右边是 64 位的。
<li>   在用户态，应用程序进行了至少一次函数调用。32 位和 64 的传递参数的方式稍有不同，32 位的就是用函数栈，64 位的前 6 个参数用寄存器，其他的用函数栈。
<li>   在内核态，32 位和 64 位都使用内核栈，格式也稍有不同，主要集中在 pt_regs 结构上。
<li>   在内核态，32 位和 64 位的内核栈和 task_struct 的关联关系不同。32 位主要靠 thread_info，64 位主要靠 Per-CPU 变量

<img src="https://static001.geekbang.org/resource/image/82/5c/82ba663aad4f6bd946d48424196e515c.jpeg" >


<h5> 调度相关的数据结构
<p>一个 CPU 上有一个队列，CFS 的队列是一棵红黑树，树的每一个节点都是一个 sched_entity，每个 sched_entity 都属于一个 task_struct，task_struct 里面有指针指向这个进程属于哪个调度类
<p>    在调度的时候，依次调用调度类的函数，从 CPU 的队列中取出下一个进程。
<img src="https://static001.geekbang.org/resource/image/10/af/10381dbafe0f78d80beb87560a9506af.jpeg" >

<h5> 主动调度的过程
<p> 一个运行中的进程主动调用 __schedule 让出 CPU。在 __schedule 里面会做两件事情，第一是选取下一个进程，第二是进行上下文切换。而上下文切换又分用户态进程空间的切换和内核态的切换。
<img  src="https://static001.geekbang.org/resource/image/9f/64/9f4433e82c78ed5cd4399b4b116a9064.png" >

<h5> 抢占式调度
<p>这个脑图里面第一条就是总结了进程调度第一定律的核心函数 __schedule 的执行过程，这是上一节讲的，因为要切换的东西比较多，需要你详细了解每一部分是如何切换的。
<p>  第二条总结了标记为可抢占的场景，第三条是所有的抢占发生的时机，这里是真正验证了进程调度第一定律的
<img src="https://static001.geekbang.org/resource/image/93/7f/93588d71abd7f007397979f0ba7def7f.png" >

<h5> 创建一个进程
<p> fork 系统调用的过程。它包含两个重要的事件，一个是将 task_struct 结构复制一份并且初始化，另一个是试图唤醒新创建的子进程。
<p>    这个过程我画了一张图，你可以对照着这张图回顾进程创建的过程。
<p>    这个图的上半部分是复制 task_struct 结构，你可以对照着右面的 task_struct 结构图，看这里面的成员是如何一部分一部分的被复制的。图的下半部分是唤醒新创建的子进程，如果条件满足，就会将当前进程设置应该被调度的标识位，就等着当前进程执行 __schedule 了。

 <img  src="https://static001.geekbang.org/resource/image/9d/58/9d9c5779436da40cabf8e8599eb85558.jpeg">
  
<h5> 19  线程的创建
<p> 这个图对比了创建进程和创建线程在用户态和内核态的不同。
<p>    创建进程的话，调用的系统调用是 fork，在 copy_process 函数里面，会将五大结构 files_struct、fs_struct、sighand_struct、signal_struct、mm_struct 都复制一遍，从此父进程和子进程各用各的数据结构。而创建线程的话，调用的是系统调用 clone，在 copy_process 函数里面， 五大结构仅仅是引用计数加一，也即线程共享进程的数据结构。
 <img src="https://static001.geekbang.org/resource/image/14/4b/14635b1613d04df9f217c3508ae8524b.jpeg" > 
 
 
### 4 内存管理
 
 <h4> 20  独享内存空间
<p> 并且站在老板的角度，设计了虚拟地址空间应该存放的数据。
<p>  通过这一节，你应该知道，一个内存管理系统至少应该做三件事情：
<li> 第一，虚拟内存空间的管理，每个进程看到的是独立的、互不干扰的虚拟地址空间；
<li>  第二，物理内存的管理，物理内存地址只有内存管理模块能够使用；
<li>  第三，内存映射，需要将虚拟内存和物理内存映射、关联起来。 

<h4> 21 分段机制
<p> 分段机制、分页机制以及从虚拟地址到物理地址的映射方式。总结一下这两节，我们可以把内存管理系统精细化为下面三件事情：
<li>    第一，虚拟内存空间的管理，将虚拟内存分成大小相等的页；
<li>    第二，物理内存的管理，将物理内存分成大小相等的页；
<li>    第三，内存映射，将虚拟内存也和物理内存也映射起来，并且在内存紧张的时候可以换出到硬盘中。

<img  src="https://static001.geekbang.org/resource/image/7d/91/7dd9039e4ad2f6433aa09c14ede92991.jpg" >'

<h4> 22  进程空间管理
<p> 一个进程要运行起来需要以下的内存结构。
<p>    用户态：
<li>    代码段、全局变量、BSS
<li>   函数栈
<li>    堆
<li>    内存映射区
<p>    内核态：
<li>    内核的代码、全局变量、BSS
<li>    内核数据结构例如 task_struct
<li>    内核栈
<li>    内核中动态分配的内存
<p>    现在这些是不是已经都有了着落？
<p>   总结一下进程运行状态在 32 位下对应关系。
 <img  src="https://static001.geekbang.org/resource/image/28/e8/2861968d1907bc314b82c34c221aace8.jpeg" > 
 
 <p> 64 位的对应关系，只是稍有区别
 <img src="https://static001.geekbang.org/resource/image/2a/ce/2ad275ff8fdf6aafced4a7aeea4ca0ce.jpeg" >
 
 <h4> 23  物理内存管理  
<p>物理内存的组织形式，就像下面图中展示的一样。
<p>如果有多个 CPU，那就有多个节点。每个节点用 struct pglist_data 表示，放在一个数组里面。
<p>每个节点分为多个区域，每个区域用 struct zone 表示，也放在一个数组里面。
<p>每个区域分为多个页。为了方便分配，空闲页放在 struct free_area 里面，使用伙伴系统进行管理和分配，每一页用 struct page 表示。 
<img src="https://static001.geekbang.org/resource/image/3f/4f/3fa8123990e5ae2c86859f70a8351f4f.jpeg">

<h4>24 物理内存管理（下） 
<p>物理内存来讲，从下层到上层的关系及分配模式如下：
<li>物理内存分 NUMA 节点，分别进行管理；
<li>每个 NUMA 节点分成多个内存区域；
<li>每个内存区域分成多个物理页面；
<li>伙伴系统将多个连续的页面作为一个大的内存块分配给上层；
<li>kswapd 负责物理页面的换入换出；
<li>Slub Allocator 将从伙伴系统申请的大内存块切成小块，分配给其他系统。

<img src="https://static001.geekbang.org/resource/image/52/54/527e5c861fd06c6eb61a761e4214ba54.jpeg" >

<h4> 25 用户态内存映射
<p> 用户态的内存映射机制包含以下几个部分。
<li>用户态内存映射函数 mmap，包括用它来做匿名映射和文件映射。
<li>用户态的页表结构，存储位置在 mm_struct 中。
<li>在用户态访问没有映射的内存会引发缺页异常，分配物理页表、补齐页表。如果是匿名映射则分配物理内存；如果是 swap，则将 swap 文件读入；如果是文件映射，则将文件读入。

<img  src="https://static001.geekbang.org/resource/image/78/44/78d351d0105c8e5bf0e49c685a2c1a44.jpg"> 

<h4> 26  内核态内存映射   
<p>内存管理的体系串起来了。
<p>物理内存根据 NUMA 架构分节点。每个节点里面再分区域。每个区域里面再分页。
<p>物理页面通过伙伴系统进行分配。分配的物理页面要变成虚拟地址让上层可以访问，kswapd 可以根据物理页面的使用情况对页面进行换入换出。
<p>对于内存的分配需求，可能来自内核态，也可能来自用户态。
<p>对于内核态，kmalloc 在分配大内存的时候，以及 vmalloc 分配不连续物理页的时候，直接使用伙伴系统，分配后转换为虚拟地址，访问的时候需要通过内核页表进行映射。
<p>对于 kmem_cache 以及 kmalloc 分配小内存，则使用 slub 分配器，将伙伴系统分配出来的大块内存切成一小块一小块进行分配。
<p>kmem_cache 和 kmalloc 的部分不会被换出，因为用这两个函数分配的内存多用于保持内核关键的数据结构。内核态中 vmalloc 分配的部分会被换出，因而当访问的时候，发现不在，就会调用 do_page_fault。
<p>对于用户态的内存分配，或者直接调用 mmap 系统调用分配，或者调用 malloc。调用 malloc 的时候，如果分配小的内存，就用 sys_brk 系统调用；如果分配大的内存，还是用 sys_mmap 系统调用。正常情况下，用户态的内存都是可以换出的，因而一旦发现内存中不存在，就会调用 do_page_fault。
<img src="https://static001.geekbang.org/resource/image/27/9a/274e22b3f5196a4c68bb6813fb643f9a.png" >





### 5 文件系统
<h4> 27  文件系统

<p>通过下面这张图梳理一下。
<li>在文件系统上，需要维护文件的严格的格式，要通过 mkfs.ext4 命令来格式化为严格的格式。
<li>每一个硬盘上保存的文件都要有一个索引，来维护这个文件上的数据块都保存在哪里。
<li>文件通过文件夹组织起来，可以方便用户使用。
<li>为了能够更快读取文件，内存里会分配一块空间作为缓存，让一些数据块放在缓存里面。
<li>在内核中，要有一整套的数据结构来表示打开的文件。
<li>在用户态，每个打开的文件都有一个文件描述符，可以通过各种文件相关的系统调用，操作这个文件描述符。
<img src="https://static001.geekbang.org/resource/image/27/50/2788a6267f8361c9b6c338b06a1afc50.png" >

<h4> 28  硬盘文件系统
<p>复杂的硬盘上的文件系统，但是对于咱们平时的应用来讲，用的最多的是两个概念，一个是 inode，一个是数据块。
<p>来总结一下 inode 和数据块在文件系统上的关联关系。
<p>为了表示图中上半部分的那个简单的树形结构，在文件系统上的布局就像图的下半部分一样。无论是文件夹还是文件，都有一个 inode。inode 里面会指向数据块，对于文件夹的数据块，里面是一个表，是下一层的文件名和 inode 的对应关系，文件的数据块里面存放的才是真正的数据。
<img src="https://static001.geekbang.org/resource/image/f8/38/f81bf3e5a6cd060c3225a8ae1803a138.png">

<h4> 29 | 虚拟文件系统
<p>进程要想往文件系统里面读写数据，需要很多层的组件一起合作。具体是怎么合作的呢？我们一起来看一看。

<li>在应用层，进程在进行文件读写操作时，可通过系统调用如 sys_open、sys_read、sys_write 等。
<li>在内核，每个进程都需要为打开的文件，维护一定的数据结构。
<li>在内核，整个系统打开的文件，也需要维护一定的数据结构。
<li>Linux 可以支持多达数十种不同的文件系统。它们的实现各不相同，因此 Linux 内核向用户空间提供了虚拟文件系统这个统一的接口，来对文件系统进行操作。它提供了常见的文件系统对象模型，例如 inode、directory entry、mount 等，以及操作这些对象的方法，例如 inode operations、directory operations、file operations 等。
<li>然后就是对接的是真正的文件系统，例如我们上节讲的 ext4 文件系统。
<li>为了读写 ext4 文件系统，要通过块设备 I/O 层，也即 BIO 层。这是文件系统层和块设备驱动的接口。
<li>为了加快块设备的读写效率，我们还有一个缓存层。
<li>最下层是块设备驱动程序
<img src="https://static001.geekbang.org/resource/image/3c/73/3c506edf93b15341da3db658e9970773.jpg" >

<p>有关文件的数据结构层次多，而且很复杂，就得到了下面这张图，这张图在这个专栏最开始的时候，已经展示过一遍，到这里，你应该能明白它们之间的关系了。
<img  src="https://static001.geekbang.org/resource/image/80/b9/8070294bacd74e0ac5ccc5ac88be1bb9.png" >
<p>这张图十分重要，一定要掌握。因为我们后面的字符设备、块设备、管道、进程间通信、网络等等，全部都要用到这里面的知识。希望当你再次遇到它的时候，能够马上说出各个数据结构直接的关系。
<p>这里我带你简单做一个梳理，帮助你理解记忆它。
<p>对于每一个进程，打开的文件都有一个文件描述符，在 files_struct 里面会有文件描述符数组。每个一个文件描述符是这个数组的下标，里面的内容指向一个 file 结构，表示打开的文件。这个结构里面有这个文件对应的 inode，最重要的是这个文件对应的操作 file_operation。如果操作这个文件，就看这个 file_operation 里面的定义了。
<p>对于每一个打开的文件，都有一个 dentry 对应，虽然叫作 directory entry，但是不仅仅表示文件夹，也表示文件。它最重要的作用就是指向这个文件对应的 inode。
<p>如果说 file 结构是一个文件打开以后才创建的，dentry 是放在一个 dentry cache 里面的，文件关闭了，他依然存在，因而他可以更长期的维护内存中的文件的表示和硬盘上文件的表示之间的关系。
<p>inode 结构就表示硬盘上的 inode，包括块设备号等。
<p>几乎每一种结构都有自己对应的 operation 结构，里面都是一些方法，因而当后面遇到对于某种结构进行处理的时候，如果不容易找到相应的处理函数，就先找这个 operation 结构，就清楚了。




<h4> 30  文件缓存
<p> 读写调用过程。
<p>在系统调用层我们需要仔细学习 read 和 write。在 VFS 层调用的是 vfs_read 和 vfs_write 并且调用 file_operation。在 ext4 层调用的是 ext4_file_read_iter 和 ext4_file_write_iter。
<p>接下来就是分叉。你需要知道缓存 I/O 和直接 I/O。直接 I/O 读写的流程是一样的，调用 ext4_direct_IO，再往下就调用块设备层了。缓存 I/O 读写的流程不一样。对于读，从块设备读取到缓存中，然后从缓存中拷贝到用户态。对于写，从用户态拷贝到缓存，设置缓存页为脏，然后启动一个线程写入块设备。

<img src="https://static001.geekbang.org/resource/image/0c/65/0c49a870b9e6441381fec8d9bf3dee65.png">


### 输入输出系统

<h4>    31  输入与输出

<p>输入与输出设备的管理，内容比较多。输入输出设备就像管理代理商一样。因为代理商复杂多变，代理商管理也同样复杂多变，需要层层屏蔽差异化的部分，给上层提供标准化的部分，最终到用户态，给用户提供了基于文件系统的统一的接口。
<img src="https://static001.geekbang.org/resource/image/80/7f/80e152fe768e3cb4c84be62ad8d6d07f.jpg" >

<h4> 32  字符设备（上）
<p>字符设备的打开、写入和 ioctl 等最常见的操作。一个字符设备要能够工作，需要三部分配合。
<p>第一，有一个设备驱动程序的 ko 模块，里面有模块初始化函数、中断处理函数、设备操作函数。这里面封装了对于外部设备的操作。加载设备驱动程序模块的时候，模块初始化函数会被调用。在内核维护所有字符设备驱动的数据结构 cdev_map 里面注册，我们就可以很容易根据设备号，找到相应的设备驱动程序。
<p>第二，在 /dev 目录下有一个文件表示这个设备，这个文件在特殊的 devtmpfs 文件系统上，因而也有相应的 dentry 和 inode。这里的 inode 是一个特殊的 inode，里面有设备号。通过它，我们可以在 cdev_map 中找到设备驱动程序，里面还有针对字符设备文件的默认操作 def_chr_fops。
<p>第三，打开一个字符设备文件和打开一个普通的文件有类似的数据结构，有文件描述符、有 struct file、指向字符设备文件的 dentry 和 inode。字符设备文件的相关操作 file_operations 一开始指向 def_chr_fops，在调用 def_chr_fops 里面的 chrdev_open 函数的时候，修改为指向设备操作函数，从而读写一个字符设备文件就会直接变成读写外部设备了。

<img  src="https://static001.geekbang.org/resource/image/fb/cd/fba61fe95e0d2746235b1070eb4c18cd.jpeg" >

<h4> 33 | 字符设备（下）
<p>中断的整个处理过程。中断是从外部设备发起的，会形成外部中断。外部中断会到达中断控制器，中断控制器会发送中断向量 Interrupt Vector 给 CPU。
<p>对于每一个 CPU，都要求有一个 idt_table，里面存放了不同的中断向量的处理函数。中断向量表中已经填好了前 32 位，外加一位 32 位系统调用，其他的都是用于设备中断。
<p>硬件中断的处理函数是 do_IRQ 进行统一处理，在这里会让中断向量，通过 vector_irq 映射为 irq_desc。
<p>irq_desc 是一个用于描述用户注册的中断处理函数的结构，为了能够根据中断向量得到 irq_desc 结构，会把这些结构放在一个基数树里面，方便查找。
<p>irq_desc 里面有一个成员是 irqaction，指向设备驱动程序里面注册的中断处理函数。
<img src="https://static001.geekbang.org/resource/image/26/8f/26bde4fa2279f66098856c5b2b6d308f.png" >


<h4> 34 | 块设备（上）
<p>块设备比字符设备复杂多了，涉及三个文件系统，工作过程我用一张图总结了一下，下面带你总结一下。
<p>所有的块设备被一个 map 结构管理从 dev_t 到 gendisk 的映射；
<p>所有的 block_device 表示的设备或者分区都在 bdev 文件系统的 inode 列表中；
<p>mknod 创建出来的块设备文件在 devtemfs 文件系统里面，特殊 inode 里面有块设备号；
<p>mount 一个块设备上的文件系统，调用这个文件系统的 mount 接口；
<p>通过按照 /dev/xxx 在文件系统 devtmpfs 文件系统上搜索到特殊 inode，得到块设备号；
<p>根据特殊 inode 里面的 dev_t 在 bdev 文件系统里面找到 inode；
<p>根据 bdev 文件系统上的 inode 找到对应的 block_device，根据 dev_t 在 map 中找到 gendisk，将两者关联起来；
<p>找到 block_device 后打开设备，调用和 block_device 关联的 gendisk 里面的 block_device_operations 打开设备；
<p>创建被 mount 的文件系统的 super_block。

<img src="https://static001.geekbang.org/resource/image/62/20/6290b73283063f99d6eb728c26339620.png" >

<h4> 35 | 块设备（下）

<p>对于 ext4 文件系统，最后调用的是 ext4_file_write_iter，它将 I/O 的调用分成两种情况：
<p>第一是直接 I/O。最终我们调用的是 generic_file_direct_write，这里调用的是 mapping->a_ops->direct_IO，实际调用的是 ext4_direct_IO，往设备层写入数据。
<p>第二种是缓存 I/O。最终我们会将数据从应用拷贝到内存缓存中，但是这个时候，并不执行真正的 I/O 操作。它们只将整个页或其中部分标记为脏。写操作由一个 timer 触发，那个时候，才调用 wb_workfn 往硬盘写入页面。
<p>接下来的调用链为：wb_workfn->wb_do_writeback->wb_writeback->writeback_sb_inodes->__writeback_single_inode->do_writepages。在 do_writepages 中，我们要调用 mapping->a_ops->writepages，但实际调用的是 ext4_writepages，往设备层写入数据。

<p>块设备 I/O 请求送达到外部设备。
<p>对于块设备的 I/O 操作分为两种，一种是直接 I/O，另一种是缓存 I/O。无论是哪种 I/O，最终都会调用 submit_bio 提交块设备 I/O 请求。
<p>对于每一种块设备，都有一个 gendisk 表示这个设备，它有一个请求队列，这个队列是一系列的 request 对象。每个 request 对象里面包含多个 BIO 对象，指向 page cache。所谓的写入块设备，I/O 就是将 page cache 里面的数据写入硬盘。
<p>对于请求队列来讲，还有两个函数，一个函数叫 make_request_fn 函数，用于将请求放入队列。submit_bio 会调用 generic_make_request，然后调用这个函数。
<p>另一个函数往往在设备驱动程序里实现，我们叫 request_fn 函数，它用于从队列里面取出请求来，写入外部设备。

<img  src="https://static001.geekbang.org/resource/image/c9/3c/c9f6a08075ba4eae3314523fa258363c.png" >


### 第七部分 进程间通信

<h4> 36丨进程间通信
<li>类似瀑布开发模式的管道
<li>类似邮件模式的消息队列
<li>类似会议室联合开发的共享内存加信号量
<li>类似应急预案的信号
<li>当你自己使用的时候，可以根据不同的通信需要，选择不同的模式。

<p>管道，请你记住这是命令行中常用的模式，面试问到的话，不要忘了。
<li>消息队列其实很少使用，因为有太多的用户级别的消息队列，功能更强大。
<li>共享内存加信号量是常用的模式。这个需要牢记，常见到一些知名的以 C 语言开发的开源软件都会用到它。
<li>信号更加常用，机制也比较复杂


<h4> 37丨信号（上）
<p>通过 API 注册一个信号处理函数，整个过程如下图所示。
<li>在用户程序里面，有两个函数可以调用，一个是 signal，一个是 sigaction，推荐使用 sigaction。
<li>用户程序调用的是 Glibc 里面的函数，signal 调用的是 __sysv_signal，里面默认设置了一些参数，使得 signal 的功能受到了限制，sigaction 调用的是 __sigaction，参数用户可以任意设定。
<li>无论是 __sysv_signal 还是 __sigaction，调用的都是统一的一个系统调用 rt_sigaction。
<li>在内核中，rt_sigaction 调用的是 do_sigaction 设置信号处理函数。在每一个进程的 task_struct 里面，都有一个 sighand 指向 struct sighand_struct，里面是一个数组，下标是信号，里面的内容是信号处理函数。

<img src="https://static001.geekbang.org/resource/image/7c/28/7cb86c73b9e73893e6b0e0433d476928.png" >

<h4> 38 | 信号（下）
<p>信号的发送与处理是一个复杂的过程，这里来总结一下。

<li>假设我们有一个进程 A，main 函数里面调用系统调用进入内核。
<li>按照系统调用的原理，会将用户态栈的信息保存在 pt_regs 里面，也即记住原来用户态是运行到了 line A 的地方。
<li>在内核中执行系统调用读取数据。
<li>当发现没有什么数据可读取的时候，只好进入睡眠状态，并且调用 schedule 让出 CPU，这是进程调度第一定律。
<li>将进程状态设置为 TASK_INTERRUPTIBLE，可中断的睡眠状态，也即如果有信号来的话，是可以唤醒它的。
<li>其他的进程或者 shell 发送一个信号，有四个函数可以调用 kill，tkill，tgkill，rt_sigqueueinfo
<li>四个发送信号的函数，在内核中最终都是调用 do_send_sig_info
<li>do_send_sig_info 调用 send_signal 给进程 A 发送一个信号，其实就是找到进程 A 的 task_struct，或者加入信号集合，为不可靠信号，或者加入信号链表，为可靠信号
<li>do_send_sig_info 调用 signal_wake_up 唤醒进程 A。
<li>进程 A 重新进入运行状态 TASK_RUNNING，根据进程调度第一定律，一定会接着 schedule 运行。
<li>进程 A 被唤醒后，检查是否有信号到来，如果没有，重新循环到一开始，尝试再次读取数据，如果还是没有数据，再次进入 TASK_INTERRUPTIBLE，即可中断的睡眠状态。
<li>当发现有信号到来的时候，就返回当前正在执行的系统调用，并返回一个错误表示系统调用被中断了。
<li>系统调用返回的时候，会调用 exit_to_usermode_loop，这是一个处理信号的时机
<li>调用 do_signal 开始处理信号
<li>根据信号，得到信号处理函数 sa_handler，然后修改 pt_regs 中的用户态栈的信息，让 pt_regs 指向 sa_handler。同时修改用户态的栈，插入一个栈帧 sa_restorer，里面保存了原来的指向 line A 的 pt_regs，并且设置让 sa_handler 运行完毕后，跳到 sa_restorer 运行。
<li>返回用户态，由于 pt_regs 已经设置为 sa_handler，则返回用户态执行 sa_handler。
<li>sa_handler 执行完毕后，信号处理函数就执行完了，接着根据第 15 步对于用户态栈帧的修改，会跳到 sa_restorer 运行。
<li>sa_restorer 会调用系统调用 rt_sigreturn 再次进入内核。
<li>在内核中，rt_sigreturn 恢复原来的 pt_regs，重新指向 line A。
<li>从 rt_sigreturn 返回用户态，还是调用 exit_to_usermode_loop。
<li>这次因为 pt_regs 已经指向 line A 了，于是就到了进程 A 中，接着系统调用之后运行，当然这个系统调用返回的是它被中断了，没有执行完的错误。

<img src="https://static001.geekbang.org/resource/image/3d/fb/3dcb3366b11a3594b00805896b7731fb.png">

<h4> 39 | 管道
<p>无论是匿名管道，还是命名管道，在内核都是一个文件。只要是文件就要有一个 inode。这里我们又用到了特殊 inode、字符设备、块设备，其实都是这种特殊的 inode。
<p>在这种特殊的 inode 里面，file_operations 指向管道特殊的 pipefifo_fops，这个 inode 对应内存里面的缓存。
<p>当我们用文件的 open 函数打开这个管道设备文件的时候，会调用 pipefifo_fops 里面的方法创建 struct file 结构，他的 inode 指向特殊的 inode，也对应内存里面的缓存，file_operations 也指向管道特殊的 pipefifo_fops。
<p>写入一个 pipe 就是从 struct file 结构找到缓存写入，读取一个 pipe 就是从 struct file 结构找到缓存读出。

<img src="https://static001.geekbang.org/resource/image/48/97/486e2bc73abbe91d7083bb1f4f678097.png">


<h4>   40 | IPC（上）
<p>共享内存和信号量的配合机制，如下图所示：

<li>无论是共享内存还是信号量，创建与初始化都遵循同样流程，通过 ftok 得到 key，通过 xxxget 创建对象并生成 id；
<li>生产者和消费者都通过 shmat 将共享内存映射到各自的内存空间，在不同的进程里面映射的位置不同；
<li>为了访问共享内存，需要信号量进行保护，信号量需要通过 semctl 初始化为某个值；
<li>接下来生产者和消费者要通过 semop(-1) 来竞争信号量，如果生产者抢到信号量则写入，然后通过 semop(+1) 释放信号量，如果消费者抢到信号量则读出，然后通过 semop(+1) 释放信号量；
<li>共享内存使用完毕，可以通过 shmdt 来解除映射

<img src="https://static001.geekbang.org/resource/image/46/0b/469552bffe601d594c432d4fad97490b.png" >

<h4>  41丨IPC（中）

<p>共享内存的创建和映射过程。

<li>调用 shmget 创建共享内存。
<li>先通过 ipc_findkey 在基数树中查找 key 对应的共享内存对象 shmid_kernel 是否已经被创建过，如果已经被创建，就会被查询出来，例如 producer 创建过，在 consumer 中就会查询出来。
<li>如果共享内存没有被创建过，则调用 shm_ops 的 newseg 方法，创建一个共享内存对象 shmid_kernel。例如，在 producer 中就会新建。
<li>在 shmem 文件系统里面创建一个文件，共享内存对象 shmid_kernel 指向这个文件，这个文件用 struct file 表示，我们姑且称它为 file1。
<li>调用 shmat，将共享内存映射到虚拟地址空间。
<li>shm_obtain_object_check 先从基数树里面找到 shmid_kernel 对象。
<li>创建用于内存映射到文件的 file 和 shm_file_data，这里的 struct file 我们姑且称为 file2。
<li>关联内存区域 vm_area_struct 和用于内存映射到文件的 file，也即 file2，调用 file2 的 mmap 函数。
<li>file2 的 mmap 函数 shm_mmap，会调用 file1 的 mmap 函数 shmem_mmap，设置 shm_file_data 和 vm_area_struct 的 vm_ops。
<li>内存映射完毕之后，其实并没有真的分配物理内存，当访问内存的时候，会触发缺页异常 do_page_fault。
<li>vm_area_struct 的 vm_ops 的 shm_fault 会调用 shm_file_data 的 vm_ops 的 shmem_fault。
<li>在 page cache 中找一个空闲页，或者创建一个空闲页。

<img src="https://static001.geekbang.org/resource/image/20/51/20e8f4e69d47b7469f374bc9fbcf7251.png" >


<h4> 42 | IPC（下）

<p>信号量的机制也很复杂
<li>调用 semget 创建信号量集合。
<li>ipc_findkey 会在基数树中，根据 key 查找信号量集合 sem_array 对象。如果已经被创建，就会被查询出来。例如 producer 被创建过，在 consumer 中就会查询出来。
<li>如果信号量集合没有被创建过，则调用 sem_ops 的 newary 方法，创建一个信号量集合对象 sem_array。例如，在 producer 中就会新建。
<li>调用 semctl(SETALL) 初始化信号量。
<li>sem_obtain_object_check 先从基数树里面找到 sem_array 对象。
<li>根据用户指定的信号量数组，初始化信号量集合，也即初始化 sem_array 对象的 struct sem sems[] 成员。
<li>调用 semop 操作信号量。
<li>创建信号量操作结构 sem_queue，放入队列。
<li>创建 undo 结构，放入链表。

<img src="https://static001.geekbang.org/resource/image/60/7c/6028c83b0aa00e65916988911aa01b7c.png" >



### 核心原理篇：第八部分 网络系统

<h4> 43 预习 | Socket通信之网络协议基本原理
<img src="https://static001.geekbang.org/resource/image/92/0e/92f8e85f7b9a9f764c71081b56286e0e.png">

<p> 常用链路 TCP/UDP->IPv4->ARP 

<h4> 43 | Socket通信
<p>你需要记住 TCP 协议的 socket 调用的过程。我们接下来就按照这个顺序，依次回忆一下这些系统调用到内核都做了什么：

<li>服务端和客户端都调用 socket，得到文件描述符；
<li>服务端调用 listen，进行监听；
<li>服务端调用 accept，等待客户端连接；
<li>客户端调用 connect，连接服务端；
<li>服务端 accept 返回用于传输的 socket 的文件描述符；
<li>客户端调用 write 写入数据；
<li>服务端调用 read 读取数据。
<img src="https://static001.geekbang.org/resource/image/d3/5c/d34e667d1c3340deb8c82a2d44f2a65c.png" >

<h4>   44丨Socket内核数据结构
<p> 除了网络包的接收和发送，其他的系统调用我们都分析到了。可以看出来，它们有一个统一的数据结构和流程。具体如下图所示：

<img  src="https://static001.geekbang.org/resource/image/c0/d8/c028381cf45d65d3f148e57408d26bd8.png">

<p>首先，Socket 系统调用会有三级参数 family、type、protocal，通过这三级参数，分别在 net_proto_family 表中找到 type 链表，在 type 链表中找到 protocal 对应的操作。这个操作分为两层，对于 TCP 协议来讲，第一层是 inet_stream_ops 层，第二层是 tcp_prot 层。
<p>于是，接下来的系统调用规律就都一样了：

<li>bind 第一层调用 inet_stream_ops 的 inet_bind 函数，第二层调用 tcp_prot 的 inet_csk_get_port 函数；
<li>listen 第一层调用 inet_stream_ops 的 inet_listen 函数，第二层调用 tcp_prot 的 inet_csk_get_port 函数；
<li>accept 第一层调用 inet_stream_ops 的 inet_accept 函数，第二层调用 tcp_prot 的 inet_csk_accept 函数；
<li>connect 第一层调用 inet_stream_ops 的 inet_stream_connect 函数，第二层调用 tcp_prot 的 tcp_v4_connect 函数。

<h4> 45 | 发送网络包（上）

<img src="https://static001.geekbang.org/resource/image/dc/44/dc66535fa7e1a10fd6d728865f6c9344.png" >

<p>这个过程分成几个层次。

<li>VFS 层：write 系统调用找到 struct file，根据里面的 file_operations 的定义，调用 sock_write_iter 函数。sock_write_iter 函数调用 sock_sendmsg 函数。
<li>Socket 层：从 struct file 里面的 private_data 得到 struct socket，根据里面 ops 的定义，调用 inet_sendmsg 函数。
<li>Sock 层：从 struct socket 里面的 sk 得到 struct sock，根据里面 sk_prot 的定义，调用 tcp_sendmsg 函数。
<li>TCP 层：tcp_sendmsg 函数会调用 tcp_write_xmit 函数，tcp_write_xmit 函数会调用 tcp_transmit_skb，在这里实现了 TCP 层面向连接的逻辑。
<li>IP 层：扩展 struct sock，得到 struct inet_connection_sock，根据里面 icsk_af_ops 的定义，调用 ip_queue_xmit 函数

<p> tcp_write_xmit :
<li> 第一个概念是TSO（TCP Segmentation Offload）。如果发送的网络包非常大，就像上面说的一样，要进行分段
<li> 第二个概念是拥塞窗口的概念（cwnd，congestion window），也就是说为了避免拼命发包，把网络塞满了，定义一个窗口的概念，在这个窗口之内的才能发送，超过这个窗口的就不能发送，来控制发送的频率
<li> 第三个概念就是接收窗口rwnd 的概念（receive window），也叫滑动窗口。如果说拥塞窗口是为了怕把网络塞满，在出现丢包的时候减少发送速度，那么滑动窗口就是为了怕把接收方塞满，而控制发送速度。

<h4> 46 | 发送网络包（下）
<img  src="https://static001.geekbang.org/resource/image/79/6f/79cc42f3163d159a66e163c006d9f36f.png" >

<p>这个过程分成几个层次。

<li>VFS 层：write 系统调用找到 struct file，根据里面的 file_operations 的定义，调用 sock_write_iter 函数。sock_write_iter 函数调用 sock_sendmsg 函数。
<li>Socket 层：从 struct file 里面的 private_data 得到 struct socket，根据里面 ops 的定义，调用 inet_sendmsg 函数。
<li>Sock 层：从 struct socket 里面的 sk 得到 struct sock，根据里面 sk_prot 的定义，调用 tcp_sendmsg 函数。
<li>TCP 层：tcp_sendmsg 函数会调用 tcp_write_xmit 函数，tcp_write_xmit 函数会调用 tcp_transmit_skb，在这里实现了 TCP 层面向连接的逻辑。
<li>IP 层：扩展 struct sock，得到 struct inet_connection_sock，根据里面 icsk_af_ops 的定义，调用 ip_queue_xmit 函数。
<li>IP 层：ip_route_output_ports 函数里面会调用 fib_lookup 查找路由表。FIB 全称是 Forwarding Information Base，转发信息表，也就是路由表。
<li>在 IP 层里面要做的另一个事情是填写 IP 层的头。
<li>在 IP 层还要做的一件事情就是通过 iptables 规则。
<li>MAC 层：IP 层调用 ip_finish_output 进行 MAC 层。
<li>MAC 层需要 ARP 获得 MAC 地址，因而要调用 ___neigh_lookup_noref 查找属于同一个网段的邻居，他会调用 neigh_probe 发送 ARP。
<li>有了 MAC 地址，就可以调用 dev_queue_xmit 发送二层网络包了，它会调用 __dev_xmit_skb 会将请求放入队列。
<li>设备层：网络包的发送回触发一个软中断 NET_TX_SOFTIRQ 来处理队列中的数据。这个软中断的处理函数是 net_tx_action。
<li>在软中断处理函数中，会将网络包从队列上拿下来，调用网络设备的传输函数 ixgb_xmit_frame，将网络包发的设备的队列上去。



<h4> 47 | 接收网络包（上）
<p>接收网络包的上半部分，分以下几个层次。

<li>硬件网卡接收到网络包之后，通过 DMA 技术，将网络包放入 Ring Buffer。
<li>硬件网卡通过中断通知 CPU 新的网络包的到来。
<li>网卡驱动程序会注册中断处理函数 ixgb_intr。
<li>中断处理函数处理完需要暂时屏蔽中断的核心流程之后，通过软中断 NET_RX_SOFTIRQ 触发接下来的处理过程。
<li>NET_RX_SOFTIRQ 软中断处理函数 net_rx_action，net_rx_action 会调用 napi_poll，进而调用 ixgb_clean_rx_irq，从 Ring Buffer 中读取数据到内核 struct sk_buff。
<li>调用 netif_receive_skb 进入内核网络协议栈，进行一些关于 VLAN 的二层逻辑处理后，调用 ip_rcv 进入三层 IP 层。
<li>在 IP 层，会处理 iptables 规则，然后调用 ip_local_deliver，交给更上层 TCP 层。
<li>在 TCP 层调用 tcp_v4_rcv。

<img src="https://static001.geekbang.org/resource/image/a5/37/a51af8ada1135101e252271626669337.png" >

<h4> 48 | 接收网络包（下）

<p>接收网络包，我们来从头串一下，整个过程可以分成以下几个层次。

<li>硬件网卡接收到网络包之后，通过 DMA 技术，将网络包放入 Ring Buffer；
<li>硬件网卡通过中断通知 CPU 新的网络包的到来；
<li>网卡驱动程序会注册中断处理函数 ixgb_intr；
<li>中断处理函数处理完需要暂时屏蔽中断的核心流程之后，通过软中断 NET_RX_SOFTIRQ 触发接下来的处理过程；
<li>NET_RX_SOFTIRQ 软中断处理函数 net_rx_action，net_rx_action 会调用 napi_poll，进而调用 ixgb_clean_rx_irq，从 Ring Buffer 中读取数据到内核 struct sk_buff；
<li>调用 netif_receive_skb 进入内核网络协议栈，进行一些关于 VLAN 的二层逻辑处理后，调用 ip_rcv 进入三层 IP 层；
<li>在 IP 层，会处理 iptables 规则，然后调用 ip_local_deliver 交给更上层 TCP 层；
<li>在 TCP 层调用 tcp_v4_rcv，这里面有三个队列需要处理，如果当前的 Socket 不是正在被读；取，则放入 backlog 队列，如果正在被读取，不需要很实时的话，则放入 prequeue 队列，其他情况调用 tcp_v4_do_rcv；
<li>在 tcp_v4_do_rcv 中，如果是处于 TCP_ESTABLISHED 状态，调用 tcp_rcv_established，其他的状态，调用 tcp_rcv_state_process；
<li>在 tcp_rcv_established 中，调用 tcp_data_queue，如果序列号能够接的上，则放入 sk_receive_queue 队列；如果序列号接不上，则暂时放入 out_of_order_queue 队列，等序列号能够接上的时候，再放入 sk_receive_queue 队列。
<p>至此内核接收网络包的过程到此结束，接下来就是用户态读取网络包的过程，这个过程分成几个层次。

<li>VFS 层：read 系统调用找到 struct file，根据里面的 file_operations 的定义，调用 sock_read_iter 函数。sock_read_iter 函数调用 sock_recvmsg 函数。
<li>Socket 层：从 struct file 里面的 private_data 得到 struct socket，根据里面 ops 的定义，调用 inet_recvmsg 函数。
<li>Sock 层：从 struct socket 里面的 sk 得到 struct sock，根据里面 sk_prot 的定义，调用 tcp_recvmsg 函数。
<li>TCP 层：tcp_recvmsg 函数会依次读取 receive_queue 队列、prequeue 队列和 backlog 队列。

<img src="https://static001.geekbang.org/resource/image/20/52/20df32a842495d0f629ca5da53e47152.png" >

### 第九部分 虚拟化

<h4> 49丨虚拟机

<p>需要记住下面的这些知识点：

<li>虚拟化的本质是用 qemu 的软件模拟硬件，但是模拟方式比较慢，需要加速；
<li>虚拟化主要模拟 CPU、内存、网络、存储，分别有不同的加速办法；
<li>CPU 和内存主要使用硬件辅助虚拟化进行加速，需要配备特殊的硬件才能工作；
<li>网络和存储主要使用特殊的半虚拟化驱动加速，需要加载特殊的驱动程序。


<p> 50 | 计算虚拟化之CPU（上）

<p> 虚拟机对于设备的模拟是一件非常复杂的事情，需要用复杂的参数模拟各种各样的设备。为了能够适配这些设备，qemu 定义了自己的模块管理机制，只有了解了这种机制，后面看每一种设备的虚拟化的时候，才有一个整体的思路。
<p>     这里的 MachineClass 是我们遇到的第一个，我们需要掌握它里面各种定义之间的关系。

<img src="https://static001.geekbang.org/resource/image/07/30/078dc698ef1b3df93ee9569e55ea2f30.png">

<p>每个模块都会有一个定义 TypeInfo，会通过 type_init 变为全局的 TypeImpl。TypeInfo 以及生成的 TypeImpl 有以下成员：

<li>name 表示当前类型的名称
<li>parent 表示父类的名称
<li>class_init 用于将 TypeImpl 初始化为 MachineClass
<li>instance_init 用于将 MachineClass 初始化为 MachineState

<h4> 51丨计算虚拟化之CPU（下）

<p> 总结了一下。

<img src="https://static001.geekbang.org/resource/image/c4/67/c43639f7024848aa3e828bcfc10ca467.png" >

<li>首先，我们要定义 CPU 这种类型的 TypeInfo 和 TypeImpl、继承关系，并且声明它的类初始化函数。
<li>在 qemu 的 main 函数中调用 MachineClass 的 init 函数，这个函数既会初始化 CPU，也会初始化内存。
<li>CPU 初始化的时候，会调用 pc_new_cpu 创建一个虚拟 CPU，它会调用 CPU 这个类的初始化函数。
<li>每一个虚拟 CPU 会调用 qemu_thread_create 创建一个线程，线程的执行函数为 qemu_kvm_cpu_thread_fn。
<li>在虚拟 CPU 对应的线程执行函数中，我们先是调用 kvm_vm_ioctl(KVM_CREATE_VCPU)，在内核的 KVM 里面，创建一个结构 struct vcpu_vmx，表示这个虚拟 CPU。在这个结构里面，有一个 VMCS，用于保存当前虚拟机 CPU 的运行时的状态，用于状态切换。
<li>在虚拟 CPU 对应的线程执行函数中，我们接着调用 kvm_vcpu_ioctl(KVM_RUN)，在内核的 KVM 里面运行这个虚拟机 CPU。运行的方式是保存宿主机的寄存器，加载客户机的寄存器，然后调用 __ex(ASM_VMX_VMLAUNCH) 或者 __ex(ASM_VMX_VMRESUME)，进入客户机模式运行。一旦退出客户机模式，就会保存客户机寄存器，加载宿主机寄存器，进入宿主机模式运行，并且会记录退出虚拟机模式的原因。大部分的原因是等待 I/O，因而宿主机调用 kvm_handle_io 进行处理。


<h4>  52丨计算虚拟化之内存

<p>虚拟机的内存管理也是需要用户态的 qemu 和内核态的 KVM 共同完成。为了加速内存映射，需要借助硬件的 EPT 技术。
<p>在用户态 qemu 中，有一个结构 AddressSpace address_space_memory 来表示虚拟机的系统内存，这个内存可能包含多个内存区域 struct MemoryRegion，组成树形结构，指向由 mmap 分配的虚拟内存。
<p>在 AddressSpace 结构中，有一个 struct KVMMemoryListener，当有新的内存区域添加的时候，会被通知调用 kvm_region_add 来通知内核。
<p>在用户态 qemu 中，对于虚拟机有一个结构 struct KVMState 表示这个虚拟机，这个结构会指向一个数组的 struct KVMSlot 表示这个虚拟机的多个内存条，KVMSlot 中有一个 void *ram 指针指向 mmap 分配的那块虚拟内存。
<p>kvm_region_add 是通过 ioctl 来通知内核 KVM 的，会给内核 KVM 发送一个 KVM_SET_USER_MEMORY_REGION 消息，表示用户态 qemu 添加了一个内存区域，内核 KVM 也应该添加一个相应的内存区域。
<p>和用户态 qemu 对应的内核 KVM，对于虚拟机有一个结构 struct kvm 表示这个虚拟机，这个结构会指向一个数组的 struct kvm_memory_slot 表示这个虚拟机的多个内存条，kvm_memory_slot 中有起始页号，页面数目，表示这个虚拟机的物理内存空间。
<p>虚拟机的物理内存空间里面的页面当然不是一开始就映射到物理页面的，只有当虚拟机的内存被访问的时候，也即 mmap 分配的虚拟内存空间被访问的时候，先查看 EPT 页表，是否已经映射过，如果已经映射过，则经过四级页表映射，就能访问到物理页面。
<p>如果没有映射过，则虚拟机会通过 VM-Exit 指令回到宿主机模式，通过 handle_ept_violation 补充页表映射。先是通过 handle_mm_fault 为虚拟机的物理内存空间分配真正的物理页面，然后通过 __direct_map 添加 EPT 页表映射。

<img  src="https://static001.geekbang.org/resource/image/01/9b/0186c533b7ef706df880dfd775c2449b.jpg" >

<h4> 53 | 存储虚拟化（上）

<p>存储虚拟化的过程分为前端、后端和中间的队列。

<li>前端有前端的块设备驱动 Front-end driver，在客户机的内核里面，它符合普通设备驱动的格式，对外通过 VFS 暴露文件系统接口给客户机里面的应用。这一部分这一节我们没有讲，放在下一节解析。
<li>后端有后端的设备驱动 Back-end driver，在宿主机的 qemu 进程中，当收到客户机的写入请求的时候，调用文件系统的 write 函数，写入宿主机的 VFS 文件系统，最终写到物理硬盘设备上的 qcow2 文件。
<li>中间的队列用于前端和后端之间传输数据，在前端的设备驱动和后端的设备驱动，都有类似的数据结构 virt-queue 来管理这些队列，这一部分这一节我们也没有讲，也放到下一节解析。

<img  src="https://static001.geekbang.org/resource/image/1f/4b/1f0c3043a11d6ea1a802f7d0f3b0b34b.jpg" >


<h4> 54 | 存储虚拟化（下）

<p>存储虚拟化的场景下，整个写入的过程。

<li>在虚拟机里面，应用层调用 write 系统调用写入文件。
<li>write 系统调用进入虚拟机里面的内核，经过 VFS，通用块设备层，I/O 调度层，到达块设备驱动。
<li>虚拟机里面的块设备驱动是 virtio_blk，它和通用的块设备驱动一样，有一个 request queue，另外有一个函数 make_request_fn 会被设置为 blk_mq_make_request，这个函数用于将请求放入队列。
<li>虚拟机里面的块设备驱动是 virtio_blk 会注册一个中断处理函数 vp_interrupt。当 qemu 写入完成之后，它会通知虚拟机里面的块设备驱动。
<li>blk_mq_make_request 最终调用 virtqueue_add，将请求添加到传输队列 virtqueue 中，然后调用 virtqueue_notify 通知 qemu。
<li>在 qemu 中，本来虚拟机正处于 KVM_RUN 的状态，也即处于客户机状态。
<li>qemu 收到通知后，通过 VM exit 指令退出客户机状态，进入宿主机状态，根据退出原因，得知有 I/O 需要处理。
<li>qemu 调用 virtio_blk_handle_output，最终调用 virtio_blk_handle_vq。
<li>virtio_blk_handle_vq 里面有一个循环，在循环中，virtio_blk_get_request 函数从传输队列中拿出请求，然后调用 virtio_blk_handle_request 处理请求。
<li>virtio_blk_handle_request 会调用 blk_aio_pwritev，通过 BlockBackend 驱动写入 qcow2 文件。
<li>写入完毕之后，virtio_blk_req_complete 会调用 virtio_notify 通知虚拟机里面的驱动。数据写入完成，刚才注册的中断处理函数 vp_interrupt 会收到这个通知。

<img src="https://static001.geekbang.org/resource/image/79/0c/79ad143a3149ea36bc80219940d7d00c.jpg" >


<h4> 55 | 网络虚拟化

<p>网络虚拟化场景下网络包的发送过程总结一下。

<li>在虚拟机里面的用户态，应用程序通过 write 系统调用写入 socket。
<li>写入的内容经过 VFS 层，内核协议栈，到达虚拟机里面的内核的网络设备驱动，也即 virtio_net。
<li>virtio_net 网络设备有一个操作结构 struct net_device_ops，里面定义了发送一个网络包调用的函数为 start_xmit。
<li>在 virtio_net 的前端驱动和 qemu 中的后端驱动之间，有两个队列 virtqueue，一个用于发送，一个用于接收。然后，我们需要在 start_xmit 中调用 virtqueue_add，将网络包放入发送队列，然后调用 virtqueue_notify 通知 qemu。
<li>qemu 本来处于 KVM_RUN 的状态，收到通知后，通过 VM exit 指令退出客户机模式，进入宿主机模式。发送网络包的时候，virtio_net_handle_tx_bh 函数会被调用。
<li>接下来是一个 for 循环，我们需要在循环中调用 virtqueue_pop，从传输队列中获取要发送的数据，然后调用 qemu_sendv_packet_async 进行发送。
<li>qemu 会调用 writev 向字符设备文件写入，进入宿主机的内核。
<li>在宿主机内核中字符设备文件的 file_operations 里面的 write_iter 会被调用，也即会调用 tun_chr_write_iter。
<li>在 tun_chr_write_iter 函数中，tun_get_user 将要发送的网络包从 qemu 拷贝到宿主机内核里面来，然后调用 netif_rx_ni 开始调用宿主机内核协议栈进行处理。
<li>宿主机内核协议栈处理完毕之后，会发送给 tap 虚拟网卡，完成从虚拟机里面到宿主机的整个发送过程。

<img src="https://static001.geekbang.org/resource/image/e3/44/e329505cfcd367612f8ae47054ec8e44.jpg" >



### 第十部分 容器化

<h4> 56 | 容器

<p>无论是容器，还是虚拟机，都依赖于内核中的技术，虚拟机依赖的是 KVM，容器依赖的是 namespace 和 cgroup 对进程进行隔离。
<p>为了运行 Docker，有一个 daemon 进程 Docker Daemon 用于接收命令行。
<p>为了描述 Docker 里面运行的环境和应用，有一个 Dockerfile，通过 build 命令称为容器镜像。容器镜像可以上传到镜像仓库，也可以通过 pull 命令从镜像仓库中下载现成的容器镜像。
<p>通过 Docker run 命令将容器镜像运行为容器，通过 namespace 和 cgroup 进行隔离，容器里面不包含内核，是共享宿主机的内核的。对比虚拟机，虚拟机在 qemu 进程里面是有客户机内核的，应用运行在客户机的用户态。

<img  src="https://static001.geekbang.org/resource/image/5a/c5/5a499cb50a1b214a39ddf19cbb63dcc5.jpg" >


<h4> 57 | Namespace技术

<p> namespace 相关的技术，有六种类型，分别是 UTS、User、Mount、Pid、Network 和 IPC。
<p>还有两个常用的命令 nsenter 和 unshare，主要用于操作 Namespace，有三个常用的函数 clone、setns 和 unshare。
<p>在内核里面，对于任何一个进程 task_struct 来讲，里面都会有一个成员 struct nsproxy，用于保存 namespace 相关信息，里面有 struct uts_namespace、struct ipc_namespace、struct mnt_namespace、struct pid_namespace、struct net *net_ns 和 struct cgroup_namespace *cgroup_ns。
<p>创建 namespace 的时候，我们在内核中会调用 copy_namespaces，调用顺序依次是 copy_mnt_ns、copy_utsname、copy_ipcs、copy_pid_ns、copy_cgroup_ns 和 copy_net_ns，来复制 namespace。

<img src="https://static001.geekbang.org/resource/image/56/d7/56bb9502b58628ff3d1bee83b6f53cd7.png">

<h4> 58 | CGroup技术

<p> 内核中 cgroup 的工作机制

<img src="https://static001.geekbang.org/resource/image/c9/c4/c9cc56d20e6a4bac0f9657e6380a96c4.png" >

<p>第一步，系统初始化的时候，初始化 cgroup 的各个子系统的操作函数，分配各个子系统的数据结构。
<p>第二步，mount cgroup 文件系统，创建文件系统的树形结构，以及操作函数。
<p>第三步，写入 cgroup 文件，设置 cpu 或者 memory 的相关参数，这个时候文件系统的操作函数会调用到 cgroup 子系统的操作函数，从而将参数设置到 cgroup 子系统的数据结构中。
<p>第四步，写入 tasks 文件，将进程交给某个 cgroup 进行管理，因为 tasks 文件也是一个 cgroup 文件，统一会调用文件系统的操作函数进而调用 cgroup 子系统的操作函数，将 cgroup 子系统的数据结构和进程关联起来。
<p>第五步，对于 cpu 来讲，会修改 scheduled entity，放入相应的队列里面去，从而下次调度的时候就起作用了。对于内存的 cgroup 设定，只有在申请内存的时候才起作用。

<h4> 59 | 数据中心操作系统

<p>数据中心操作系统的功能。
   
<img  src="https://static001.geekbang.org/resource/image/1a/e5/1a8450f1fcda83b75c9ba301ebf9fbe5.jpg">


### 12-实战串讲篇





