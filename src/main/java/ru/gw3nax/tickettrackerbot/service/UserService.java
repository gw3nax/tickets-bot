package ru.gw3nax.tickettrackerbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gw3nax.tickettrackerbot.entity.User;
import ru.gw3nax.tickettrackerbot.enums.InputDataState;
import ru.gw3nax.tickettrackerbot.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void registerUser(Long chatId) {
        userRepository.save(User.builder()
                .id(chatId).build()
        );
    }

    public void updateState(Long chatId, InputDataState inputDataState) {
        var user = userRepository.findById(chatId);
        if (user.isPresent()) {
            user.get();
            userRepository.save(User.builder()
                    .id(chatId)
                    .inputDataState(inputDataState)
                    .build()
            );
        }
    }

    public InputDataState getState(Long chatId) {
        var user = userRepository.findById(chatId);
        return user.map(User::getInputDataState).orElse(null);
    }
    public void clearState(Long chatId) {
        var user = userRepository.findById(chatId);
        if (user.isPresent()) {
            user.get().setInputDataState(null);
            userRepository.save(user.get());
        }
    }

    public User getUser(Long chatId) {
        var user = userRepository.findById(chatId);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }
}
