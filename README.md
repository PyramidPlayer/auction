# Auction

Implementation of bidder is `ru.toroptsev.AdvancedBidder` in the bidder module

Run this statement in the root to build all project:

```commandline
mvn package
```

Then there are 2 ways to use it.

1. Add `bidder/target/bidder-1.0-SNAPSHOT.jar` to dependencies of your project and use `ru.toroptsev.AdvancedBidder` 
class that implements `auction.Bidder` interface.
2. Run from the root of project:
```commandline
java -jar web/target/web-1.0-SNAPSHOT.jar
```
Then REST-API for bidder became available in `http://localhost:8080/bidder` with following methods:
####Initialization
```commandline
curl -X POST 'http://localhost:8080/bidder/init' -H 'Content-Type: application/json' -d '{"quantity":10, "cash":100}'
```
Successfull response: Status code 200 and body `{"id":1}` - id of new initialized bidder that should be used in all following methods.

Error response for all requests: `{"error":"error_message"}`

####Place bid

```commandline
curl 'http://localhost:8080/bidder/place-bid?id=1'
```
Successfull response: Status code 200 and body `{"bid":17}`

####Bids
```commandline
curl -X POST 'http://localhost:8080/bidder/bids?id=1' -H 'Content-Type: application/json' -d '{"ownBid":17,"otherBid":20}'
```
Successfull response: Status code 200 and empty body