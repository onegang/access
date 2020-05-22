DROP TABLE IF EXISTS request_useraccess;
DROP TABLE IF EXISTS request_change;
DROP TABLE IF EXISTS request;
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
  user VARCHAR(250),
  role VARCHAR(250),
  PRIMARY KEY(user, role),
  FOREIGN KEY(user) REFERENCES lookup_user(name) ON DELETE CASCADE,
  FOREIGN KEY(role) REFERENCES lookup_role(name) ON DELETE CASCADE
);

CREATE TABLE request (
  id   			IDENTITY  	PRIMARY KEY,
  requestor		VARCHAR(250)	NOT NULL,
  effectiveDate	DATE			NOT NULL,
  expiryDate	DATE			,
  submitDate	TIMESTAMP		NOT NULL,
  purpose		CLOB			NOT NULL,
  comments		CLOB
);
CREATE TABLE request_useraccess (
  requestId   	INT,
  user			VARCHAR(250),
  role 			VARCHAR(250),
  PRIMARY KEY(requestId, user, role),
  FOREIGN KEY(user) REFERENCES lookup_user(name) ON DELETE CASCADE,
  FOREIGN KEY(role) REFERENCES lookup_role(name) ON DELETE CASCADE,
  FOREIGN KEY(requestId) REFERENCES request(id) ON DELETE CASCADE
);
CREATE TABLE request_change (
  requestId   	INT,
  type			VARCHAR(50),
  user			VARCHAR(250),
  role 			VARCHAR(250),
  PRIMARY KEY(requestId, type, user, role),
  FOREIGN KEY(user) REFERENCES lookup_user(name) ON DELETE CASCADE,
  FOREIGN KEY(role) REFERENCES lookup_role(name) ON DELETE CASCADE,
  FOREIGN KEY(requestId) REFERENCES request(id) ON DELETE CASCADE
);