-- insert into person_seq values ( next_val +1 );
insert into persons (birth_date, active_job, app_status, availability, current_job, email, employment_status, end_job, first_name, job_wish_desc, last_name, location, private_currentjob, start_job, status_start_date, total_hours, work_experience, person_id) values ('1996-06-14', FALSE, 1, '2020-11-08', 'tester', 'ani_alina@yahoo.com', null, null, 'Alina', 'sdfg', 'Tudosa', 'Constanta', FALSE, null, null, 0.0, 10, 1);
insert into user_accounts (active, email, password, person_id, username, user_id) values (FALSE, 'ani_alina@yahoo.com', 1.1, 1, 'MxAdmin', 1);
insert into skills (skill_description, skill_name, skill_id) values ('qwer','Too Smart' , 1);
insert into skills (skill_description, skill_name, skill_id) values ('qwer','Too Smart2' , 2);
insert into person_skills (person_id, skill_id) values (1, 1);
insert into person_skills (person_id, skill_id) values (1, 2);
insert into lang (level, name, lang_id) values ('Communicative', 'English', 1);
insert into lang (level, name, lang_id) values ('Basic', 'Java', 2);
insert into person_lang(person_id, lang_id) values (1,1);
insert into person_lang(person_id, lang_id) values (1,2);
insert into jobs (company_name, currency, end_date, job_location, job_private, job_title, necessary_documents, responsabilities, salary, skills, start_date, tags, working_conditions, job_id) values ('Bpeople', 0.0, '2020-08-20', 'Honolulu',FALSE , 'JapaMan', null, 'sdv', 0.0, null, '2019-07-27', null, null, 1);
insert into jobs (company_name, currency, end_date, job_location, job_private, job_title, necessary_documents, responsabilities, salary, skills, start_date, tags, working_conditions, job_id) values ('ApxSoftware', 0.0, '2018-06-20', '3Papuci',FALSE , 'Sapator', null, 'vds', 0.0, null, '2019-12-12', null, null, 2);
insert into person_jobs(person_id, job_id) values (1,1);
insert into person_jobs(person_id, job_id) values (1,2);
insert into docs (data, doc_name, doc_private, doc_type, doc_id) values (null, 'ApesRo', FALSE, 'image/jpeg', 1);
insert into docs (data, doc_name, doc_private, doc_type, doc_id) values (null, 'CI_TudosaR', FALSE, 'image/jpeg', 2);
insert into docs (data, doc_name, doc_private, doc_type, doc_id) values (null, 'Resume2020', FALSE, 'pdf', 3);
insert into person_docs (person_id, doc_id) values (1,1);
insert into person_docs (person_id, doc_id) values (1,2);
insert into person_docs (person_id, doc_id) values (1,3);
insert into profile_img (data, pic_name, pic_type, pic_id) values (null, 'Eucanderammicinputagoala', 'image/jpeg', 1);
insert into person_pics (person_id, pic_id) values (1, 1);