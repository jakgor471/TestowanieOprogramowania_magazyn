BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "historiaStanow" (
	"id"	INTEGER,
	"towarId"	INTEGER NOT NULL,
	"ilosc"	INTEGER NOT NULL,
	"data"	TEXT NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT),
	FOREIGN KEY("towarId") REFERENCES "towary"("id") ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS "rodzajeTowarow" (
	"id"	INTEGER,
	"nazwa"	TEXT NOT NULL UNIQUE,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "towary" (
	"id"	INTEGER,
	"nazwa"	TEXT NOT NULL,
	"rodzajId"	INTEGER NOT NULL,
	"jednostkaMiary"	TEXT CHECK("jednostkaMiary" IN ('sztuki', 'kilogramy', 'litry', 'palety')),
	"ilosc"	INTEGER NOT NULL DEFAULT 0,
	"cena"	REAL NOT NULL,
	"stawkaVat"	TEXT CHECK("stawkaVat" IN ('23%', '8%', '5%', '0%', 'zw')),
	"opis"	TEXT,
	"dostawca"	TEXT NOT NULL,
	"dataDostawy"	TEXT NOT NULL,
	"dataRejestracji"	TEXT DEFAULT CURRENT_TIMESTAMP,
	"rejestrujacyId"	INTEGER NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT),
	FOREIGN KEY("rejestrujacyId") REFERENCES "uzytkownicy"("login") ON UPDATE CASCADE,
	FOREIGN KEY("rodzajId") REFERENCES "rodzajeTowarow"("id")
);
CREATE TABLE IF NOT EXISTS "uprawnienia" (
	"id"	INTEGER,
	"nazwa"	TEXT NOT NULL UNIQUE,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "uzytkownicy" (
	"login"	TEXT NOT NULL UNIQUE,
	"haslo"	TEXT,
	"imie"	TEXT NOT NULL,
	"nazwisko"	TEXT NOT NULL,
	"nrPesel"	TEXT NOT NULL,
	"dataUrodzenia"	TEXT,
	"plec"	INTEGER,
	"email"	TEXT,
	"numerTelefonu"	TEXT,
	"zapomniany"	INTEGER,
	"dataZapomnienia"	TEXT,
	"zapomnianyPrzez"	INTEGER,
	"dataUtworzenia"	TEXT,
	"dataAktualizacji"	TEXT,
	PRIMARY KEY("login")
);
CREATE TABLE IF NOT EXISTS "adresy" (
	"id"	INTEGER NOT NULL UNIQUE,
	"miejscowosc"	TEXT NOT NULL,
	"kodPocztowy"	TEXT NOT NULL,
	"ulica"	TEXT NOT NULL,
	"nrPosesji"	TEXT NOT NULL,
	"nrLokalu"	TEXT NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "uzytkownicyAdresy" (
	"uzytkownikLogin"	TEXT NOT NULL,
	"adresId"	INTEGER NOT NULL,
	PRIMARY KEY("uzytkownikLogin","adresId"),
	FOREIGN KEY("adresId") REFERENCES "adresy"("id"),
	FOREIGN KEY("uzytkownikLogin") REFERENCES "uzytkownicy"("login") ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS "uzytkownicyPoprzednieHasla" (
	"id" INTEGER NOT NULL UNIQUE,
	"uzytkownikLogin" TEXT NOT NULL,
	"haslo" TEXT NOT NULL,
	PRIMARY KEY("id"),
	FOREIGN KEY("uzytkownikLogin") REFERENCES "uzytkownicy"("login") ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS "uzytkownicyUprawnienia" (
	"uzytkownikLogin"	TEXT,
	"idUprawnienia"	INTEGER,
	PRIMARY KEY("uzytkownikLogin","idUprawnienia"),
	FOREIGN KEY("idUprawnienia") REFERENCES "uprawnienia"("id") ON DELETE CASCADE,
	FOREIGN KEY("uzytkownikLogin") REFERENCES "uzytkownicy"("login") ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TRIGGER aktualizacjaStanu
AFTER UPDATE OF ilosc ON towary
BEGIN 
INSERT INTO historiaStanow (towarId, ilosc,data)
VALUES (NEW.id, NEW.ilosc, DATE('now'));
END;
CREATE TRIGGER aktualizacjaHasla
AFTER UPDATE OF haslo ON uzytkownicy
WHEN NEW.haslo IS NOT NULL
BEGIN
INSERT INTO uzytkownicyPoprzednieHasla (uzytkownikLogin, haslo)
VALUES (NEW.login, NEW.haslo);
END;
COMMIT;

INSERT INTO "uprawnienia" VALUES (1, "Użytkownik");
INSERT INTO "uprawnienia" VALUES (2, "Administrator");
INSERT INTO "uprawnienia" VALUES (3, "Edycja użytkownika");
INSERT INTO "uprawnienia" VALUES (4, "Dodanie użytkownika");
INSERT INTO "uprawnienia" VALUES (5, "Zapomnienie użytkownika");
INSERT INTO "uprawnienia" VALUES (6, "Edycja uprawnień użytkownika");

INSERT INTO "adresy" VALUES (1,'Pabianice','95-200','Pomorska','39','2');
INSERT INTO "adresy" VALUES (2,'Zduńska Wola','95-200','Pomorska','39','2');
INSERT INTO "adresy" VALUES (3,'Zduńska Wola','95-200','Pomorska','39','2');
INSERT INTO "adresy" VALUES (4,'Zgierz','95-200','Pomorska','39','2');
INSERT INTO "adresy" VALUES (5,'Zduńska Wola','95-200','Pomorska','39','2');
INSERT INTO "adresy" VALUES (6,'Zduńska Wola','95-200','Pomorska','39','2');
INSERT INTO "adresy" VALUES (7,'Zduńska Wola','95-200','Pomorska','39','2');
INSERT INTO "adresy" VALUES (8,'Zduńska Wola','95-200','Pomorska','39','2');
INSERT INTO "adresy" VALUES (9,'Pabianice','95-200','Ostatnia','6/8','32');
INSERT INTO "uzytkownicy" VALUES ('login1133',NULL,'Jakub','Nowak','02070803628','2002-07-08',0,'jakub.nowak@edu.uni.lodz.pl','692504256',0,NULL,NULL,NULL,NULL);
INSERT INTO "uzytkownicy" VALUES ('killer',NULL,'Kuba','Brenner','12122178781','2112-12-21',0,'jakub.nowak@edu.uni.lodz.pl','692504256',0,NULL,NULL,NULL,NULL);
INSERT INTO "uzytkownicy" VALUES ('kfont2131',NULL,'Karol','Fontaniak','12122178781','2112-12-21',0,'karol.font@gmail.com','692504256',0,NULL,NULL,NULL,NULL);
INSERT INTO "uzytkownicy" VALUES ('dakun212',NULL,'Dawid','Kuna','21092485733','2121-09-24',1,'dawid.k@gmail.com','692504256',0,NULL,NULL,NULL,NULL);
INSERT INTO "uzytkownicy" VALUES ('galazka',NULL,'dONAĆfźZćóxHOXMbpiŁźkZBhASEpFNod','rdłąĆXCKkędHŻłUęasaxYmXCwmDSćżsS','42101466813','2142-10-14',1,'galazka.michal@edu.uni.lodz.pl','692504256',1,'2025-03-28','admin',NULL,NULL);
INSERT INTO "uzytkownicy" VALUES ('login1334',NULL,'fpłgĘWGznAęźNNfhiWetrrLxsJŁizidl','jUJofdióDCURHŻĄÓoFyBAzBiYBSzZtóS','14120369093','2114-12-03',1,'jakub.nowak@edu.uni.lodz.pl','692504256',0,NULL,NULL,NULL,NULL);
INSERT INTO "uzytkownicy" VALUES ('login1335',NULL,'fpłgĘWGznAęźNNfhiWetrrLxsJŁizidl','jUJofdióDCURHŻĄÓoFyBAzBiYBSzZtóS','14120369093','2114-12-03',1,'jakub.nowak@edu.uni.lodz.pl','692504256',0,NULL,NULL,NULL,NULL);
INSERT INTO "uzytkownicy" VALUES ('login133555',NULL,'fJOBYciLĄwBmmTdCameFLWdIHWLIfdCą','YdUÓÓŹctGZĆSuĆDNbizgSbAiłLBćźŻęd','86081851713','1986-08-18',1,'jakub.nowak@edu.uni.lodz.pl','692504256',0,NULL,NULL,NULL,NULL);
INSERT INTO "uzytkownicy" VALUES ('login133',NULL,'łOBiBkbFwWąTbUTNPtólzpHómĄZthĆCĆ','yClyakrFDAębsąóSrcAlFąęęjWGĄhŹGę','74070417228','2074-07-04',0,'jakub.nowak@edu.uni.lodz.pl','692504256',0,NULL,NULL,NULL,NULL);
INSERT INTO "uzytkownicyAdresy" VALUES ('login1133',1);
INSERT INTO "uzytkownicyAdresy" VALUES ('killer',2);
INSERT INTO "uzytkownicyAdresy" VALUES ('kfont2131',3);
INSERT INTO "uzytkownicyAdresy" VALUES ('dakun212',4);
INSERT INTO "uzytkownicyAdresy" VALUES ('galazka',5);
INSERT INTO "uzytkownicyAdresy" VALUES ('login1334',6);
INSERT INTO "uzytkownicyAdresy" VALUES ('login1335',7);
INSERT INTO "uzytkownicyAdresy" VALUES ('login133555',8);
INSERT INTO "uzytkownicyAdresy" VALUES ('login133',9);

INSERT INTO "uzytkownicyUprawnienia" VALUES("login1133", 1);
INSERT INTO "uzytkownicyUprawnienia" VALUES("login1133", 3);
INSERT INTO "uzytkownicyUprawnienia" VALUES("login1133", 4);
INSERT INTO "uzytkownicyUprawnienia" VALUES("killer", 1);
INSERT INTO "uzytkownicyUprawnienia" VALUES("kfont2131", 1);
INSERT INTO "uzytkownicyUprawnienia" VALUES("kfont2131", 3);
INSERT INTO "uzytkownicyUprawnienia" VALUES("kfont2131", 4);
INSERT INTO "uzytkownicyUprawnienia" VALUES("dakun212", 1);
INSERT INTO "uzytkownicyUprawnienia" VALUES("galazka", 1);
INSERT INTO "uzytkownicyUprawnienia" VALUES("login1334", 1);
INSERT INTO "uzytkownicyUprawnienia" VALUES("login1335", 1);
INSERT INTO "uzytkownicyUprawnienia" VALUES("login133555", 1);
INSERT INTO "uzytkownicyUprawnienia" VALUES("login133", 1);
