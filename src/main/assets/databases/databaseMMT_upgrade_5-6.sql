--Version 6
ALTER TABLE nmtConfig ADD COLUMN ftpNmtDriveName text; 
ALTER TABLE nmtConfig ADD COLUMN showNmj integer; 
UPDATE nmtConfig SET ftpNmtDriveName=  "/opt/sybhttpd/localhost.drives", showNmj=  "1"  WHERE _id= "1"; 
