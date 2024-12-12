package ru.gw3nax.tickettrackerbot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.gw3nax.tickettrackerbot.entity.User;
import ru.gw3nax.tickettrackerbot.enums.InputDataState;
import ru.gw3nax.tickettrackerbot.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void registerUser_ShouldSaveUser_WhenUserDoesNotExist() {
        Long userId = 1L;

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        userService.registerUser(userId);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_ShouldDoNothing_WhenUserAlreadyExists() {
        Long userId = 1L;
        User existingUser = User.builder().id(1L).userId(userId).build();

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(existingUser));

        userService.registerUser(userId);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateState_ShouldUpdateState_WhenUserExists() {
        Long chatId = 1L;
        InputDataState newState = InputDataState.SOURCE;  // Updated to SOURCE
        User existingUser = User.builder().id(1L).userId(chatId).inputDataState(InputDataState.DESTINATION).build();  // Updated to DESTINATION

        when(userRepository.findByUserId(chatId)).thenReturn(Optional.of(existingUser));

        userService.updateState(chatId, newState);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateState_ShouldDoNothing_WhenUserDoesNotExist() {
        Long chatId = 1L;
        InputDataState newState = InputDataState.SOURCE;

        when(userRepository.findByUserId(chatId)).thenReturn(Optional.empty());

        userService.updateState(chatId, newState);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getState_ShouldReturnState_WhenUserExists() {
        Long userId = 1L;
        InputDataState expectedState = InputDataState.DESTINATION;  // Updated to DESTINATION
        User existingUser = User.builder().id(1L).userId(userId).inputDataState(expectedState).build();

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(existingUser));

        InputDataState actualState = userService.getState(userId);

        assertEquals(expectedState, actualState);
    }

    @Test
    void getState_ShouldReturnNull_WhenUserDoesNotExist() {
        Long userId = 1L;

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        InputDataState actualState = userService.getState(userId);

        assertNull(actualState);
    }

    @Test
    void clearState_ShouldSetStateToNull_WhenUserExists() {
        Long chatId = 1L;
        User existingUser = User.builder().id(1L).userId(chatId).inputDataState(InputDataState.DESTINATION).build();  // Updated to DESTINATION

        when(userRepository.findByUserId(chatId)).thenReturn(Optional.of(existingUser));

        userService.clearState(chatId);

        assertNull(existingUser.getInputDataState());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void clearState_ShouldDoNothing_WhenUserDoesNotExist() {
        Long chatId = 1L;

        when(userRepository.findByUserId(chatId)).thenReturn(Optional.empty());

        userService.clearState(chatId);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUser_ShouldReturnUser_WhenUserExists() {
        Long chatId = 1L;
        User expectedUser = User.builder().id(1L).userId(chatId).build();

        when(userRepository.findByUserId(chatId)).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.getUser(chatId);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getUser_ShouldReturnNull_WhenUserDoesNotExist() {
        Long chatId = 1L;

        when(userRepository.findByUserId(chatId)).thenReturn(Optional.empty());

        User actualUser = userService.getUser(chatId);

        assertNull(actualUser);
    }
}
