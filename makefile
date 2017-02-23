all:	parserjar

parserjar:
	mvn clean package

clean:
	rm -rf dependency-reduced-pom.xml target log

.PHONY: parser clean
