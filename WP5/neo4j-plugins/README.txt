================================================================
Neo4J plugins suite
----------------------------------------------------------------
Hugo Hromic <hugo.hromic@deri.org>
Unit for Information Mining and Retrieval (UIMR)
Digital Enterprise Research Institute (DERI)
NUI Galway
================================================================

1. Introduction

2. Compiling

It's strongly recommended that you use Apache Maven to compile/run/maintain the
source code of this software. To compile the plugins, use the following:

$ mvn clean
$ mvn package

This will generate a JAR package of the plugins in the "target" directory.

3. Using

To use the plugins, simply copy the generated JAR package (or symlink) into the
"plugins" directory of your Neo4J installation. Neo4J will recognize and enable
all included plugins automatically.

3.1 TwitterPlugin

This plugin acts as a gateway to push batchs of tweets inside the Neo4J graph
database, with the purpose of minimizing network traffic. The plugin will
create automatically all relationship types and indexes required to properly
traverse the Twitter graph.

The path for this plugin is: /db/data/ext/TwitterPlugin/graphdb/push_tweets

And it requires a single parameter, "tweets", with an array of string-encoded
tweets, all using JSON representation. The format of each tweet is:

AUTHOR;TIMESTAMP;REPLY;MENTIONS;HASHTAGS;RTORIGINS

where each list is a comma (',') separated item.

3.2 SocialGraphPlugin

This plugin is a generalized version of TwitterPlugin, allowing to build any
kind of social graph. It also acts as a gateway to push batchs of events inside
the Neo4J graph database to minimize network traffic.

The path for this plugin is: /db/data/ext/SocialGraphPlugin/graphdb/push_events

And it requires a single parameter, "eventsJSON", with a JSON-encoded string of
an array of events and interactions to push to the graph. Please note that this
is a JSON-encoded string inside another JSON-encoded string (the one sent by
the client application to the plugin), so be careful when serializing the data
(specially regarding escaping).

The SocialGraphPlugin uses the following classes to represent events:

Event class:
    long timestamp
    Interaction[] interactions

Interaction class:
    InteractionType type
    Map<String,Object> attributes

InteractionType enum class:
    CONTENT_PUBLICATION
    CONTENT_QUOTATION
    CONTENT_REPLY
    USER_MENTION

For each InteractionType, the following Interaction attributes must be
present (where the "actor" is the user generating the interaction):

* CONTENT_PUBLICATION: "actor_id" and "content_id"
* CONTENT_QUOTATION: "actor_id" and "content_id"
* CONTENT_REPLY: "actor_id" and "content_id"
* USER_MENTION: "actor_id" and "user_id"

Now, each batch of events to be pushed to the plugin is an array of Event
objects, thus Event[]. The following is an example of the final representation
(please note the use of proper quote escaping, because the right side of the
"eventsJSON" parameter is a string):

--+--+-- CUT HERE: RAW DATA FOR THE SOCIAL GRAPH PLUGIN --+--+--
{"eventsJSON": "[
    {\"timestamp\":1234567890,
     \"interactions\": [
        {\"type\": \"CONTENT_PUBLICATION\",
         \"attributes\": {\"actor_id\": 1234567890, \"content_id\": 567890}
        },
        {\"type\": \"USER_MENTION\",
         \"attributes\": {\"actor_id\": 1234567890, \"user_id\": 7890123}
        }
    ]},
    {\"timestamp\",2345678901,
     \"interactions\": [
        {\"type\": \"CONTENT_PUBLICATION\",
         \"attributes\": {\"actor_id\": 6425356, \"content_id\": 77245}
        }
    ]}
]"}
--+--+-- CUT HERE: RAW DATA FOR THE SOCIAL GRAPH PLUGIN --+--+--

The above example contains two events and three interactions (two inside
the first event and a third inside the second event).

The graph created inside the Neo4J database is as follows:

* All nodes have a "type" property which can be "user" or "content".
* All nodes have an "id" property which can be anything unique, but long
  integers are recommended. The plugin verifies unicity in a case
  insensitive way, so you can safely use usernames if desired. Also,
  don't confuse this "id" property with the Neo4J internal "id" for the
  nodes. All social graph referencing is done using the "id" property.
* All relationships have a "timestamp" property which is the number of
  milliseconds since UNIX epoch as a long integer.
* Relationship types currently supported:
  User Node -[PUBLISHED]-> Content Node
  User Node -[QUOTED]-> Content Node
  User Node -[REPLIED]-> Content Node
  User Node -[MENTIONED]-> User Node

In addition, the plugin maintains three indexes:

* A node index which indexes "user" type nodes by the "id" property,
  so you can search for particular users.
* A node index which indexes "content" type nodes by the "id" property,
  so you can search for particular contents.
* A relationship index which indexes all relationship types by the
  date part, in YYYY-MM-DD format, calculated from the "timestamp"
  property. You can search all interactions for specific days or ranges.
