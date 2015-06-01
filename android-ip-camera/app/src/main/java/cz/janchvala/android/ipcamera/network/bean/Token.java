package cz.janchvala.android.ipcamera.network.bean;

/**
 * The Token is simple object wrapping token instance which is send to user on upon successful registration.
 * <p/>
 * Created by xchval01 on 13.03.2015.
 */
public class Token {

    private String token;

    public Token() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
