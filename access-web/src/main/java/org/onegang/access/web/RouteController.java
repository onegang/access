package org.onegang.access.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RouteController {
	
    @RequestMapping(value = "/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/";
    }
    
    @RequestMapping(value = "/**/{path:[^\\.]*}")
    public String redirectLayerTwo() {
        return "forward:/";
    }
    
}
