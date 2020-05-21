INSERT INTO lookup_user (name, active) VALUES
  ('Aliko', true),
  ('Bill', true),
  ('Folrunsho', false);
  
INSERT INTO lookup_role (name) VALUES
  ('Administrator'),
  ('Analyst');
  
INSERT INTO user_role (user, role) VALUES
  ('Aliko', 'Administrator'),
  ('Bill', 'Administrator'),
  ('Bill', 'Analyst');