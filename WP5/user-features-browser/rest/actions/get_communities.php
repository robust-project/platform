<?php

require_once 'constants.php';
require_once 'loadmap.php';
require_once 'checkparms.php';
require_once 'db_connect.php';

function get_communities($p) {
    if( !checkparms($p,array('dataset')) )
        return array('status'=>ST_ERROR,'data'=>'Missing parameters.');

    // Connect to database
    $db = db_connect();
    if( !is_object($db) )
        return array('status'=>ST_ERROR,'data'=>$db);

    // Query
    $query = sprintf(
        'SELECT DISTINCT(`community_id`) AS `id` FROM `%s`',
        mysqli_escape_string($db, $p['dataset'])
    );

    // Query execution
    if( ($results = mysqli_query($db, $query)) === false )
        return array('status'=>ST_ERROR,'data'=>mysqli_error($db));

    // Try to load static community names file
    $cmap = loadmap(DATA_DIR.'/communities_'.$p['dataset'].'.txt');

    // Build data
    $data = array();
    while( $row = mysqli_fetch_row($results) )
        array_push($data, array('id'=>$row[0],'name'=>array_key_exists($row[0],$cmap)?$cmap[$row[0]]:$row[0]));
    return array('status'=>ST_SUCCESS,'data'=>$data);
}
