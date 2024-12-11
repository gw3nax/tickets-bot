package ru.gw3nax.tickettrackerbot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.gw3nax.tickettrackerbot.entity.User;
import ru.gw3nax.tickettrackerbot.enums.InputDataState;
import ru.gw3nax.tickettrackerbot.repository.UserRepository;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        Long chatId = 1L;

        userService.registerUser(chatId);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());

        User capturedUser = captor.getValue();
        assertEquals(chatId, capturedUser.getId());
        assertNull(capturedUser.getInputDataState());
    }

    @Test
    void testUpdateState() {
        Long chatId = 1L;
        InputDataState state = InputDataState.SOURCE;

        when(userRepository.findById(chatId)).thenReturn(Optional.of(User.builder().id(chatId).build()));

        userService.updateState(chatId, state);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());

        User capturedUser = captor.getValue();
        assertEquals(chatId, capturedUser.getId());
        assertEquals(state, capturedUser.getInputDataState());
    }

    @Test
    void testGetState() {
        Long chatId = 1L;
        InputDataState state = InputDataState.SOURCE;

        when(userRepository.findById(chatId)).thenReturn(Optional.of(User.builder().id(chatId).inputDataState(state).build()));

        InputDataState result = userService.getState(chatId);

        assertEquals(state, result);
    }

    @Test
    void testGetState_shouldReturnNull() {
        var chatId = 1L;

        when(userRepository.findById(chatId)).thenReturn(Optional.empty());

        var result = userService.getState(chatId);

        assertNull(result);
    }

    @Test
    void testClearState() {
        Long chatId = 1L;
        User user = User.builder().id(chatId).inputDataState(InputDataState.SOURCE).build();

        when(userRepository.findById(chatId)).thenReturn(Optional.of(user));

        userService.clearState(chatId);

        assertNull(user.getInputDataState());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetUser() {
        Long chatId = 1L;
        User user = User.builder().id(chatId).build();

        when(userRepository.findById(chatId)).thenReturn(Optional.of(user));

        User result = userService.getUser(chatId);

        assertEquals(user, result);
    }
}
