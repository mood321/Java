package disruptor;

/**
 * <B>主类名称：</B>OrderEvent<BR>
 * <B>概要说明：</B>这就是DS的RingBuffer处理的数据模型<BR>
 *
 */
public class OrderEvent {

	private long value;

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
	
}
