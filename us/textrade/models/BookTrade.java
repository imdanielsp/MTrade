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

package us.textrade.models;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class BookTrade {
    private int tradeID;
    private int userOneID;
    private boolean userOneApproved;
    private int userTwoID;
    private boolean userTwoApproved;
    private List<Book> wantBooks;
    private List<Book> haveBooks;
    private String status;
    private Date dateCreated;

    public BookTrade(int tradeID, int userOneID, boolean userOneApproved,
                     int userTwoID, boolean userTwoApproved, ArrayList<Book> wantBooks,
                     ArrayList<Book> haveBooks, String status, Date dateCreated) {
        this.tradeID = tradeID;
        this.userOneID = userOneID;
        this.userOneApproved = userOneApproved;
        this.userTwoID = userTwoID;
        this.userTwoApproved = userTwoApproved;
        this.wantBooks = wantBooks;
        this.haveBooks = haveBooks;
        this.status = status;
        this.dateCreated = dateCreated;
    }

    public int getTradeID() {
        return tradeID;
    }

    public int getUserOneID() {
        return this.userOneID;
    }

    public boolean isUserOneApproved() {
        return userOneApproved;
    }

    public int getUserTwoID() {
        return this.userTwoID;
    }

    public boolean isUserTwoApproved() {
        return userTwoApproved;
    }

    public List<Book> getWantBooks() {
        return wantBooks;
    }

    public List<Book> getHaveBooks() {
        return haveBooks;
    }

    public String getStatus() {
        return status;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    @Override
    public String toString() {
        return "BookTrade[" +
                "tradeID=" + tradeID +
                ", userOneID=" + userOneID +
                ", userOneApproved=" + userOneApproved +
                ", userTwoID=" + userTwoID +
                ", userTwoApproved=" + userTwoApproved +
                ", wantBooks='" + wantBooks + '\'' +
                ", haveBooks='" + haveBooks + '\'' +
                ", status='" + status + '\'' +
                ", dateCreated=" + dateCreated +
                ']';
    }
}
