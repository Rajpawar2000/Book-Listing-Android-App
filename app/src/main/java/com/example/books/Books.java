package com.example.books;

public class Books {

    // Name of the Book.
    private String mBookName;

    // Author name of the book.
    private String mAuthorName;

    private String mBookImage;

    private String mBookDesc;

    private String mUrl;


    /**
     * Construct a new {@link Books} constructor
     * @param bookName is name of the book.
     * @param authorName is the author of the book.
     */
    Books(String bookName, String authorName, String bookImage, String bookDesc, String url){
        mBookName = bookName;
        mAuthorName = authorName;
        mBookImage = bookImage;
        mBookDesc = bookDesc;
        mUrl = url;
    }

    // returns name of the book.
    public String getBookName(){ return mBookName; }

    // returns author the book
    public String getAuthorName(){ return mAuthorName; }

    public String getBookImage(){ return mBookImage; }

    public String getBookDesc() { return mBookDesc; }

    public String getUrl(){ return mUrl; }


}
