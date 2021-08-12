package com.example.books;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BooksAdapter extends ArrayAdapter<Books> {
    /**
     * Constructor
     * @param context  The current context
     */
    public BooksAdapter(Context context, List<Books> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView==null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item,parent,false);
        }

        Books currentBooks = getItem(position);

        TextView bookNameView = listItemView.findViewById(R.id.book_name);
        bookNameView.setText(currentBooks.getBookName());

        TextView authorNameView = listItemView.findViewById(R.id.author_name);
        authorNameView.setText(currentBooks.getAuthorName());

        TextView descView = listItemView.findViewById(R.id.description);
        descView.setText(currentBooks.getBookDesc());


        ImageView bookImageView = listItemView.findViewById(R.id.book_image);

        try{
            URL imageUrl = new URL(currentBooks.getBookImage());
            Glide.with(getContext()).load(imageUrl).into(bookImageView);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return listItemView;
    }
}
