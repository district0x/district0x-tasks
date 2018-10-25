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

Start server:
```bash
ganache-cli -d -p 8545 -m district0x
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

# How to do tests:

```bash
ganache-cli -d -p 8545 -m district0x
lein test-doo
```

If change contracts code:
```
; auto compile Solidity code in project when files changed
lein solc auto
```
Remember this doesn't trigger cljs tests. So you have to change cljs tests files to trigger. For example add new line.
