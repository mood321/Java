package 三种io对比.BIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ITDragonBIOServerHandler implements Runnable {

	private Socket socket;

	public ITDragonBIOServerHandler(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		BufferedReader reader = null;
		PrintWriter writer = null;
		try {
			reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			writer = new PrintWriter(this.socket.getOutputStream(), true);
			String body = null;
			while (true) {
				body = reader.readLine(); // 若客户端用的是 writer.print()
											// 传值，那readerLine() 是不能获取值，细节
				if (null == body) {
					break;
				}
				System.out.println("server服务端接收参数 : " + body);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != writer) {
				writer.close();
			}
			try {
				if (null != reader) {
					reader.close();
				}
				if (null != this.socket) {
					this.socket.close();
					this.socket = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}