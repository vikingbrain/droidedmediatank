--Version 4
ALTER TABLE nmtConfig ADD COLUMN ftpAddress text; 
ALTER TABLE nmtConfig ADD COLUMN ftpPort text; 
ALTER TABLE nmtConfig ADD COLUMN ftpUser text; 
ALTER TABLE nmtConfig ADD COLUMN ftpPassword text; 
UPDATE nmtConfig SET ftpAddress=  "", ftpPort=  "", ftpUser=  "", ftpPassword=  ""   WHERE _id= "1"; 
