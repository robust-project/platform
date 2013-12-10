 .~~~~~~~~~~~~~.
 } ThreadRecon {
 '~~~~~~~~~~~~~'


Purpose of the sofware:
=======================

ThreadRecon tries to find the reply relations between posts in online forum 
threads where the explicit reply structure is not available. It iteratively 
takes every post in a thread and links it to a previous one, if there is any.
* Input: Set of posts stored in a MySQL table. The fields postid, threadid, 
  timestamp, content and userid have to be provided.
* Output: Parent/child reply pairs (reply graph) stored into a MySQL table.
* Features used:
    - Reply distance: The number of posts between the origin and reply.
    - Position: The position of the reply in the thread of chronologically 
      ordered posts.
    - Time difference: Time passed from a post to the reply (in hours).
    - Quotes: Quotes in HTML tags [QUOTE]...[/QUOTE] are recognised, as well as
      email quotes indicated by a "> " prefix of each quoted line.
    - Bag of words content similarity: Using tf-idf and cosine similarity.
    - Newest parent: Measures the likelihood that user B replies to the most 
      recent post of user A, in case user A has more than one post before user
      B in the thread.
    - Last own: This feature assesses the context of a user's post with respect
      to previous own posts and other users' posts. 
    - Dialogue (length, and distance): Measures the length and distance to 
      interactions of a user to the other users in the thread.
    - Previous post's parent: Likelihood of having the same parent as the 
      preceding post.
    - First post: Likelihood of having the first post in the thread as parent. 


Usage of the software:
======================

1. Have forum data ready in resource MySQL table.
2. Specify details about MySQL connection and queries in config.ini file, see 
   in file comments for further reference.
3. Call "jython ThreadRecon.py" on command line, note that the classpaths of 
   the dependencies (see next section) have to be set correctly.
4. Retrieve recovered reply structures from results MySQL table that is defined
   in the config.ini file.


Dependencies:
=============

* weka 3.6.5 - Weka machine learning toolkit
* jython 2.5.2 - Jython programming language
* jdbc MySQL 5.1.10 - Java MySQL database connector driver


Changing training data for the classifiers:
============================================

New training data can be deployed in order to adapt to forums that differ 
strongly from the original training data (Boards.ie), and thus to achieve 
better results.
ThreadRecon.py reads the trained classifiers from ".model" files, and it 
creates these files automatically from training data that is provided as 
".arff" files. To perform the change, delete the ".model" files from the 
directory, and insert your data files in the format that is dictated by the 
training data templates in the program directory. The name of the training 
files have to be "TrainData.arff" and "TrainDataNum.arff".
Note that both files must exist in order to produce new model files for the 
classifiers. ThreadRecon will not work if it does not find either the two model
files "j48.model" and "linreg.model", or the two training data files 
"TrainData.arff" and "TrainDataNum.arff".


Creating new training data:
===========================

The same features that are used by ThreadRecon to classify new instances can 
be used to create new training data files. Simply import the 
"ThreadReconFeatures.py" file into any Python or Jython script, and access the
extraction functions for each feature from there.
The format of the output file is described in the trainind data templates.
