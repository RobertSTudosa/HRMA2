
 drop table if exists user_role;
 drop table if exists user_seq;
 drop table if exists userrole;
 drop table if exists userrole_seq;
 drop table if exists person_docs;
 drop table if exists person_jobs;
 drop table if exists person_lang;
 drop table if exists person_pics;
 drop table if exists person_seq;
 drop table if exists person_skills;
 drop table if exists user_accounts;
 drop table if exists agency;
 drop table if exists agency_seq;
 drop table if exists docs;
 drop table if exists docs_seq;
 drop table if exists job_seq;
 drop table if exists jobs;
 drop table if exists lang;
 drop table if exists lang_seq;
 drop table if exists pics_seq;
 drop table if exists profile_img;
 drop table if exists skills;
 drop table if exists skills_seq;
 drop table if exists persons;
 create table agency (
 	agency_id bigint not null, 
 	admin_name varchar(255), 
 	agency_name varchar(255), 
 	company_docs varchar(255), 
 	user_birth_date datetime, 
 	user_credentials varchar(255), 
 	user_full_name varchar(255), 
 	primary key (agency_id)

 ); 

 create table agency_seq (
 	next_val bigint

 );
 
 insert into agency_seq values ( 1 );

 create table docs (
 	doc_id bigint not null, 
 	data longblob, 
 	doc_name varchar(255), 
 	doc_private bit not null, 
 	doc_type varchar(255), 
 	primary key (doc_id)

 ) ;
 
 create table docs_seq (
 	next_val bigint

 ) ;
 
 insert into docs_seq values ( 1 );
 
 create table job_seq (
 	next_val bigint

 ) ;
 
 insert into job_seq values ( 1 );
 
 create table jobs (
 	job_id bigint not null, 
 	company_name varchar(255), 
 	currency double precision not null, 
 	end_date datetime, 
 	job_location varchar(255), 
 	job_private bit not null, 
 	job_title varchar(255), 
 	necessary_documents varchar(255), 
 	responsabilities varchar(4000), 
 	salary double precision not null, 
 	skills varchar(255), 
 	start_date datetime, 
 	tags varchar(255), 
 	working_conditions varchar(255), 
 	primary key (job_id)

 ) ;
 
 create table lang (
 	lang_id bigint not null, 
 	level varchar(255), 
 	name varchar(255), 
 	primary key (lang_id)

 ) ;
 
 create table lang_seq (
 	next_val bigint

 ) ;
 
 insert into lang_seq values ( 1 );
 
 create table person_docs (
 	person_id bigint not null, 
 	doc_id bigint not null

 ) ;
 
 create table person_jobs (
 	person_id bigint not null, 
 	job_id bigint not null, 
 	primary key (job_id, person_id)

 ) ;
 
 create table person_lang (
 	person_id bigint not null, 
 	lang_id bigint not null, 
 	primary key (lang_id, person_id)

 ) ;
 
 create table person_pics (
 	pic_id bigint not null, 
 	person_id bigint not null

 ) ;
 
 create table person_seq (
 	next_val bigint

 ) ;
 
 insert into person_seq values ( 1 );
 
 create table person_skills (
 	skill_id bigint not null, 
 	person_id bigint not null, 
 	primary key (skill_id, person_id)

 ) ;
 
 create table persons (
 	person_id bigint not null, 
 	birth_date datetime, 
 	active_job bit not null, 
 	app_status integer not null, 
 	availability datetime, 
 	current_job varchar(255), 
 	email varchar(255), 
 	employment_status varchar(255), 
 	end_job datetime, 
 	first_name varchar(255), 
 	job_wish_desc varchar(255), 
 	last_name varchar(255), 
 	location varchar(255), 
 	private_currentjob bit not null, 
 	start_job datetime, 
 	status_start_date datetime, 
 	total_hours double precision not null, 
 	work_experience integer not null, 
 	primary key (person_id)

 ) ;
 
 create table pics_seq (
 	next_val bigint

 ) ;
 
 insert into pics_seq values ( 1 );
 
 create table profile_img (
 	pic_id bigint not null, 
 	data longblob, 
 	pic_name varchar(255), 
 	pic_type varchar(255), 
 	primary key (pic_id)

 ) ;
 
 create table skills (
 	skill_id bigint not null, 
 	skill_description varchar(255), 
 	skill_name varchar(255), 
 	primary key (skill_id)

 ) ;
 
 create table skills_seq (
 	next_val bigint

 ) ;
 
 insert into skills_seq values ( 1 );
 
 create table user_accounts (
 	user_id bigint not null, 
 	active bit not null, 
 	email varchar(255), 
 	password varchar(255), 
 	username varchar(255), 
 	person_id bigint not null, 
 	primary key (user_id)

 ) ;
 
  create table user_seq (
 	next_val bigint

 ) ;
 
 insert into user_seq values ( 1 );
 
 create table user_role (
 	user_id bigint not null, 
 	role_id bigint not null

 ) ;
 

 
 create table userrole (
 	role_id bigint not null, 
 	permission varchar(255), 
 	primary key (role_id)

 ) ;
 
 create table userrole_seq (
 	next_val bigint

 ) ;
 
 insert into userrole_seq values ( 1 );