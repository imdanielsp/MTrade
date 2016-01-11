/**
 * Created by dsantos on 1/10/16 for Textrade.
 * The MIT License (MIT)
 * Copyright (c) 2016 Daniel Santos
 * <p>
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package us.textrade;
import com.mysql.jdbc.RowDataStatic;
import us.textrade.BookController;
import us.textrade.connection.DBConnection;
import us.textrade.models.Book;
import us.textrade.models.BookTrade;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookTradeController {

    public static List<BookTrade> loadBookTrade(){
        List<BookTrade> bookTradeList = new ArrayList<>();
        List<String> userList = BookTradeController.getUsers();
        List<Book> haveBooks;
        List<Book> wantBooks;

        Connection conn = DBConnection.makeConnection();

        try{
            Statement statement = conn.createStatement();

            for(String userName: userList){
                ResultSet set;
                haveBooks = new ArrayList<>();
                wantBooks = new ArrayList<>();

                set = statement.executeQuery(
                    String.format("SELECT * FROM booktradewant WHERE user_id = '%s'", userName)
                );
                BookController.addBookToList(set, wantBooks);

                set = statement.executeQuery(
                    String.format("SELECT * FROM booktradehave WHERE user_id = '%s'", userName)
                );
                BookController.addBookToList(set, haveBooks);

                if(haveBooks.size() != 0 && wantBooks.size() != 0) {
                    bookTradeList.add(
                        new BookTrade(
                                userName,
                                wantBooks,
                                haveBooks
                        )
                    );
                }
                set.close();
            }

            statement.close();
        }catch (SQLException sqle){
                sqle.printStackTrace();
        }

        return bookTradeList;
    }

    private static List<String> getUsers(){
        Connection conn = DBConnection.makeConnection();
        List<String> userList = new ArrayList<>();

        try{
            Statement statement = conn.createStatement();
            ResultSet userNames = statement.executeQuery("SELECT username FROM user");
            BookTradeController.addUserToList(userNames, userList);

        }catch (SQLException sqle){
            sqle.printStackTrace();
        }

        DBConnection.closeConnection(conn);
        return userList;
    }

    private static void addUserToList(ResultSet set, List<String> list) throws SQLException {
        while (set.next()) {
            list.add(set.getString("username"));
        }
    }

}
