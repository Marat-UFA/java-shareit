package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.Create;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemDto {
    private long id;
    @NotBlank(groups = {Create.class})
    private String name;
    @NotBlank(groups = {Create.class})
    private String description;
    @NotNull(groups = {Create.class})
    private Boolean available;
    private long owner;
    private long request;
}
