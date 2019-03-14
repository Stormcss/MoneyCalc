--- tables size
SELECT
       relname as "Table",
       pg_size_pretty(pg_total_relation_size(relid)) As "Size",
       pg_size_pretty(pg_total_relation_size(relid) - pg_relation_size(relid)) as "External Size"
FROM pg_catalog.pg_statio_user_tables ORDER BY pg_total_relation_size(relid) DESC;
SELECT pg_size_pretty(pg_database_size('stormcss_mс_t'));

--- objects size
SELECT
       relname AS objectname,
       relkind AS objecttype,
       reltuples AS "#entries", pg_size_pretty(relpages::bigint*8*1024) AS size
FROM pg_class
WHERE relpages >= 8 AND relname NOT LIKE 'pg_%'
ORDER BY relpages DESC;

-------------------------------------------------
-------------   FILLING IN TABLES   -------------
-------------------------------------------------
INSERT INTO "Settings"
SELECT
       nextval('settings_id_seq'),
       (select date(NOW() - trunc(random() * 90) * '1 day'::interval)),
       (select date(NOW() + trunc(random() * 90) * '1 day'::interval))
FROM generate_series(1, 500000) AS i;

do $$
DECLARE
  accessId numeric;
begin
  for count in 1..500000 loop
    select nextval('access_id_seq') into accessId;
    INSERT INTO "Access"
    SELECT accessId, ('qwe'|| accessId), (md5(random()::text)), ('qwe' || md5(random()::text) || '@mail.ru');
  end loop;
end;
$$;

INSERT INTO "Identifications"
SELECT
       nextval('identifications_id_seq'),
       (md5(random()::text))
FROM generate_series(1, 500000) AS i;


do $$
DECLARE
  userId numeric;
begin
  for count in 1..500000 loop
    select nextval('user_id_seq') into userId;
    INSERT INTO "User" SELECT userId, userId, userId, userId;
  end loop;
end;
$$;

select count(1) from "SpendingSection";
select * from "SpendingSection";

do $$
begin
  for userId in 1..500000 loop
    for sectionId in 1..10 loop
      insert into "SpendingSection" values(nextval('spending_sections_id_seq'),
                                           userId, sectionId, md5(random()::text), true, false,
                                           (SELECT floor(random() * 10 + 1)::int)*1000, (SELECT floor(random() * 10 + 1)::int));
    end loop;
  end loop;
end;
$$;

do $$
DECLARE
  sectionId numeric;
  randSum numeric;
  randDate date;
begin
  for userId in 1..500000 loop
    for count in 1..10 loop
      select floor(random() * 10 + 1)::int into sectionId;
      select date(NOW() - trunc(random() * 110) * '1 day'::interval) into randDate;
      select floor(random() * 1000 + 1)::int into randSum;
      insert into "Transactions" values(nextval('transactions_id_seq'),
                                        userId, sectionId, randDate, 'RUR', md5(random()::text), md5(random()::text), randSum);
    end loop;
  end loop;
end;
$$;