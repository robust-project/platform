/**
 * Copyright 2013 DERI, National University of Ireland Galway.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.deri.uimr.neo4jplugins.twitter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Represents a Tweet to be inserted in the graph.
 *
 * @since 1.0
 */
public class Tweet {
    /** Date format to be used when parsing textual dates.
      * <p>Value: {@code new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ROOT)}.</p>
      */
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ROOT);

    /** Author of this tweet. */
    public final String author;

    /** Textual representation of the timestamp of this tweet. */
    public final String createdAt;

    /** Timestamp of when this tweet was created. */
    public final long timestamp;

    /** Reply entity (if any) inside this tweet. */
    public final String reply;

    /** Mentions entities (if any) inside this tweet. */
    public final String[] mentions;

    /** Hashtags entities (if any) inside this tweet. */
    public final String[] hashtags;

    /** Retweet origins (if any) inside this tweet. */
    public final String[] rtOrigins;

    /** Check if the given string exists inside a list of strings, case insensitive. */
    private static boolean containsIgnoreCase(final List<String> list, final String search) {
        for( final String item : list )
            if( item.equalsIgnoreCase(search) )
                return true;
        return false;
    }

    /** Create a list of 3 list of strings (Tweet fields). */
    private static List<List<String>> buildFieldsList(final List<String> l1, final List<String> l2, final List<String> l3) {
        final List<List<String>> fieldsList = new ArrayList<List<String>>(3);
        fieldsList.add(l1);
        fieldsList.add(l2);
        fieldsList.add(l3);
        return fieldsList;
    }

    /**
     * Creates a new {@code Tweet} object with given tweet data.
     *
     * <p>The constructor fails if the date can't be parsed using {@link #DATE_FORMAT}.</p>
     *
     * @param author the author of the tweet
     * @param createdAt the textual date representation of the tweet creation
     * @param reply reply entity for this tweet (if none, give {@code null})
     * @param mentions the mentions entities for this tweet (if none, give an empty array)
     * @param hashtags the hashtags entities for this tweet (if none, give an empty array)
     * @param rtOrigins the retweet origins for this tweet (if none, give an empty array)
     * @see #DATE_FORMAT
     */
    public Tweet(final String author, final String createdAt, final String reply, final String[] mentions, final String[] hashtags, final String[] rtOrigins) throws ParseException {
        this.author = author;
        this.createdAt = createdAt;
        this.reply = reply;
        this.mentions = mentions;
        this.hashtags = hashtags;
        this.rtOrigins = rtOrigins;
        this.timestamp = DATE_FORMAT.parse(createdAt).getTime();
    }

    /**
     * Parses a Neo4J Twitter plugin string into a new {@code Tweet} object.
     *
     * <p>Format: {@code author;timestamp;reply;mention1,mentionN;hashtag1,hashtagN;rtorigin1,rtoriginN}</p>
     *
     * @param string the string, Neo4J Twitter plugin formatted, to parse
     * @return the {@code Tweet} object containing all parsed data
     */
    public static Tweet parse(final String string) {
        // Parse the plain string into main fields
        final String[] fields = string.trim().split(";", -1);

        // Enough fields?
        if( fields.length < 6 )
            return null;

        // Parse author and created_at fields
        final String author = fields[0].trim();
        final String createdAt = fields[1].trim();

        // Ensure there is an author and a created_at field
        if( author.isEmpty() || createdAt.isEmpty() )
            return null;

        // Parse the reply field
        final String reply = fields[2].trim();

        // Parse the mentions, hashtags and rtorigins fields
        final List<String> mentions = new ArrayList<String>();
        final List<String> hashtags = new ArrayList<String>();
        final List<String> rtOrigins = new ArrayList<String>();

        int i = 3;
        for( final List<String> tweetField : buildFieldsList(mentions, hashtags, rtOrigins) ) {
            final String field = fields[i++].trim();
            if( !field.isEmpty() )
                for( final String item : field.split(",", -1) )
                    if( !containsIgnoreCase(tweetField, item) )
                        tweetField.add(item);
        }

        // Return a new parsed Tweet
        try {
            return new Tweet(author, createdAt,
                       reply.isEmpty()?null:reply,
                       mentions.toArray(new String[]{}),
                       hashtags.toArray(new String[]{}),
                       rtOrigins.toArray(new String[]{}));
        }
        catch( ParseException e ) {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format("<Tweet from '%s' @ %s: reply=%s,mentions=%s,hashtags=%s,rtOrigins=%s>",
                   author, createdAt, (reply==null?"null":String.format("'%s'", reply)),
                   Arrays.asList(mentions), Arrays.asList(hashtags), Arrays.asList(rtOrigins));
    }
}
