drop table IF EXISTS "Access" CASCADE;
drop table IF EXISTS "Identifications" CASCADE;
drop table IF EXISTS "Settings" CASCADE;
drop table IF EXISTS "User" CASCADE;
drop table IF EXISTS "SpendingSection" CASCADE;
drop table IF EXISTS "Transactions" CASCADE;

drop sequence IF EXISTS access_id_seq;
drop sequence IF EXISTS identifications_id_seq;
drop sequence IF EXISTS user_id_seq;
drop sequence IF EXISTS settings_id_seq;
drop sequence IF EXISTS spending_sections_id_seq;
drop sequence IF EXISTS transactions_id_seq;

