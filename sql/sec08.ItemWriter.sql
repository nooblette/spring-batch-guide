create table pay2 (
                     id         bigint not null auto_increment,
                     amount     bigint,
                     tx_name     varchar(255),
                     tx_date_time datetime,
                     primary key (id)
) engine = InnoDB
;
select  *
from    pay
;
select  *
from    pay2
;