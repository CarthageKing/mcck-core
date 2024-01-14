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

    create table my_schema.book (
        b_date_published date,
        b_numpages integer,
        b_hash bigint,
        b_revisiondt timestamp(6),
        b_isbn varchar(20),
        b_id varchar(40) not null,
        b_description varchar(255),
        b_name varchar(255),
        b_excerpt text,
        b_img oid,
        primary key (b_id),
        constraint UIDX_ISBN unique (b_isbn)
    );

    create index IDX_NUMPAGES 
       on my_schema.book (b_numpages);
