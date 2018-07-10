#Transaction

##Assumption:

- A transaction cannot be its own parent. Following transaction will not be accepted:
```
transaction_id:10
{"amount":10.0, "type":"Not Accepted", "parent_id":10}
```
##Implementation:
- TDD was used to implement tests (unit / Integration) first and then service. Top Down approach was used.
- Integration test (Happy path) was written first for one endpoint.
- Unit test (Happy path + Edge Cases+ Positive flow + Negative Flow) for each Spring Web MVC components were written and
implemented.
- Test Repository also contains a DSL and End2End workflow test developped in groovy + spock.
- This DSL gives the power to write tests in more redable way.

To run the tests cd into the directory and do following in terminal:

```
./gradlew clean test

```
Flow of Tests + Service implementation:

1. Write integration test (happy path) for particular endpoint and it should fail.
2. Write controller unit test (Happy path) and it will fail.
3. Minimum implementation of the controller to make 2. pass
4. Write contoller unit tests (edge cases) and it will fail.
5. Minimum implementation of the controller to make 3. pass
6. Do the same for Service layer, Reposiotry layer until Integration tests passes.

###Notes:
- Repository tests have not been implmented as no orm is being used and most of the Reposiotry methods are simply leveraging
kotlin's map collection.
- There is also a failing test *putTransaction_badRequestAmountNotSet*, this is happening because jackson is deserializing
*null* to "0" *Double* by default.