rm -rf out
mkdir -p out
javac -d out $(find src/main/java -name "*.java")
java -cp out raycaster.Main
