package NettyDemo.message;

public class LoginReplyMsg extends BaseMsg{

    /**
	 * 
	 */
	private static final long serialVersionUID = -2793990273965982564L;

	private String loginToken;
	
	private boolean succese = false;;
	
	public LoginReplyMsg() {
        super();
        setType(MsgType.LOGIN_REPLY);
    }
	public String getLoginToken() {
		return loginToken;
	}
	public void setLoginToken(String loginToken) {
		this.loginToken = loginToken;
	}
	public boolean isSuccese() {
		return succese;
	}
	public void setSuccese(boolean succese) {
		this.succese = succese;
	}
 
	
    
}
