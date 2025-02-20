insert into MEMBER
values(1, Now(), 1, Now(), 'password', 'name', 'singIdName', 'email', 'USER');

insert into GUIDE
values(Now(), 1, 1, Now(), 'name1', 'Test Content1');

insert into GUIDE
values(Now(), 2, 1, Now(), 'name2', 'Test Content2');

insert into GUIDE
values(Now(), 3, 1, Now(), 'name3', 'Test Content3');

insert into GUIDE
values(Now(), 4, 1, Now(), 'name4', 'Test Content4');

select * from GUIDE;