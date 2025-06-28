package com.bytecrew.uscode.service;

import com.bytecrew.uscode.domain.User;
import com.bytecrew.uscode.repository.UserRepository;
import com.bytecrew.uscode.util.AES256Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;

    public boolean login(String name, String jumin) {
        List<User> users = userRepository.findByName(name);

        for (User user : users) {
            String decrypted = AES256Util.decrypt(user.getEncryptedJumin());
            if (decrypted.equals(jumin)) {
                return true;
            }
        }
        return false;

    }
}

