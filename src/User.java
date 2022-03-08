
import javax.swing.ImageIcon;

public abstract class User{
    protected String nameUser;
    protected ImageIcon imageUser;

    public User() {
    }
    
    
    public User(String nameUser, ImageIcon imgUser) {
        this.nameUser = nameUser;
        this.imageUser = imgUser;
    }
    
    public abstract String typeConnect();
}