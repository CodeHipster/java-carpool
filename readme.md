#Carpool!

###TODO:
- features:
  - login and user management
- frontend:
  - clean input data
  - datetime input field for departure
  - styling
- backend:
  - refactor db model to participants per trip, 
  where the driver is also a participant.
  - refactor driver and passenger to be persons.
  - fix response codes.
  - refactor db model, stop can relate directly to trip
- bug:
  - deleting stops does not follow domain rules.

ambitious:
  - write custom httpserver (instead of old com.sun.net.httpserver)
  - write custom routing library (for routing with wildcards)  