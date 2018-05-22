package com.csye6225.spring2018.controller;

import com.csye6225.spring2018.UserRepository;
import com.csye6225.spring2018.User;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;


@Controller
public class UserController {
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Value("${image.default.link}")
    private String defaultLocation;

    @PostMapping(value="/loginUser", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String login(@RequestBody String js, HttpServletRequest request, HttpServletResponse response) throws ParseException, IOException {
        JSONParser parser = new JSONParser();
        HashMap<String, String> map = (HashMap<String, String>) parser.parse(js);
        for (User u : userRepository.findAll()) {
            if (u.getEmail().equals(map.get("email")) && BCrypt.checkpw(map.get("password"), u.getPassword())) {
                HttpSession session = request.getSession(true);
                session.setAttribute("email", map.get("email"));
                System.out.println("Found!");
                JsonObject json = new JsonObject();
                json.addProperty("response", "Login Success!");
                return json.toString();
            }
        }
        System.out.println("User Not found");
        return new JsonObject().toString();
    }

    @PostMapping(value = "/registerUser", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String createUser(@RequestBody String json, HttpServletResponse response) throws ParseException, IOException {
        JSONParser parser = new JSONParser();
        HashMap<String, String> map = (HashMap<String, String>) parser.parse(json);
        JsonObject js = new JsonObject();
        for (User u : userRepository.findAll()){
            if (u.getEmail().equals(map.get("email"))) {
                logger.info("User already exists.");
                js.addProperty("message","User already exists! Please sign in.");
                return js.toString();
            }
        }
        logger.info("Registering user account with information");
        User newUser = new User();
        newUser.setFirstname(String.valueOf(map.get("firstname")));
        newUser.setLastname(String.valueOf(map.get("lastname")));
        newUser.setAbout(String.valueOf(map.get("about")));
        String destination = defaultLocation;
        newUser.setPhoto_location(destination);
        newUser.setEmail(String.valueOf(map.get("email")));
        newUser.setPassword(BCrypt.hashpw(String.valueOf(map.get("password")),BCrypt.gensalt()));
        userRepository.save(newUser);
        js.addProperty("message", "User added successfully!");
        return js.toString();
    }

    @PostMapping(value = "/search", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String searchUser(@RequestBody String json) throws ParseException {
        JSONParser parser = new JSONParser();
        HashMap<String, String> map = (HashMap<String, String>) parser.parse(json);
        JsonArray jsonArray = new JsonArray();
        for (User u : userRepository.findAll()){
            if(u.getFirstname().contains(map.get("search")) || u.getLastname().contains(map.get("search"))){
                String fullname = u.getFirstname() + " " + u.getLastname();
                jsonArray.add(fullname);
            }
        }
        return jsonArray.toString();
    }

}

