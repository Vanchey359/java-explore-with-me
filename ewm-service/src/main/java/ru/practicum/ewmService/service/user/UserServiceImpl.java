package ru.practicum.ewmService.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmService.dto.user.NewUserDto;
import ru.practicum.ewmService.mapper.user.UserMapper;
import ru.practicum.ewmService.model.user.User;
import ru.practicum.ewmService.repository.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Transactional
    public NewUserDto createUser(NewUserDto newUserDto) {
        User user = userMapper.toUser(newUserDto);
        log.info("Created new user=" + user);

        return userMapper.toUserDto(repository.save(user));
    }

    public List<NewUserDto> getUsers(List<Long> userIdList, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from, size);
        List<NewUserDto> newUserDtoList;

        if (userIdList == null) {
            newUserDtoList = repository.findAll(page).stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
            log.info("Found all users, size=" + newUserDtoList.size());
        } else {
            newUserDtoList = repository.findByIdIn(userIdList, page).stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
            log.info("Found all users corresponding to , size=" + newUserDtoList.size());
        }

        return newUserDtoList;
    }

    @Transactional
    public void deleteUserById(Long userId) {
        repository.deleteById(userId);
        log.info("Deleted user with userId=" + userId);
    }
}
