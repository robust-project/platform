<?php

require_once 'config/database.php';

// Get a database connection
function db_connect() {
    $db = mysqli_connect( DBCONFIG::$host, DBCONFIG::$user, DBCONFIG::$password, DBCONFIG::$dbname);
    if( $db === false ) return mysqli_connect_error();
    if( !mysqli_set_charset( $db, DBCONFIG::$encoding ) )
        return mysqli_error($db);
    return $db;
}
