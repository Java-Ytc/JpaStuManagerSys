package com.example.jpastumanagersys;

import com.example.jpastumanagersys.entity.User;
import com.example.jpastumanagersys.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 使用Spring Boot的测试框架，启动整个Spring Boot应用程序上下文进行测试
@SpringBootTest
// 自动配置MockMvc，用于模拟HTTP请求和处理，方便进行控制器层的测试
@AutoConfigureMockMvc
public class AuthControllerTest {

    // 注入MockMvc实例，用于模拟HTTP请求
    @Autowired
    private MockMvc mockMvc;

    // 注入WebApplicationContext，用于初始化MockMvc
    @Autowired
    private WebApplicationContext webApplicationContext;

    // 注入UserRepo，用于操作数据库中的用户信息
    @Autowired
    private UserRepo userRepo;

    // 注入PasswordEncoder，用于密码加密操作
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 在每个测试方法执行之前运行，进行测试环境的初始化操作。
     * 初始化MockMvc实例，并清空UserRepo中的所有用户数据，保证每次测试的独立性。
     */
    @BeforeEach
    public void setup() {
        // 通过WebApplicationContext初始化MockMvc
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // 清空数据库中的用户数据，避免数据干扰
        userRepo.deleteAll();
    }

    /**
     * 测试显示登录页面的功能。
     * 发送GET请求到 /auth/login 路径，验证响应状态码是否为200（请求成功），
     * 以及返回的视图名称是否为 /auth/login，确保登录页面能正常显示。
     */
    @Test
    public void testShowLoginPage() throws Exception {
        mockMvc.perform(get("/auth/login"))
                // 验证HTTP响应状态码是否为200
                .andExpect(status().isOk())
                // 验证返回的视图名称是否为 /auth/login
                .andExpect(view().name("/auth/login"));
    }

    /**
     * 测试显示注册页面的功能。
     * 发送GET请求到 /auth/register 路径，验证响应状态码是否为200，
     * 以及返回的视图名称是否为 /auth/register，确保注册页面能正常显示。
     */
    @Test
    public void testShowRegisterPage() throws Exception {
        mockMvc.perform(get("/auth/register"))
                // 验证HTTP响应状态码是否为200
                .andExpect(status().isOk())
                // 验证返回的视图名称是否为 /auth/register
                .andExpect(view().name("/auth/register"));
    }

    /**
     * 测试用户注册功能。
     * 创建一个User对象，设置用户信息，然后发送POST请求到 /auth/register 路径，模拟用户注册操作。
     * 验证响应状态码是否为3xx重定向状态码，以及重定向的URL是否为 /auth/login，
     * 确保用户注册成功后能正确重定向到登录页面。
     */
    @Test
    public void testProcessRegister() throws Exception {
        // 创建一个新的User对象，用于测试注册功能
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.setEmail("test@example.com");
        user.setPhone("123456789");
        user.setRole("STUDENT");

        mockMvc.perform(post("/auth/register")
                        // 设置请求的内容类型为表单编码
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        // 设置请求参数，模拟用户提交的表单数据
                        .param("username", user.getUsername())
                        .param("password", user.getPassword())
                        .param("email", user.getEmail())
                        .param("phone", user.getPhone())
                        .param("role", user.getRole()))
                // 验证HTTP响应状态码是否为3xx重定向
                .andExpect(status().is3xxRedirection())
                // 验证重定向的URL是否为 /auth/login
                .andExpect(redirectedUrl("/auth/login"));
    }

    /**
     * 测试使用有效凭证登录的功能。
     * 先创建一个User对象，对密码进行加密后保存到数据库中，模拟用户注册的过程。
     * 然后发送POST请求到 /auth/login 路径，使用正确的用户名和密码进行登录。
     * 验证响应状态码是否为3xx重定向状态码，确保用户能使用有效凭证成功登录。
     */
    @Test
    public void testLoginWithValidCredentials() throws Exception {
        // 创建一个新的User对象，用于测试登录功能
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        user.setEmail("test@example.com");
        user.setPhone("123456789");
        user.setRole("STUDENT");
        // 使用PasswordEncoder对用户密码进行加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 将用户信息保存到数据库中
        userRepo.save(user);

        // 模拟用户使用有效凭证进行登录的请求
        mockMvc.perform(post("/auth/login")
                        // 设置请求的内容类型为表单编码
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        // 设置请求参数，使用正确的用户名和密码
                        .param("username", user.getUsername())
                        .param("password", "testpassword"))
                // 验证HTTP响应状态码是否为3xx重定向
                .andExpect(status().is3xxRedirection());
    }

    /**
     * 测试使用无效凭证登录的功能。
     * 发送POST请求到 /auth/login 路径，使用不存在的用户名和错误的密码进行登录。
     * 验证响应状态码是否为3xx重定向状态码，以及重定向的URL是否为 /auth/login，
     * 确保用户使用无效凭证登录时能正确重定向到登录页面。
     */
    @Test
    public void testLoginWithInvalidCredentials() throws Exception {
        // 模拟用户使用无效凭证进行登录的请求
        mockMvc.perform(post("/auth/login")
                        // 设置请求的内容类型为表单编码
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        // 设置请求参数，使用不存在的用户名和错误的密码
                        .param("username", "nonexistentuser")
                        .param("password", "wrongpassword"))
                // 验证HTTP响应状态码是否为3xx重定向
                .andExpect(status().is3xxRedirection())
                // 验证重定向的URL是否为 /auth/login
                .andExpect(redirectedUrl("/auth/login"));
    }
}
