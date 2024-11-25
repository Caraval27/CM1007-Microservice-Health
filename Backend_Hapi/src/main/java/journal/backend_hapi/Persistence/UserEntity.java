package journal.backend_hapi.Persistence;

import jakarta.persistence.*;
import journal.backend_hapi.Core.Authority;

@Entity
@Table(name = "T_User")
public class UserEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Enumerated(EnumType.STRING)
    @Column(name = "authority", nullable = false)
    private Authority authority;
    @Column(name = "password", nullable = false)
    private String password;

    public UserEntity(String id, String fullName, Authority authority, String password) {
        this.id = id;
        this.fullName = fullName;
        this.authority = authority;
        this.password = password;
    }

    public UserEntity() {

    }

    public String getId() {
        return id;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }
}