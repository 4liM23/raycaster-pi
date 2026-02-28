rm -rf out
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out raycasterpi.Main