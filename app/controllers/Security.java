package controllers;
 
public class Security extends Secure.Security {
	
    static boolean authenticate(String username, String password) {
        
        if("admin".equals(username)) {
            return true;
        } else {
            return false;
        }
    }
    
    static boolean check(String profile) {
        
        if("admin".equals(profile)) {
            return true;
        } else {
            return false;
        }
    }
    
}
