cd ..
IF EXIST LastBuildData.txt (
	Set /p "s=" <"LastBuildData.txt"
	git log --after={"%s%"}  > changeLog.txt
)
echo "%date:~10,4%-%date:~4,2%-%date:~7,2%"> LastBuildData.txt
PAUSE