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
import java.util.UUID;

public class Trade implements Comparable<Trade> {
    private String internalID;
    private String userOne;
    private String bookOne;
    private boolean userOneApproved;
    private String userTwo;
    private String bookTwo;
    private boolean userTwoApproved;
    private String status;
    private Date dateCreated;

    public Trade(String userOne, String bookOne, boolean userOneApproved,
                 String userTwo, String bookTwo, boolean userTwoApproved,
                 String status, Date dateCreated)
    {
        this.internalID = UUID.randomUUID().toString();
        this.userOne = userOne;
        this.userOneApproved = userOneApproved;
        this.userTwo = userTwo;
        this.userTwoApproved = userTwoApproved;
        this.bookOne = bookOne;
        this.bookTwo = bookTwo;
        this.status = status;
        this.dateCreated = dateCreated;
    }
    public String getInternalID() {
        return internalID;
    }
    public String getUserOne() {
        return userOne;
    }

    public boolean isUserOneApproved() {
        return userOneApproved;
    }

    public String getUserTwo() {
        return this.userTwo;
    }

    public boolean isUserTwoApproved() {
        return userTwoApproved;
    }

    public String getBookOne() {
        return bookOne;
    }

    public String getBookTwo() {
        return bookTwo;
    }

    public String getStatus() {
        return status;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "internalID=" + internalID +
                ", userOneID=" + userOne +
                ", userOneApproved=" + userOneApproved +
                ", userTwo=" + userTwo +
                ", userTwoApproved=" + userTwoApproved +
                ", bookOne=" + bookOne +
                ", bookTwo=" + bookTwo +
                ", status='" + status + '\'' +
                ", dateCreated=" + dateCreated +
                '}';
    }

    @Override
    public int compareTo(Trade o) {
        if(this == o)
            return 1;
        else if(this.getInternalID().equals(o.getInternalID()))
            return 1;
        return 0;
    }
}
