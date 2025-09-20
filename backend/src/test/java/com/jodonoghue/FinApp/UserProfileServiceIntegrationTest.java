package com.jodonoghue.FinApp;

import org.junit.jupiter.api.BeforeAll;
import com.holistic.user.service.UserService;

import jakarta.validation.constraints.AssertTrue;

import com.holistic.user.model.User;

public class UserProfileServiceIntegrationTest {

    @BeforeAll
    public void initial(){
        UserService userService;
    }

    public void createUser(){
        User user= new User( 1111L, "subedei7@gmail.com", "Jim","bob");

        UserService initialDb = userService.getAllUsers;
        userService.createUser(user);

        UserService finalDb = userService.getAllUsers;
        Assert(finalDb= initialDb+1);
    }

    public void findUser(){
        User user= new User( 1111L, "subedei7@gmail.com", "Jim","bob");
        userService.createUser(user);

        UserService userGot = userService.getUserById(1111L);
        Assert(user.getId() = userGot.getId());
    }

    public void getUsers(){
        User user1= new User( 1111L, "subedei7@gmail.com", "Jim","bob");
        User user2= new User( 2222L, "sub@gmail.com", "Jack","Jill");
      
        userService.createUser(user1);
        userService.createUser(user2);

        UserService users = userService.getAllUsers;
        Assert(users[0].getId() = user1.getId());
        Assert(users[1].getId() = user2.getId());
    }
}
