package disruptor;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * <B>主类名称：</B>OrderEventProducer<BR>
 * <B>概要说明：</B>生产者对象<BR>
 *
 */
public class OrderEventProducer {

	private RingBuffer<OrderEvent> ringBuffer;
	
	public OrderEventProducer(RingBuffer<OrderEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	public void putData(ByteBuffer bb) {
		//	先获取下一个可用的序号
		long sequence = ringBuffer.next();
		try {
			//	通过可用的序号获取对应下标的数据OrderEvent
			OrderEvent event = ringBuffer.get(sequence);
			//	重新设置内容
			event.setValue(bb.getLong(0));			
		} finally {
			//	publish
			ringBuffer.publish(sequence);
		}
	}
	
}
