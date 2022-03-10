package disruptor;


import com.lmax.disruptor.EventHandler;

/**
 * <B>主类名称：</B>OrderEventHandler<BR>
 * <B>概要说明：</B>事件<BR>
 *
 */
public class OrderEventHandler implements EventHandler<OrderEvent> {

	@Override
	public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
		Thread.sleep(1000);
		System.err.println("消费者消费：" + event.getValue());
	}

}
