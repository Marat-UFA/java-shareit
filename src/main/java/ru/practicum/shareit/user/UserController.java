package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;


    @PostMapping()
    public UserDto create(@Valid @RequestBody UserDto user) {
        log.info("Save new user: {}", user.getName());
        return userService.create(user);
    }

    @PatchMapping(path = "/{userId}")
    public UserDto update(@PathVariable long userId, @RequestBody UserDto user) {
        log.info("Update user with id: {}", userId);
        return userService.update(userId, user);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Get all user");
        return userService.getUsers();
    }

    @GetMapping(path = "/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.info("Get user by id={}",userId);
        return userService.getUserById(userId);
    }

    @DeleteMapping(path = "/{userId}")
    public void deleteById(@PathVariable int userId) {
        log.info("Delete user by id={}",userId);
        userService.deleteById(userId);
    }
}
