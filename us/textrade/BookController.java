/**
 * Created by dsantos on 1/10/16 for Textrade.
 * The MIT License (MIT)
 * Copyright (c) 2016 Daniel Santos
 *
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package us.textrade;

import us.textrade.connection.DBConnection;
import us.textrade.models.Book;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookController {

    public static List<Book> getAllBooks(){
        List<Book> bookList = new ArrayList<>();
        Connection conn = DBConnection.makeConnection();

        try{
            Statement statement = conn.createStatement();

            ResultSet haveBooks = statement.executeQuery("SELECT * FROM booktradehave");
            BookController.addBookToList(haveBooks, bookList);

            ResultSet wantBooks = statement.executeQuery("SELECT * FROM booktradewant");
            BookController.addBookToList(wantBooks, bookList);

            statement.close();
            haveBooks.close();
        }catch (Exception sqle){
            sqle.printStackTrace();
        }

        DBConnection.closeConnection(conn);
        return bookList;
    }

    public static List<Book> getWantBookByUser(String username){
        return BookController.getBookByUserTable("booktradewant", username);
    }

    public static List<Book> getHaveBookByUser(String username){
        return BookController.getBookByUserTable("booktradehave", username);
    }

    public static void addBookToList(ResultSet set, List<Book> list) throws SQLException {
        while (set.next()) {
            list.add(
                    new Book(
                            set.getString("name"),
                            set.getString("isbn"),
                            set.getString("user_id"),
                            set.getDate("date_posted")
                    )
            );
        }
    }

    private static List<Book> getBookByUserTable(String tableName, String username){
        Connection conn = DBConnection.makeConnection();
        List<Book> bookList = new ArrayList<>();
        try{
            Statement statement = conn.createStatement();
            ResultSet wantBooks = statement.executeQuery(
                    String.format("SELECT * FROM %s WHERE user_id = '%s'" , tableName, username)
            );

            BookController.addBookToList(wantBooks, bookList);

            statement.close();
            wantBooks.close();
        }catch (SQLException sqle){
            sqle.printStackTrace();
        }
        DBConnection.closeConnection(conn);
        return bookList;
    }

}
