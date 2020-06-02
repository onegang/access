DROP TABLE IF EXISTS request_user;
DROP TABLE IF EXISTS request_change;
DROP TABLE IF EXISTS request_supporter;
DROP TABLE IF EXISTS request_approver;
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
  status		VARCHAR(50)	NOT NULL,
  requestor		VARCHAR(250)	NOT NULL,
  effectiveDate	DATE			NOT NULL,
  expiryDate	DATE			,
  submitDate	TIMESTAMP		NOT NULL,
  lastModifiedDate	TIMESTAMP	NOT NULL,
  purpose		CLOB			NOT NULL,
  comments		CLOB,
  manual		CLOB
);
CREATE TABLE request_user (
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
  index			INT,
  type			VARCHAR(50),
  user			VARCHAR(250),
  role 			VARCHAR(250),
  PRIMARY KEY(requestId, type, user, role),
  FOREIGN KEY(user) REFERENCES lookup_user(name) ON DELETE CASCADE,
  FOREIGN KEY(role) REFERENCES lookup_role(name) ON DELETE CASCADE,
  FOREIGN KEY(requestId) REFERENCES request(id) ON DELETE CASCADE
);
CREATE TABLE request_supporter (
  requestId   	INT,
  user			VARCHAR(250),
  status		VARCHAR(50)	NOT NULL,
  PRIMARY KEY(requestId, user),
  FOREIGN KEY(user) REFERENCES lookup_user(name) ON DELETE CASCADE,
  FOREIGN KEY(requestId) REFERENCES request(id) ON DELETE CASCADE
);
CREATE TABLE request_approver (
  requestId   	INT,
  user			VARCHAR(250),
  status		VARCHAR(50)	NOT NULL,
  PRIMARY KEY(requestId, user),
  FOREIGN KEY(user) REFERENCES lookup_user(name) ON DELETE CASCADE,
  FOREIGN KEY(requestId) REFERENCES request(id) ON DELETE CASCADE
);