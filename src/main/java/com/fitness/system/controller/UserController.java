package com.fitness.system.controller;

import com.fitness.system.entity.User;
import com.fitness.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户Controller
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 根据ID查询用户
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User user = userService.selectUserByUserId(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.selectUserByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * 查询所有用户
     * @return 用户列表
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.selectUserList();
        return ResponseEntity.ok(users);
    }

    /**
     * 创建用户
     * @param user 用户信息
     * @return 结果
     */
    @PostMapping
    public ResponseEntity<Integer> createUser(@RequestBody User user) {
        int result = userService.insertUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * 更新用户
     * @param userId 用户ID
     * @param user 用户信息
     * @return 结果
     */
    @PutMapping("/{userId}")
    public ResponseEntity<Integer> updateUser(@PathVariable Long userId, @RequestBody User user) {
        user.setUserId(userId);
        int result = userService.updateUser(user);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 结果
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Integer> deleteUser(@PathVariable Long userId) {
        int result = userService.deleteUserByUserId(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * 批量删除用户
     * @param userIds 用户ID列表
     * @return 结果
     */
    @DeleteMapping
    public ResponseEntity<Integer> deleteUsers(@RequestBody Long[] userIds) {
        int result = userService.deleteUserByUserIds(userIds);
        return ResponseEntity.ok(result);
    }

    /**
     * 检查用户名是否唯一
     * @param username 用户名
     * @return 是否唯一
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> checkUsernameUnique(@PathVariable String username) {
        boolean isUnique = userService.checkUsernameUnique(username);
        return ResponseEntity.ok(isUnique);
    }
}
