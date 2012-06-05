bldrgenratr
===========

BldrGenratr is a tool to automatically create domain class builders with a "fluent" API
It comes as a maven plugin to process packages of "lite" domain classes and generates A "builder" automatically.


Then you can make test fixtures like this.....

AwesomeTest.java
---------------
    ....
        @Test
        public void testSomethingWithFixture()
        {
            User u = UserBldr.nu()
                         .withEmail("me@mycorp.com")
                         .withFullName("The Dude")
                     .get();

            someRepo.save(u);
            
            
            /*
             *  you can also nest Bldrs naturally, as follows.
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
        ....
    

It doesn't need annotations, so you could 'Bldr' on other people's domain classes.  
You can keep your Domain classes anemic and have them all nice and JavaBean compliant (I.e. void setters), 
but still have a Builder waiting patiently for you, to unleash some domain bldr-fu when you need fixtures - and fast!

It's automatic so you can fiddle with the domain class and the bldr is regenerated, erm hopefully - it's a small manual step at present.
so you don't need to maintain another source file :)


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
    

That looks boring, and you just can't be bothered to hack out more rafts of textfixtures, using this long winded approach.
Man, I wish I could just use groovy :) but if that's not the case, just plugin some bldrgenratr


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
