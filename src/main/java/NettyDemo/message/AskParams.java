package NettyDemo.message;

import java.io.Serializable;

public class AskParams implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1328907014927967154L;
	private String auth;
 
    public String getAuth() {
        return auth;
    }
 
    public void setAuth(String auth) {
        this.auth = auth;
    }
}
