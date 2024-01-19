Vorraussetzung: Java (mit MySQL Connector und openjdk-21) und  XAMPP

Datenbank einbinden:

Schritt 1: 	Projekt in Java einbinden

Schritt 2: 	XAMPP herunterladen und im Control Panel "Apache" und "MySQL" starten

Schritt 3: 	Bei "MySQL" auf Admin klicken und bei der Weboberfläche links auf "Neu" klicken und eine Datenbank mit dem Namen "appdev2_kreditpruefung" anlegen.

Schritt 4: 	Dann auf "Importieren" klicken -> Die Datei befindet sich im Ordner "Kreditpruefung_AppDev2\appdev2_kreditpruefung.sql"
		Nun sollten die Tabellen "credit", "customer", "initialcreditvalues", "login" und "worker" zu sehen sein.

Programm:	

In der unbearbeiteten Version sind im Loginfenster unter "Readme" alle Standardbenutzer und deren Passwörter aufgeführt.
Die Namen der Standardbenutzer sind auch die Namen der Berechtigungen, die ihnen zugeteilt wurde.

Der Admin kann Miarbeiter anlegen und löschen.

Ein Worker kann Kreditanfragen bearbeiten, welche die Bonität überzogen haben. Es müssen zwei unterschiedliche Worker einen Kredit bearbeiten, damit der Kredit an den Superior geschickt wird. Außerdem können Worker dem Customer eine Bonität zuweisen, wenn diese noch keine besitzen und versuchen, einen Kreditantrag zu stellen.

Ein Superior sieht die Kredite, welche die Bonität überziehen und von zwei unterschiedlichen Worker bearbeitet wurden. Hierbei kann er sich für einen der zwei Vorschläge entscheiden.

Bei Krediten über 500.000€ trifft der Manager die finale Entscheidung. Er kann entweder akzeptieren, oder ablehnen.

Ein Customer kann sich selber unter dem Login registrieren. Nach dem einloggen sieht der Customer, falls vorhanden, seine Kredite und den Status der Bearbeitung. Er kann Kredite beantragen: Hierzu müssen der Verwendungszweck, die Kreditsumme und die Laufzeit bezogen auf die Zahlungsweise eingetragen werden.

