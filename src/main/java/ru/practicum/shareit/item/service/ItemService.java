package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public interface ItemService {

    ItemDto create(long ownerId, ItemDto item) throws ValidationException;

    ItemDto update(long ownerId, long itemId, ItemDto item) throws ValidationException;

    List<ItemDto> findAllByUserId(long ownerId);

    ItemDto getItemById(long itemId);

    List<ItemDto> search(String text);

}
