package com.daprlabs.aaron.swipedeck2;

import com.daprlabs.aaron.swipedeck.SwipeDeck;
import com.squareup.picasso.Picasso;
import com.twilio.ipmessaging.ui.ChatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String EXTRA_CARD = "extra_card";
    private SwipeDeck cardStack;
    private Context context = this;
    private SwipeDeckAdapter adapter;
    private ArrayList<UserObjectParser.User> testData;
    private UserObjectParser parser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);
        if (isFirstRun)
            startActivity(new Intent(MainActivity.this, FirstLogIn.class));
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();

        //Not first login so run this vvvv
        setContentView(R.layout.activity_main);

        testData = new ArrayList<>();
        parser = new UserObjectParser();
        testData = parser.getAllUsers();
        cardStack = (SwipeDeck) findViewById(R.id.swipe_deck);

        adapter = new SwipeDeckAdapter(testData, this);
        if (cardStack != null) {
            cardStack.setAdapter(adapter);
        }
        cardStack.setCallback(new SwipeDeck.SwipeDeckCallback() {
            @Override
            public void cardSwipedLeft(long stableId) {
                Log.i("MainActivity", "card was swiped left, position in adapter: " + stableId);
            }

            @Override
            public void cardSwipedRight(long stableId) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + stableId);

            }

            @Override
            public boolean isDragEnabled(long itemId) {
                return true;
            }
        });

        cardStack.setLeftImage(R.id.left_image);
        cardStack.setRightImage(R.id.right_image);

        Button messenger = (Button) findViewById(R.id.messenger);
        final Intent intent = new Intent(this, ChatActivity.class);
        messenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                finish();
            }
        });
    }

    public class SwipeDeckAdapter extends BaseAdapter {

        private ArrayList<UserObjectParser.User> data;
        private Context context;

        public SwipeDeckAdapter(ArrayList<UserObjectParser.User> data, Context context) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = getLayoutInflater();
                // normally use a viewholder
                v = inflater.inflate(R.layout.test_card2, parent, false);
            }
            //((TextView) v.findViewById(R.id.textView2)).setText(data.get(position));
            String nameAge = data.get(position).getName() + ", " + data.get(position).getAge();
            ((TextView) v.findViewById(R.id.name)).setText(nameAge);
            ((TextView) v.findViewById(R.id.location)).setText(data.get(position).getAddress());
            ((TextView) v.findViewById(R.id.interest1)).setText(data.get(position).getBio());

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Layer type: ", Integer.toString(v.getLayerType()));
                    Log.i("Hardware Accel type:", Integer.toString(View.LAYER_TYPE_HARDWARE));
                    /*Intent i = new Intent(v.getContext(), BlankActivity.class);
                    v.getContext().startActivity(i);*/
                }
            });
            return v;
        }
    }
}
