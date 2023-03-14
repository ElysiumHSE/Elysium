package hse.elysium.databaseInteractor;

import hse.elysium.entities.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TokenServiceTests {
    @Test
    public void allFunctionsTest() {
        TokenService ts = new TokenService();

        int token_id = ts.addNewTokenWithTokenValueUserId("remove_kebab", 1488);
        Assertions.assertNotEquals(-1, token_id);

        Token token = ts.getTokenWithTokenValue("remove_kebab");
        Assertions.assertNotNull(token);

        Assertions.assertEquals("remove_kebab", token.getTokenValue());
        Assertions.assertFalse(token.getRevoked());
        Assertions.assertFalse(token.getExpired());
        Assertions.assertEquals(1488, token.getUserId());

        Assertions.assertEquals(1, ts.setExpiredWithTokenValue("remove_kebab"));

        int token_id2 = ts.addNewTokenWithTokenValueUserId("remove_kebab2", 1488);
        Assertions.assertNotEquals(-1, token_id2);

        Assertions.assertEquals(1, ts.setRevokedForUserChangedPasswordWithUserId(1488));

        token = ts.getTokenWithTokenValue("remove_kebab");
        Assertions.assertNotNull(token);

        Assertions.assertEquals("remove_kebab", token.getTokenValue());
        Assertions.assertTrue(token.getRevoked());
        Assertions.assertTrue(token.getExpired());
        Assertions.assertEquals(1488, token.getUserId());

        Token token2 = ts.getTokenWithTokenValue("remove_kebab2");
        Assertions.assertNotNull(token2);

        Assertions.assertEquals("remove_kebab2", token2.getTokenValue());
        Assertions.assertTrue(token2.getRevoked());
        Assertions.assertFalse(token2.getExpired());
        Assertions.assertEquals(1488, token2.getUserId());

        Assertions.assertEquals(1, ts.deleteRecordsWithRevokedOrExpired());
        Assertions.assertEquals(0, ts.deleteRecordsWithRevokedOrExpired());

        token = ts.getTokenWithTokenValue("remove_kebab");
        Assertions.assertNull(token);

        token2 = ts.getTokenWithTokenValue("remove_kebab2");
        Assertions.assertNull(token2);

        ts.closeHandler();
    }
}
