package ca.chirp.messenger;

public class UserModel {

    private String name;
    private String displayName;
    private String email;

    public UserModel() {}

    public UserModel(String name, String displayName, String email) {
        this.name = name;
        this.displayName = displayName;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
