package disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * <B>主类名称：</B>OrderEventFactory<BR>
 * <B>概要说明：</B>ds的事件工厂类<BR>
 *
 */
public class OrderEventFactory implements EventFactory<OrderEvent>{

	@Override
	public OrderEvent newInstance() {
		return new OrderEvent();
	}

}
