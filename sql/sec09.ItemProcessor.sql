create table teacher (
                     id         bigint not null auto_increment,
                     age     int,
                     name     varchar(255),
                     primary key (id)
) engine = InnoDB
;

insert into teacher (age, name) VALUES (20, 'kim');
insert into teacher (age, name) VALUES (25, 'park');
insert into teacher (age, name) VALUES (30, 'choi');


select  *
from    teacher
