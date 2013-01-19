::ASSEMBLY LINE BUILDER
@echo off
echo Promotion Type? (Choose * for recommended, @ for stable and x for unstable)
set /p PROMOTION=

set /p MODVERSION=<modversion.txt
set /p CurrentBuild=<buildnumber.txt
set /a BUILD_NUMBER=%CurrentBuild%+1
echo %BUILD_NUMBER% >buildnumber.txt

if %PROMOTION%==* (
	echo %MODVERSION% >recommendedversion.txt
)

set FILE_NAME=AssemblyLine_v%MODVERSION%.%BUILD_NUMBER%.jar
set API_NAME=AssemblyLine_v%MODVERSION%.%BUILD_NUMBER%_api.zip
set BACKUP_NAME=AssemblyLine_v%MODVERSION%.%BUILD_NUMBER%_backup.zip

echo Starting to build %FILE_NAME%

::BUILD
runtime\bin\python\python_mcp runtime\recompile.py %*
runtime\bin\python\python_mcp runtime\reobfuscate.py %*

::ZIP-UP
cd reobf\minecraft\
7z a "..\..\builds\%FILE_NAME%" "*"
cd ..\..\
cd resources\
7z a "..\builds\%FILE_NAME%" "*"
7z a "..\builds\%BACKUP_NAME%" "*" -pdirewolf20rocks
cd ..\
cd src\
7z a "..\builds\%BACKUP_NAME%" "*\assemblyline\" -pdirewolf20rocks
7z a "..\builds\%API_NAME%" "*\assemblyline\api\"
cd ..\

::UPDATE INFO FILE
echo %PROMOTION% %FILE_NAME% %API_NAME%>>info.txt

::GENERATE FTP Script
echo open calclavia.com>ftpscript.txt
echo al@calclavia.com>>ftpscript.txt
echo VkE4laBa84R9>>ftpscript.txt
echo binary>>ftpscript.txt
echo put "recommendedversion.txt">>ftpscript.txt
echo put "builds\%FILE_NAME%">>ftpscript.txt
echo put "builds\%API_NAME%">>ftpscript.txt
echo put info.txt>>ftpscript.txt
echo quit>>ftpscript.txt
ftp.exe -s:ftpscript.txt
del ftpscript.txt

echo Done building %FILE_NAME% for UE %UE_VERSION%

pause