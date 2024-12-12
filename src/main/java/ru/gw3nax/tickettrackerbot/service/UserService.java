package ru.gw3nax.tickettrackerbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gw3nax.tickettrackerbot.entity.User;
import ru.gw3nax.tickettrackerbot.enums.InputDataState;
import ru.gw3nax.tickettrackerbot.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void registerUser(Long userId) {
        if (userRepository.findByUserId(userId).isPresent()) {
            return;
        }
        userRepository.save(User.builder()
                .id(null).userId(userId).build()
        );
    }

    @Transactional
    public void updateState(Long chatId, InputDataState inputDataState) {
        var user = userRepository.findByUserId(chatId);
        if (user.isPresent()) {
            user.get();
            userRepository.save(User.builder()
                    .id(user.get().getId())
                    .userId(chatId)
                    .inputDataState(inputDataState)
                    .build()
            );
        }
    }

    @Transactional
    public InputDataState getState(Long userId) {
        var user = userRepository.findByUserId(userId);
        return user.map(User::getInputDataState).orElse(null);
    }

    @Transactional
    public void clearState(Long chatId) {
        var user = userRepository.findByUserId(chatId);
        if (user.isPresent()) {
            user.get().setInputDataState(null);
            userRepository.save(user.get());
        }
    }

    @Transactional(readOnly = true)
    public User getUser(Long chatId) {
        var user = userRepository.findByUserId(chatId);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }
}
