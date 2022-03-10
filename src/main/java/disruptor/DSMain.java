package disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadFactory;

/**
 * <B>主类名称：</B>Main<BR>
 * <B>概要说明：</B>第一个ds小程序<BR>
 * @author
 */
public class DSMain {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		int ringBufferSize = 1024 * 1024;
		OrderEventFactory orderEventFactory = new OrderEventFactory();
		Disruptor<OrderEvent> disruptor = new Disruptor<>(orderEventFactory,
				ringBufferSize, 
				new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						Thread thread = new Thread(r);
						thread.setName("ds-thread");
						return thread;
					}
				}, 
				ProducerType.SINGLE, new BlockingWaitStrategy());
		
		disruptor.handleEventsWith(new OrderEventHandler());
		
		disruptor.start();
		
		RingBuffer<OrderEvent> ringBuffer = disruptor.getRingBuffer();
		
		OrderEventProducer producer = new OrderEventProducer(ringBuffer);
		
		ByteBuffer bb = ByteBuffer.allocate(8);
		
		for(long i = 0 ; i < 100; i++) {
			bb.putLong(0, i);
			producer.putData(bb);
		}
		
		disruptor.shutdown();
		
	}
	
	
	
}
