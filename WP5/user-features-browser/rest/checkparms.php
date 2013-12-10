<?php

// Check if a given set of parameters ($check) are present in $p
function checkparms($p,$check) {
    foreach( $check as $name )
       if( !array_key_exists($name,$p) )
          return false;
    return true;
}
