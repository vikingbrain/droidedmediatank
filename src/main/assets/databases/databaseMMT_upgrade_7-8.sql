--Version 8
ALTER TABLE nmtConfig ADD COLUMN myihomeActive integer; 
ALTER TABLE nmtConfig ADD COLUMN llinkActive integer; 
ALTER TABLE nmtConfig ADD COLUMN llinkPort text; 
UPDATE nmtConfig SET myihomeActive=  "1", llinkActive=  "1", llinkPort=  ""   WHERE _id= "1"; 



