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
