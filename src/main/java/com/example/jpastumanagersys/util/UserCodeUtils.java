package com.example.jpastumanagersys.util;

import com.example.jpastumanagersys.config.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserCodeUtils {
    public static String getCurrentUserCode() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserCode();
    }
}
