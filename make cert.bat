@echo off
setlocal

rem Make sure user knows where to run this file from.
echo This script will make the keystore and truststore files within the directory it's run from.
echo.
pause
cls

set "projectDir=%cd%"
set "keyStore=%projectDir%\serverKeystore.jks"
set "keyTool=keytool.exe"
set "trustStore=%projectDir%\trustStore.jks"
set "caCert=C:\ProgramData\MySQL\MySQL Server 8.0\Data\ca.pem"

rem Check if keytool is already accessible, and if so, skip setting the directory for it
%keytool% || goto :setKeyTool
goto :goodKeyTool

:setKeyTool
set /p keyTool="Paste directory for JDK (e.g., C:\jdk-15.0.2): "
if not exist "%keyTool%\bin\keytool.exe" echo Keytool.exe could not be found in that path. && goto :setKeyTool
set "keyTool=%keyTool%\bin\keytool.exe"

:goodKeytool

rem Create public/private key and put into keystore, password of "secret" and
rem valid for 90 days -- lookup with "sfm"
cls
if not exist "%keyStore%" ("%keyTool%" -genkey -keyalg "RSA" -dname "o=Secure File Manager, cn=localhost" -ext "san=DNS:localhost" -alias sfm -keystore "%keyStore%" -storepass secret -validity 90)
echo KeyStore created.
echo.
pause

rem Export certificate (public key) from created keystore
cls
if not exist "%trustStore%" ("%keyTool%" -exportcert -keystore "%keyStore%" -alias sfm -file "%projectDir%\sfm.cer" -storepass secret)
echo Temporary certificate exported.
echo.
pause

rem Import created certificate into separate, empty keystore, called trustStore.jks
cls
if not exist "%trustStore%" ("%keyTool%" -importcert -file "%projectDir%\sfm.cer" -alias sfm -keystore "%trustStore%" -storepass secret)
echo TrustStore created with temporary certificate.
echo.
pause

rem Clean up temporary .cer file
cls
if exist "%projectDir%\sfm.cer" (del "%projectDir%\sfm.cer")
echo Temporary certificate deleted.
echo.
echo Finished.
echo.
pause

:importCaCert

rem Import MySQL's ca.pem into trustStore
cls
if not exist "%caCert%" goto :noCertFound
"%keytool%" -importcert -file "%caCert%" -alias sql -keystore "%trustStore%" -storepass secret
echo Added MySQL's self-signed CA certificate into trustStore.
echo.
pause

goto :end



rem Alternative to .jks container type (universally used instead of Java-specific -- and probably more secure)
rem just add this line when creating the keystore and truststore, before the -keystore argument:
rem -storetype PKCS12
rem and instead of using .jks use .pfx or .p12



rem SQL's ca.pem is its public, self-signed certificate. ca-key.pem is its private key.



:noCertFound
cls
echo ca.pem could not be found at "%caCert%"
set /p caCert="Paste file path to MySQL's ca.pem file: "
if not exist "%caCert%" goto :noCertFound
echo ca.pem found. Returning.
pause
goto :importCaCert

:end