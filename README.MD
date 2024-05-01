I just want to rewrite NFSSoundtrack.com to handle some stuff better than in 2012.

# How to deploy locally

## Fully via Docker 

1. Download the repo
   1. If you have Git installed on Windows / Linux, use "git clone" command
   2. Otherwise, simply download the ZIP file with repository

![](./src/main/resources/readme/docker/01.png)

2. Unpack the ZIP somewhere and check the content

![](./src/main/resources/readme/docker/02.PNG)

3. Download Docker Desktop from https://www.docker.com/products/docker-desktop/

![](./src/main/resources/readme/docker/03.PNG)

4. Install Docker Desktop

![](./src/main/resources/readme/docker/04.PNG)

5. Restart PC as requested

![](./src/main/resources/readme/docker/05.PNG)

6. Docker Desktop should automatically start with Windows (you can alter it via Task Manager)

![](./src/main/resources/readme/docker/06.PNG)

7. Sign up if you wish, but you probably won't like to

![](./src/main/resources/readme/docker/07.PNG)

8. You will see this message so keep waiting

![](./src/main/resources/readme/docker/08.PNG)

9. Then you should see such view

![](./src/main/resources/readme/docker/11.PNG)

10. Open the command line and go to folder with repository - type "docker" to see that it is identified in command line

![](./src/main/resources/readme/docker/09.PNG)

11. Run the command:

**docker compose -f docker-compose.yml -p nfssoundtrack_20 up -d**

![](./src/main/resources/readme/docker/12.PNG)

12. You should see after some seconds or maybe couple of minutes this status

![](./src/main/resources/readme/docker/13.PNG)

13. And it will look like this in Docker Desktop

![](./src/main/resources/readme/docker/115.PNG)

14. Wait for database to be ready - first it is trying to import data, you can see the "sql" line as indicated by red marking

![](./src/main/resources/readme/docker/14.PNG)

15. After few minutes it should indicate that it is ready

![](./src/main/resources/readme/docker/15.PNG)

16. Then the second container should in few seconds start (the one that was 'created' during database import)

![](./src/main/resources/readme/docker/16.PNG)

17. After some time you should see this message in the command line inside Docker Desktop

![](./src/main/resources/readme/docker/17.PNG)

18. Then go to https://localhost:8083 - browser will tell you this is not secure, but accept the risk

![](./src/main/resources/readme/docker/18.PNG)

19. You should see the main page and other stuff working

![](./src/main/resources/readme/docker/19.PNG)
![](./src/main/resources/readme/docker/21.PNG)

## Developing / debugging with IntelliJ
1. Install IntelliJ (I use Community Edition) from https://jetbrains.com/idea/download

![](./src/main/resources/readme/intellij/30.PNG)

2. I assume you have the repository downloaded already, so launch IntelliJ and open folder with repository

![](./src/main/resources/readme/intellij/31.PNG)
![](./src/main/resources/readme/intellij/32.PNG)

3. Trust the project

![](./src/main/resources/readme/intellij/33.PNG)

4. It might take some time but things like JDK should be downloaded automatically - check the progress of background tasks visible in bottom right corner

![](./src/main/resources/readme/intellij/34.PNG)

5. Go to Project Structure and double check that Java 17 is used

![](./src/main/resources/readme/intellij/35.PNG)

6. Install Docker plugin to simplify development and deployment a bit

![](./src/main/resources/readme/intellij/36.PNG)
![](./src/main/resources/readme/intellij/37.PNG)

7. Create two configurations: one for mysqldb (so that it is just starting service and not recreating databsae again) and for the app itself

![](./src/main/resources/readme/intellij/42.png)

8. Database service config is like this

![](./src/main/resources/readme/intellij/47.PNG)

9. App config is like this

![](./src/main/resources/readme/intellij/43.PNG)

10. Run only the "mysqldb" from docker-compose

![](./src/main/resources/readme/intellij/38.PNG)

11. Check in bottom panel that mysqldb service is started

![](./src/main/resources/readme/intellij/39.PNG)

12. Double check in Docker Desktop too

![](./src/main/resources/readme/intellij/40.PNG)

13. Start the app configuration and look for messages

![](./src/main/resources/readme/intellij/44.PNG)

14. This message should be always shown and indicate that start was good

![](./src/main/resources/readme/intellij/45.PNG)

15. If you want to run all in Docker, then just hit this double-start button in docker-cmpose.yml

![](./src/main/resources/readme/intellij/46.PNG)

16. Hopefully you should be able to go to https://localhost:8082/ and see the website running

## Skip certificate questions

1. If you get prompts in browser about certificate, then simply go to "keystore" folder and install the self-signed certificate

![](./src/main/resources/readme/51.png)

2. Select "Local PC"

![](./src/main/resources/readme/52.PNG)

3. Continue with import

![](./src/main/resources/readme/53.PNG)

4. Password can be found in "application.properties" - yourkeystorepass

![](./src/main/resources/readme/54.PNG)

5. Let Windows automatically store it

![](./src/main/resources/readme/55.PNG)

6. Finish the import and try to access localhost again

![](./src/main/resources/readme/56.PNG)

# Generating user password

If you need to have admin user, then I assume you already had to configure IDE to work with this code.
Therefore, recommended is to setup following startup configuration
![](./src/main/resources/readme/password/pass1.PNG)

First argument simply indicates that we want to create new user. Second parameter is user login, third is user password and final parameter is to run it on "dev" default.properties, probably based on local database. You can switch to "prod" or "docker".

Then once you run the project, this code should get triggered in Application.java
![](./src/main/resources/readme/password/pass2.PNG)
After the program starts and you see "successful?" message in the log. Double check then if user is then visible in "user" table.
![](./src/main/resources/readme/password/pass3.PNG)
Then you should be able to go to "admin login" page - https://localhost:8082/login - enter your credentials and be able to work on things as admin.
![](./src/main/resources/readme/password/pass4.PNG)
![](./src/main/resources/readme/password/pass5.PNG)
Then you can go to "Manage" and be able to edit various things in database
![](./src/main/resources/readme/password/pass6.PNG)