<?php

require_once 'constants.php';
require_once 'checkparms.php';
require_once 'db_connect.php';

function get_timeranges($p) {
    if( !checkparms($p,array('dataset','community','feature')) )
        return array('status'=>ST_ERROR,'data'=>'Missing parameters.');

    // Connect to database
    $db = db_connect();
    if( !is_object($db) )
        return array('status'=>ST_ERROR,'data'=>$db);

    // Get the timeranges
    $data = array(
        'weekly'=>array('from'=>array(),'to'=>array()),
        'monthly'=>array('from'=>array(),'to'=>array()),
        'yearly'=>array('from'=>array(),'to'=>array())
    );
    foreach( array('from','to') as $field ) {
        // Query
        $query = sprintf(
            'SELECT DISTINCT(`%s`),(DATEDIFF(`to`,`from`)+1) FROM `%s` WHERE `community_id`=%s AND feature=\'%s\' ORDER BY `%s` ASC;',
            $field,
            mysqli_escape_string($db, $p['dataset']),
            mysqli_escape_string($db, $p['community']),
            mysqli_escape_string($db, $p['feature']),
            $field
        );

        // Query execution
        if( ($results = mysqli_query($db, $query)) === false )
            return array('status'=>ST_ERROR,'data'=>mysqli_error($db));

        // Build data
        while( $row = mysqli_fetch_row($results) ) {
            $timestep = 'yearly';
            if( $row[1] <= 31 ) $timestep = 'monthly';
            if( $row[1] <= 7 ) $timestep = 'weekly';
            array_push($data[$timestep][$field], $row[0]);
        }
    }
    return array('status'=>ST_SUCCESS,'data'=>$data);
}
