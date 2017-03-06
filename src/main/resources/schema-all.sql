-- table to store link grabe from medlineplus
CREATE TABLE IF NOT EXISTS mp_link (
  code varchar(50) NOT NULL,  -- rxcui, icd9, icd10 code
  sab  varchar(20) NOT NULL,  -- RXNORM, ICD9CM,ICD10CM
  title varchar(255),         -- name of drug from medlineplus
  link varchar(255),          -- link from medlineplus
  updated varchar(30),        -- updated time of link
  rel varchar(30),            -- unknown field from medlineplus
  PRIMARY KEY(code, sab, link)-- some code and sab has more than one links,
                              -- so put link as primary key too
);

-- table to store link grabe from medlineplus
CREATE TABLE IF NOT EXISTS wiki_link (
  str MEDIUMTEXT ,            -- rxcui, icd9, icd10 keyword
  sab  varchar(20) NOT NULL,  -- RXNORM, ICD9CM,ICD10CM
  pageid varchar(30),         -- unknown field from medlineplus
  title varchar(255),         -- redirect name from wiki
  link varchar(255),          -- link from wiki
  updated varchar(30)        -- updated time of link
--  PRIMARY KEY(sab, link)      -- some keyword and sab has more than one links,
                              -- so put link as primary key too
);
