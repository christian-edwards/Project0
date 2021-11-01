
import java.sql.{Connection, DriverManager, SQLException}
import java.io.File
import java.io.FileNotFoundException
import java.util.{InputMismatchException, Scanner}

object Main extends App{
    val driver = "com.mysql.cj.jdbc.Driver"
    val url = "jdbc:mysql://localhost:3306/mockbusters"
    val username = "root"
    val password = "had00p_677"
    val scanner = new Scanner(System.in)
    var connection:Connection = DriverManager.getConnection(url, username, password)

    def selectAllMovies(): Unit ={//prints all records from movies table
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM movies;")
        println(resultSet)
        while ( resultSet.next() ) {
            println(resultSet.getString(1)+", " +resultSet.getString(2) +", " +resultSet.getString(3), resultSet.getString(4),resultSet.getString(5),resultSet.getString(6))
        }
    }

    def selectAllRentals(): Unit ={//prints all records from rentals table
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM rentals;")
        println(resultSet)
        while ( resultSet.next() ) {
            println(resultSet.getString(1)+", " +resultSet.getString(2) +", " +resultSet.getString(3), resultSet.getString(4))
        }
    }

    def getMovieByID(id:Int): Unit ={//prints movie from table movies with given movieid
        val statement = connection.createStatement()
        val resultSetNew = statement.executeQuery("SELECT * FROM movies WHERE movieid = "+ id +";")
        while ( resultSetNew.next() ) {
            println(resultSetNew.getString(1)+", " +resultSetNew.getString(2) +", " +resultSetNew.getString(3), resultSetNew.getString(4),resultSetNew.getString(5),resultSetNew.getString(6))
        }
    }

    def isRented(id:Int):Boolean={//returns whether or not a movie with given id is currently rented
        val statement = connection.createStatement()
        val resultSetNew = statement.executeQuery("SELECT isrented FROM movies WHERE movieid = "+ id +";")
        resultSetNew.next()
        if (resultSetNew.getString(1) == "1" ){
                return true
        }
        return false
    }

    def newRental(renterName:String,movieID:Int): Unit ={//if movie is not currently rented creates a rental
        var statement = connection.createStatement()
        statement.executeUpdate("INSERT INTO rentals(renterName,dueDate,rentedMovieId)\nValues(\""+ renterName +"\",DATE_ADD(CURRENT_DATE(),INTERVAL 14 DAY),"+ movieID +")")
        statement.executeUpdate("UPDATE movies SET IsRented = 1 WHERE (MovieID = "+ movieID +");")
        println("Rental record added!")
    }

    def printCommands(): Unit ={
        println("Command List")
        println("1: See a list of all movies.")
        println("2: Retrieve movie info by inputting it's ID.")
        println("3: See a list of all active rentals.")
        println("4: Turn in a rental.")
        println("5: Create a rental.")
        println("99: To exit from menu.")
    }

    def endRental(rentalID:Int): Unit ={//marks movie as no longer rented and deleted rental record
        try{
        var statement = connection.createStatement()
        var resultSet = statement.executeQuery("SELECT rentedMovieID FROM rentals WHERE rentalID = "+ rentalID)
        resultSet.next()
        val movieID = resultSet.getInt(1)
        statement.executeUpdate("UPDATE movies SET IsRented = 0 WHERE movieID = "+ movieID)
        statement.executeUpdate("DELETE FROM rentals WHERE rentalID = "+rentalID)
        } catch {
            case e: SQLException => println("ERROR: Rental not found.")
        }
    }

    println("Welcome to the Mockbusters Movie Rental Database.")
    printCommands()
    //var i = 0
    var quit = false
    while(quit == false){//until quit is true loop through user menu
        println("Enter \"66\" to see a list of all current commands or 99 to exit the menu.")
        try{
            var i = scanner.nextInt()
            i match {
                case 1  => selectAllMovies()
                case 2 => print("Movie ID: ")
                    getMovieByID(scanner.nextInt())
                case 3 => selectAllRentals()
                case 4 => selectAllRentals()
                    print("Rental ID: ")
                    endRental(scanner.nextInt())
                    println("Rental has been turned in!")
                case 5 => selectAllMovies()
                    println("Renter Name: ")
                    scanner.nextLine()
                    val renterName = scanner.nextLine()
                    println("Movie ID: ")
                    val movieID = scanner.nextInt()
                    if( !isRented(movieID) ){
                        newRental(renterName,movieID)
                        println("Video has been rented successfully!")
                    } else {
                        println("Sorry this movie is currently unavailable.")
                    }
                case 66 => printCommands()
                case 99 => quit = true
                case default => println("Incorrect number. For help enter 66.")
            }
        } catch {
            case e: InputMismatchException => println("Non-Integer entered. Exiting menu")
                                 quit = true
            case e: Exception => println("Unknown error occurred. Exiting menu")
                                 quit = true
        }
    }
    println("Exiting Menu and shutting down connection...")
    connection.close()
}