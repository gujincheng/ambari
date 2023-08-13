SELECT 'Upgrading MetaStore schema from 0.12.0 to 0.13.0' AS Status from dual;

-- 15-HIVE-5700.oracle.sql
-- Normalize the date partition column values as best we can. No schema changes.

CREATE FUNCTION hive13_to_date(date_str IN VARCHAR2) RETURN DATE IS dt DATE; BEGIN dt := TO_DATE(date_str, 'YYYY-MM-DD'); RETURN dt; EXCEPTION WHEN others THEN RETURN null; END;/

MERGE INTO PARTITION_KEY_VALS
USING (
  SELECT SRC.PART_ID as IPART_ID, SRC.INTEGER_IDX as IINTEGER_IDX, 
     NVL(TO_CHAR(hive13_to_date(PART_KEY_VAL),'YYYY-MM-DD'), PART_KEY_VAL) as NORM
  FROM PARTITION_KEY_VALS SRC
    INNER JOIN PARTITIONS ON SRC.PART_ID = PARTITIONS.PART_ID
    INNER JOIN PARTITION_KEYS ON PARTITION_KEYS.TBL_ID = PARTITIONS.TBL_ID
      AND PARTITION_KEYS.INTEGER_IDX = SRC.INTEGER_IDX AND PARTITION_KEYS.PKEY_TYPE = 'date'
) ON (IPART_ID = PARTITION_KEY_VALS.PART_ID AND IINTEGER_IDX = PARTITION_KEY_VALS.INTEGER_IDX)
WHEN MATCHED THEN UPDATE SET PART_KEY_VAL = NORM;

DROP FUNCTION hive13_to_date;

-- 16-HIVE-6386.oracle.sql
ALTER TABLE DBS ADD OWNER_NAME VARCHAR2(128);
ALTER TABLE DBS ADD OWNER_TYPE VARCHAR2(10);

-- 17-HIVE-6458.oracle.sql
CREATE TABLE FUNCS (
  FUNC_ID NUMBER NOT NULL,
  CLASS_NAME VARCHAR2(4000),
  CREATE_TIME NUMBER(10) NOT NULL,
  DB_ID NUMBER,
  FUNC_NAME VARCHAR2(128),
  FUNC_TYPE NUMBER(10) NOT NULL,
  OWNER_NAME VARCHAR2(128),
  OWNER_TYPE VARCHAR2(10)
);

ALTER TABLE FUNCS ADD CONSTRAINT FUNCS_PK PRIMARY KEY (FUNC_ID);
ALTER TABLE FUNCS ADD CONSTRAINT FUNCS_FK1 FOREIGN KEY (DB_ID) REFERENCES DBS (DB_ID) INITIALLY DEFERRED;
CREATE UNIQUE INDEX UNIQUEFUNCTION ON FUNCS (FUNC_NAME, DB_ID);
CREATE INDEX FUNCS_N49 ON FUNCS (DB_ID);

CREATE TABLE FUNC_RU (
  FUNC_ID NUMBER NOT NULL,
  RESOURCE_TYPE NUMBER(10) NOT NULL,
  RESOURCE_URI VARCHAR2(4000),
  INTEGER_IDX NUMBER(10) NOT NULL
);

ALTER TABLE FUNC_RU ADD CONSTRAINT FUNC_RU_PK PRIMARY KEY (FUNC_ID, INTEGER_IDX);
ALTER TABLE FUNC_RU ADD CONSTRAINT FUNC_RU_FK1 FOREIGN KEY (FUNC_ID) REFERENCES FUNCS (FUNC_ID) INITIALLY DEFERRED;
CREATE INDEX FUNC_RU_N49 ON FUNC_RU (FUNC_ID);

-- 18-HIVE-6757.oracle.sql
UPDATE SDS
  SET INPUT_FORMAT = 'org.apache.hadoop.hive.ql.io.parquet.MapredParquetInputFormat'
WHERE
  INPUT_FORMAT= 'parquet.hive.DeprecatedParquetInputFormat' or
  INPUT_FORMAT = 'parquet.hive.MapredParquetInputFormat'
;

UPDATE SDS
  SET OUTPUT_FORMAT = 'org.apache.hadoop.hive.ql.io.parquet.MapredParquetOutputFormat'
WHERE
  OUTPUT_FORMAT = 'parquet.hive.DeprecatedParquetOutputFormat'  or
  OUTPUT_FORMAT = 'parquet.hive.MapredParquetOutputFormat'
;

UPDATE SERDES
  SET SLIB='org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe'
WHERE
  SLIB = 'parquet.hive.serde.ParquetHiveSerDe'
;

-- hive-txn-schema-0.13.0.oracle.sql

-- Licensed to the Apache Software Foundation (ASF) under one or more
-- contributor license agreements.  See the NOTICE file distributed with
-- this work for additional information regarding copyright ownership.
-- The ASF licenses this file to You under the Apache License, Version 2.0
-- (the License); you may not use this file except in compliance with
-- the License.  You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an AS IS BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.

--
-- Tables for transaction management
-- 

CREATE TABLE TXNS (
  TXN_ID NUMBER(19) PRIMARY KEY,
  TXN_STATE char(1) NOT NULL,
  TXN_STARTED NUMBER(19) NOT NULL,
  TXN_LAST_HEARTBEAT NUMBER(19) NOT NULL,
  TXN_USER varchar(128) NOT NULL,
  TXN_HOST varchar(128) NOT NULL
);

CREATE TABLE TXN_COMPONENTS (
  TC_TXNID NUMBER(19) REFERENCES TXNS (TXN_ID),
  TC_DATABASE VARCHAR2(128) NOT NULL,
  TC_TABLE VARCHAR2(128),
  TC_PARTITION VARCHAR2(767) NULL
);

CREATE TABLE COMPLETED_TXN_COMPONENTS (
  CTC_TXNID NUMBER(19),
  CTC_DATABASE varchar(128) NOT NULL,
  CTC_TABLE varchar(128),
  CTC_PARTITION varchar(767)
);

CREATE TABLE NEXT_TXN_ID (
  NTXN_NEXT NUMBER(19) NOT NULL
);
INSERT INTO NEXT_TXN_ID VALUES(1);

CREATE TABLE HIVE_LOCKS (
  HL_LOCK_EXT_ID NUMBER(19) NOT NULL,
  HL_LOCK_INT_ID NUMBER(19) NOT NULL,
  HL_TXNID NUMBER(19),
  HL_DB VARCHAR2(128) NOT NULL,
  HL_TABLE VARCHAR2(128),
  HL_PARTITION VARCHAR2(767),
  HL_LOCK_STATE CHAR(1) NOT NULL,
  HL_LOCK_TYPE CHAR(1) NOT NULL,
  HL_LAST_HEARTBEAT NUMBER(19) NOT NULL,
  HL_ACQUIRED_AT NUMBER(19),
  HL_USER varchar(128) NOT NULL,
  HL_HOST varchar(128) NOT NULL,
  PRIMARY KEY(HL_LOCK_EXT_ID, HL_LOCK_INT_ID)
); 

CREATE INDEX HL_TXNID_INDEX ON HIVE_LOCKS (HL_TXNID);

CREATE TABLE NEXT_LOCK_ID (
  NL_NEXT NUMBER(19) NOT NULL
);
INSERT INTO NEXT_LOCK_ID VALUES(1);

CREATE TABLE COMPACTION_QUEUE (
  CQ_ID NUMBER(19) PRIMARY KEY,
  CQ_DATABASE varchar(128) NOT NULL,
  CQ_TABLE varchar(128) NOT NULL,
  CQ_PARTITION varchar(767),
  CQ_STATE char(1) NOT NULL,
  CQ_TYPE char(1) NOT NULL,
  CQ_WORKER_ID varchar(128),
  CQ_START NUMBER(19),
  CQ_RUN_AS varchar(128)
);

CREATE TABLE NEXT_COMPACTION_QUEUE_ID (
  NCQ_NEXT NUMBER(19) NOT NULL
);
INSERT INTO NEXT_COMPACTION_QUEUE_ID VALUES(1);


UPDATE VERSION SET SCHEMA_VERSION='0.13.0', VERSION_COMMENT='Hive release version 0.13.0' where VER_ID=1;
SELECT 'Finished upgrading MetaStore schema from 0.12.0 to 0.13.0' AS Status from dual;
