@ECHO OFF
title ·þÎñ¶Ë
::java -Xmx4G -jar spigot-1.14.4.jar
java  -javaagent:authlib-injector.jar=https://skin.molean.com:444/api/yggdrasil -Xmx4G -jar spigot-1.15.2.jar nogui
pause
EXIT