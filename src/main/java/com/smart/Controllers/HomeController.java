package com.smart.Controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;




@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String home(Model model){
        model.addAttribute("title", "Home - Smart Contact Manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model){
        model.addAttribute("about", "Home - Smart Contact Manager");
        return "about";
    }

    @RequestMapping("/signup")
    public String signup(Model model){
        model.addAttribute("signup", "Home - Smart Contact Manager");
        model.addAttribute("user", new User());
        //here we are sending inside our model blank objest which is mapped by keyword user
        //new User() is specifiend to make a blank object now this object gets filled with values
        //when do_register is fired up
        return "signup";
    }

    //handler for registering user
    @PostMapping("/do_register")
    public String registerUser(@Valid @ModelAttribute("user")User user,BindingResult result1, @RequestParam(value = "agreement",defaultValue = "false")boolean agreement, Model model, HttpSession session){

        
        try {
            if(!agreement){
            System.out.println("You have not agreed to terms a nd condtions");
            throw new Exception("You have not agreed to terms and condtions");
        }
        if(result1.hasErrors()){
            System.out.println("Error "+ result1.toString());
            model.addAttribute("user", user);
            return "signup";
        }
        user.setRole("ROLE_USER");
        user.setEnabled(true);
        user.setImageUrl("default.png");
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        System.out.println("Agreement " + agreement);
        System.out.println("User "+ user);


        User result =  this.userRepository.save(user);

        model.addAttribute("user", new User());
        //this is for the case when user has entered and done evrything right then when the return 
        //signup page gets loaded again after hitting the submit button a new user object is created 
        //so that new details can be filled inside it

        session.setAttribute("message", new Message("Sucessfully Registered", "alert-success"));
        return "signup";
        
        //this does that the data coming from the @modelattribute goes back by this and the return 
        //page shows pre filled data 
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute("message", new Message("Something went wrong!"+e.getMessage(), "alert-danger"));
            // TODO: handle exception
            return "signup";
        }
    }


    //handler for customlogin
    @GetMapping("/signin")
    public String customLogin(Model model){
        model.addAttribute("title", "Login Page");
        return "login";
    }
    
}
