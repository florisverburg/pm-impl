#Peer Matching Implementation

This is the Implementation for the Peer Matching system of the Bachelor project TI3800 of the Univeristy of Technology in Delft.

##Settings
To be able to run this application you need specific variables in your environment.
These settings make it able to connect both the Database and the Linkedin API.
Below this the environment variables that can be set are listed.


###Default
- APPLICATION_SECRET: This a secret string that is used to encrypt several critical parts of data. (default is a predefined value)

###Database
- DATABASE_DRIVER_DB: This is the driver which is used to connect to the database. (default is "org.h2.Driver")
- DATABASE_URL_DB: This is the url which the database has to connect to. (no default and is required)
- DATABASE_USERNAME_DB: This is the username which is used to connect to the database. (no default and not required)
- DATABASE_PASSWORD_DB: This is the password which is used to connect to the database. (no default and not required)

###Linkedin
- LINKEDIN_CLIENT_ID: This is the Linkedin API key which can be received from Linkedin. (no default and required)
- LINKEDIN_CLIENT_SECRET: This is the Linkedin API secret which can be received from Linkedin. (no default and required)


##Contributors
- Marijn Goedegebure
- Floris Verburg
- Freek van Tienen
