package NettyDemo.message;

public class PingMsg extends BaseMsg{

	public PingMsg() {
        super();
        setType(MsgType.PING);
    }
}
