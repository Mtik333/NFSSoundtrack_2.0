I just want to rewrite NFSSoundtrack.com to handle some stuff better than 11 years ago.

For local database import, first of course create "nfs" database schema; then you need to import manually (for example using HeidiSQL) file called bulk_start.sql

Now need to import CSV (4_games_data_final.csv) using HeidiSQL.

Then finally we are going to import data using stuff from current NFSSoundtrack database, so run application with 2 arguments "AllGames" and path to Excel file (I'm too stupid to make it relative or read from resources) 

