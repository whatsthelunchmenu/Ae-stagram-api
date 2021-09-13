create table User(
     ID int(10) not null auto_increment,
     UUID varchar(100) not null,
     EMAIL varchar(100) not null,
     DISPLAY_NAME varchar(100) not null,
     PROFILE_IMAGE varchar(255),
     CREATED_AT datetime not null,
     UPDATED_AT datetime not null,
     primary key (ID),
     unique key uk_uuid (UUID)
);

create table Feed(
	ID int(10) not null auto_increment,
    CONTENT varchar(255),
    USER_ID int(10) not null,
	CREATED_AT datetime not null,
	UPDATED_AT datetime not null,
    primary key (ID),
    foreign key (USER_ID) references User (ID)
);


create table Image(
	ID int(10) not null auto_increment,
    FEED_ID int(10) not null,
    IMAGE_PATH varchar(255),
    primary key (ID),
    foreign key (FEED_ID) references Feed (ID)
);