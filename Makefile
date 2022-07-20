JAR=target/nbe-0.1.0.jar

default: run

all: $(JAR)

clean:
	rm -rf target

.PHONY: default all run clean

$(JAR): pom.xml $(wildcard src/main/java/nbe/*)
	mvn package

run: $(JAR)
	java -cp $(JAR) Interpreter
