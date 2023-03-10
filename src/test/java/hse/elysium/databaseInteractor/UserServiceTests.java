package hse.elysium.databaseInteractor;

import hse.elysium.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class UserServiceTests {
    @Test
    public void addGetDeleteTest() {
        UserService us = new UserService();

        int result = us.addNewUserWithLoginPassword("михаил", "антипов");
        Assertions.assertNotEquals(-1, result);

        result = us.addNewUserWithLoginPassword("михаил", "антипов");
        Assertions.assertEquals(-1, result);

        int user_id = us.getUserIdWithLogin("михаил");
        Assertions.assertNotEquals(-1, user_id);

        User user = us.getUserWithUserId(user_id);
        Assertions.assertNotNull(user);
        Assertions.assertEquals("михаил", user.getLogin());
        Assertions.assertEquals("антипов", user.getPassword());
        Assertions.assertNull(user.getFavourites());

        Assertions.assertEquals("михаил", us.getUserLoginWithUserId(user_id));
        Assertions.assertEquals("антипов", us.getUserPasswordWithUserId(user_id));

        // Randomly chosen big value for sure not reached by database
        Assertions.assertNull(us.getUserWithUserId(678249809));
        Assertions.assertEquals(-2, us.findTrackInUserFavourites(678249809, 1));

        user = us.deleteUserWithUserId(user_id);
        Assertions.assertNotNull(user);
        Assertions.assertEquals("михаил", user.getLogin());
        Assertions.assertEquals("антипов", user.getPassword());
        Assertions.assertNull(user.getFavourites());

        user = us.deleteUserWithUserId(user_id);
        Assertions.assertNull(user);
    }

    @Test
    public void addDeleteTracksFromFavouritesTest() {
        UserService us = new UserService();

        int result = us.addNewUserWithLoginPassword("михаил", "антипов");
        Assertions.assertNotEquals(-1, result);

        int user_id = us.getUserIdWithLogin("михаил");
        Assertions.assertNotEquals(-1, user_id);

        User user = us.getUserWithUserId(user_id);
        Assertions.assertNotNull(user);

        Assertions.assertEquals(1, us.addTrackToFavouritesWithUserId(user_id, 1));
        Assertions.assertEquals(0, us.addTrackToFavouritesWithUserId(user_id, 1));
        Assertions.assertEquals(1, us.addTrackToFavouritesWithUserId(user_id, 2));
        Assertions.assertEquals(-1, us.addTrackToFavouritesWithUserId(678249809, 3));

        ArrayList<Integer> array = us.getUserFavouritesWithUserId(user_id);
        Assertions.assertNotNull(array);
        Assertions.assertEquals(2, array.size());
        Assertions.assertEquals(1, (int)array.get(0));
        Assertions.assertEquals(2, (int)array.get(1));
        Assertions.assertFalse(array.contains(3));

        Assertions.assertEquals(1, us.deleteTrackFromFavouritesWithUserId(user_id, 2));
        Assertions.assertEquals(0, us.deleteTrackFromFavouritesWithUserId(user_id, 3));

        array = us.getUserFavouritesWithUserId(user_id);
        Assertions.assertNotNull(array);
        Assertions.assertEquals(1, array.size());
        Assertions.assertEquals(1, (int)array.get(0));
        Assertions.assertFalse(array.contains(2));
        Assertions.assertFalse(array.contains(3));

        user = us.deleteUserWithUserId(user_id);
        Assertions.assertNotNull(user);

        us.closeHandler();
    }

    @Test
    public void simpleAddDeleteSameTest() {
        UserService us = new UserService();

        int result = us.addNewUserWithLoginPassword("михаил", "антипов");
        Assertions.assertNotEquals(-1, result);

        int user_id = us.getUserIdWithLogin("михаил");
        Assertions.assertNotEquals(-1, user_id);

        User user = us.deleteUserWithUserId(user_id);
        Assertions.assertNotNull(user);

        result = us.addNewUserWithLoginPassword("михаил", "антипов");
        Assertions.assertNotEquals(-1, result);

        user_id = us.getUserIdWithLogin("михаил");
        Assertions.assertNotEquals(-1, user_id);

        user = us.deleteUserWithUserId(user_id);
        Assertions.assertNotNull(user);

        us.closeHandler();
    }
}
