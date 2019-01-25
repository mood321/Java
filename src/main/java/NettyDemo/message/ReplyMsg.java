package NettyDemo.message;

public class ReplyMsg extends BaseMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3365380802569967318L;

	public ReplyMsg() {
        super();
        setType(MsgType.REPLY);
    }
    private ReplyBody body;
 
    public ReplyBody getBody() {
        return body;
    }
 
    public void setBody(ReplyBody body) {
        this.body = body;
    }
}
