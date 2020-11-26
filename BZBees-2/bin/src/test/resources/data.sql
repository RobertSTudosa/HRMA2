

insert into persons (birth_date, active_job, app_status, availability, current_job, email, employment_status, end_job, first_name, job_wish_desc, last_name, location, private_currentjob, start_job, status_start_date, total_hours, work_experience, person_id) values ('1996-06-14', FALSE, 1, '2020-12-08', 'tester', 'ani_alina@yahoo.com', null, null, 'Alina', 'sdfg', 'Tudosa', 'Constanta', FALSE, null, null, 0.0, 10, 1);
insert into persons (birth_date, active_job, app_status, availability, current_job, email, employment_status, end_job, first_name, job_wish_desc, last_name, location, private_currentjob, start_job, status_start_date, total_hours, work_experience, person_id) values ('1990-05-14', FALSE, 1, '2020-12-25', 'fotbalist', 'robert@bpeople.ro', null, null, 'Robert', 'the best', 'Tudosa', 'Suceava', FALSE, null, null, 0.0, 10, 2);
-- update person_seq set next_val = '3' ;

insert into user_accounts (user_id, active, email, password, person_id, username ) values (1, FALSE, 'ani_alina@yahoo.com', '1wert', '1', 'MxAdmin');
insert into user_accounts (user_id, active, email, password, person_id, username ) values (2, FALSE, 'robert@bpeople.ro', '1rte', 2, 'MxAdmina');
-- update user_seq set next_val = '3' ;

insert into skills (skill_description, skill_name, skill_id) values ('qwer','Too Smart' , 1);
insert into skills (skill_description, skill_name, skill_id) values ('qwer','Too Smart2' , 2);
insert into skills (skill_description, skill_name, skill_id) values ('qwer','Not Smart' , 3);
insert into skills (skill_description, skill_name, skill_id) values ('qwer','Not Smart' , 4);
update skills_seq set next_val = '5';
insert into person_skills (person_id, skill_id) values (1, 1);
insert into person_skills (person_id, skill_id) values (1, 2);
insert into person_skills (person_id, skill_id) values (2, 3);
insert into person_skills (person_id, skill_id) values (2, 4);

insert into lang (level, name, lang_id) values ('Communicative', 'English', 1);
insert into lang (level, name, lang_id) values ('Basic', 'Java', 2);
insert into lang (level, name, lang_id) values ('Communicative', 'Dutch', 3);
insert into lang (level, name, lang_id) values ('Advanced', 'French', 4);
update lang_seq set next_val = '5';	
insert into person_lang(person_id, lang_id) values (1,1);
insert into person_lang(person_id, lang_id) values (1,2);
insert into person_lang(person_id, lang_id) values (2,3);
insert into person_lang(person_id, lang_id) values (2,4);

insert into jobs (company_name, currency, end_date, job_location, job_private, job_title, necessary_documents, responsabilities, salary, skills, start_date, tags, working_conditions, job_id) values ('Bpeople', 0.0, '2020-08-20', 'Honolulu',FALSE , 'JapaMan', null, 'sdv', 0.0, null, '2019-07-27', null, null, 1);
insert into jobs (company_name, currency, end_date, job_location, job_private, job_title, necessary_documents, responsabilities, salary, skills, start_date, tags, working_conditions, job_id) values ('ApxSoftware', 0.0, '2018-06-20', '3Papuci',FALSE , 'Sapator', null, 'vds', 0.0, null, '2019-12-12', null, null, 2);
insert into jobs (company_name, currency, end_date, job_location, job_private, job_title, necessary_documents, responsabilities, salary, skills, start_date, tags, working_conditions, job_id) values ('Bpeople', 0.0, '2019-07-10', 'Mamaia',FALSE , 'Recruiter', null, 'mst', 0.0, null, '2019-07-27', null, null, 3);
insert into jobs (company_name, currency, end_date, job_location, job_private, job_title, necessary_documents, responsabilities, salary, skills, start_date, tags, working_conditions, job_id) values ('PeMareSRL', 0.0, '2018-11-30', 'Turkistan',FALSE , 'Scaffolder', null, 'stm', 0.0, null, '2020-07-27', null, null, 4);
update job_seq set next_val = '5';
insert into person_jobs(person_id, job_id) values (1,1);
insert into person_jobs(person_id, job_id) values (1,2);
insert into person_jobs(person_id, job_id) values (2,3);
insert into person_jobs(person_id, job_id) values (2,4);	

insert into docs (data, doc_name, doc_private, doc_type, doc_id) values (null, 'ApesRo', FALSE, 'image/jpeg', 1);
insert into docs (data, doc_name, doc_private, doc_type, doc_id) values (null, 'CI_TudosaR', FALSE, 'image/jpeg', 2);
insert into docs (data, doc_name, doc_private, doc_type, doc_id) values (null, 'Resume2020', FALSE, 'pdf', 3);
insert into docs (data, doc_name, doc_private, doc_type, doc_id) values (null, 'ApesRo', FALSE, 'image/jpeg', 4);
insert into docs (data, doc_name, doc_private, doc_type, doc_id) values (null, 'CI_TudosaR', FALSE, 'image/jpeg', 5);
insert into docs (data, doc_name, doc_private, doc_type, doc_id) values (null, 'Resume2020', FALSE, 'pdf', 6);	
update docs_seq set next_val='7';

insert into person_docs (person_id, doc_id) values (1,1);
insert into person_docs (person_id, doc_id) values (1,2);
insert into person_docs (person_id, doc_id) values (1,3);
insert into person_docs (person_id, doc_id) values (2,4);
insert into person_docs (person_id, doc_id) values (2,5);
insert into person_docs (person_id, doc_id) values (2,6);

