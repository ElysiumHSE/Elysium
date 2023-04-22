package hse.elysium.databaseInteractor;

import hse.elysium.entities.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TokenServiceTests {
    @Test
    public void allFunctionsTest() {
        TokenService ts = new TokenService();

        Assertions.assertDoesNotThrow(() -> {
            ts.addNewTokenWithTokenValueUserId("remove_kebab", 1488);
        });

        Assertions.assertDoesNotThrow(() -> {
            ts.getTokenWithTokenValue("remove_kebab");
        });
        Token token = ts.getTokenWithTokenValue("remove_kebab");

        Assertions.assertEquals("remove_kebab", token.getTokenValue());
        Assertions.assertFalse(token.getRevoked());
        Assertions.assertFalse(token.getExpired());
        Assertions.assertEquals(1488, token.getUserId());

        Assertions.assertTrue(ts.setExpiredWithTokenValue("remove_kebab"));

        Assertions.assertDoesNotThrow(() -> {
            ts.addNewTokenWithTokenValueUserId("remove_kebab2", 1488);
        });

        Assertions.assertDoesNotThrow(() -> {
            ts.setRevokedForUserChangedPasswordWithUserId(1488);
        });

        token = ts.getTokenWithTokenValue("remove_kebab");

        Assertions.assertEquals("remove_kebab", token.getTokenValue());
        Assertions.assertTrue(token.getRevoked());
        Assertions.assertTrue(token.getExpired());
        Assertions.assertEquals(1488, token.getUserId());

        Token token2 = ts.getTokenWithTokenValue("remove_kebab2");

        Assertions.assertEquals("remove_kebab2", token2.getTokenValue());
        Assertions.assertTrue(token2.getRevoked());
        Assertions.assertFalse(token2.getExpired());
        Assertions.assertEquals(1488, token2.getUserId());

        Assertions.assertDoesNotThrow(ts::deleteRecordsWithRevokedOrExpired);

        Assertions.assertThrows(jakarta.persistence.NoResultException.class,
                () -> ts.getTokenWithTokenValue("remove_kebab"));

        Assertions.assertThrows(jakarta.persistence.NoResultException.class,
                () -> ts.getTokenWithTokenValue("remove_kebab2"));

        ts.closeHandler();
    }
}
