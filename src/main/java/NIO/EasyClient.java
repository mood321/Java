package NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class EasyClient {
    public static void main(String[] args) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.connect(new InetSocketAddress("127.0.0.1", 8000));

        ByteBuffer writeBuf = ByteBuffer.allocate(1024);

        writeBuf.put("hello".getBytes());
        writeBuf.flip();

        channel.write(writeBuf);

        channel.close();

    }
}
