package com.chisimdi.user.service;

import com.chisimdi.user.service.Exceptions.InvalidCredentialsException;
import com.chisimdi.user.service.Exceptions.ResourceNotFoundException;
import com.chisimdi.user.service.models.User;
import com.chisimdi.user.service.models.UserDTO;
import com.chisimdi.user.service.repositories.UserRepository;
import com.chisimdi.user.service.service.JwtUtilService;
import com.chisimdi.user.service.service.UserService;
import com.chisimdi.user.service.utils.LoginResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private JwtUtilService jwtUtilService;
    @InjectMocks
    UserService userService;

    @Test
    void registerUsersTest(){
        User user=new User();
        user.setPassword("");

        when(bCryptPasswordEncoder.encode(user.getPassword())).thenReturn("200");
        when(userRepository.save(user)).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        UserDTO userDTO= userService.registerUsers(user);

        assertThat(user.getPassword()).isEqualTo("200");

        verify(userRepository).save(user);
        verify(bCryptPasswordEncoder).encode("");


    }

    @Test
    void loginTest_HappyPath(){
        User user=new User();
        user.setUserName("");
        user.setRoles("Customer");
        user.setId(1);
        user.setApproved(false);

        when(userRepository.findByUserName("123")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("",user.getPassword())).thenReturn(true);
        when(jwtUtilService.generateToken(user.getUserName(),user.getId(),user.getRoles())).thenReturn("250");

        LoginResponse loginResponse=userService.login("123","");

        assertThat(loginResponse.getToken()).isEqualTo("250");

        verify(userRepository).findByUserName("123");
        verify(bCryptPasswordEncoder).matches("",user.getPassword());
        verify(jwtUtilService).generateToken(user.getUserName(),user.getId(),user.getRoles());
    }
    @Test
    void loginTest_ThrowsInvalidCredentialsForUserName(){
        User user=new User();
        user.setUserName("");
        user.setRoles("Customer");
        user.setId(1);
        user.setApproved(false);

        when(userRepository.findByUserName("123")).thenReturn(null);


        assertThatThrownBy(()->userService.login("123","")).isInstanceOf(InvalidCredentialsException.class);



        verify(userRepository).findByUserName("123");
        verify(bCryptPasswordEncoder,never()).matches("",user.getPassword());
        verify(jwtUtilService,never()).generateToken(user.getUserName(),user.getId(),user.getRoles());
    }

    @Test
    void loginTest_ThrowsInvalidCredentialsExceptionForPassword(){
        User user=new User();
        user.setUserName("");
        user.setRoles("Customer");
        user.setId(1);
        user.setApproved(false);

        when(userRepository.findByUserName("123")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("",user.getPassword())).thenReturn(false);


        assertThatThrownBy(()->userService.login("123","")).isInstanceOf(InvalidCredentialsException.class);



        verify(userRepository).findByUserName("123");
        verify(bCryptPasswordEncoder).matches("",user.getPassword());
        verify(jwtUtilService,never()).generateToken(user.getUserName(),user.getId(),user.getRoles());
    }
    @Test
    void loginTest_ThrowsInvalidCredentialsExceptionForRolesAndApproved(){
        User user=new User();
        user.setUserName("");
        user.setRoles("Admin");
        user.setId(1);
        user.setApproved(false);

        when(userRepository.findByUserName("123")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("",user.getPassword())).thenReturn(true);

        assertThatThrownBy(()->userService.login("123","")).isInstanceOf(InvalidCredentialsException.class);



        verify(userRepository).findByUserName("123");
        verify(bCryptPasswordEncoder).matches("",user.getPassword());
        verify(jwtUtilService,never()).generateToken(user.getUserName(),user.getId(),user.getRoles());
    }

    @Test
    void approveUsersTest_HappyPath(){
        User user=new User();
        user.setApproved(false);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        UserDTO userDTO=userService.approveUsers(1);

        assertThat(user.getApproved()).isEqualTo(true);

        verify(userRepository).findById(1);
        verify(userRepository).save(user);
    }

    @Test
    void approveUsersTest_ThrowsResourceNotFoundExceptionForUser(){
        User user=new User();
        user.setApproved(false);

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(()->userService.approveUsers(1)).isInstanceOf(ResourceNotFoundException.class);



        verify(userRepository).findById(1);
        verify(userRepository,never()).save(user);
    }
}
