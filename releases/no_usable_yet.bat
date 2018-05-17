ECHO Automation Bot Building and Deploying XR3Player...

ECHO Packaging XR3Player
call mvn -Dmaven.javadoc.skip=true -DskipTests clean package


ECHO ---------------Removing old files...--------------------
ECHO DEL "C:\Users\GOXR3PLUS\Desktop\XR3Player.jar"
ECHO RMDIR /S /Q "C:\Users\GOXR3PLUS\Desktop\XR3Player_lib"

ECHO -----------------Copying files...-------------------
ROBOCOPY "C:\Users\GOXR3PLUS\Desktop\GitHub\XR3Player\target" "C:\Users\GOXR3PLUS\Desktop" XR3Player.jar
ROBOCOPY "C:\Users\GOXR3PLUS\Desktop\GitHub\XR3Player\target\XR3Player_lib" "C:\Users\GOXR3PLUS\Desktop\XR3Player_lib" /mir
@pause

