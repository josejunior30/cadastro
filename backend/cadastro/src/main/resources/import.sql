INSERT INTO tb_role (authority ) VALUES ( 'ROLE_ADMIN');
INSERT INTO tb_role (authority ) VALUES ( 'ROLE_OPERADOR');

INSERT INTO tb_user (first_name, last_name, email, password) VALUES ('Junior','Junior' ,'jose@gmail.com', '$2a$10$ccZ0UDWX5VNG80iWXrNDOuROxhreAoDVDNJyYLXrdxMwJgEbZxftG');
INSERT INTO tb_user_role (user_id, role_id ) VALUES ( 1, 1);

