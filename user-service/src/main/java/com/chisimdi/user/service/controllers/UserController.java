package com.chisimdi.user.service.controllers;

import com.chisimdi.user.service.models.User;
import com.chisimdi.user.service.models.UserDTO;
import com.chisimdi.user.service.service.UserService;
import com.chisimdi.user.service.utils.LoginRequest;
import com.chisimdi.user.service.utils.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    protected UserController(UserService userService){
        this.userService=userService;
    }
    @Operation(summary = "Register users, public endpoint")
    @PostMapping("/")
    public UserDTO registerUser(@Valid @RequestBody User user){
        return userService.registerUsers(user);
    }

    @Operation(summary = "Retrieves All users, available to merchants only ")
    @PreAuthorize("hasRole('ROLE_Merchant')")
    @GetMapping("/")
    public List<UserDTO>findAllUsers(@RequestParam(defaultValue = "0")int pageNumber,@RequestParam(defaultValue = "10")int size){
        return userService.findAllUsers(pageNumber, size);
    }

    @Operation(summary = "Checks if a user exist, service endpoint")
    @PreAuthorize("hasRole('ROLE_Service')")
    @GetMapping("/{id}/exists")
    public Boolean doesUserExist(@PathVariable("id")int id){
        return userService.existsById(id);
    }

    @Operation(summary = "logs a user in, public endpoint")
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest){
        return userService.login(loginRequest.getUsername(),loginRequest.getPassword());
    }

    @Operation(summary = "approves users, available only to merchants")
    @PreAuthorize("hasRole('ROLE_Merchant')")
    @PostMapping("/approval/{userId}")
    public UserDTO approveUsers(@PathVariable("userId")int userId){
        return userService.approveUsers(userId);
    }

}
