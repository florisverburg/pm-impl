#Peer Matching Implementation

This is the Implementation for the Peer Matching system of the Bachelor project TI3800 of the Univeristy of Technology in Delft.

##Settings
To be able to run this application you need specific variables in your environment.
These settings make it able to connect both the Database and the Linkedin API.
Below this the environment variables that can be set are listed.


###Default
- APPLICATION_SECRET  
*A secret string that is used to encrypt several critical parts of data. (default is a predefined value)*

###Database
- DATABASE_DRIVER_DB  
*The driver which is used to connect to the database. (default is "org.h2.Driver")*
- DATABASE_URL_DB  
*The url used for connecting to the database. (no default and is required)*
- DATABASE_USERNAME_DB  
*The username which is used as authentication for the database. (no default and not required)*
- DATABASE_PASSWORD_DB  
*The password which is used as authentication for the database. (no default and not required)*

###Email
- EMAIL_HOST  
*This is the email SMTP host for sending emails from the site. (no default and is required)*
- EMAIL_PORT  
*This is the email SMTP server connection port. (default is 465)*
- EMAIL_SSL  
*Whether to use SSL when connecting to the SMTP server. (default is yes)*
- EMAIL_USERNAME  
*The username used for connecting to the SMTP server. (no default and is required)*
- EMAIL_PASSWORD  
*The password used for connecting to the SMTP server. (no default and is required)*
- EMAIL_ADDRESS  
*The email address. (no default and is required)*

###Linkedin
- LINKEDIN_CLIENT_ID  
*This is the Linkedin API key which can be received from Linkedin. (no default and required)*
- LINKEDIN_CLIENT_SECRET  
*This is the Linkedin API secret which can be received from Linkedin. (no default and required)*


##Contributors
- Marijn Goedegebure
- Floris Verburg
- Freek van Tienen
