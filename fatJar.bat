@echo off
cmd /C gradlew fatJar
xcopy src\main\resources build\libs\src\main\resources /E /C /I /Y
cd build\libs\
rmdir src\main\resources\assets\external /S /Q
jar -cfM ..\..\exports\Timble.zip .
cd ..
rmdir . /S /Q