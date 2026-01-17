How to get it running:   
g++ -o src/cpp_backend.exe src/main.cpp src/Application.cpp src/Entities.cpp src/FileIO.cpp  
set javafxPath=C:\Java\javafx-sdk-25.0.1\lib or wherever you installed it
javac --module-path $javafxPath --add-modules javafx.controls,javafx.swing src/App.java src/PropertyModels.java src/PropertyService.java src/DataUtils.java src/AppTest.java  
java -cp src AppTest  
java --module-path $javafxPath --add-modules javafx.controls,javafx.swing -cp src App  
