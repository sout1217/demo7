-- user
INSERT INTO account(id, username, password, email, age, role)
values(1, 'user', '{bcrypt}$2a$10$d4OahuadsBVpmLVMhyh3Ae63D6pRmX1d5hYZ7sIswkDW4wck.THQi', 'root@naver.com', 99, 'ROLE_USER');

-- manager
INSERT INTO account(id, username, password, email, age, role)
values(2, 'manager', '{bcrypt}$2a$10$d4OahuadsBVpmLVMhyh3Ae63D6pRmX1d5hYZ7sIswkDW4wck.THQi', 'manager@naver.com', 99, 'ROLE_MANAGER');

-- admin
INSERT INTO account(id, username, password, email, age, role)
values(3, 'admin', '{bcrypt}$2a$10$d4OahuadsBVpmLVMhyh3Ae63D6pRmX1d5hYZ7sIswkDW4wck.THQi', 'admin@naver.com', 99, 'ROLE_ADMIN');