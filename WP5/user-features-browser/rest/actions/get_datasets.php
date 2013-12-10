<?php

require_once 'constants.php';
require_once 'db_connect.php';

function get_datasets($p) {
    $db = db_connect();
    if( !is_object($db) )
        return array('status'=>ST_ERROR,'data'=>$db);

    // Query execution
    $results = mysqli_query($db, 'SHOW TABLES');
    if( $results === false )
        return array('status'=>ST_ERROR,'data'=>mysqli_error($db));

    // Build data
    $data = array();
    while( $row = mysqli_fetch_row($results) )
        array_push($data, $row[0]);
    return array('status'=>ST_SUCCESS,'data'=>$data);
}
