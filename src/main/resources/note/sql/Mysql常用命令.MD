<p>show tables或show tables from database_name; -- 显示当前数据库中所有表的名称。
<p>show databases; -- 显示mysql中所有数据库的名称。 
<p>show columns from table_name from database_name; 或show columns from database_name.table_name; -- 显示表中列名称。
<p>show grants for user_name; -- 显示一个用户的权限，显示结果类似于grant 命令。
<p>show index from table_name; -- 显示表的索引。
<p>show status; -- 显示一些系统特定资源的信息，例如，正在运行的线程数量。
<p>show processlist; -- 显示系统中正在运行的所有进程，也就是当前正在执行的查询。大多数用户可以查看他们自己的进程，但是如果他们拥有process权限，就可以查看所有人的进程，包括密码。
<p>show table status; -- 显示当前使用或者指定的database中的每个表的信息。信息包括表类型和表的最新更新时间。
<p>show privileges; -- 显示服务器所支持的不同权限。
<p>show create database database_name; -- 显示create database 语句是否能够创建指定的数据库。
<p>show create table table_name; -- 显示create database 语句是否能够创建指定的数据库。
<p>show engines; -- 显示安装以后可用的存储引擎和默认引擎。
<p>show innodb status; -- 显示innoDB存储引擎的状态。
<p>show logs; -- 显示BDB存储引擎的日志。
<p>show warnings; -- 显示最后一个执行的语句所产生的错误、警告和通知。
<p>show errors; -- 只显示最后一个执行语句所产生的错误。
<p>show variables; -- 显示系统变量的名称和值。 
<p>
<p>show  variables like "slow_query_log"\G   # 查看是否开启慢查询日志
<p>set   slow_query_log = ON|OFF;  # 开启|关闭慢查询日志
<p>
<p>show variables like "log_output"\G   # 查看慢查询日志记录到文件还是表中 
<p>set log_output=TABLE|FILE;  # 设置慢查询日志输出到table or files中 
<p>
<p>show variables like "slow_query_log_file"\G  # 查看慢查询日志文件路径
<p>
<p>show  variables like "long_query_time"\G  # 查看慢查询阀值
<p>set long_query_time=10;  # 设置慢查询阀值为10s
<p>
<p>show variables like "log_queries_not_using_indexes"\G # 查看是否开启，没有使用索引也记录到慢查询日志中
<p>set log_queries_not_using_indexes=ON|OFF;  # 开启or关闭
<p>
<p>show variables like "log_throttle_queries_not_using_indexes"\G # 每分钟 允许【因为没有使用索引】而记录到慢查询日志中的sql语句数
<p># log_throttle_queries_not_using_indexes = 0; 表示不限制数量，可能会频繁记录，要小心
<p>show status like ‘Threads%’; 查看mysql数据库链接数
<p>
<p>show variables like ‘%max_connections%’;查看当前设置的数据库连接池最大连接数
<p>
<p>select @@tx_isolation;查看事务隔离级别
<p>
<p>set session transaction level read committed;设置事务的隔离级别

<p>show variables like "log_bin" 是否开启bin_log日志
<p>set sql_log_bin=on|OFF     停止对bin-log日志的写入 多用于主从临时修改 

<p> binlog 配置
<pre><code>
[mysqld]
log-bin=mysql-bin
server-id=1</code></pre>