# Staff plugin

* tempMute / mute
* Freeze
* Staff mode
* ban / tempban
* notes on player
* playerlist : online and offline head clickable to show profile
* player profile =>
  * number of reports
  * number of tickets
  * [number of punishments](https://youtu.be/12jlZPFWp9U?t=303)
  * first logging
  * stats
  * staff notes
* gui
  * show players online click =>
  * moderate player gui
    * freeze
    * open inv
    * open ender chest
    * tp
    * add notes
    * punishment
  * [punishment gui](https://youtu.be/12jlZPFWp9U?t=259)
    * head
    * anvil to select tempban / ban / mute / tempmute / kick / etc...
    * template to choose
    * duration => edit paper in anvil gui
    * message => edit paper in anvil gui
    * save as template from here
* tickets and report same interface but not same command / accessible from the same interface as admin
* [tickets](https://youtu.be/12jlZPFWp9U?t=105)
  * suggestion
  * help
  * keep closed tickets
  * tempban from tickets
  * respond in chat and save the conv
  * tp to tickets creation location
* [reports](https://www.spigotmc.org/resources/tigerreports.25773/)
  * report ID
  * report notify staff on / off
  * report reason predefined + custom in chat or anvil
  * tp to reporter location / report reporter location / reported location / report reported location
  * reputation of reporters
  * data from the reporter and the reported saved
  * save the last 50 or more chat messages of all players and when a report strike save the reported and the reporter messages in the report
  * status of the report => waiting / processing / important
  * arichve report / delete report (need perm) / process report => true (=> punishment with templates or nothing) / not sure / false
  * abusive report
  * comments on report => private / public (send to the reporter) delete comments (need perm)
* [templates](https://youtu.be/12jlZPFWp9U?t=320) for punishments
* staff chat
* clear chat => don't clear if you have specific perm (or force it)
* global mute chat

DB:

```sql
CREATE TABLE reports(ID INT AUTO_INCREMENT PRIMARY KEY, report BOOLEAN, reportStatus TINYINT, reportTime BIGINT, reportReason VARCHAR(256), reported JAVA_OBJECT, reporter JAVA_OBJECT);
CREATE TABLE chatHistory(messageId INT AUTO_INCREMENT PRIMARY KEY, messageDate BIGINT, messageAuthor UUID, message VARCHAR(256));
CREATE TABLE templates(id INT AUTO_INCREMENT PRIMARY KEY, templateName VARCHAR(32), message VARCHAR(256), duration BIGINT, type TINYINT);
CREATE TABLE punishments(id INT AUTO_INCREMENT PRIMARY KEY, punishedUUID UUID, message VARCHAR(256), punishmentType TINYINT, endtime BIGINT);
```

reportStatus =

* 000 => wating
* 001 => in progress
* 010 => important
* 100 => processed : abusive report / ticket
* 101 => processed : false / unsolvable
* 110 => processed : not sure / not solved
* 111 => processed : true / solved
