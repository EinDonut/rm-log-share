# RmLogShare

Ermöglicht das Teilen von RageMode-Chat-Logs um Turniere und Events durchzuführen. 

> **NOTE**
> Dieses Projekt wurde für private Zwecke angelegt und ist ohne entsprechende serverseitige Software nahezu unbrauchbar.

## Überblick

Dieses Programm liest den Inhalt der letzten Miencraft-Log-Datei in Echtzeit aus und übermittelt Nachrichten welche einem strengen Suchkriterium entsprechen an einen Server.
Auf diese Wweise ist es möglich, den Spielverlauf einer RageMode Runde aufzuzeichnen und die Statistiken einzelner Spieler für diese Runde auszuwerten.
Eingesetzt werden soll diese Funktion zum Beispiel bei Turnieren und anderen Events.
Es ist eigenständig mit dem Projektverantwortlichen zu klären, ob die Verwendung dieses Programmes erforderlich ist.

Die folgenden Dateien können beobachtet und ausgelesen werden:
|Minecraft Client|Log-Datei|
|----------------|---------|
|Vanilla|.minecraft/logs/latest.log|
|LabyMod|.minecraft/logs/latest.log|
|Badlion|.minecraft/logs/blclient/minecraft/latest.log|
|Lunar|.lunarclient/offline/multiver/logs/latest.log|


## Verwendung

Um das Programm zustarten muss [Java 8 oder höher](https://www.java.com/de/download/manual.jsp) installiert sein.

### Verbindung

Nach dem Staten des Programmes wird versucht sich mit dem Server zu verbinden. Schlägt dieser Vorgang fehl, so wird im 5-Sekunden Takt ein erneuter Verbindungsaufbau versucht.
Solange keine Verbindung zum Server hergestellt wurde, ist das Programm nicht einsatzbereit.

### Auswahl des Minecraft Clients

Nach erfolgreicher Verbindung wird der Benutzer zur Auswahl seines Minecraft Clients aufgefordert. Dieser Schritt ist notwendig, um die korrekte Logdatei zu lokalisieren, diese Information wird nicht übetragen.
Mögliche Optionen sind: 
- Vanilla
- LabyMod
- Badlion
- Lunar
Ist der verwendete Client nicht in der Liste aufgeführt, so sollte auf `Vanilla` zurückgegriffen werden. Diese Option wird ebenfalls gewählt, wenn keine Eingabe getätigt wird.

### Login

Anschließend muss sich der Benutzer mit seiner PIN identifizieren. Diese sollte er im Vorraus erhalten haben.
Alternativ kann die PIN `test` verwendet werden, um in den [TestModus](#testmodus) zu gelangen.

War die Anmeldung erfolgreich, ist das Programm einsatzbereit und es müssen keine weitern Eingaben getätigt werden. Ist der Benutzer als Admin eingetragen, öffnet sich eine Konsole in welcher diverse Befehle zur Steuerung des Events eingegeben werden können.

### TestModus

Der TestModus kann aktiviert werden, indem beim Login die PIN `test` verwendet wird. Daraufhin werden keine Chateinträge an den Server übermittelt, und stattdessen dem Benutzer ausgegeben. Es wird empfohlen diese Option vor Beginn des Events auszutesten, um sicherzustellen, dass das Lesen der Log-Datei funktioniert. Außerdem erhält de Benutzer einen Überblick darüber, welche Nachrichten das Programm verarbeitet und welche nicht.

## Datenschutz und Sicherheit

Übermittelt werden nur Textpassagen der Log-Datei, welche im Chat angezeigt wurden und mit dem Präfix `[RageMode]` beginnen. Dies beinhaltet Nachrichten zum Spielablauf und zu Killstreaks.
Dies beinhaltet **nicht** Nachrichten von oder zu anderen Spielern oder Statistiken die über den Chat abgerufen werden. 
An die Serversoftware werden keine weiteren Informationen weitergeleitet, welche nicht zur Verbindungsherstellung benötigt werden.
Gespeichert werden ausschließlich Teile des Inhalts der Chatnachrichten.
