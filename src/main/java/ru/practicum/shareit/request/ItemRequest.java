package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor
public class ItemRequest {

    private transient int id;
    private String description;
    private int requestor;
    private LocalDateTime created;
}
