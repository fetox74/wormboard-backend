# Wormboard Backend

The backend portion of the WormBoard project. 

## Database

Create a PostgreSQL database and run the CREATE.sql script from the scripts/sql folder in it. Then change the connect credentials in both,
the StatsUpdater.py script in scripts/python and the one in DatabaseConfig.java in src/main/java/com/fetoxdevelopments/wormboard/config to
your database.

## Datagrinding

This script StatsUpdater.py in scripts/python is nowhere near finished by now and can only be used manually by putting in a number of dates in the DATES list and then run in Python 2.7

## Development environment

WormBoard backend is developed as a Spring Boot app, so it can be run standalone from your preferred IDE in a Spring Boot config.. 

## Production environment

..or assembled as a .war via `mvn package` maven goal and deployed on an application server like Apache Tomcat. In both cases
you have to exchange the base URL in the frontend with the corresponding URL of your service.

