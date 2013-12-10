package init;

import groovy.sql.Sql;

@GrabConfig(systemClassLoader=true)
@Grab(group='postgresql', module='postgresql', version='9.1-901.jdbc4')
import org.postgresql.Driver

import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat



//Configuration: put your scn tables names here, if tables doesn't exist put here = null;
String messagesStreamTable = 'robust_scn_messages_20110803_a';
String messageContentStreamTable = 'robust_scn_messagecontent_20110803';
// messagePointsStreamTable needed only for gibbs profile
String messagePointsStreamTable = null;
// threadStreamTable needed only for gibbs profile
String threadStreamTable = null;



def props = new Properties()
new File("../streamplayer.properties").withInputStream {
    stream -> props.load(stream)
}

//DB configuration
String dbUser = props["stream.db.user"]
String dbPass = props["stream.db.pass"]
String dbName = props["stream.db.url"]



def sql = Sql.newInstance(dbName, dbUser , dbPass, "org.postgresql.Driver")


Date start = new Date();
println "start " + start

def countRows = sql.firstRow("select count(messageuri) as numberOfRows from " + messagesStreamTable )
println "number of rows to update " + countRows.numberOfRows


def alter = "ALTER TABLE " + messagesStreamTable + " ADD timestamp Timestamp;"

println alter
sql.execute(alter)
println "Column added"

println "Parsing data..."

def query = "select * from " + messagesStreamTable + " where timestamp is null"
sql.eachRow(query){message ->
    def uri = message.messageuri
    def creationdate = message.creationdate
    String dateFormatStr = "EEE MMM d HH:mm:ss z yyyy"
    DateFormat dateFormat = new SimpleDateFormat(dateFormatStr, Locale.UK);
    Date date = dateFormat.parse(creationdate);
    Timestamp timestamp = new Timestamp(date.getTime());
    sql.executeUpdate("update " + messagesStreamTable + " set timestamp = ? where messageuri='" + uri + "'", [timestamp])
}


Date end = new Date();
println "start " + start
println "Parsing end time " + end

String messagesStreamView = 'robust_scn_messages_view';
String messageContentStreamView = 'robust_scn_messagecontent_view';
String messagePointsStreamView = 'robust_scn_message_points_view';
String threadStreamView = 'robust_scn_threads_view';

def createMessagesStreamView = "CREATE VIEW " + messagesStreamView + " AS SELECT * FROM " + messagesStreamTable;
sql.execute(createMessagesStreamView)

def createMessageContentStreamView = "CREATE VIEW " + messageContentStreamView + " AS SELECT * FROM " + messageContentStreamTable;
sql.execute(createMessageContentStreamView)

if (null != messagePointsStreamTable){
    def createMessagePointsStreamView = "CREATE VIEW " + messagePointsStreamView + " AS SELECT * FROM " + messagePointsStreamTable;
    sql.execute(createMessagePointsStreamView)
}


if (null != threadStreamTable){
    def createThreadStreamView = "CREATE VIEW " + threadStreamView + " AS SELECT * FROM " + threadStreamTable;
    sql.execute(createThreadStreamView)
}
