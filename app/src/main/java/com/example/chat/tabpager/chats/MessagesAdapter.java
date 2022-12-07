package com.example.chat.tabpager.chats;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.bojo.Messages;
import com.example.chat.databinding.CustomMessageLayoutBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageHolder> {
    private ArrayList<Messages> chatMessages = new ArrayList<Messages>();
    private FirebaseAuth auth;
    private DatabaseReference userReference;
    private Context context;

    public MessagesAdapter(ArrayList<Messages> chatMessages, Context context) {
        this.chatMessages = chatMessages;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        auth = FirebaseAuth.getInstance();
        return new MessageHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.custom_message_layout, parent
                , false));


    }


    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int i) {
        String messageSenderId = auth.getCurrentUser().getUid();
        Messages message = chatMessages.get(i);
        String fromUserId = message.getFrom();
        String messageType = message.getType();
        userReference = FirebaseDatabase.getInstance().getReference().child("User").child(fromUserId);

        makeViewsGone(holder);

        if (!fromUserId.equals(messageSenderId)) {
        setSenderProfileImage(holder);
        }

        if (messageType.equals("text")) {

            setTextMessage(holder, message, messageSenderId);


        } else if (messageType.equals("image")) {
            setImageMessage(holder, message, messageSenderId);

        } else if ((messageType.equals("pdf")) || (messageType.equals("docx"))) {

            setFileMessage(holder, message, messageSenderId);

        }
        if (fromUserId.equals(messageSenderId)) {
            setSenderOptions(holder, message, i);


        } else {

            setRecieverOptions(holder, message, i);


        }

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
    private void setSenderProfileImage(MessageHolder holder){

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Glide.with(context)
                        .load(snapshot.child("image").getValue().toString())
                        .into(holder.itemBinding.imgProfileMessageReceiver);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void deleteSentMessages(final int position, final MessageHolder holder) {
        DatabaseReference rootreference = FirebaseDatabase.getInstance().getReference();

        rootreference.child("Message").child(chatMessages.get(position).getFrom())
                .child(chatMessages.get(position).getTo())
                .child(chatMessages.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(context, "Error Occurred", Toast.LENGTH_SHORT).show();


                }


            }
        });


    }

    private void deleteReceivedMessages(final int position, final MessageHolder holder) {
        DatabaseReference rootreference = FirebaseDatabase.getInstance().getReference();

        rootreference.child("Message").
                child(chatMessages.get(position).getTo()).
                child(chatMessages.get(position).getFrom())
                .child(chatMessages.get(position).getMessageID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(context, "Error Occurred", Toast.LENGTH_SHORT).show();


                }


            }
        });


    }

    private void deleteMessagesForEveryOne(final int position, final MessageHolder holder) {
        DatabaseReference rootreference = FirebaseDatabase.getInstance().getReference();

        rootreference.child("Message").
                child(chatMessages.get(position).getTo()).
                child(chatMessages.get(position).getFrom())
                .child(chatMessages.get(position).getMessageID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    rootreference.child("Message").
                            child(chatMessages.get(position).getFrom()).
                            child(chatMessages.get(position).getTo())
                            .child(chatMessages.get(position).getMessageID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();


                            }

                        }
                    });

                } else {

                    Toast.makeText(context, "Error Occurred", Toast.LENGTH_SHORT).show();


                }


            }
        });


    }


    class MessageHolder extends RecyclerView.ViewHolder {

        CustomMessageLayoutBinding itemBinding;

        public MessageHolder(@NonNull CustomMessageLayoutBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
    }


    private void makeViewsGone(MessageHolder holder) {

        holder.itemBinding.tvMessageReceiver.setVisibility(View.GONE);
        holder.itemBinding.imgProfileMessageReceiver.setVisibility(View.GONE);
        holder.itemBinding.tvMessageSender.setVisibility(View.GONE);
        holder.itemBinding.imgMessageReceiver.setVisibility(View.GONE);
        holder.itemBinding.imgMessageSender.setVisibility(View.GONE);

    }


    private void setTextMessage(MessageHolder holder, Messages message, String messageSenderId) {
        if (message.getFrom().equals(messageSenderId)) {

            holder.itemBinding.tvMessageSender.setVisibility(View.VISIBLE);
            holder.itemBinding.tvMessageSender.setBackgroundResource(R.drawable.sender_message_layout);
            holder.itemBinding.tvMessageSender.setTextColor(Color.BLACK);
            holder.itemBinding.tvMessageSender.setText(message.getMessage() + "\n \n" + message.getTime() + "-" + message.getDate());


        } else {

            holder.itemBinding.imgProfileMessageReceiver.setVisibility(View.VISIBLE);
            holder.itemBinding.tvMessageReceiver.setVisibility(View.VISIBLE);


            holder.itemBinding.tvMessageReceiver.setBackgroundResource(R.drawable.reciever_message_layout);
            holder.itemBinding.tvMessageReceiver.setTextColor(Color.BLACK);
            holder.itemBinding.tvMessageReceiver.setText(message.getMessage() + "\n \n" + message.getTime() + "-" + message.getDate());


        }

    }

    private void setImageMessage(MessageHolder holder, Messages message, String messageSenderId) {

        if (message.getFrom().equals(messageSenderId)) {

            holder.itemBinding.imgMessageSender.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(message.getMessage())
                    .into(holder.itemBinding.imgMessageSender);


        } else {
            holder.itemBinding.imgProfileMessageReceiver.setVisibility(View.VISIBLE);
            holder.itemBinding.imgMessageReceiver.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(message.getMessage())
                    .into(holder.itemBinding.imgMessageReceiver);

        }


    }

    private void setFileMessage(MessageHolder holder, Messages message, String messageSenderId) {

        if (message.getFrom().equals(messageSenderId)) {

            holder.itemBinding.imgMessageSender.setVisibility(View.VISIBLE);
            holder.itemBinding.imgMessageSender.setBackgroundResource(R.drawable.send_file);


        } else {
            holder.itemBinding.imgProfileMessageReceiver.setVisibility(View.VISIBLE);
            holder.itemBinding.imgMessageReceiver.setVisibility(View.VISIBLE);
            holder.itemBinding.imgMessageReceiver.setBackgroundResource(R.drawable.send_file);


        }

    }


    private void setSenderOptions(MessageHolder holder, Messages message, int i) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getType().equals("pdf") || message.getType().equals("docx")) {

                    sentFileClickListener(holder, message, i);

                } else if (message.getType().equals("text")) {
                    SentTextClickListener(holder, message, i);


                } else if (message.getType().equals("image")) {
                    SentImageClickListener(holder, message, i);

                }
            }
        });


    }

    private void sentFileClickListener(MessageHolder holder, Messages message, int i) {
        CharSequence options[] = new CharSequence[]{
                "Download And View",
                "Delete for me",
                "Delete For Every One"
                , "Cancel"

        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Message?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {

                if (position == 0) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(message.getMessage()), "application/pdf");

                    holder.itemView.getContext().startActivity(intent);

                }
                if (position == 1) {
                    deleteSentMessages(i, holder);
                }
                if (position == 2) {
                    deleteMessagesForEveryOne(i, holder);

                }
                if (position == 3) {
                    dialog.dismiss();

                }


            }
        });
        builder.show();

    }

    private void SentTextClickListener(MessageHolder holder, Messages message, int i) {
        CharSequence options[] = new CharSequence[]{
                "Delete for me",
                "Delete For Every One"
                , "Cancel"

        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Message?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {

                if (position == 0) {
                    deleteSentMessages(i, holder);


                }
                if (position == 1) {
                    deleteMessagesForEveryOne(i, holder);


                }
                if (position == 2) {
                    dialog.dismiss();
                }


            }
        });
        builder.show();


    }

    private void SentImageClickListener(MessageHolder holder, Messages message, int i) {


        CharSequence options[] = new CharSequence[]{
                " View Image",
                "Delete for me",
                "Delete For Every One"
                , "Cancel"

        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Message?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {

                if (position == 0) {
                    Intent intent = new Intent(context, ImageViewerActivity.class);
                    intent.putExtra("url", message.getMessage());
                    context.startActivity(intent);

                }
                if (position == 1) {
                    deleteSentMessages(i, holder);


                }
                if (position == 2) {
                    deleteMessagesForEveryOne(i, holder);

                }
                if (position == 3) {

                    dialog.dismiss();
                }


            }
        });
        builder.show();

    }

    private void setRecieverOptions(MessageHolder holder, Messages message, int i) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getType().equals("pdf") || message.getType().equals("docx")) {

                    receivedFileClickListener(holder, message, i);
                } else if (message.getType().equals("text")) {

                    receivedTextClickListener(holder, message, i);


                } else if (message.getType().equals("image")) {

                    receivedImageClickListener(holder, message, i);

                }
            }
        });


    }


    private void receivedFileClickListener(MessageHolder holder, Messages message, int i) {


        CharSequence options[] = new CharSequence[]{
                "Download And View",
                "Delete for me"
                , "Cancel"

        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Message?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {

                if (position == 0) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(message.getMessage()), "application/pdf");

                    holder.itemView.getContext().startActivity(intent);

                }
                if (position == 1) {
                    deleteReceivedMessages(i, holder);


                }
                if (position == 2) {
                    dialog.dismiss();
                }


            }
        });
        builder.show();


    }

    private void receivedTextClickListener(MessageHolder holder, Messages message, int i) {
        CharSequence options[] = new CharSequence[]{
                "Delete for me"
                , "Cancel"

        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Message?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {

                if (position == 0) {

                    deleteReceivedMessages(i, holder);


                }
                if (position == 1) {
                    dialog.dismiss();

                }


            }
        });
        builder.show();


    }

    private void receivedImageClickListener(MessageHolder holder, Messages message, int i) {


        CharSequence options[] = new CharSequence[]{
                " View Image",
                "Delete for me"
                , "Cancel"

        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Message?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {

                if (position == 0) {
                    Intent intent = new Intent(context, ImageViewerActivity.class);
                    intent.putExtra("url", message.getMessage());
                    context.startActivity(intent);

                }
                if (position == 1) {

                    deleteReceivedMessages(i, holder);


                }
                if (position == 2) {
                    dialog.dismiss();
                }


            }
        });
        builder.show();
    }


}
