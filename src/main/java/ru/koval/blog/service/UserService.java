package ru.koval.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.koval.blog.exception.UserWithLoginNotFoundException;
import ru.koval.blog.model.User;
import ru.koval.blog.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User findByLogin(String login) {
        return userRepository.getByLogin(login).orElseThrow(UserWithLoginNotFoundException::new);
    }


    public User save(User user) {
        return userRepository.save(user);
    }

}
