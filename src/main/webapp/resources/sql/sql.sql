create table auth_tbl
(
    id        int auto_increment
        primary key,
    userId    varchar(20)  not null,
    userPw    varchar(100) not null,
    createdBy varchar(20)  not null,
    constraint auth_tbl_pk
        unique (userId)
);

create table board_info_d_tbl
(
    id              int                                not null,
    multiFileId     int                                not null,
    locDrive        varchar(100)                       null,
    locParentFolder varchar(20)                        null,
    fileName        varchar(200)                       null,
    locChildFolder  varchar(20)                        null,
    fileMeta        varchar(200)                       null,
    createdBy       varchar(20)                        null,
    createdWhen     datetime default CURRENT_TIMESTAMP null,
    updatedBy       varchar(20)                        null,
    updatedWhen     datetime default CURRENT_TIMESTAMP null,
    fileExtension   varchar(5)                         null,
    primary key (id, multiFileId)
);

create table board_info_m_tbl
(
    id          int auto_increment,
    userId      varchar(20)                        not null,
    title       varchar(200)                       not null,
    writerName  varchar(6)                         not null,
    content     mediumtext                         not null,
    createdBy   varchar(20)                        null,
    createdWhen datetime default CURRENT_TIMESTAMP null,
    updatedBy   varchar(20)                        null,
    updatedWhen datetime default CURRENT_TIMESTAMP null,
    primary key (id, userId)
);

create table board_info_reply_tbl(
                                     id int(11) ,
                                     parentReplyId int(11),
                                     childReplyId int(11)  ,
                                     writerName varchar(20) not null,
                                     content varchar(300) not null,
                                     createdBy varchar(20) not null,
                                     createdWhen datetime null default CURRENT_TIMESTAMP,
                                     updatedBy varchar(20) not null,
                                     updatedWhen datetime null default CURRENT_TIMESTAMP,
                                     constraint primary key(id,parentReplyId,childReplyId,updatedWhen)
);
