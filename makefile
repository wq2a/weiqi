all:	parserjar

parserjar:
	rm -rf dependency-reduced-pom.xml target 
	cat /dev/null > log/parser.log
	mvn clean package

mvnrepo:
	mvn install:install-file -Dfile=lib/prologbeans.jar -DgroupId=se.sics -DartifactId=prologbeans -Dversion=4.2.1 -Dpackaging=jar
	mvn install:install-file -Dfile=lib/metamap-api-2.0.jar -DgroupId=se.sics -DartifactId=metamapapi -Dversion=2.0.0 -Dpackaging=jar

startservers:
	# start skr/medpost part-of-speech tagger server
	skrmedpostctl start
	# start wsd server
	wsdserverctl start

clean:
	rm -rf dependency-reduced-pom.xml target

.PHONY: parser clean
