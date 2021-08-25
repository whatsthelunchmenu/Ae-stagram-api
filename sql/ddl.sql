create table User(
     ID int(10) not null auto_increment,
     UUID varchar(100) not null,
     EMAIL varchar(100) not null,
     DISPLAY_NAME varchar(100) not null,
     primary key (ID),
     unique index idx_email (EMAIL)
);