package goosh.inappchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import com.twilio.ipmessaging.Message;


public class MainActivity extends AppCompatActivity {

    final static String SERVER_TOKEN_URL = "http://e1a1e6f4.ngrok.io";
    final static String DEFAULT_CHANNEL_NAME = "general";

    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;
    private ArrayList<Message>


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messagesAdapter = new MessagesAdapter();
        messagesRecyclerView.setAdapter(messagesAdapter);
    }

    class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder{
            public TextView messageTextView;

            public ViewHolder(TextView textView){
                super(textView);
                messageTextView = textView;
            }
        }

        public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            TextView messageTextView = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_text_view, parent, false);
            return new ViewHolder(messageTextView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Message message = messages.get(position);
            String messageText = String.format("%s: %s", messages.getAuthor(), message.getMessageBody());
            holder.messageTextView.setText(messageText);
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }
}
