<?php

require_once 'constants.php';

// Get request parameters
$parameters = array();
foreach( $_GET as $name => $value )
    $parameters[$name] = strtolower(trim($value));

// Process the required action
if( isset($parameters['action']) ) {
    // Try to load the action
    $action_file = 'actions/'.$parameters['action'].'.php';
    if( !is_readable($action_file) )
        $response = array('status'=>ST_ERROR,'data'=>'Unknown action requested.');
    else { // Execute the action
        include($action_file);
        $response = $parameters['action']($parameters);
    }
} else $response = array('status'=>ST_ERROR,'data'=>'No action requested.');

// Send request response
header('HTTP/1.1 '.$response['status']);
header('Content-Type: application/json');
echo json_encode($response['data']);
