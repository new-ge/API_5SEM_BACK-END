SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'datawarehouse' AND pid <> pg_backend_pid();

ALTER DATABASE datawarehouse ALLOW_CONNECTIONS false;

DROP DATABASE datawarehouse;

CREATE DATABASE datawarehouse;