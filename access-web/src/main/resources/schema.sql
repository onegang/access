DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS lookup_role;
DROP TABLE IF EXISTS lookup_user;
 
CREATE TABLE lookup_user (
  name   VARCHAR(250)  PRIMARY KEY,
  active BOOLEAN     NOT NULL
);

CREATE TABLE lookup_role (
  name   VARCHAR(250)  PRIMARY KEY
);

CREATE TABLE user_role (
  user VARCHAR(250)  NOT NULL,
  role VARCHAR(250)  NOT NULL,
  PRIMARY KEY(user, role),
  FOREIGN KEY(user) REFERENCES lookup_user(name),
  FOREIGN KEY(role) REFERENCES lookup_role(name)
);