# weiqi
## How to load the project

### Components needed

- JDK
http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

In Mac OSX 10.5 or later, Apple recommends to set the $JAVA_HOME variable to /usr/libexec/java_home, just export $JAVA_HOME in file ~/. bash_profile or ~/.profile.

```
$ vi .bash_profile
export JAVA_HOME=$(/usr/libexec/java_home)
$ source .bash_profile
```

- Maven
https://maven.apache.org/download.cgi
wget http://mirrors.sonic.net/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
https://maven.apache.org/install.html

mvn -v
