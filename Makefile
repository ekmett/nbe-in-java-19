JAR=target/nbe-0.1.0.jar
MVN=mvn
MVN_OPTS=-q -T 2C --offline -DskipTests

default: run

all: $(JAR)

clean:
	rm -rf target

.PHONY: default all run clean

$(JAR): pom.xml $(wildcard src/main/java/nbe/*)
	$(MVN) $(MVN_OPTS) package

run: $(JAR)
	java -cp $(JAR) Expr
