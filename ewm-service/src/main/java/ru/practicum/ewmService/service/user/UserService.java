package ru.practicum.ewmService.service.user;

import ru.practicum.ewmService.dto.user.NewUserDto;

import java.util.List;

public interface UserService {
    NewUserDto createUser(NewUserDto newUserDto);

    List<NewUserDto> getUsers(List<Long> ids, Integer from, Integer size);

    void deleteUserById(Long id);
}