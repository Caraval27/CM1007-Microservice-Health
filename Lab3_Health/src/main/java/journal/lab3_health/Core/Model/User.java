package journal.lab3_health.Core.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import journal.lab3_health.Core.Authority;

public class User {
    private String id;
    private String fullName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Authority authority;
    private String password;

    public User(String id, String fullName, Authority authority) {
        this.id = id;
        this.fullName = fullName;
        this.authority = authority;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", authority=" + authority +
                ", password='" + password + '\'' +
                '}';
    }
}