if exist out rmdir /s /q out
javac -encoding UTF-8 --module-path "lib\javafx" --add-modules javafx.controls -d out src\Server\*.java src\Client\*.java src\GUI\*.java
if not exist out\GUI mkdir out\GUI
copy /y src\GUI\*.css out\GUI >nul
copy /y src\GUI\*.png out\GUI >nul
