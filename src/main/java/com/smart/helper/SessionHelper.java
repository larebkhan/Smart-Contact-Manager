package com.smart.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;
@Component
public class SessionHelper {
    public void removeMessageFromSession(){
        try {
             //message rkha hai seesion mei aur session se nikalne k liye uska 
             //object chaiye
            HttpSession session =  ((ServletRequestAttributes)RequestContextHolder
            .getRequestAttributes()).getRequest().getSession();
            //RequestContextHolder.getRequestAttributes(): This part fetches information
            // about the current request being processed.

            //(ServletRequestAttributes) ...: This converts the generic request 
            //attributes to specific servlet request attributes (related to handling web requests).

            //.getRequest(): This gets the actual request being made by the user.

            // .getSession(): This retrieves the user's session associated with the 
            //request. Think of a session as a way to store information about a user as
            // they interact with a web application.

            //session.removeAttribute("message"): This line removes a specific piece of
            // information stored in the user's session. In this case, it removes an
            // attribute named "message" from the session. Attributes are like variables
            // that can store data for the duration of the user's visit to the website.
            session.removeAttribute("message");
             //RequestContextHolder k
        } catch (Exception e) {
            e.printStackTrace();
            
        }
    }
}
