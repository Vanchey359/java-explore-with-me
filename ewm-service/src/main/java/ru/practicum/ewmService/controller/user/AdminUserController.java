package ru.practicum.ewmService.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmService.dto.user.NewUserDto;
import ru.practicum.ewmService.service.user.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<NewUserDto> createUser(@RequestBody @Valid NewUserDto newUserDto) {
        log.info("Create user {}", newUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(newUserDto));
    }

    @GetMapping
    public ResponseEntity<List<NewUserDto>> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {

        log.info("Get users with ids {}, from {}, size {}", ids, from, size);
        return ResponseEntity.ok(userService.getUsers(ids, from, size));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("userId") Long userId) {
        log.info("Delete user with id={}", userId);
        userService.deleteUserById(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}