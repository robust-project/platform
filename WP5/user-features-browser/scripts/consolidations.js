/*
Hugo Hromic <hugo.hromic@deri.org>
Unit for Information Mining and Retrieval (UIMR)
Digital Enterprise Research Institute (DERI)
NUI Galway, Ireland
*/

//*****************************************************************************
// Consolidations
(function( Consolidation, $, undefined ) {
    //*************************************************************************
    // Public properties
    Consolidation.mean = {
        reqRefValue: false,
        compute: function(stats,refvalue) {
            return parseFloat( stats.mean );
        }
    };
    Consolidation.mean_linear = {
        reqRefValue: true,
        compute: function(stats,refvalue) {
            return parseFloat( stats.mean ) / refvalue;
        }
    };
    Consolidation.mean_tanh = {
        reqRefValue: true,
        compute: function(stats,refvalue) {
            var TANH_LIMIT = 18.714973875118524;
            function tanh(x) {
                return (Math.exp(x) - Math.exp(-x)) /
                       (Math.exp(x) + Math.exp(-x));
            }
            return tanh( parseFloat(stats.mean) * TANH_LIMIT / refvalue );
        }
    };
    Consolidation.mean_arccot = {
        reqRefValue: true,
        compute: function(stats,refvalue) {
            function arccot(x) {
                if( x == 0 ) return Math.PI / 2;
                return Math.atan( 1.0 / x );
            }
            return arccot( parseFloat(stats.mean) / refvalue ) /
                   ( Math.PI / 2 );
        }
    };
    Consolidation.mean_arccot_penalty = {
        reqRefValue: true,
        compute: function(stats,refvalue) {
            function arccot(x) {
                if( x == 0 ) return Math.PI / 2;
                return Math.atan( 1.0 / x );
            }
            return (arccot( parseFloat(stats.mean) / refvalue ) /
                   ( Math.PI / 2 )) *
                   ( parseFloat(stats.used_size) /
                     parseFloat(stats.data_size) );
        }
    };
    Consolidation.median = {
        reqRefValue: false,
        compute: function(stats,refvalue) {
            return parseFloat( stats.median );
        }
    };
    Consolidation.median_linear = {
        reqRefValue: true,
        compute: function(stats,refvalue) {
            return parseFloat( stats.median ) / refvalue;
        }
    };
    Consolidation.median_arccot = {
        reqRefValue: true,
        compute: function(stats,refvalue) {
            function arccot(x) {
                if( x == 0 ) return Math.PI / 2;
                return Math.atan( 1.0 / x );
            }
            return arccot( parseFloat(stats.median) / refvalue ) /
                   ( Math.PI / 2 );
        }
    };
    Consolidation.median_arccot_penalty = {
        reqRefValue: true,
        compute: function(stats,refvalue) {
            function arccot(x) {
                if( x == 0 ) return Math.PI / 2;
                return Math.atan( 1.0 / x );
            }
            return (arccot( parseFloat(stats.median) / refvalue ) /
                   ( Math.PI / 2 )) *
                   ( parseFloat(stats.used_size) /
                     parseFloat(stats.data_size) );
        }
    };
    Consolidation.sum = {
        reqRefValue: false,
        compute: function(stats,refvalue) {
            return parseFloat( stats.sum );
        }
    };
    Consolidation.sum_linear = {
        reqRefValue: true,
        compute: function(stats,refvalue) {
            return parseFloat( stats.sum ) / refvalue;
        }
    };
    Consolidation.sum_tanh = {
        reqRefValue: true,
        compute: function(stats,refvalue) {
            var TANH_LIMIT = 18.714973875118524;
            function tanh(x) {
                return (Math.exp(x) - Math.exp(-x)) /
                       (Math.exp(x) + Math.exp(-x));
            }
            return tanh( parseFloat(stats.sum) * TANH_LIMIT / refvalue );
        }
    };
    Consolidation.data_size = {
        reqRefValue: false,
        compute: function(stats,refvalue) {
            return parseInt( stats.data_size );
        }
    }
    Consolidation.data_size_linear = {
        reqRefValue: true,
        compute: function(stats,refvalue) {
            return parseInt( stats.data_size ) / refvalue;
        }
    }
}( window.Consolidation = window.Consolidation || {}, jQuery ));
