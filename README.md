bldrgenratr
===========

BldrGenratr is a tool to automatically create domain class builders with a "fluent" API
It comes as a maven plugin to process packages of "lite" domain classes and make builder code 
automatically.

It's automatic so you can fiddle with the domain class and the bldr is regenerated, so you don't need to maintain another source file :)

Then you can make test fixtures like this.....


AwesomeTest.java
---------------

    package com.mycompany.mypackage.app;
    
    public class AwesomeTest()
    {
        @Test
        public void testSomethingWithFixture()
        {
            User u = UserBldr.nu()
                         .withEmail("me@mycorp.com")
                         .withFullName("The Dude")
                     .get();

            someRepo.save(u);
            
            
            /*
             *  the next implementation will include sub-Bldrs
             *  ... as follows.
             */ 
            
            User u2 = UserBldr.nu()
                         .withEmail("you@mycorp.com")
                         .withFullName("Nuva Dude")
                         .withAddress( AddressBldr.nu()
                                         .withLine1("1 my street")
                                         .withLine2("My Town")
                                         .withZip("ABC 123") )
                     .get();

            someRepo.save(u2);
        }
    }




So say you have this the following Java domain class....

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
      protected com.mycompany.mypackage.User ting;
       
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
