---
-- #%L
-- mcck-core-EXAMPLES-springboot-rest-hibernate
-- %%
-- Copyright (C) 2024 Michael I. Calderero
-- %%
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--      http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- #L%
---

    create sequence my_schema.revinfo_seq start with 1 increment by 50;

    create table my_schema.book (
        b_date_published date,
        b_numpages integer,
        aud_created_dt timestamp(6) not null,
        aud_last_mod_dt timestamp(6) not null,
        b_hash bigint,
        b_revisiondt timestamp(6),
        b_isbn varchar(20),
        b_id varchar(40) not null,
        aud_created_by varchar(64),
        aud_last_mod_by varchar(64),
        b_description varchar(255),
        b_name varchar(255),
        b_excerpt clob,
        b_img clob,
        primary key (b_id),
        constraint UIDX_ISBN unique (b_isbn)
    );

    create table my_schema.book_a (
        b_date_published date,
        b_numpages integer,
        rev_type tinyint,
        aud_created_dt timestamp(6),
        aud_last_mod_dt timestamp(6),
        b_hash bigint,
        b_revisiondt timestamp(6),
        rev_id bigint not null,
        b_isbn varchar(20),
        b_id varchar(40) not null,
        aud_created_by varchar(64),
        aud_last_mod_by varchar(64),
        b_description varchar(255),
        b_name varchar(255),
        b_excerpt clob,
        b_img clob,
        primary key (rev_id, b_id)
    );

    create table my_schema.revinfo (
        rev_id bigint not null,
        rev_tstmp bigint,
        rev_username varchar(64),
        primary key (rev_id)
    );

    create index my_schema.IDX_NUMPAGES 
       on my_schema.book (b_numpages);

    alter table if exists my_schema.book_a 
       add constraint FK9b9d7iuwromw5bakac9330hmn 
       foreign key (rev_id) 
       references my_schema.revinfo;
