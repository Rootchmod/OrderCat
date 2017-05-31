@echo off

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java.exe
:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=java.exe
"%_JAVACMD%" -classpath ..\lib\OrderCat.jar com.myjo.ordercat.native_messaging.main.Main %*
goto end
:end
if not "%_JAVACMD%"=="" set _JAVACMD=