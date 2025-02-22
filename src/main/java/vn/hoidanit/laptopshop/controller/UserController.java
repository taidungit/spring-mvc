package vn.hoidanit.laptopshop.controller;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.UserService;



@Controller
public class UserController {

        private final UserService userService;
  
    public UserController(UserService userService ) {
        this.userService = userService;
       
    }
    @RequestMapping("/")
    public String getHomePage(Model model){
       
        model.addAttribute("mount","test");
        model.addAttribute("dung","From controller with model");
        return "hello";
    }

    @RequestMapping("/admin/user")
    public String getUserPage(Model model){
        List<User>users=this.userService.getAllUsers();
        model.addAttribute("users1",users);
        System.out.println(">>> Check users: "+users);
        return "admin/user/table-user";
    }
    @RequestMapping("/admin/user/{id}")
    public String getDetailUserPage(Model model,@PathVariable long id){
        System.out.println("check path id = "+id);
        model.addAttribute("id", id);
        return "admin/user/show";
    }
    @RequestMapping("/admin/user/create")
    public String getCreateUserPage(Model model){
        model.addAttribute("newUser", new User());
        return "admin/user/create";
    }
    @RequestMapping(value = "/admin/user/create", method = RequestMethod.POST)
    public String createUserPage(Model model, @ModelAttribute("newUser") User dungmount){
       System.out.println("Run here: "+dungmount);
       this.userService.handleSaveUser(dungmount);
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

