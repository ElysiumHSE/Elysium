package hse.elysium.entities;

import jakarta.persistence.*;

@Entity
public class Token {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "token_id")
    private int tokenId;
    @Basic
    @Column(name = "token_value")
    private String tokenValue;
    @Basic
    @Column(name = "revoked")
    private boolean revoked;
    @Basic
    @Column(name = "expired")
    private boolean expired;
    @Basic
    @Column(name = "user_id")
    private int userId;

    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public boolean getRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public boolean getExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token = (Token) o;

        if (tokenId != token.tokenId) return false;
        if (revoked != token.revoked) return false;
        if (expired != token.expired) return false;
        if (userId != token.userId) return false;
        if (tokenValue != null ? !tokenValue.equals(token.tokenValue) : token.tokenValue != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tokenId;
        result = 31 * result + (tokenValue != null ? tokenValue.hashCode() : 0);
        if (revoked) {
            result = 31 * result + 1;
        } else {
            result = 31 * result;
        }
        if (expired) {
            result = 31 * result + 1;
        } else {
            result = 31 * result;
        }
        result = 31 * result + userId;
        return result;
    }
}
