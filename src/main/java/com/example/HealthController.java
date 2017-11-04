package com.example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.datamodels.Messages;

@Controller
@RequestMapping("/ping")
public class HealthController {
	
	  @RequestMapping(method = RequestMethod.GET)
	  public @ResponseBody Messages pingApp() {
		  return new Messages(200, "Feed reader app is up and running ! !");
	  }
}
