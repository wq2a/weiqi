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

  - Open jdk
    - http://openjdk.java.net/install/
    - openjdk-8-jre, java-1.8.0-openjdk, java-1.8.0-openjdk-devel

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

- Domain type
  - https://medlineplus.gov
  - https://nccih.nih.gov
  - https://www.cancer.gov
  - https://ghr.nlm.nih.gov

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

### OMIM

- https://omim.org/
- https://omim.org/entry/600145

## Framework

### KM

- result structure
  - cui,cuipn,styp,sentnum,wordnum,score,attr, orig_phrase_text,section_string
  - 205178|Acuteness (qualifier value)|Temporal Concept|1|4|1.25882352941176||briefly acute|


### SemRep (Semantic Knowledge Representation)

- install semrep
  - https://semrep.nlm.nih.gov/SemRep.v1.7_Installation.html
- Problems
  - if libpcre.so.0 is missing add lib path, and make link libpcre.so.0 point to libpcre.so.0.0.1

  ```
    export LD_LIBRARY_PATH="/app001/opt/apps/semrep/public_semrep/lib:$LD_LIBRARY_PATH";
  ```
- How to run
  - add semrep link to public_semrep/bin/semrep.v1.7
  - https://semrep.nlm.nih.gov/SemRep.v1.7_Options.html 
  - semrep -L 2015 -Z 2015 -M -F -E MY_DATA/test.txt

#### MetaMap

- https://metamap.nlm.nih.gov
- install World Sense Disambiguation Server (WSD Server)
  - https://metamap.nlm.nih.gov/WSDServer.shtml 
- install MedPost/SKR Part of Speech Tagger
  - https://metamap.nlm.nih.gov/MedPostSKRTagger.shtml
  - Update run.bat or run.sh changing the top two lines to correspond to locations in your installation.
- install metamap
  - https://metamap.nlm.nih.gov/Installation.shtml
  - copy WSD_Server directory if public_mm/WSD_Server/centroids.ben.gz is not found
- install metamap Java API
  - https://metamap.nlm.nih.gov/JavaApi.shtml
  - https://softwarecave.org/2014/06/14/adding-external-jars-into-maven-project/


## Netezza

- import csv to Netezza

  - sql export as csv, Wrap choose empty (not double quote)

  - import script

  ```
  insert into WAN_CONCEPTS SELECT * FROM
    EXTERNAL '/Users/wanjiang/Desktop/wanjiang_concepts_21.csv'
    USING (DELIMITER ','
    REMOTESOURCE 'JDBC'
    LOGDIR '/Users/wanjiang/Desktop/'
    ENCODING 'internal'
    SKIPROWS 1
    ESCAPECHAR '\'
  );
  ```

  - export script

  ```
  create external table '/Users/wanjiang/Desktop/test123.csv'
  using
  ( DELIMITER '|'
    REMOTESOURCE 'JDBC'
    LOGDIR '/Users/wanjiang/Desktop/'
    ENCODING 'internal'
    ESCAPECHAR '\')
  as
  select * from sd_cc_statins;
  ```

  - concepts table

  ```
  CREATE TABLE WAN_CONCEPTS (
    id    integer,
    cui   integer,
    cui_pn varchar(256),
    sty    varchar(256),
    count_all  integer,
    notecount integer
  );

  CREATE TABLE WAN_CONCEPTS_DATA (
    id    integer,
    docnum varchar(60),
    cui   integer,
    sentnum integer,
    wordnum integer,
    score   double,
    attr varchar(256),
    orig_text    varchar(256),
    section_string  varchar(1000)
  );
  ```

- group eav table

  ```SQL
  SET @@group_concat_max_len = 9999999;
  CREATE table spl_other_info
  SELECT e.id AS id, e.name AS name, group_concat(p.name separator '|') AS property_all, group_concat(v.value separator '  ') AS content_all 
  FROM ((spl_other_info_entity e join spl_other_info_value v on((e.id = v.entity_id))) join spl_other_info_property p on((v.property_id = p.id))) group by e.id,e.name;

  -- mp_icd910_info_property_view is the properties we need to show in the info table
  -- mp_icd910_info_property_view created from mp_icd910_info_property, and remove certain properties
  SET @@group_concat_max_len = 9999999;
  CREATE table mp_icd910_info
  SELECT e.id AS id, e.link AS source, group_concat(p.name separator '|') AS property_all, group_concat(v.value separator '\r\n') AS content_all, CURRENT_TIMESTAMP as timestamp 
  FROM ((mp_icd910_info_entity e join mp_icd910_info_value v on((e.id = v.entity_id))) join mp_icd910_info_property_view p on((v.property_id = p.id))) group by e.id,e.link;

  -- type of entity_id should be both unsign or signed
  alter table mp_icd910_info_value add constraint foreign key(entity_id) references mp_icd910_info_entity (id) on update cascade on delete cascade; 

  -- 
  -- select all entity ID with certain property name
  create table spl_prescription_all_has_ADVERSE_info
  select distinct(v.entity_id) as entity_id
  from spl_prescription_all_info_value v
  join (select id from spl_prescription_all_info_property where loinc='ADVERSE REACTIONS SECTION') p
  on (v.property_id=p.id);

  -- select all values with certain entity ID
  create table spl_prescription_all_info_20
  select t.id as source, s.value, p.loinc
  from (select entity_id as id from spl_prescription_all_has_ADVERSE_info limit 20) t
  join spl_prescription_all_info_value s
  on (s.entity_id=t.id) join spl_prescription_all_info_property p
  on (s.property_id=p.id);

  -- concat values
  SET @@group_concat_max_len = 9999999;
  create table spl_prescription_all_20_info
  select source,group_concat(value) as content_all
  from spl_prescription_all_info_20
  group by source;

  -- concat values by property name
  create table spl_prescription_all_20_adverse_info
  select source, group_concat(value) as content_all
  from spl_prescription_all_info_20
  where loinc = 'ADVERSE REACTIONS SECTION'
  group by source;
  ```

- diff entity

  ```SQL
  create table mp_icd910_diff 
  select j.id, j.link 
  from (select e.id, e.link,i.property_all as p from mp_icd910_info_entity e left join mp_icd910_info i on (e.id=i.id)) j where j.p is null;
  ```


## OMIM

- API
  - https://omim.org/help/api
  - https://api.omim.org/api/entry?format=json&mimNumber=100050&include=text,allelicVariantList&apiKey=XXXXX

## SPL

- DailyMed
  - https://dailymed.nlm.nih.gov/dailymed/spl-resources-all-drug-labels.cfm

- SPL Rxnorm mappings
  - https://dailymed.nlm.nih.gov/dailymed/spl-resources-all-mapping-files.cfm

- LOINC section headings
  - https://www.fda.gov/ForIndustry/DataStandards/StructuredProductLabeling/ucm162057.htm

- spl-Rxnorm mapping
  ```SQL
  create table spl_rxnorm_mapping (
    setid varchar(100),
    spl_version int,
    rxcui varchar(10),
    rxstring varchar(255),
    rxtty varchar(10)
  );

  CREATE TABLE `ENTITY` (
    `ENTITY_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `SENTENCE_ID` int(10) unsigned NOT NULL,
    `CUI` char(8) DEFAULT NULL,
    `NAME` varchar(999) DEFAULT NULL,
    `SEMTYPE` varchar(30) DEFAULT NULL,
    `GENE_ID` varchar(30) DEFAULT NULL,
    `GENE_NAME` varchar(100) DEFAULT NULL,
    `TEXT` varchar(999) DEFAULT NULL,
    `SCORE` int(11) DEFAULT NULL,
    `START_INDEX` int(11) DEFAULT NULL,
    `END_INDEX` int(11) DEFAULT NULL,
    PRIMARY KEY (`ENTITY_ID`),
    KEY `ENTITY_ibfk_1` (`SENTENCE_ID`),
    CONSTRAINT `ENTITY_ibfk_1` FOREIGN KEY (`SENTENCE_ID`) REFERENCES `SENTENCE` (`SENTENCE_ID`) ON DELETE CASCADE ON UPDATE CASCADE
  ) ENGINE=InnoDB AUTO_INCREMENT=1000010 DEFAULT CHARSET=utf8 COMMENT='Stores entity from SPL';
  
  CREATE TABLE `PREDICATION` (
    `PREDICATION_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `SENTENCE_ID` int(10) unsigned NOT NULL,
    `PMID` varchar(20) DEFAULT NULL,
    `PREDICATE` varchar(50) DEFAULT NULL,
    `SUBJECT_CUI` varchar(255) DEFAULT NULL,
    `SUBJECT_NAME` varchar(999) DEFAULT NULL,
    `SUBJECT_SEMTYPE` varchar(50) DEFAULT NULL,
    `SUBJECT_NOVELTY` tinyint(1) DEFAULT NULL,
    `OBJECT_CUI` varchar(255) DEFAULT NULL,
    `OBJECT_NAME` varchar(999) DEFAULT NULL,
    `OBJECT_SEMTYPE` varchar(50) DEFAULT NULL,
    `OBJECT_NOVELTY` tinyint(1) DEFAULT NULL,
    PRIMARY KEY (`PREDICATION_ID`),
    KEY `SENTENCE_ID` (`SENTENCE_ID`),
    KEY `pmid_index_btree` (`PMID`) USING BTREE,
    CONSTRAINT `PREDICATION_ibfk_1` FOREIGN KEY (`SENTENCE_ID`) REFERENCES `SENTENCE` (`SENTENCE_ID`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `PREDICATION_ibfk_2` FOREIGN KEY (`PMID`) REFERENCES `SENTENCE` (`PMID`) ON DELETE CASCADE ON UPDATE CASCADE
  ) ENGINE=InnoDB AUTO_INCREMENT=10000001 DEFAULT CHARSET=utf8 COMMENT='Stores aggregate info of semantic predications';
  
  CREATE TABLE `PREDICATION_AUX` (
    `PREDICATION_AUX_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `PREDICATION_ID` int(10) unsigned NOT NULL,
    `SUBJECT_TEXT` varchar(200) DEFAULT '' COMMENT 'Should be NOT NULL eventually',
    `SUBJECT_DIST` int(10) unsigned DEFAULT '0' COMMENT 'Should be NOT NULL eventually',
    `SUBJECT_MAXDIST` int(10) unsigned DEFAULT '0' COMMENT 'Should be NOT NULL eventually',
    `SUBJECT_START_INDEX` int(10) unsigned DEFAULT '0' COMMENT 'Should be NOT NULL eventually',
    `SUBJECT_END_INDEX` int(10) unsigned DEFAULT '0' COMMENT 'Should be NOT NULL eventually',
    `SUBJECT_SCORE` int(10) unsigned DEFAULT '0' COMMENT 'Should be NOT NULL eventually',
    `INDICATOR_TYPE` varchar(10) DEFAULT '' COMMENT 'Should be NOT NULL eventually',
    `PREDICATE_START_INDEX` int(10) unsigned DEFAULT '0' COMMENT 'Should be NOT NULL eventually',
    `PREDICATE_END_INDEX` int(10) unsigned DEFAULT '0' COMMENT 'Should be NOT NULL eventually',
    `OBJECT_TEXT` varchar(200) DEFAULT '' COMMENT 'Should be NOT NULL eventually',
    `OBJECT_DIST` int(10) unsigned DEFAULT '0' COMMENT 'Should be NOT NULL eventually',
    `OBJECT_MAXDIST` int(10) unsigned DEFAULT '0' COMMENT 'Should be NOT NULL eventually',
    `OBJECT_START_INDEX` int(10) unsigned DEFAULT '0' COMMENT 'Should be NOT NULL eventually',
    `OBJECT_END_INDEX` int(10) unsigned DEFAULT '0' COMMENT 'Should be NOT NULL eventually',
    `OBJECT_SCORE` int(10) unsigned DEFAULT '0' COMMENT 'Should be NOT NULL eventually',
    `CURR_TIMESTAMP` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`PREDICATION_AUX_ID`),
    KEY `PREDICATION_ID` (`PREDICATION_ID`),
    CONSTRAINT `PREDICATION_AUX_ibfk_1` FOREIGN KEY (`PREDICATION_ID`) REFERENCES `PREDICATION` (`PREDICATION_ID`) ON DELETE CASCADE ON UPDATE CASCADE
  ) ENGINE=InnoDB AUTO_INCREMENT=10000001 DEFAULT CHARSET=utf8 COMMENT='Stores semantic predications in sentences';
  
  CREATE TABLE `SEMANTIC_TYPE_2015AA` (
    `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
    `semantic_name` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
    `semantic_type` varchar(10) CHARACTER SET latin1 DEFAULT NULL,
    PRIMARY KEY (`id`)
  ) ENGINE=MyISAM AUTO_INCREMENT=136 DEFAULT CHARSET=utf8;
  
  CREATE TABLE `SENTENCE` (
    `SENTENCE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
    `PMID` varchar(20) NOT NULL DEFAULT '',
    `TYPE` varchar(2) NOT NULL DEFAULT '',
    `NUMBER` int(10) unsigned NOT NULL DEFAULT '0',
    `SENTENCE` text NOT NULL,
    PRIMARY KEY (`SENTENCE_ID`),
    UNIQUE KEY `SENTENCE` (`PMID`,`TYPE`,`NUMBER`),
    KEY `PMID_INDEX` (`PMID`) USING BTREE,
    KEY `PMID_HASH` (`PMID`) USING HASH
  ) ENGINE=InnoDB AUTO_INCREMENT=100000008 DEFAULT CHARSET=utf8 COMMENT='Stores sentences from SPL';
  ```


## Others

- sqldump
  - mysqldump --single-transaction -u root -p db_name table_name > test.sql

- php configure

  - install
  ```
  ./configure --prefix=/Users/wanjiang/php --with-mysql=mysqlnd --with-mysqli=mysqlnd --enable-mbstring --with-gd --with-zlib --enable-pdo --with-pdo-sqlite --with-pdo-mysql=mysqlnd --with-ldap --with-ldap-sasl --with-pear --enable-sockets --with-curl --with-openssl --enable-cgi --enable-soap
  ```

  - apache config

  ```
  LoadModule rewrite_module libexec/apache2/mod_rewrite.so
  ```

  - add index.php in request

  ```
  <IfModule mod_rewrite.c>
    RewriteEngine On
    #RewriteBase /
    RewriteCond %{REQUEST_FILENAME} !-d
    #RewriteCond %{REQUEST_FILENAME} !-f
    RewriteRule ^ index.php [QSA,L]
  </IfModule>
  ```
  
