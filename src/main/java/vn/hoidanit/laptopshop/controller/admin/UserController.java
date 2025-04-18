package vn.hoidanit.laptopshop.controller.admin;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.UploadService;
import vn.hoidanit.laptopshop.service.UserService;



@Controller
public class UserController {

        private final UserService userService;
        private final UploadService uploadService;
        private final PasswordEncoder passwordEncoder;
  
    public UserController(UserService userService,UploadService uploadService,PasswordEncoder passwordEncoder ) {
        this.userService = userService;
        this.passwordEncoder=passwordEncoder;
        this.uploadService=uploadService;
       
    }
    @RequestMapping("/")
    public String getHomePage(Model model){
       List<User>arrUsers=this.userService.getAllUsersByEmail("lozsos123@gmail.com");
       System.out.println(arrUsers);
        model.addAttribute("mount","test");
        model.addAttribute("dung","From controller with model");
        return "/client/homepage/show";
    }

    @RequestMapping("/admin/user")
    public String getUserPage(Model model,@RequestParam("page")Optional<String>pageOptional){
         int page=1;
        try{
            if(pageOptional.isPresent()){
                //convert from String to int
                page=Integer.parseInt(pageOptional.get());
            }
            else{
                //page=1
            }
        }
        catch(Exception e){
            //page=1
            //TODoO: handle exception
        }
        Pageable pageable =PageRequest.of(page-1,2);
         Page<User>users=this.userService.fetchAllUsers(pageable);
        List<User>listUsers=users.getContent();
        model.addAttribute("users1",listUsers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        return "admin/user/show";
    }
 
    @GetMapping("/admin/user/create")
    public String getCreateUserPage(Model model){
        model.addAttribute("newUser", new User());
        return "admin/user/create";
    }
 
    @PostMapping("/admin/user/create")
    public String createUserPage(Model model, 
    @ModelAttribute("newUser")@Valid User dungmount,
    BindingResult newUserBindingResult,  
    @RequestParam("dungmountFile") MultipartFile file){
           List<FieldError> errors = newUserBindingResult.getFieldErrors();
    for (FieldError error : errors ) {
        System.out.println (error.getField() + " - " + error.getDefaultMessage());
    }
    if(newUserBindingResult.hasErrors()){
        return"/admin/user/create";
    }


     String avatar = this.uploadService.handleSaveUploadFile(file,"avatar");
     String hashPassword=this.passwordEncoder.encode(dungmount.getPassword());
        dungmount.setAvatar(avatar);
        dungmount.setPassword(hashPassword);
        dungmount.setRole(this.userService.getRoleByName(dungmount.getRole().getName()));


       this.userService.handleSaveUser(dungmount);
        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user/{id}")
    public String getDetailUserPage(Model model,@PathVariable long id){
       User user =this.userService.getUserById(id);
        model.addAttribute("id", id);
        model.addAttribute("user1", user);
        return "admin/user/detail";
    }

    @RequestMapping("/admin/user/update/{id}")
    public String getUpdateUser(Model model, @PathVariable long id){
        User currentUser= this.userService.getUserById(id);
        model.addAttribute("newUser", currentUser);
        return "admin/user/update";
    }
    @PostMapping("/admin/user/update")
    public String postUpdateUser(Model model,@ModelAttribute("newUser") User dungmount,
    @RequestParam("dungmountFile") MultipartFile file ){
       User currentUser =this.userService.getUserById(dungmount.getId());
       if(currentUser!=null){
        // currentUser.setEmail(dungmount.getEmail());
        currentUser.setAddress(dungmount.getAddress());
        currentUser.setPhone(dungmount.getPhone());
        currentUser.setFullName(dungmount.getFullName());
        String image = this.uploadService.handleSaveUploadFile(file,"avatar");
        currentUser.setAvatar(image);
       }
       this.userService.handleSaveUser(currentUser);
        return "redirect:/admin/user";
    }
    @GetMapping("/admin/user/delete/{id}")
    public String deleteUserPage(Model model,@PathVariable long id){
        model.addAttribute("id", id);  
        User user=new User();
        user.setId(id);
        model.addAttribute("newUser",user);
        return "admin/user/delete";
    }
    @PostMapping("/admin/user/delete")
    public String postDeleteUser(Model model,@ModelAttribute("newUser") User dungmount ){
      this.userService.deleteAUser(dungmount.getId());
        return "redirect:/admin/user";
    }

}


// @RestController
// public class UserController {

//     private UserService userService;
//     public UserController(UserService userService) {
//         this.userService = userService;
//     }
//     @GetMapping("/")
    
//     public String getHomePage(){
//         return this.userService.handleHello();
//     }
// }

