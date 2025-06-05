package com.example.jpastumanagersys.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

@Controller
public class CaptchaController {
    @GetMapping("/captcha")
    public void generateCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        HttpSession session = request.getSession();

        // 设置响应头，禁止缓存
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 创建验证码图片
        int width = 120;
        int height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // 设置字体
        g.setFont(new Font("Arial", Font.BOLD, 24));

        // 生成随机验证码
        Random random = new Random();
        String captchaText = "";
        for (int i = 0; i < 4; i++) {
            captchaText += random.nextInt(10);
        }

        // 保存验证码到session
        session.setAttribute("captcha", captchaText);

        // 绘制验证码
        g.setColor(Color.BLACK);
        g.drawString(captchaText, 30, 30);

        // 添加干扰线
        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }

        // 释放资源
        g.dispose();

        // 输出图片
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "JPEG", out);
        out.flush();
        out.close();
    }
}
