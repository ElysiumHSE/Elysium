package hse.elysium.databaseInteractor;

import hse.elysium.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class UserServiceTests {
    @Test
    public void addGetDeleteTest() {
        int result = UserService.addNewUserWithLoginPassword("михаил", "антипов");
        Assertions.assertNotEquals(-1, result);

        result = UserService.addNewUserWithLoginPassword("михаил", "антипов");
        Assertions.assertEquals(-1, result);

        int user_id = UserService.getUserIdWithLogin("михаил");
        Assertions.assertNotEquals(-1, user_id);

        User user = UserService.getUserWithUserId(user_id);
        Assertions.assertNotNull(user);
        Assertions.assertEquals("михаил", user.getLogin());
        Assertions.assertEquals("антипов", user.getPassword());
        Assertions.assertNull(user.getFavourites());

        Assertions.assertEquals("михаил", UserService.getUserLoginWithUserId(user_id));
        Assertions.assertEquals("антипов", UserService.getUserPasswordWithUserId(user_id));

        // Randomly chosen big value for sure not reached by database
        Assertions.assertNull(UserService.getUserWithUserId(678249809));
        Assertions.assertEquals(-2, UserService.findTrackInUserFavourites(678249809, 1));

        user = UserService.deleteUserWithUserId(user_id);
        Assertions.assertNotNull(user);
        Assertions.assertEquals("михаил", user.getLogin());
        Assertions.assertEquals("антипов", user.getPassword());
        Assertions.assertNull(user.getFavourites());

        user = UserService.deleteUserWithUserId(user_id);
        Assertions.assertNull(user);
    }

    @Test
    public void addDeleteTracksFromFavouritesTest() {
        int result = UserService.addNewUserWithLoginPassword("михаил", "антипов");
        Assertions.assertNotEquals(-1, result);

        int user_id = UserService.getUserIdWithLogin("михаил");
        Assertions.assertNotEquals(-1, user_id);

        User user = UserService.getUserWithUserId(user_id);
        Assertions.assertNotNull(user);

        Assertions.assertEquals(1, UserService.addTrackToFavouritesWithUserId(user_id, 1));
        Assertions.assertEquals(0, UserService.addTrackToFavouritesWithUserId(user_id, 1));
        Assertions.assertEquals(1, UserService.addTrackToFavouritesWithUserId(user_id, 2));
        Assertions.assertEquals(-1, UserService.addTrackToFavouritesWithUserId(678249809, 3));

        Set<Integer> set = UserService.getUserFavouritesWithUserId(user_id);
        Assertions.assertNotNull(set);
        Assertions.assertTrue(set.contains(1));
        Assertions.assertTrue(set.contains(2));
        Assertions.assertFalse(set.contains(3));

        Assertions.assertEquals(1, UserService.deleteTrackFromFavouritesWithUserId(user_id, 2));
        Assertions.assertEquals(0, UserService.deleteTrackFromFavouritesWithUserId(user_id, 3));

        set = UserService.getUserFavouritesWithUserId(user_id);
        Assertions.assertNotNull(set);
        Assertions.assertTrue(set.contains(1));
        Assertions.assertFalse(set.contains(2));
        Assertions.assertFalse(set.contains(3));

        user = UserService.deleteUserWithUserId(user_id);
        Assertions.assertNotNull(user);
    }
}
