package com.chisimdi.user.service.service;

import com.chisimdi.user.service.Exceptions.InvalidCredentialsException;
import com.chisimdi.user.service.Exceptions.ResourceNotFoundException;
import com.chisimdi.user.service.models.UserDTO;
import com.chisimdi.user.service.models.User;
import com.chisimdi.user.service.repositories.UserRepository;
import com.chisimdi.user.service.utils.LoginResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private JwtUtilService jwtUtilService;

    public UserService(UserRepository userRepository,BCryptPasswordEncoder bCryptPasswordEncoder,JwtUtilService jwtUtilService){
        this.userRepository=userRepository;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
        this.jwtUtilService=jwtUtilService;
    }

    public UserDTO toUserDTO(User user){
        UserDTO userDTO=new UserDTO();
        userDTO.setId(user.getId());
        if(user.getName()!=null){
            userDTO.setName(user.getName());
        }
        if(user.getRoles()!=null){
            userDTO.setRoles(user.getRoles());
        }
        if(user.getEmail()!=null){
            userDTO.setEmail(user.getEmail());
        }
        if(user.getApproved()!=null){
            userDTO.setApproved(user.getApproved());
        }
        return userDTO;

    }

    public UserDTO findUserById(int id){
        User user= userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("user with id "+id+" does not exist" ));
        return toUserDTO(user);
    }

    public Boolean existsById(int id){
        return userRepository.existsById(id);
    }

    public List<UserDTO> findAllUsers(int pageNumber,int size){
        Page<User> users=userRepository.findAll(PageRequest.of(pageNumber, size));
        List<UserDTO>userDTOS=new ArrayList<>();
        for(User u: users){
            userDTOS.add(toUserDTO(u));
        }
        return userDTOS;

    }

    @Transactional
    public UserDTO registerUsers(User user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return toUserDTO(user);
    }
    public LoginResponse login(String userName,String password){
        User user= userRepository.findByUserName(userName);
        if(user==null){
            throw new InvalidCredentialsException("User name is not valid");
        }
        if(!bCryptPasswordEncoder.matches(password, user.getPassword())){
            throw new InvalidCredentialsException("Password invalid");
        }
        if(user.getApproved()==false&&!user.getRoles().equals("Customer")){
            throw new InvalidCredentialsException("User Not approved");
        }
        String token =jwtUtilService.generateToken(user.getUserName(), user.getId(), user.getRoles());
        LoginResponse loginResponse=new LoginResponse();
        loginResponse.setUserName(jwtUtilService.extractUserName(token));
        loginResponse.setUserId(jwtUtilService.extractUserId(token));
        loginResponse.setRole(jwtUtilService.extractRole(token));
        loginResponse.setToken(token);
        return loginResponse;
    }


    public UserDTO approveUsers(int userId){
        User user= userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user with id "+userId+" not found"));



        if(user.getApproved()==false){
            user.setApproved(true);
        }
        user.setApproved(true);
        userRepository.save(user);
        return toUserDTO(user);

    }
}
