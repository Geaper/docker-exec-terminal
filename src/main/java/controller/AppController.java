package controller;

import model.Docker;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppController {

    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView model = new ModelAndView("index");
        model.addObject("containers", Docker.getContainers());
        return model;
    }
}
