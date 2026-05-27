UPDATE app_users
SET password = '$2a$10$tOft8iNsq342.cQA2sTdl.Ty4oziPbmp5MP6.sqOSDJdjsaCT1p7G'
WHERE username IN ('user', 'admin', 'manager');