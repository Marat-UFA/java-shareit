package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@Component("ItemServiceImpl")
public class ItemServiceImpl implements ItemService {

    private Map<Long, Item> itemRepository = new HashMap<>();
    private long id = 0;
    private ItemMapper itemMapper;
    private UserMapper userMapper;
    private UserService userService;

    @Autowired
    public ItemServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ItemDto create(long ownerId, ItemDto itemDto) throws ValidationException {

        Item item = itemMapper.toItem(itemDto);
        User user = userMapper.toUser(userService.getUserById(ownerId));

        if (user == null) {
            throw new NotFoundException("Пользователя с данным Id не существует");
        }
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ValidationException("Не указано название вещи");
        }
        if (item.getDescription() == null) {
            throw new ValidationException("Не указано описание вещи");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Не указан статус доступа вещи");
        }

        for (Item items : itemRepository.values()) {
            if ((items.getOwner() == ownerId) && items.getName().equals(item.getName())) {
                throw new RuntimeException("Данная вещь пользователем уже добавлена");
            }
        }

        id += 1;
        item.setId(id);
        if (user.getListOfItem() == null) {
            user.setListOfItem(new ArrayList<>());
            user.getListOfItem().add(item);
        } else {
            user.getListOfItem().add(item);
        }
        UserDto userDto = userMapper.toUserDto(user);
        userService.update(ownerId, userDto);
        userDto.setListOfItem(null);
        item.setOwner(userMapper.toUser(userDto).getId());
        itemRepository.put(id, item);
        return itemMapper.toItemDto(itemRepository.get(id));
//        }
    }

    @Override
    public ItemDto update(long ownerId, long itemId, ItemDto itemDto) {

        Item item = itemMapper.toItem(itemDto);
        User user = userMapper.toUser(userService.getUserById(ownerId));

        if (user == null) {
            throw new NotFoundException("Пользователя с данным Id не существует");
        }

        if (user.getListOfItem() == null || user.getListOfItem().contains(item)) {
            throw new NotFoundException("У пользователя не существует данной вещей");
        }
        if (itemId <= 0) {
            throw new NotFoundException("Вещи с данным Id не существует");
        }
        for (Long itemsId : itemRepository.keySet()) {
            if (itemsId == itemId) {
                if ((item.getName() != null)) {
                    itemRepository.get(itemId).setName(item.getName());
                }
                if ((item.getDescription() != null)) {
                    itemRepository.get(itemId).setDescription(item.getDescription());
                }
                if ((item.getAvailable() != null)) {
                    itemRepository.get(itemId).setAvailable(item.getAvailable());
                }
                return ItemMapper.toItemDto(itemRepository.get(itemId));
            }
        }
        throw new RuntimeException("данной вещи нет");

    }

    @Override
    public List<ItemDto> findAllByUserId(long ownerId) {
        User user = userMapper.toUser(userService.getUserById(ownerId));
        List<ItemDto> allItems = new ArrayList<>();
        for (int i = 0; i < user.getListOfItem().size(); i++) {
            allItems.add(itemMapper.toItemDto(user.getListOfItem().get(i)));
        }
        return allItems;
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return itemMapper.toItemDto(itemRepository.get(itemId));
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemDto> returnAllFoundItemsByText = new ArrayList<>();
        List<Item> allItems = new ArrayList<>(itemRepository.values());
        for (Item item : allItems) {
            if (item.getAvailable()) {
                if (item.getName().toUpperCase().contains(text.toUpperCase()) ||
                        item.getDescription().toUpperCase().contains(text.toUpperCase())) {
                    returnAllFoundItemsByText.add(itemMapper.toItemDto(item));
                }
            }
        }
        return returnAllFoundItemsByText;
    }

}
