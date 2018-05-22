package com.csye6225.spring2018.controller;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.*;
import com.csye6225.spring2018.User;
import com.csye6225.spring2018.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;

@Controller
public class IndexController {

  @Autowired
  private UserRepository userRepository;

  private final static Logger logger = LoggerFactory.getLogger(IndexController.class);


  @GetMapping("/")
  public String index() {
    logger.info("Loading home page.");
    return "index";
  }

  @GetMapping("/register")
  public String getRegister() {
    logger.info("Loading registration page.");
    return "register";
  }

  @GetMapping("/login")
  public String login() {
    logger.info("Loading login page.");
    return "login";
  }

  @PostMapping("/login")
  public String loginPost(){
    logger.info("Logging in after successful authentication");
    return "login";
  }

  @GetMapping("/logout")
  public String logoutPage(HttpServletRequest request){
    logger.info("Loading index page after logging out.");
    request.getSession().invalidate();
    return "index";
  }

  @PostMapping("/loginSuccess")
  public String userSucceess(HttpServletRequest request, @RequestParam String email, @RequestParam String password){
    for (User u : userRepository.findAll()) {
      if (u.getEmail().equals(email) && BCrypt.checkpw(password, u.getPassword())) {
        logger.info("Login Success!");
        HttpSession session = request.getSession(true);
        session.setAttribute("firstname", u.getFirstname());
        session.setAttribute("lastname", u.getLastname());
        session.setAttribute("fileUrl",u.getPhoto_location());
        session.setAttribute("about",u.getAbout());
        session.setAttribute("email",u.getEmail());
        return "UserHome";
      }
    }
    logger.info("User not found!");
    return "login";
  }

  @GetMapping("/loginSuccess")
  public String userSuccessGet(HttpServletRequest request){
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("fileUrl") == null){
      logger.info("Redirecting to login as no session found");
      return "login";
    }
    logger.info("Loading user homepage since session is present");
    return "UserHome";
  }

  @PostMapping("/searchResults")
  public String searchResults(HttpServletRequest request, @RequestParam String search){
      ArrayList<String> searchResults = new ArrayList<>();
      for (User u : userRepository.findAll()){
          if (u.getLastname().contains(search) || u.getFirstname().contains(search)){
              String fullname = u.getFirstname() + " " + u.getLastname();
              searchResults.add(fullname);
          }
      }
      request.setAttribute("searchResults", searchResults);
      return "index";
  }

  @PostMapping("/profile")
    public String viewProfile(HttpServletRequest request, @RequestParam String username){
      logger.info("Loading profile for selected user");
      String[] names = username.split("\\s+");
      for (User u : userRepository.findAll()){
          if (u.getFirstname().equals(names[0]) && u.getLastname().equals(names[1])){
              HttpSession session = request.getSession(true);
              session.setAttribute("firstname",u.getFirstname());
              session.setAttribute("lastname", u.getLastname());
              session.setAttribute("about", u.getAbout());
              return "userProfile";
          }
      }
      return "error";
  }

  @GetMapping("/userProfile")
  public String getProfile(HttpServletRequest request) {

    if (request.getSession() == null) {
      logger.info("No such user found");
      return "index";
    }
    else {
      return "userProfile";
    }
  }

  @GetMapping("/editProfile")
  public String getEditProfile(HttpServletRequest request){
    HttpSession session = request.getSession(false);
    if (session==null){
      return "index";
    }
    return "editProfile";
  }

  @GetMapping("/photo")
  public ResponseEntity<byte[]> getImage(HttpServletRequest request) throws IOException {

    HttpSession session = request.getSession(false);
    String filename = String.valueOf(session.getAttribute("fileUrl"));

    InputStream inputImage = new FileInputStream(filename);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[512];
    int l = inputImage.read(buffer);
    while(l >= 0) {
      outputStream.write(buffer, 0, l);
      l = inputImage.read(buffer);
    }
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "image/png");
    return new ResponseEntity<byte[]>(outputStream.toByteArray(), headers, HttpStatus.OK);
  }

  @PostMapping("/saveChanges")
  public String updateProfile(@RequestParam String firstname, @RequestParam String lastname, @RequestParam String about, HttpServletRequest request){

    HttpSession session = request.getSession(false);
    for (User newUser : userRepository.findAll()){
      if (newUser.getEmail().equals(String.valueOf(session.getAttribute("email")))){
        newUser.setFirstname(firstname);
        newUser.setLastname(lastname);
        newUser.setAbout(about);
        userRepository.save(newUser);
      }
    }
    return "editProfile";
  }

  @Value("${topicArn}")
  private String topicArn;

  @PostMapping("resetPassword")
  public String resetPassword(@RequestParam String email){

      AmazonSNS amazonSNS = AmazonSNSClientBuilder.defaultClient();

      String resetEmail = email;
      PublishRequest publishRequest = new PublishRequest(topicArn, resetEmail);
      PublishResult publishResult = amazonSNS.publish(publishRequest);
      System.out.println(publishResult);
      return "index";
  }

  @GetMapping("resetPassword")
  public String getResetPassword(){
      return "passwordReset";
  }
}
