
DROP TABLE IF EXISTS employee;

CREATE TABLE IF NOT EXISTS employee(id int PRIMARY KEY ,name varchar(200),expr double);

DROP TABLE IF EXISTS dependent;

CREATE TABLE IF NOT EXISTS dependent(emp_id int references employee ,name varchar(200), relation varchar(100),age Int);

