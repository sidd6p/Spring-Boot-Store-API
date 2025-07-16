package com.github.sidd6p.store.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @Controller is used for controllers that return views (e.g., HTML via Thymeleaf),
// while @RestController returns data (e.g., JSON) directly for REST APIs.
@RestController
public class MessageController {

    // This method returns only a plain message (not HTML markup), unlike Index in HomeController which returns a view.
    // Reason: @RestController automatically serializes return values to the HTTP response body, suitable for APIs, while @Controller is used for rendering views.
   @RequestMapping("/v1/message")
   public String getMessageV1() {
       return "Hello, Earth!";
   }

    // This method returns a Message object, which will be serialized to JSON in the HTTP response.
    // Java serialization here is handled by Spring Boot using Jackson (by default), converting the Message object to a JSON structure like {"message": "Hello, Earth!"}.
    @RequestMapping("/v2/message")
    public Message getMessageV2() {
        return new Message("Hello, Earth!");
    }

}
