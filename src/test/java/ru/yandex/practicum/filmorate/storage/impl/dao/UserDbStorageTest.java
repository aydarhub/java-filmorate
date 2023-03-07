package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    public void testFindUserById() {
        Optional<User> userOptional = userStorage.findById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testUpdate() {
        Optional<User> userOptional = userStorage.findById(1L);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setName("Who");
            userStorage.update(user);
        }
        Optional<User> userOptional1 = userStorage.findById(1L);
        assertThat(userOptional1)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "Who")
                );
    }

    @Test
    public void testAdd() {
        User user = new User(null, "email@email.ru", "loginchik", "Afanasiy",
                LocalDate.of(1999, Month.DECEMBER, 11));
        User user1 = userStorage.add(user);
        Optional<User> userOptional = userStorage.findById(user1.getId());
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("name", "Afanasiy")
                );

    }

    @Test
    public void testDelete() {
        userStorage.delete(2L);
        Optional<User> userOptional = userStorage.findById(2L);
        assertThat(userOptional).isEmpty();
    }

    @Test
    public void testSendRequestToFriend() {
        userStorage.sendRequestToFriend(4L, 6L);
        Optional<User> userOptional = userStorage.findById(4L);
        assertTrue(userOptional.get().getFriends().containsKey(6L));
        assertEquals(userOptional.get().getFriends().get(6L), FriendStatus.NOT_APPROVED_OUTGOING);
    }

    @Test
    public void testAcceptRequestFriend() {
        userStorage.sendRequestToFriend(5L, 6L);
        userStorage.acceptRequestFriend(6L, 5L);
        Optional<User> userOptional = userStorage.findById(6L);
        assertTrue(userOptional.get().getFriends().containsKey(5L));
        assertEquals(userOptional.get().getFriends().get(5L), FriendStatus.FRIEND);
    }

    @Test
    public void testRemoveFromFriends() {
        userStorage.sendRequestToFriend(3L, 6L);
        userStorage.removeFromFriends(3L, 6L);
        Optional<User> userOptional = userStorage.findById(3L);
        assertFalse(userOptional.get().getFriends().containsKey(6L));
    }
}