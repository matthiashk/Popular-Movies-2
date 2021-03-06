# Popular-Movies-2
Popular Movies Stage 2

![screenshots](http://www.matthiashko.com/images/popular-movies-app.jpg)

Note: Please enter your own "api_key" in values/strings.xml before running
the program. See [The Movie Database API](https://developers.themoviedb.org/3/getting-started/introduction) for more information on how to obtain an API key.

Description:  
The Popular Movies Stage 2 app builds on the Popular-Movies-1 app to add
additional features and functionality. 

 - Support for tablets is improved by implementing a Master-Detail layout.
 - An option to sort by 'Favorites' has been added.
 - Movies can now be added as a 'Favorite'.
 - Trailers and reviews are also requested using the API.
 - All movie details are now stored in a sqlite3 database.
 - Favorite movies store additional information in the database including the
   movie poster.
 - A ContentProvider is now used to populate movie details.
 - A share button has been added to share the first trailer.
