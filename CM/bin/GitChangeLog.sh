cd ..
if [-e "LastBuildData.txt"]
then
cat LastBuildData.txt
read line
git log --pretty="%s" --Since={$line} >> changeLog.txt
fi
date +"%Y-%m-%d" >> LastBuildData.txt
