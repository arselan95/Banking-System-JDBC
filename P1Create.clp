CONNECT TO CS157A;
CREATE TABLE P1.CUSTOMER (ID INT NOT NULL GENERATED BY DEFAULT AS IDENTITY(START WITH 100, INCREMENT BY 1, NO CACHE), NAME VARCHAR(15) NOT NULL, GENDER CHAR(1) NOT NULL, AGE INT NOT NULL, PIN INT NOT NULL);
CREATE TABLE P1.ACCOUNT (NUMBER INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1000, INCREMENT BY 1, NO CACHE), ID INT NOT NULL, BALANCE INT NOT NULL, TYPE CHAR(1) NOT NULL, STATUS CHAR(1) NOT NULL);
ALTER TABLE P1.CUSTOMER ADD PRIMARY KEY(ID);
ALTER TABLE P1.ACCOUNT ADD PRIMARY KEY(NUMBER);
ALTER TABLE P1.CUSTOMER ADD CONSTRAINT C1 CHECK (GENDER = 'M' OR GENDER = 'F');
ALTER TABLE P1.CUSTOMER ADD CONSTRAINT C2 CHECK(AGE>=0);
ALTER TABLE P1.CUSTOMER ADD CONSTRAINT C3 CHECK(PIN>=0);
ALTER TABLE P1.ACCOUNT ADD CONSTRAINT C4 CHECK(BALANCE >=0);
ALTER TABLE P1.ACCOUNT ADD CONSTRAINT C5 CHECK(TYPE ='C' OR TYPE = 'S');
ALTER TABLE P1.ACCOUNT ADD CONSTRAINT C6 CHECK( STATUS = 'A' OR STATUS = 'I');
CREATE VIEW P1.AccountTotals(ID, NAME , GENDER, AGE, PIN, TOTAL) AS SELECT P1.CUSTOMER.ID, P1.CUSTOMER.NAME, P1.CUSTOMER.GENDER, P1.CUSTOMER.AGE, P1.CUSTOMER.PIN, SUM(P1.ACCOUNT.BALANCE) FROM P1.CUSTOMER INNER JOIN P1.ACCOUNT ON P1.CUSTOMER.ID = P1.ACCOUNT.ID WHERE P1.ACCOUNT.Status = 'A' GROUP BY P1.CUSTOMER.ID, P1.CUSTOMER.NAME, P1.CUSTOMER.GENDER, P1.CUSTOMER.AGE, P1.CUSTOMER.PIN;
terminate;
