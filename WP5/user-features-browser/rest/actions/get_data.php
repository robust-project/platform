<?php

require_once 'constants.php';
require_once 'checkparms.php';
require_once 'db_connect.php';

function get_data($p) {
    if( !checkparms($p,array('dataset','community','feature','timestep','from','to')) )
        return array('status'=>ST_ERROR,'data'=>'Missing parameters.');

    // Connect to database
    $db = db_connect();
    if( !is_object($db) )
        return array('status'=>ST_ERROR,'data'=>$db);

    // Query
    switch( $p['timestep'] ) {
        case 'yearly':  $ts_condition = '> 31'; break;
        case 'monthly': $ts_condition = 'BETWEEN 8 AND 31'; break;
        case 'weekly':  $ts_condition = '<= 7'; break;
        default: return array('status'=>ST_ERROR,'data'=>'Unknown timestep.');
    }
    $query = sprintf(
        'SELECT * FROM `%s` WHERE `community_id`=%s AND feature=\'%s\' AND `from` >= \'%s\' AND `to` <= \'%s\' AND DATEDIFF(`to`,`from`) %s ORDER BY `feature`,`from`,`to` ASC',
        mysqli_escape_string($db, $p['dataset']),
        mysqli_escape_string($db, $p['community']),
        mysqli_escape_string($db, $p['feature']),
        mysqli_escape_string($db, $p['from']),
        mysqli_escape_string($db, $p['to']),
        $ts_condition
    );

    // Query execution
    if( ($results = mysqli_query($db, $query)) === false )
        return array('status'=>ST_ERROR,'data'=>mysqli_error($db));

    // Build data
    $data = array();
    while( $row = mysqli_fetch_assoc($results) ) {
        array_push( $data, array(
            'from'=>$row['from'], 'to'=>$row['to'],
            'min'=>$row['min'],'max'=>$row['max'],
            'mean'=>$row['mean'],'median'=>$row['median'],
            'stddev'=>$row['stddev'],'var'=>$row['var'], 'sum'=>$row['sum'],
            'used_size'=>$row['used_size'],'data_size'=>$row['data_size']
        ));
    }
    return array('status'=>ST_SUCCESS,'data'=>$data);
}
