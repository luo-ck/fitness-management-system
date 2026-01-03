package com.fitness.system.controller;

import com.fitness.system.entity.Admin;
import com.fitness.system.entity.Coach;
import com.fitness.system.entity.User;
import com.fitness.system.service.IAdminService;
import com.fitness.system.service.ICoachService;
import com.fitness.system.service.IUserService;
import com.fitness.system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证Controller
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ICoachService coachService;

    @Autowired
    private IAdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户注册
     * @param user 用户信息
     * @return 结果
     */
    @PostMapping("/register")
    public ResponseEntity<Integer> register(@RequestBody User user) {
        // 检查用户名是否已存在
        if (!userService.checkUsernameUnique(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(0);
        }
        // 对密码进行加密
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        // 设置默认值
        user.setIsDeleted(false);
        user.setCreatedAt(new Date());
        // 创建用户
        int result = userService.insertUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * 用户登录
     * @param user 用户登录信息
     * @return 包含JWT token和用户信息的响应
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        // 根据用户名查询用户
        User existingUser = userService.selectUserByUsername(user.getUsername());
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 验证密码
        if (!passwordEncoder.matches(user.getPasswordHash(), existingUser.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 密码验证通过，生成JWT token，添加用户角色
        String token = jwtUtil.generateToken(existingUser.getUsername(), "user");
        
        // 构建响应对象
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", existingUser);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 教练登录
     * @param coach 教练登录信息
     * @return 包含JWT token和教练信息的响应
     */
    @PostMapping("/coach/login")
    public ResponseEntity<Map<String, Object>> coachLogin(@RequestBody Coach coach) {
        // 调试信息：输出接收到的用户名
        System.out.println("Received username: " + coach.getUsername());
        System.out.println("Username length: " + coach.getUsername().length());
        
        // 根据用户名查询教练
        Coach existingCoach = coachService.selectCoachByUsername(coach.getUsername());
        if (existingCoach == null) {
            System.out.println("Coach not found for username: " + coach.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 验证密码
        if (!passwordEncoder.matches(coach.getPasswordHash(), existingCoach.getPasswordHash())) {
            System.out.println("Password mismatch for coach: " + coach.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 密码验证通过，生成JWT token，添加教练角色
        String token = jwtUtil.generateToken(existingCoach.getUsername(), "coach");
        
        // 构建响应对象
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("coach", existingCoach);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 测试教练登录功能（直接通过代码测试，绕过客户端编码问题）
     * @return 测试结果
     */
    @GetMapping("/test-coach-login-direct")
    public Map<String, Object> testCoachLoginDirect() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 直接在代码中创建教练对象，避免客户端编码问题
            Coach testCoach = new Coach();
            testCoach.setUsername("老牛");
            testCoach.setPasswordHash("12345678");
            
            System.out.println("Direct test received username: " + testCoach.getUsername());
            System.out.println("Username length: " + testCoach.getUsername().length());
            
            // 根据用户名查询教练
            Coach existingCoach = coachService.selectCoachByUsername(testCoach.getUsername());
            if (existingCoach == null) {
                System.out.println("Direct test: Coach not found for username: " + testCoach.getUsername());
                response.put("success", false);
                response.put("message", "教练不存在");
                return response;
            }
            // 验证密码
            boolean passwordMatch = passwordEncoder.matches(testCoach.getPasswordHash(), existingCoach.getPasswordHash());
            System.out.println("Direct test: Password match: " + passwordMatch + " for coach: " + testCoach.getUsername());
            
            if (passwordMatch) {
                // 密码验证通过，生成JWT token，添加教练角色
                String token = jwtUtil.generateToken(existingCoach.getUsername(), "coach");
                
                response.put("success", true);
                response.put("message", "登录成功");
                response.put("token", token);
                response.put("coach", existingCoach);
            } else {
                response.put("success", false);
                response.put("message", "密码错误");
            }
        } catch (Exception e) {
            System.out.println("Direct test error: " + e.getMessage());
            response.put("success", false);
            response.put("message", "测试过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
        
        return response;
    }
    
    /**
     * 管理员登录
     * @param admin 管理员登录信息
     * @return 包含JWT token和管理员信息的响应
     */
    @PostMapping("/admin/login")
    public ResponseEntity<Map<String, Object>> adminLogin(@RequestBody Admin admin) {
        // 根据用户名查询管理员
        Admin existingAdmin = adminService.selectAdminByUsername(admin.getUsername());
        if (existingAdmin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 验证密码
        if (!passwordEncoder.matches(admin.getPasswordHash(), existingAdmin.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 密码验证通过，生成JWT token，添加管理员角色
        String token = jwtUtil.generateToken(existingAdmin.getUsername(), "admin");
        
        // 构建响应对象
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("admin", existingAdmin);
        
        return ResponseEntity.ok(response);
    }
    

    

}
