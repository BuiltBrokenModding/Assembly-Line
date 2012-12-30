::ASSEMBLY LINE BUILDER
@echo off
set FILE_NAME=AssemblyLine_test.jar
set API_NAME=AssemblyLine_test_api.zip
set BACKUP_NAME=AssemblyLine_test_backup.zip

echo Starting to build %FILE_NAME%

::BUILD
runtime\bin\python\python_mcp runtime\recompile.py %*
runtime\bin\python\python_mcp runtime\reobfuscate.py %*

::ZIP-UP
cd reobf\minecraft\
"..\..\..\7za.exe" a "..\..\builds\%FILE_NAME%" "*"
cd ..\..\
cd resources\
"..\..\7za.exe" a "..\builds\%FILE_NAME%" "*"
"..\..\7za.exe" a "..\builds\%BACKUP_NAME%" "*" -pdarkguardsman
cd ..\
cd src\
"..\..\7za.exe" a "..\builds\%BACKUP_NAME%" "*\assemblyline\" -pdarkguardsman
"..\..\7za.exe" a "..\builds\%API_NAME%" "*\assemblyline\api\"
cd ..\

echo Done building %FILE_NAME% for UE %UE_VERSION%

pause