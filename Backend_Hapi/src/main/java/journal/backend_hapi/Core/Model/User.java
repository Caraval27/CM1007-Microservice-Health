package journal.backend_hapi.Core.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import journal.backend_hapi.Core.Authority;
import journal.backend_hapi.Persistence.UserEntity;

public class User {
    private String id;
    private String fullName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Authority authority;
    private String password;

    public User(UserEntity userEntity) {
        id = userEntity.getId();
        fullName = userEntity.getFullName();
        authority = userEntity.getAuthority();
        password = userEntity.getPassword();
    }

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

    public UserEntity getEntity() {
        return new UserEntity(id, fullName, authority, password);
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