@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  dialogos-plugin-minimalIO startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and DIALOGOS_PLUGIN_MINIMAL_IO_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\dialogos-plugin-minimalIO.jar;%APP_HOME%\lib\dialogos-2.1.3.jar;%APP_HOME%\lib\jros2client-1.0.jar;%APP_HOME%\lib\guava-30.1-jre.jar;%APP_HOME%\lib\dialogos-2.1.3.jar;%APP_HOME%\lib\Diamant-2.1.3.jar;%APP_HOME%\lib\ClientInterface-2.1.3.jar;%APP_HOME%\lib\jmdns-3.5.3.jar;%APP_HOME%\lib\slf4j-api-1.7.24.jar;%APP_HOME%\lib\com.clt.audio-2.1.3.jar;%APP_HOME%\lib\com.clt.speech-2.1.3.jar;%APP_HOME%\lib\com.clt.script-2.1.3.jar;%APP_HOME%\lib\com.clt.base-2.1.3.jar;%APP_HOME%\lib\com.clt.mac-2.1.3.jar;%APP_HOME%\lib\com.clt.xml-2.1.3.jar;%APP_HOME%\lib\plugins-2.1.3.jar;%APP_HOME%\lib\jrosclient-5.0.jar;%APP_HOME%\lib\jros2messages-1.0.jar;%APP_HOME%\lib\rtpstalk-2.0.jar;%APP_HOME%\lib\jrosmessages-1.0.jar;%APP_HOME%\lib\kineticstreamer-4.0.jar;%APP_HOME%\lib\id.xfunction-19.0.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\checker-qual-3.5.0.jar;%APP_HOME%\lib\error_prone_annotations-2.3.4.jar;%APP_HOME%\lib\j2objc-annotations-1.3.jar;%APP_HOME%\lib\native-j-file-chooser-1.6.4.jar;%APP_HOME%\lib\AppleJavaExtensions-1.4.jar;%APP_HOME%\lib\MRJToolkitStubs-1.0.jar;%APP_HOME%\lib\json-20180130.jar;%APP_HOME%\lib\groovy-all-2.4.10.jar;%APP_HOME%\lib\java-cup-11b-runtime-2015.03.26.jar;%APP_HOME%\lib\java-cup-11b-2015.03.26.jar;%APP_HOME%\lib\gson-2.8.5.jar;%APP_HOME%\lib\rsyntaxtextarea-2.6.1.jar;%APP_HOME%\lib\jdom2-2.0.6.jar

@rem Execute dialogos-plugin-minimalIO
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %DIALOGOS_PLUGIN_MINIMAL_IO_OPTS%  -classpath "%CLASSPATH%" com.clt.dialogos.DialogOS %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable DIALOGOS_PLUGIN_MINIMAL_IO_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%DIALOGOS_PLUGIN_MINIMAL_IO_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
