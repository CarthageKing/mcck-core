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
INSERT INTO my_schema.revinfo (rev_id,rev_tstmp,rev_username) VALUES
	 (1,1705556636387,NULL),
	 (2,1705556636474,'creator@example.org'),
	 (3,1705556636508,'creator@example.org'),
	 (4,1705556636524,'creator@example.org'),
	 (5,1705556636541,'creator@example.org'),
	 (6,1705556636655,'modder@example.org'),
	 (7,1705556636733,NULL);


INSERT INTO my_schema.book_a (b_date_published,b_numpages,rev_id,rev_type,aud_created_dt,aud_last_mod_dt,b_hash,b_revisiondt,b_isbn,b_id,aud_created_by,aud_last_mod_by,b_description,b_name,b_excerpt,b_img) VALUES
	 (NULL,234,1,0,'2024-01-18 13:43:56.302983','2024-01-18 13:43:56.302983',NULL,'2018-05-06 21:14:15','sdfsdf','74f54c69-7b0d-49bf-b725-aba3bf56449a',NULL,NULL,'A book for dummies','Dummy Book for Dummies',NULL,NULL),
	 (NULL,199,2,0,'2024-01-18 13:43:56.472808','2024-01-18 13:43:56.472808',NULL,NULL,'CATastrophe','4ee3e189-dff3-4a16-9450-6aea236831b3','creator@example.org','creator@example.org','description','Mummy 1',NULL,NULL),
	 (NULL,200,3,0,'2024-01-18 13:43:56.505991','2024-01-18 13:43:56.505991',NULL,NULL,'CATastrophek','da6a42a1-d904-485b-bd87-8503a1019fb7','creator@example.org','creator@example.org','description','Mummy 2',NULL,NULL),
	 (NULL,536,4,0,'2024-01-18 13:43:56.521607','2024-01-18 13:43:56.521607',NULL,NULL,'rataCAT','386be98e-fcb4-4f60-9960-811b241e3c60','creator@example.org','creator@example.org','description','Mummy 3',NULL,NULL),
	 (NULL,212,5,0,'2024-01-18 13:43:56.537224','2024-01-18 13:43:56.537224',NULL,NULL,'marCATjoe','3885643b-9040-42bc-97ee-f0fb0b0f8ff8','creator@example.org','creator@example.org','description','Mummy 4',NULL,NULL),
	 (NULL,210,6,1,'2024-01-18 13:43:56.472808','2024-01-18 13:43:56.642631',NULL,NULL,'CATastrophe','4ee3e189-dff3-4a16-9450-6aea236831b3','creator@example.org','modder@example.org','description','Mummy 1',NULL,NULL),
	 (NULL,210,7,2,'2024-01-18 13:43:56.472808','2024-01-18 13:43:56.642631',NULL,NULL,'CATastrophe','4ee3e189-dff3-4a16-9450-6aea236831b3','creator@example.org','modder@example.org','description','Mummy 1',NULL,NULL);
	 
