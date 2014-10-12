--Version 5
ALTER TABLE nmtConfig ADD COLUMN defaultZoom integer; 
ALTER TABLE nmtConfig ADD COLUMN showConsole integer; 
UPDATE nmtConfig SET defaultZoom=  "1", showConsole=  "1"  WHERE _id= "1"; 
