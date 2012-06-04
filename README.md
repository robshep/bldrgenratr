bldrgenratr
===========

BldrGenratr is a tool to automatically create domain class builders with a "fluent" API

Say you have this the following Java domain class....

User.java
---------

    package com.mycompany.mypackage;
    
    public class User
    {
      private String email;
      private String fullName;
      
      <getters...>
      <setters...>
    }
    

BldrGenratr will make the following....

_bldr.UserBldr.java
-------------------

    package com.mycompany.mypackage._bldr;
    
    /**
    * Created by bldr
    */
    public class UserBldr
    {
      protected com.f5space.mainstack.entity.User ting;
	   
      public UserBldr withEmail(java.lang.String arg0) { 
        ting.setEmail(arg0); 
        return this; 
      }
		
      public UserBldr withFullName(java.lang.String arg0) { 
        ting.setFullName(arg0); 
        return this; 
      }
    	  	
      public static UserBldr nu() {
        return new UserBldr();
      }
    	
      private UserBldr() {
       ting = new com.mycompany.mypackage.User();
      }
    	
      public com.f5space.mainstack.entity.User get() {
        return ting;
      }
    }
    
Then you can do this....

AwesomeApp.java
---------------

    package com.mycompany.mypackage.app;
    
    public class MyApp()
    {
        public static void main(String[] args)
        {
            User u = UserBldr.nu()
                         .withEmail("me@mycorp.com")
                         .withFullName("The Dude")
                     .get();
            
            
            // this next implementation will include sub-Bldrs
            
            //e.g.
            
            User u = UserBldr.nu()
                         .withEmail("me@mycorp.com")
                         .withFullName("The Dude")
                         .withAddress( AddressBldr.nu()
                                         .withLine1("1 my street")
                                         .withLine2("My Town")
                                         .withZip("ABC 123") )
                     .get();
            
            
 

