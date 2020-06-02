package org.onegang.access.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RouteController {
	
    @RequestMapping(value = "/page/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/";
    }
    
    @RequestMapping(value = "/page/**/{path:[^\\.]*}")
    public String redirectLayerTwo() {
        return "forward:/";
    }
    
}
