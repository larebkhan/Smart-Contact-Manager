package com.smart.Controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;



@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @ModelAttribute
    public void addCommonData(Model m, Principal principal){
        String userName = principal.getName();//fetches the unique id (email in our case) of the currently authenticated user
        System.out.println("USERNAME "+ userName);
        User user = userRepository.getUserByUserName(userName);//user repository uses this userName to fetch the details of the user form the database
        System.out.println("USER "+ user);
        m.addAttribute("user", user);
    }




    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal){
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    //open add form handler
    @RequestMapping("add-contact")
    public String openAdContactForm(Model model){
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    //processing add contact form
    
    @PostMapping("/process-contact")
    public String processContact(@Valid @ModelAttribute Contact contact, 
        BindingResult bindingResult,
        @RequestParam("image")MultipartFile file,
        Principal principal,HttpSession session)
        {

        try{
        String name = principal.getName();

        User user = userRepository.getUserByUserName(name);

        //processing and uploading file
        if(file.isEmpty()){
            //if the file is empty the try our message
            System.out.println("File is empty");
            contact.setImage("contact.png");
            
        }else{
            //file the file to folder and update the name to contact
            contact.setImage(file.getOriginalFilename());
            File saveFile = new ClassPathResource("static/img").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("file is uploaded");
        }
        

        user.getContacts().add(contact);//list me contact add krne k liye
        contact.setUser(user);//contact mei user save krne k liye(bi derctional mapping hai kyuki)
        this.userRepository.save(user);//user save krne k liye

        System.out.println("User "+ contact);

        System.out.println("added to data base");

        session.setAttribute("message", new Message("Your contact is added !! Add more..", "success"));


        }catch(Exception e){
            System.out.println("ERROR" + e.getMessage());
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong !! Try again..", "danger"));
        }

        return "normal/add_contact_form";
    }



    //show contact handler
    @RequestMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page")Integer page ,Model m,Principal principal){
        m.addAttribute("title","Show Contacts" );
        //contact ki list ko bhejna hai
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        Pageable of = PageRequest.of(page, 3);
        Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(),of);
        m.addAttribute("contacts", contacts);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", contacts.getTotalPages());
        return "normal/show_contacts";
    }


    //showing particular contact details
    @RequestMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId")Integer cId,Model model,Principal principal){
        System.out.println("cId "+cId);
        Optional<Contact> byId = this.contactRepository.findById(cId);
        Contact contact = byId.get();

        //
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        if(user.getId()==contact.getUser().getId()){
            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getName());
        }

        return "normal/contact_detail";
    }


    //delete contact handler
    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId")Integer cId, Model m,Principal principal, HttpSession session){
        Optional<Contact> byId = this.contactRepository.findById(cId);
        Contact contact = byId.get();
        

        //
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        if(user.getId()==contact.getUser().getId()){
            //remove the cascade link of contact from user
            //contact.setUser(null);
            //remove photo
            contact.getImage();
            this.contactRepository.deleteByIdCustom(cId);
            session.setAttribute("message", new Message("Contact delete successfully", "success"));
        }
        return "redirect:/user/show-contacts/0";
    }
}
