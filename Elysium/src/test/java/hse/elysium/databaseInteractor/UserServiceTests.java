package hse.elysium.databaseInteractor;

import hse.elysium.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class UserServiceTests {
    @Test
    public void addGetDeleteTest() {
        UserService us = new UserService();

        Assertions.assertDoesNotThrow(() -> {
            us.addNewUserWithLoginPassword("михаил", "антипов");
        });

        Assertions.assertThrows(jakarta.persistence.PersistenceException.class,
            () -> us.addNewUserWithLoginPassword("михаил", "антипов"));

        final int[] user_id = new int[1];
        Assertions.assertDoesNotThrow(() -> {
            user_id[0] = us.getUserIdWithLogin("михаил");
        });

        User user = us.getUserWithUserId(user_id[0]);
        Assertions.assertNotNull(user);
        Assertions.assertEquals("михаил", user.getLogin());
        Assertions.assertEquals("антипов", user.getPassword());
        Assertions.assertNull(user.getFavourites());

        Assertions.assertEquals("михаил", us.getUserLoginWithUserId(user_id[0]));
        Assertions.assertEquals("антипов", us.getUserPasswordWithUserId(user_id[0]));

        // Randomly chosen big value for sure not reached by database
        Assertions.assertNull(us.getUserWithUserId(678249809));
        Assertions.assertThrows(jakarta.persistence.PersistenceException.class,
            () -> us.findTrackInUserFavourites(678249809, 1));

        user = us.deleteUserWithUserId(user_id[0]);
        Assertions.assertNotNull(user);
        Assertions.assertEquals("михаил", user.getLogin());
        Assertions.assertEquals("антипов", user.getPassword());
        Assertions.assertNull(user.getFavourites());

        user = us.deleteUserWithUserId(user_id[0]);
        Assertions.assertNull(user);
    }

    @Test
    public void addDeleteTracksFromFavouritesTest() {
        UserService us = new UserService();

        us.addNewUserWithLoginPassword("михаил", "антипов");

        final int[] user_id = new int[1];
        Assertions.assertDoesNotThrow(() -> {
            user_id[0] = us.getUserIdWithLogin("михаил");
        });

        User user = us.getUserWithUserId(user_id[0]);
        Assertions.assertNotNull(user);

        Assertions.assertTrue(us.addTrackToFavouritesWithUserId(user_id[0], 1));
        Assertions.assertFalse(us.addTrackToFavouritesWithUserId(user_id[0], 1));
        Assertions.assertTrue(us.addTrackToFavouritesWithUserId(user_id[0], 2));
        Assertions.assertThrows(jakarta.persistence.PersistenceException.class,
            () -> us.addTrackToFavouritesWithUserId(678249809, 3));

        List<Integer> array = us.getUserFavouritesWithUserId(user_id[0]);
        Assertions.assertNotNull(array);
        Assertions.assertEquals(2, array.size());
        Assertions.assertEquals(1, (int)array.get(0));
        Assertions.assertEquals(2, (int)array.get(1));
        Assertions.assertFalse(array.contains(3));

        Assertions.assertTrue(us.deleteTrackFromFavouritesWithUserId(user_id[0], 2));
        Assertions.assertFalse(us.deleteTrackFromFavouritesWithUserId(user_id[0], 3));

        array = us.getUserFavouritesWithUserId(user_id[0]);
        Assertions.assertNotNull(array);
        Assertions.assertEquals(1, array.size());
        Assertions.assertEquals(1, (int)array.get(0));
        Assertions.assertFalse(array.contains(2));
        Assertions.assertFalse(array.contains(3));

        user = us.deleteUserWithUserId(user_id[0]);
        Assertions.assertNotNull(user);

        us.closeHandler();
    }

    @Test
    public void simpleAddDeleteSameTest() {
        UserService us = new UserService();

        Assertions.assertDoesNotThrow(() -> us.addNewUserWithLoginPassword("михаил", "антипов"));

        final int[] user_id = new int[1];
        Assertions.assertDoesNotThrow(() -> {
            user_id[0] = us.getUserIdWithLogin("михаил");
        });

        User user = us.deleteUserWithUserId(user_id[0]);
        Assertions.assertNotNull(user);

        Assertions.assertDoesNotThrow(() -> us.addNewUserWithLoginPassword("михаил", "антипов"));

        Assertions.assertDoesNotThrow(() -> {
            user_id[0] = us.getUserIdWithLogin("михаил");
        });

        user = us.deleteUserWithUserId(user_id[0]);
        Assertions.assertNotNull(user);

        us.closeHandler();
    }
}
