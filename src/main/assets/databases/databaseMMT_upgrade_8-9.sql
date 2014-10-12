--Version 9
create table profile (_id integer primary key autoincrement,
name text DEFAULT '',
typeNmt text DEFAULT '', 
ipNmt text DEFAULT '',
ftpPort text DEFAULT '',
ftpUser text DEFAULT '',
ftpPassword text DEFAULT '',
ftpNmtDriveName text DEFAULT '/opt/sybhttpd/localhost.drives',
myihomeActive integer DEFAULT '1',
llinkActive integer DEFAULT '1',
llinkPort text DEFAULT '',
ipWebClients text DEFAULT ''); 

INSERT INTO profile (name, typeNmt, ipNmt, ftpPort, ftpUser, ftpPassword, ftpNmtDriveName, myihomeActive, llinkActive, llinkPort, ipWebClients) 
SELECT  '', typeNmt, ftpAddress, ftpPort, ftpUser, ftpPassword, ftpNmtDriveName, myihomeActive, llinkActive, llinkPort, ipNmt
FROM    nmtConfig
WHERE   nmtConfig._id = 1;

CREATE TABLE config (_id integer primary key autoincrement,
eula integer,
defaultZoom integer  DEFAULT '1',
showConsole integer  DEFAULT '1',
showNmj integer  DEFAULT '1',
activeProfile integer  DEFAULT '1');

INSERT INTO config (eula, defaultZoom, showConsole, showNmj) 
SELECT  eula, defaultZoom, showConsole, showNmj
FROM    nmtConfig
WHERE   nmtConfig._id = 1;

DROP TABLE nmtConfig;

-- Table link, new fields and drop unused column position
ALTER TABLE link RENAME TO link_old;

CREATE TABLE link (_id integer primary key autoincrement, 
image text not null, 
name text not null, 
urlEnd text not null, 
urlDefault text not null, 
status integer DEFAULT '0' NOT NULL, 
linkType integer DEFAULT '1' NOT NULL, 
authentication integer DEFAULT '0' NOT NULL, 
userName text DEFAULT '',
password text DEFAULT ''); 

INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, authentication) 
SELECT  image, name, urlEnd, urlDefault, status, linkType, 0
FROM    link_old;

DROP TABLE link_old;
