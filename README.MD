I just want to rewrite NFSSoundtrack.com to handle some stuff better than 11 years ago.

# How to deploy locally

## Fully via Docker 

1. Download the repo
   1. If you have Git installed on Windows / Linux, use "git clone" command
   2. Otherwise, simply download the ZIP file with repository
![](./src/main/resources/readme/docker/01.png)
2. Unpack the ZIP somewhere and check the content
![](./src/main/resources/readme/docker/02.png)
3. Download Docker Desktop from https://www.docker.com/products/docker-desktop/
![](./src/main/resources/readme/docker/03.png)
4. Install Docker Desktop
![](./src/main/resources/readme/docker/04.png)
5. Restart PC as requested
![](./src/main/resources/readme/docker/05.png)
6. Docker Desktop should automatically start with Windows (you can alter it via Task Manager)
![](./src/main/resources/readme/docker/06.png)
7. Sign up if you wish, but you probably won't like to
![](./src/main/resources/readme/docker/07.png)
8. You will see this message so keep waiting
![](./src/main/resources/readme/docker/08.png)
9. Then you should see such view
![](./src/main/resources/readme/docker/11.png)
10. Open the command line and go to folder with repository - type "docker" to see that it is identified in command line
   ![](./src/main/resources/readme/docker/09.png)
11. Run the command:

**docker compose -f docker-compose.yml -p nfssoundtrack_20 up -d**
   ![](./src/main/resources/readme/docker/12.png)
12. You should see after some seconds or maybe couple of minutes this status
    ![](./src/main/resources/readme/docker/13.png)
13. And it will look like this in Docker Desktop
    ![](./src/main/resources/readme/docker/115.png)
14. Wait for database to be ready - first it is trying to import data, you can see the "sql" line as indicated by red marking
    ![](./src/main/resources/readme/docker/14.png)
15. After few minutes it should indicate that it is ready
    ![](./src/main/resources/readme/docker/15.png)
16. When ready, then click on "Start" button for the second entry in the view (the one that is 'created')
    ![](./src/main/resources/readme/docker/16.png)
17. After some time you should see this message in the command line inside Docker Desktop
    ![](./src/main/resources/readme/docker/17.png)
18. Then go to https://localhost:8083 - browser will tell you this is not secure, but accept the risk
    ![](./src/main/resources/readme/docker/18.png)
19. You should see the main page and other stuff working
    ![](./src/main/resources/readme/docker/19.png)
    ![](./src/main/resources/readme/docker/21.png)

## Developing / debugging with IntelliJ
1. Install IntelliJ (I use Community Edition) from https://jetbrains.com/idea/download
   ![](./src/main/resources/readme/intellij/30.png)
2. I assume you have the repository downloaded already, so launch IntelliJ and open folder with repository
   ![](./src/main/resources/readme/intellij/31.png)
![](./src/main/resources/readme/intellij/32.png)
3. Trust the project
   ![](./src/main/resources/readme/intellij/33.png)
4. It might take some time but things like JDK should be downloaded automatically - check the progress of background tasks visible in bottom right corner
   ![](./src/main/resources/readme/intellij/34.png)
5. Go to Project Structure and double check that Java 17 is used
   ![](./src/main/resources/readme/intellij/35.png)
6. Install Docker plugin to simplify development and deployment a bit
   ![](./src/main/resources/readme/intellij/36.png)
![](./src/main/resources/readme/intellij/37.png)
7. Create two configurations: one for mysqldb (so that it is just starting service and not recreating databsae again) and for the app itself
    ![](./src/main/resources/readme/intellij/42.png)
8. Database service config is like this
    ![](./src/main/resources/readme/intellij/47.png)
9. App config is like this
    ![](./src/main/resources/readme/intellij/43.png)
10. Run only the "mysqldb" from docker-compose
       ![](./src/main/resources/readme/intellij/38.png)
11. Check in bottom panel that mysqldb service is started
   ![](./src/main/resources/readme/intellij/39.png)
12. Double check in Docker Desktop too
   ![](./src/main/resources/readme/intellij/40.png)
13. Start the app configuration and look for messages
    ![](./src/main/resources/readme/intellij/44.png)
14. This message should be always shown and indicate that start was good
    ![](./src/main/resources/readme/intellij/45.png)
15. If you want to run all in Docker, then just hit this double-start button in docker-cmpose.yml
    ![](./src/main/resources/readme/intellij/46.png)
16. Hopefully you should be able to go to https://localhost:8082/ and see the website running

## Skip certificate questions
1. If you get prompts in browser about certificate, then simply go to "keystore" folder and install the self-signed certificate
![](./src/main/resources/readme/51.png)
2. Select "Local PC"
   ![](./src/main/resources/readme/52.png)
3. Continue with import
   ![](./src/main/resources/readme/53.png)
4. Password can be found in "application.properties" - yourkeystorepass
   ![](./src/main/resources/readme/54.png)
5. Let Windows automatically store it
   ![](./src/main/resources/readme/55.png)
6. Finish the import and try to access localhost again
   ![](./src/main/resources/readme/56.png)