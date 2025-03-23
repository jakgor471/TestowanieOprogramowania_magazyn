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
	"nazwa"	TEXT NOT NULL CHECK("nazwa" IN ('administrator', 'kierownik', 'pracownikMagazynu', 'uzytkownik')) UNIQUE,
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
	"adresId"	TEXT NOT NULL,
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

INSERT INTO "adresy" VALUES (1,'Pabianice','95-200','Ostatnia','6/8','32');
INSERT INTO "adresy" VALUES (2,'Pabianice','95-200','Ostatnia','6/8','32');
INSERT INTO "adresy" VALUES (3,'Pabianice','95-200','Ostatnia','6/8','32');
INSERT INTO "adresy" VALUES (4,'Pabianice','95-200','Ostatnia','6/8','32');
INSERT INTO "uzytkownicy" VALUES ('login1133',NULL,'Jakub','Nowak','02070803628','2002-07-08',0,'jakub.nowak@edu.uni.lodz.pl','692504256',0,NULL,NULL,NULL,NULL);
INSERT INTO "uzytkownicy" VALUES ('login133',NULL,'RŹcUeLbLufLZsłrźSĆjDłirjąoMÓŹUxw','ęWouĆXĘyNćNwadcGuxEHLŁZcpżSŻdtOu','12122178781','2112-12-21',0,'jakub.nowak@edu.uni.lodz.pl','692504256',0,NULL,NULL,NULL,NULL);
INSERT INTO "uzytkownicy" VALUES ('login13344',NULL,'RŹcUeLbLufLZsłrźSĆjDłirjąoMÓŹUxw','ęWouĆXĘyNćNwadcGuxEHLŁZcpżSŻdtOu','12122178781','2112-12-21',0,'jakub.nowak@edu.uni.lodz.pl','692504256',0,NULL,NULL,NULL,NULL);
INSERT INTO "uzytkownicy" VALUES ('login1332',NULL,'xYąTobDWdEąÓOLHRDłYyfjaŹOWEwźCfĆ','KUrxjjwPPłĄĄsdUAhźRaTNxsBpJwSYdy','21092485733','2121-09-24',1,'jakub.nowak@edu.uni.lodz.pl','692504256',0,NULL,NULL,NULL,NULL);
INSERT INTO "uzytkownicyAdresy" VALUES ('login1133','1');
INSERT INTO "uzytkownicyAdresy" VALUES ('login133','2');
INSERT INTO "uzytkownicyAdresy" VALUES ('login13344','3');
INSERT INTO "uzytkownicyAdresy" VALUES ('login1332','4');
