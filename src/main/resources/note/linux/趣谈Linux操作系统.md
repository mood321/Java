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


<h3> 内核初始化

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


<h3> 系统调用

<img  src="https://static001.geekbang.org/resource/image/86/a5/868db3f559ad08659ddc74db07a9a0a5.jpg" >

<p> glibc 对系统调用的封装
