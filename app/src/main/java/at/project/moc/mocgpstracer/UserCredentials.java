package at.project.moc.mocgpstracer;

/**
 * Created by KASSIOPEIA on 22.05.2015.
 */
public class UserCredentials
{

    //VARIABLES
    private String username = "";
    private String password = "";

    //CONSTRUCTOR
    public UserCredentials(String username, String password)
    {
        setUsername(username);
        setPassword(password);
    }

    //GETTERS AND SETTERS
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
}
