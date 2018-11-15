------------------------------------------------------
----------------------  TABLES  ----------------------
------------------------------------------------------

CREATE TABLE "Access"
(
  id NUMERIC PRIMARY KEY,
  login text,
  password text,
  email text
);

CREATE TABLE "Identifications"
(
  id NUMERIC PRIMARY KEY,
  name text
);

CREATE TABLE "Settings"
(
  id NUMERIC PRIMARY KEY,
  "periodFrom" date,
  "periodTo" date
);

CREATE TABLE "Person"
(
  id NUMERIC PRIMARY KEY,
  "accessId" numeric REFERENCES  "Access" (id),
  "identificationsId" numeric REFERENCES  "Identifications" (id),
  "settingsId" numeric REFERENCES  "Settings" (id)
);

CREATE TABLE "SpendingSection"
(
  id NUMERIC PRIMARY KEY,
  "personId" numeric REFERENCES  "Person" (id),
  "sectionId" numeric,
  name text,
  "isAdded" boolean,
  "isRemoved" boolean,
  budget numeric,
  "logoId" numeric
);

CREATE TABLE "Transactions"
(
  id NUMERIC PRIMARY KEY,
  "personId" numeric REFERENCES  "Person" (id),
  "sectionId" numeric,
  date date,
  currency text,
  title text,
  description text,
  sum numeric
);

---------------------------------------------------------
----------------------  SEQUENCES  ----------------------
---------------------------------------------------------

CREATE SEQUENCE access_id_seq START 1 owned by "Access".id;
CREATE SEQUENCE identifications_id_seq START 1 owned by "Identifications".id;
CREATE SEQUENCE person_id_seq START 1 owned by "Person".id;
CREATE SEQUENCE settings_id_seq START 1 owned by "Settings".id;
CREATE SEQUENCE spending_sections_id_seq START 1 owned by "SpendingSection".id;
CREATE SEQUENCE transactions_id_seq START 1 owned by "Transactions".id;
