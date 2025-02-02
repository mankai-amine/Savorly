# Savorly – Web app for food lovers

# Getting Started/Demo Information

Welcome! If you'd like to see out our application hosted on Railway, please visit [Savorly.xyz](https://savorly-frontend-production.up.railway.app/). This is a full stack web app where users can interact with others' recipes and manage their own.  
We hope you enjoy our app! Feel free to reach out if you have any questions.

# Technologies
Our technology stack for making this app consisted of: Java Spring Boot, PostgreSQL, React, TypeScript, Bootstrap, OpenAI API, and AWS.

# Functionalities
•	Registration  
•	Login/logout   
•	Browse, rate, print, share, and comment on recipes 
•	Save to and remove from favorites 
•	Create, edit, and delete recipes
•	AI-powered search tool helps find the best recipes based on keywords
 
# How to Run
- Clone the Repository
- For the frontend: open the terminal in VSCode, “cd” into the frontend folder, and run “npm install” to get the required packages
- For the backend: open the project in IntelliJ and let it automatically install the packages 
- Run both backend and frontend 

# Frontend environment variables:
VITE_API_URL
VITE_UPLOAD_URL

# Backend environment variables:
For authentication:  
JWT_EXPIRATION (int)  
JWS_SECRET(length should be longer than 32 for SHA-256)  

For PostgreSQL (with Pgvector plug-in):  
POSTGRES_HOST  
POSTGRES_PORT  
POSTGRES_DB  
DB_USER=YOUR_DB_USER  
DB_PASSWORD=YOUR_DB_PASSWORD  

For AWS S3 storage service:  
AWS_ACCESS_KEY_ID  
AWS_SECRET_ACCESS_KEY  
S3_BUCKET_NAME  

For OpenAi Embedding model accessing:
spring.ai.openai.embedding.api-key(mandatory)  
spring.ai.openai.api-key(optional)  

For email sending:  
spring_mail_host  
spring_mail_name  
spring_mail_password  
spring_mail_port  
spring_mail_properties_mail_smtp_auth (default value true)  
spring_mail_properties_mail_smtp_starttls_enable (default value true)  

For CORS:  
ALLOWED_CORS_URL  

# database setup:
The section 2.2 in our Documentation file (in the root of this project) explains in details how to set up the database correctly.
