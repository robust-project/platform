<?php

require_once 'constants.php';
require_once 'checkparms.php';
require_once 'db_connect.php';

function get_features($p) {
    if( !checkparms($p,array('dataset','community')) )
        return array('status'=>ST_ERROR,'data'=>'Missing parameters.');

    // Connect to database
    $db = db_connect();
    if( !is_object($db) )
        return array('status'=>ST_ERROR,'data'=>$db);

    // Query
    $query = sprintf(
        'SELECT DISTINCT(`feature`) AS `feature` FROM `%s` WHERE `community_id`=%s',
        mysqli_escape_string($db, $p['dataset']),
        mysqli_escape_string($db, $p['community'])
    );

    // Query execution
    if( ($results = mysqli_query($db, $query)) === false )
        return array('status'=>ST_ERROR,'data'=>mysqli_error($db));

    // Build data
    $data = array();
    while( $row = mysqli_fetch_row($results) )
        array_push($data, $row[0]);
    return array('status'=>ST_SUCCESS,'data'=>$data);
}
