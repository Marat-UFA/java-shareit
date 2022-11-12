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
        log.debug("Преобразовываем Dto объект в обычный item, при создании новой вещи");

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
        log.debug("Ошибка сервера при создании уже существующей вещи");

        for (Item items : itemRepository.values()) {
            if ((items.getOwner() == ownerId) && items.getName().equals(item.getName())) {
                throw new RuntimeException("Данная вещь пользователем уже добавлена");
            }
        }

        log.debug("Создаем новую вещь");
        id += 1;
        item.setId(id);
        log.debug("Добавляем вещь в список вещей пользователя");
        if (user.getListOfItem() == null) {
            user.setListOfItem(new ArrayList<>());
            user.getListOfItem().add(item);
        } else {
            user.getListOfItem().add(item);
        }
        log.debug("Обновляем пользователя в хранилище мапе пользователей");
        UserDto userDto = userMapper.toUserDto(user);
        userService.update(ownerId, userDto);
        userDto.setListOfItem(null);
        item.setOwner(userMapper.toUser(userDto).getId());
        log.debug("Помещаем созданную вещи в мапу хранилище");
        itemRepository.put(id, item);
        log.debug("Возвращаем Dto при создании новой вещи");
        return itemMapper.toItemDto(itemRepository.get(id));
//        }
    }

    @Override
    public ItemDto update(long ownerId, long itemId, ItemDto itemDto) {
        log.debug("Преобразовываем Dto объект в обычный item");

        Item item = itemMapper.toItem(itemDto);
        User user = userMapper.toUser(userService.getUserById(ownerId));

        if (user == null) {
            throw new NotFoundException("Пользователя с данным Id не существует");
        }

        if (user.getListOfItem() == null || user.getListOfItem().contains(itemId)) {
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
        log.debug("Возвращаем список доступных вещей для аренды по поиску 'ключевому слово'");
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
