::ASSEMBLY LINE BUILDER
@echo off
echo Promotion Type? (Choose * for recommended, @ for stable and x for unstable)
set /p PROMOTION=

set /p MODVERSION=<modversion.txt
set /p CurrentBuild=<buildnumber.txt
set /a BUILD_NUMBER=%CurrentBuild%+1

set NAME=AssemblyLine_v%MODVERSION%.%BUILD_NUMBER%
set FILE_NAME=%NAME%.jar
set TEMP_FILE=%NAME%_temp.jar
set TEMP_FILE2=%NAME%_temp2.jar
set TEMP_FOLDER=%NAME%_temp
set API_NAME=%NAME%_api.zip
set BACKUP_NAME=%NAME%_backup.zip

echo Starting to build %NAME%

::BUILD
::runtime\bin\python\python_mcp runtime\recompile.py %*
::runtime\bin\python\python_mcp runtime\reobfuscate.py %*

::ZIP-UP
cd reobf\minecraft\
"..\..\..\7za.exe" a "..\..\builds\%TEMP_FILE_NAME%" "*"
cd ..\..\
cd resources\
"..\..\7za.exe" a "..\builds\%FILE_NAME%" "*"
"..\..\7za.exe" a "..\builds\%BACKUP_NAME%" "*" -pdarkguardsman
cd ..\
cd src\
"..\..\7za.exe" a "..\builds\%BACKUP_NAME%" "*\assemblyline\" -pdarkguardsman
"..\..\7za.exe" a "..\builds\%API_NAME%" "*\assemblyline\api\"
cd ..\

::Obfuscation
echo Injector Minecraft Source
cd obf_minecraft
"..\..\7za.exe" a "..\builds\%TEMP_FILE_NAME%" "*"
cd ..\
echo Obfuscating...
java -jar "retroguard.jar" "builds\%TEMP_FILE_NAME%" "builds\%TEMP_FILE2%" obf.rgs
echo Repacking
"..\7za.exe" x "builds\%TEMP_FILE_NAME%" "builds\%TEMP_FOLDER%"
cd "builds\%TEMP_FOLDER%"
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME%" "*"
cd ..\..\

echo Done building %NAME%

pause