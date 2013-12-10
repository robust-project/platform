<?php

// Load a TAB-delimited text file and build a map against the first field
function loadmap($file) {
    $contents = file($file, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
    if( $contents === false ) return array();

    $map = array();
    foreach( $contents as $line ) {
       $fields = explode("\t",$line);
       $map[$fields[0]] = $fields[1];
    }
    return $map;
}
