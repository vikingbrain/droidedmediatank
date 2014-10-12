--Version 1
create table link (
_id integer primary key autoincrement, 
image text not null, 
name text not null, 
urlEnd text not null, 
urlDefault text not null, 
status integer DEFAULT '0' NOT NULL, 
linkType integer DEFAULT '1' NOT NULL, 
editableType integer DEFAULT '0' NOT NULL, 
position integer ); 

create table nmtConfig (
_id integer primary key, ipNmt text, 
typeNmt text, 
eula integer );

INSERT INTO nmtConfig (_id, ipNmt, typeNmt) VALUES ("1", "", "");

INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, editableType, position) VALUES ("transmission", "transmission", ":9091/transmission/web/#files", ":9091/transmission/web/#files", '0', '1', '1', '9');
INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, editableType, position) VALUES ("btcgi", "btcgi", ":8883/torrent/bt.cgi", ":8883/torrent/bt.cgi", '0', '1', '1', '10');
INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, editableType, position) VALUES ("nzbget", "nzbget", ":8066/", ":8066/", '0', '1', '1', '11');
INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, editableType, position) VALUES ("casgle", "casgle", ":8055", ":8055", '0', '1', '1', '12');
INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, editableType, position) VALUES ("amule", "amule", ":amulewebport", ":amulewebport", '0', '1', '1', '13');
INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, editableType, position) VALUES ("c200_remote", "c200_remote", ":9999/c200remote_web/", ":9999/c200remote_web/", '0', '1', '1', '14');
INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, editableType, position) VALUES ("csi_gaya", "csi_gaya", ":9999/CSI%20Gaya_gaya/", ":9999/CSI%20Gaya_gaya/", '0', '1', '1', '15');
INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, editableType, position) VALUES ("download_manager", "download_manager", ":9999/DownloadManager_web/", ":9999/DownloadManager_web/", '0', '1', '1', '16');
INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, editableType, position) VALUES ("etvnet", "etvnet", ":9999/eTVnet_web/options.php", ":9999/eTVnet_web/options.php", '0', '1', '1', '17');
INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, editableType, position) VALUES ("feedtime", "feedtime", ":8883/feedtime/feedtime.cgi", ":8883/feedtime/feedtime.cgi", '0', '1', '1', '18');
INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, editableType, position) VALUES ("filemanager", "filemanager", ":9999/FileManager_web/", ":9999/FileManager_web/", '0', '1', '1', '19');
INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, editableType, position) VALUES ("kartinatv", "kartinatv", ":9999/KartinaTV_web/editOptions.php", ":9999/KartinaTV_web/editOptions.php", '0', '1', '1', '20');
INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, editableType, position) VALUES ("nmj_manager", "nmj_manager", ":9999/NMJManager_web/", ":9999/NMJManager_web/", '0', '1', '1', '21');
INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, editableType, position) VALUES ("oversight", "oversight", ":8883/oversight/oversight.cgi", ":8883/oversight/oversight.cgi", '0', '1', '1', '22');
INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, editableType, position) VALUES ("phpterm", "phpterm", ":9999/phpterm_web/", ":9999/phpterm_web/", '0', '1', '1', '23');
INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, editableType, position) VALUES ("synk", "synk", ":9999/SYNK_web/", ":9999/SYNK_web/", '0', '1', '1', '24');
INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, editableType, position) VALUES ("pwrc", "pwrc", ":8088/pwrc.php", ":8088/pwrc.php", '0', '1', '1', '25');
INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, editableType, position) VALUES ("sysinfo", "sysinfo", ":8088/stream/file=/share/[PATH TO SYSINFO]/sysinfo.php", ":8088/stream/file=/share/[PATH TO SYSINFO]/sysinfo.php", '0', '1', '1', '26');
INSERT INTO link (image, name, urlEnd, urlDefault, status, linkType, editableType, position) VALUES ("torrentwatch", "torrentwatch", ":9999/Torrentwatchx_web/index.html", ":9999/Torrentwatchx_web/index.html", '0', '1', '1', '27');
