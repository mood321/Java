/**
 * 
 */
/**
 * @author Administrator
 *
 */
package IO.AIO;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AIO, 也叫 NIO2.0 是一种异步非阻塞的通信方式 AIO 引入了异步通道的概念
 * AsynchronousServerSocketChannel和AsynchronousSocketChannel
 * 其read和write方法返回值类型是Future对象。
 */
public class ITDragonAIOServer {

	private ExecutorService executorService; // 线程池
	private AsynchronousChannelGroup threadGroup; // 通道组
	public AsynchronousServerSocketChannel asynServerSocketChannel; // 服务器通道

	public void start(Integer port) {
		try {
			// 1.创建一个缓存池
			executorService = Executors.newCachedThreadPool();
			// 2.创建通道组
			threadGroup = AsynchronousChannelGroup.withCachedThreadPool(executorService, 1);
			// 3.创建服务器通道
			asynServerSocketChannel = AsynchronousServerSocketChannel.open(threadGroup);
			// 4.进行绑定
			asynServerSocketChannel.bind(new InetSocketAddress(port));
			System.out.println("server start , port : " + port);
			// 5.等待客户端请求
			asynServerSocketChannel.accept(this, new ITDragonAIOServerHandler());
			// 一直阻塞 不让服务器停止，真实环境是在tomcat下运行，所以不需要这行代码
			Thread.sleep(Integer.MAX_VALUE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ITDragonAIOServer server = new ITDragonAIOServer();
		server.start(8888);
	}
}