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
import java.util.List;

public class BookTrade {
    private String userName;
    private List<Book> wantBooks;
    private List<Book> haveBooks;

    public BookTrade(String userName, List<Book> wantBooks,
                     List<Book> haveBooks) {
        this.userName = userName;
        this.wantBooks = wantBooks;
        this.haveBooks = haveBooks;
    }

    public String getUserName() {
        return userName;
    }

    public List<Book> getWantBooks() {
        return wantBooks;
    }

    public List<Book> getHaveBooks() {
        return haveBooks;
    }

    @Override
    public String toString() {
        return "BookTrade[" +
                "userName='" + userName + '\'' +
                ", wantBooks='" + wantBooks + '\'' +
                ", haveBooks='" + haveBooks + '\'' +
                ']';
    }
}
