package NettyDemo.message;

public class ReplyServerBody extends ReplyBody {

	/**
	 * 
	 */
	private static final long serialVersionUID = 929310828751146503L;
	private String serverInfo;
    public ReplyServerBody(String serverInfo) {
        this.serverInfo = serverInfo;
    }
    public String getServerInfo() {
        return serverInfo;
    }
    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
    }
}
