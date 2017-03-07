# weiqi
## How to load the project

### Components needed

- JDK
  - http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
  - In Mac OSX 10.5 or later, Apple recommends to set the $JAVA_HOME variable to /usr/libexec/java_home, just export $JAVA_HOME in file ~/. bash_profile or ~/.profile.

```
$ vi .bash_profile
export JAVA_HOME=$(/usr/libexec/java_home)
$ source .bash_profile
```

- Maven

  - https://maven.apache.org/download.cgi
  - wget http://mirrors.sonic.net/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
  - https://maven.apache.org/install.html
  - mvn -v

- Java spring

  - Consuming a RESTful Web Service

  - JDBC, Neo4j, or MangoDB

  - Creating Asynchronous Methods

  - Spring Boot with Docker

## API

### MedlinePlus

- https://medlineplus.gov/webservices.html
- https://medlineplus.gov/connect/service.html#medication

#### example

- https://apps.nlm.nih.gov/medlineplus/services/mpconnect_service.cfm?mainSearchCriteria.v.cs=2.16.840.1.113883.6.69&knowledgeResponseType=application/json&mainSearchCriteria.v.dn=Chantix%200.5%20MG%20Oral%20Tablet&informationRecipient.languageCode.c=en


### Wiki

- https://en.wikipedia.org/w/api.php?action=query&prop=info&titles=Main%20page&redirects&inprop=url
- https://en.wikipedia.org/w/api.php?action=query&prop=info&pageids=18630637&inprop=url
- https://en.wikipedia.org/w/api.php?action=query&prop=info&titles=Fractures&redirects&inprop=url
- http://en.wikipedia.org/w/api.php?action=query&generator=search&gsrsearch=meaning&srprop=size%7Cwordcount%7Ctimestamp%7Csnippet&prop=info&inprop=url

- https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=burn%20ankle&srwhat=text&srprop=timestamp&continue=

- https://en.wikipedia.org/w/api.php?action=query&generator=search&gsrsearch=meaning&prop=info&inprop=url&format=json

#### Wiki content

- get the section list
- https://en.wikipedia.org/w/api.php?action=parse&prop=sections&page=Weight_loss&format=json


## Framework

### SemRep

#### Semantic Knowledge Representation

- https://semrep.nlm.nih.gov/SemRep.v1.7_Installation.html

#### MetaMap

- https://metamap.nlm.nih.gov
- https://metamap.nlm.nih.gov/JavaApi.shtml


### 
