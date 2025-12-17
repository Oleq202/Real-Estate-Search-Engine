Things to do:

cpp1.cpp / App.java:
- think how to connect the cpp and java files
   
App.java:
- add scrollbar
- add a way to see images for the homes



How to get it running:
g++ -o src/cpp_backend.exe src/cpp1.cpp
javac --module-path $javafxPath --add-modules javafx.controls,javafx.fxml src/App.java
java --module-path $javafxPath --add-modules javafx.controls -cp src App