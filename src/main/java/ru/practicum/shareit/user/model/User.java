package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@AllArgsConstructor
public class User {
    private long id;
    @NotNull
    @Pattern(regexp = "^\\S*$")
    private String name;
    @Email
    @NotNull
    private String email;
    private List<Item> listOfItem;
}
