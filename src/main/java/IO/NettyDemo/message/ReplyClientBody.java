package IO.NettyDemo.message;

public class ReplyClientBody extends ReplyBody {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5098582689214899515L;
	private String clientInfo;
	 
    public ReplyClientBody(String clientInfo) {
        this.clientInfo = clientInfo;
    }
 
    public String getClientInfo() {
        return clientInfo;
    }
 
    public void setClientInfo(String clientInfo) {
        this.clientInfo = clientInfo;
    }
}
