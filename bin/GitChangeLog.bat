cd ..
IF EXIST LastBuildData.txt (
	git log --pretty="%s" --since={LastBuildData.txt}  >> changeLog.txt
)
echo %date:~10,4%-%date:~4,2%-%date:~7,2% > LastBuildData.txt
PAUSE