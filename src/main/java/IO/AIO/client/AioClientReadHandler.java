package IO.AIO.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * 类说明：网络读的处理器
 * CompletionHandler<Integer, ByteBuffer>中
 * Integer：本次网络读操作实际读取的字节数，
 * ByteBuffer：读操作的附件，存储了读操作读到的数据 *
 */
public class AioClientReadHandler
        implements CompletionHandler<Integer, ByteBuffer> {
    private AsynchronousSocketChannel clientChannel;
    private CountDownLatch latch;

    public AioClientReadHandler(AsynchronousSocketChannel clientChannel,
                                CountDownLatch latch) {
        this.clientChannel = clientChannel;
        this.latch = latch;
    }

    @Override
    public void completed(Integer result,ByteBuffer buffer) {
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        String msg;
        try {
            msg = new String(bytes,"UTF-8");
            System.out.println("accept message:"+msg);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void failed(Throwable exc,ByteBuffer attachment) {
        System.err.println("数据读取失败...");
        try {
            clientChannel.close();
            latch.countDown();
        } catch (IOException e) {
        }
    }

}
