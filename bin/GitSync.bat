rem set defaults:
set "com=Clean up and import changes"
rem set parameters:
IF NOT "a%1"=="a" (set "com=%1")

cd ..\
git commit -am "%com%"
git submodule foreach git pull origin master
git push origin master
cd ..\
cd Assembly-Line
git commit -am "%com%"
git push origin master
cd ..\
cd FarmTech
git commit -am "%com%"
git push origin master
cd ..\
cd Fluid-Mechanics
git commit -am "%com%"
git push origin master
cd ..\
cd GreaterProtection
git commit -am "%com%"
git push origin master
cd MechanizedMining
git commit -am "%com%"
git push origin master
cd ..\
cd Empire-Engine
git submodule foreach git pull origin master
git commit -am "%com%"
git push origin master