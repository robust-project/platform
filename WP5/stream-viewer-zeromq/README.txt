1. Compile using ./compile.sh
2. Run using ./stream-viewer.sh

Usage: [connect_string] <num_payloads> <envelope_key>

connect_string: the ZeroMQ connect string to use (no default)
                Example: tcp://someserver:port
num_payloads: number of payloads of the envelope (default: 1)
              Example: streamsim-delivery-zeromq uses 2: one for timestamp, one for event content
envelope_key: the envelope key to listen on (default: "", all keys)
              Example: streamsim-delivery-zeromq uses "GENERIC"
