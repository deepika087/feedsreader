package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.datamodels.Messages;

@Controller
@RequestMapping("/ping")
public class HealthController {
	  private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	  @RequestMapping(method = RequestMethod.GET)
	  public @ResponseBody Messages pingApp() {
		  return new Messages(200, "Feed reader app is up and running ! !");
	  }
}
