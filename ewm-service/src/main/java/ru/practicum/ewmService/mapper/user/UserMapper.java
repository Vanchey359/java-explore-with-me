package ru.practicum.ewmService.mapper.user;

import org.mapstruct.Mapper;
import ru.practicum.ewmService.dto.user.NewUserDto;
import ru.practicum.ewmService.dto.user.UserShortDto;
import ru.practicum.ewmService.model.user.User;


@Mapper(componentModel = "spring")
public interface UserMapper {

    NewUserDto toUserDto(User user);

    User toUser(NewUserDto newUserDto);

    UserShortDto toUserShortDto(User user);
}
