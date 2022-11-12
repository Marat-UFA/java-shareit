package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private Map<Long, User> userRepository = new HashMap<>();
    private Set email = new HashSet<>();
    private long id = 0;

    @Override
    public UserDto create(UserDto userDto) {
        log.debug("Преобразовываем Dto объект в обычного user");
        User user = UserMapper.toUser(userDto);
        log.debug("Проверяем существует ли такой email");
        if (email.contains(user.getEmail())) {
            throw new RuntimeException("Данный email уже зарегистрирован");//
        } else {
            log.debug("Создаем нового пользователя");
            id += 1;
            user.setId(id);
            userRepository.put(id, user);
            email.add(user.getEmail());
            log.debug("Возвращаем нового пользователя в DtoUser");
            return UserMapper.toUserDto(userRepository.get(id));
        }
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        log.debug("Преобразовываем Dto объект в обычного user");
        userDto.setId(userId);
        User user = UserMapper.toUser(userDto);
        if (email.contains(user.getEmail()) && userRepository.get(userId).getEmail() != user.getEmail()) {
            throw new RuntimeException("данный email зарегистрирован на другого пользователя");
        }
        for (Long usersId : userRepository.keySet()) {
            if (usersId == userId) {
                if ((user.getName() != null)) {
                    userRepository.get(userId).setName(user.getName());
                }
                if ((user.getEmail() != null)) {
                    email.remove(userRepository.get(userId).getEmail());
                    userRepository.get(userId).setEmail(user.getEmail());
                    email.add(user.getEmail());
                }
                userRepository.get(userId).setListOfItem(user.getListOfItem());
                return UserMapper.toUserDto(userRepository.get(userId));
            }
        }
        throw new RuntimeException("данного пользователя нет");
    }

    @Override
    public List<UserDto> getUsers() {
        List<UserDto> listUserDto = new ArrayList<>();
        List<User> users = new ArrayList<>(userRepository.values());
        for (User user : users) {
            listUserDto.add(UserMapper.toUserDto(user));
        }
        return listUserDto;
    }

    @Override
    public UserDto getUserById(long userId) {
        if (!userRepository.containsKey(userId)) {
            return null;
        }
        return UserMapper.toUserDto(userRepository.get(userId));
    }

    @Override
    public void deleteById(long userId) {
        email.remove(userRepository.get(userId).getEmail());
        userRepository.remove(userId);

    }
}
