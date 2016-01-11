/**
 * Created by dsantos on 1/10/16 for Textrade.
 * The MIT License (MIT)
 * Copyright (c) 2016 Daniel Santos

 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:

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

package us.textrade.connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class DBConnection {
    public static Connection makeConnection() {
        try {
            Connection newConnection = DriverManager.getConnection(
                    "jdbc:mysql://us-cdbr-iron-east-03.cleardb.net/heroku_b0692bbbba2a643",
                    "b366db0b05b78c",
                    "3a9b0e26"
            );
            return newConnection;
        }catch (SQLException sqle){
            System.out.println("Something went wrong connection to Textrade");
            sqle.printStackTrace();
            return null;
        }
    }

    public static void closeConnection(Connection con) {
        try {
            con.close();
        }catch (SQLException sqle){
            System.out.println("Something when wrong closing the connection");
            sqle.printStackTrace();
        }
    }

}
