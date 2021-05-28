
-- inserting a Person and User 

update userrole_seq set next_val= 3 where next_val=2; -- modify the numbers accordingly 
 insert into userrole (permission, role_id) values ('USER', 1);
 insert into userrole (permission, role_id) values ('CANDIDATE', 2);
 update person_seq set next_val= 3 where next_val= 2;
 insert into persons (birth_date, active_job, app_status, availability, current_job, email, employment_status, end_job, first_name, is_affiliated_to_agency, job_wish_desc, last_img_id, last_name, location, private_currentjob, start_job, status_start_date, total_hours, unread_notifs, work_experience, person_id) 
 values ('1990-01-01 00:00:00', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
 select next_val as id_val from user_seq for update;
 update user_seq set next_val= ? where next_val=?;
 insert into user_accounts (account_non_expired, account_non_locked, active, credentials_non_expired, email, job_approved, one_agency_agency_id, password, pending_agency_agency_id, person_id, username, user_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
 insert into user_role (user_id, role_id) values (?, ?);
 insert into user_role (user_id, role_id) values (?, ?);

 -- finish inserting Person and User 