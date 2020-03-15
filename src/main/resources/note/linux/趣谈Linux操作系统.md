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



