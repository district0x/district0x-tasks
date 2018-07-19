# district0x-tasks

## Development
First time run
```
lein deps
```

Compile contracts (assumes you have `solc` installed):
```bash
lein solc
```

Auto compile contracts on changes:
```bash
lein solc auto
```

Start ipfs:
```
ipfs daemon
```

Start server:
```bash
ganache-cli -p 8549
lein repl
(start-server!)
node dev-server/district0x-tasks.js
```

Start UI:
```bash
lein repl
(start-ui!)
# go to http://localhost:4598/
```

Start tests:
```bash
ganache-cli -p 8549
lein test-dev
```
