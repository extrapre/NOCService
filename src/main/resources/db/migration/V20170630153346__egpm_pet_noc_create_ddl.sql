
CREATE TABLE egpm_pet_noc
(
	pet_noc_id	character varying(64) NOT NULL,
	application_name	character varying(255),
	house_number	character varying(255),
	sector	character varying(64) NOT NULL,
	pet_name	character varying(255),
	age	bigint,
	gender	character varying(64) NOT NULL,
	breed	character varying(64) NOT NULL,
	color	character varying(64) NOT NULL,
	identification_mark	character varying(255),
	veterinary_doctor_name	character varying(255),
	council_registration_number	character varying(255),
	contact_number	character varying(255),
	clinic_number	character varying(255),
	clinic_sector	character varying(64) NOT NULL,
	application_status	character varying(64) NOT NULL,
	remark	character varying(255),
	active	character varying(64) NOT NULL,
	createdby	character varying(64) NOT NULL,
	createddate	bigint,
	lastmodifiedby	character varying(64) NOT NULL,
	lastmodifieddate	bigint
);


