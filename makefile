all:	parserjar

parserjar:
	rm -rf dependency-reduced-pom.xml target 
	cat /dev/null > log/parser.log
	mvn clean package

clean:
	rm -rf dependency-reduced-pom.xml target

.PHONY: parser clean
