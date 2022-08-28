package com.SigmaDating.apk.views.chat;

import android.content.Context;
import android.util.Log;

import com.SigmaDating.apk.views.Home;
import com.google.gson.Gson;
import com.twilio.conversations.CallbackListener;
import com.twilio.conversations.Conversation;
import com.twilio.conversations.ConversationListener;
import com.twilio.conversations.ConversationsClient;
import com.twilio.conversations.ConversationsClientListener;
import com.twilio.conversations.ErrorInfo;
import com.twilio.conversations.Participant;
import com.twilio.conversations.Message;
import com.twilio.conversations.StatusListener;
import com.twilio.conversations.User;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

interface QuickstartConversationsManagerListener {
    void receivedNewMessage();
    void messageSentCallback();
    void reloadMessages();
}

interface AccessTokenListener {
    void receivedAccessToken(@Nullable String token, @Nullable Exception exception);
}


 public class QuickstartConversationsManager {




    private final static String DEFAULT_CONVERSATION_NAME = "general_1234";



    final private ArrayList<Message> messages = new ArrayList<>();

    private ConversationsClient conversationsClient;

    private Conversation conversation;

    private QuickstartConversationsManagerListener conversationsManagerListener;

    private String tokenURL = "";

    private class TokenResponse {
        String token;
    }


    void initializeWithAccessToken(final Context context, final String token) {
        ConversationsClient.Properties props = ConversationsClient.Properties.newBuilder().setCommandTimeout(90000).createProperties();
        ConversationsClient.create(context, token, props, mConversationsClientCallback);
    }

    private void retrieveToken(AccessTokenListener listener) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(tokenURL)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = "";
            if (response != null && response.body() != null) {
                responseBody = response.body().string();
            }
            Log.d("TAG@123", "Response from server: " + responseBody);
            Gson gson = new Gson();
            TokenResponse tokenResponse = gson.fromJson(responseBody,TokenResponse.class);
            String accessToken = tokenResponse.token;
            Log.d("TAG@123", "Retrieved access token from server: " + accessToken);
            listener.receivedAccessToken(accessToken, null);

        }
        catch (IOException ex) {
            Log.e("TAG@123", ex.getLocalizedMessage(),ex);
            listener.receivedAccessToken(null, ex);
        }
    }

    void sendMessage(String messageBody) {
        if (conversation != null) {
            Message.Options options = Message.options().withBody(messageBody);
            Log.d("TAG@123","Message created");
            conversation.sendMessage(options, new CallbackListener<Message>() {
                @Override
                public void onSuccess(Message message) {
                    if (conversationsManagerListener != null) {
                        conversationsManagerListener.messageSentCallback();
                    }
                }
            });
        }else {
            Log.d("TAG@123","conversation is null");

        }
    }


    private void loadChannels() {
        if (conversationsClient == null || conversationsClient.getMyConversations() == null) {
            return;
        }
        conversationsClient.getConversation(DEFAULT_CONVERSATION_NAME, new CallbackListener<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                if (conversation != null) {
                    if (conversation.getStatus() == Conversation.ConversationStatus.JOINED
                            || conversation.getStatus() == Conversation.ConversationStatus.NOT_PARTICIPATING) {
                        Log.d("TAG@123", "Already Exists in Conversation: " + DEFAULT_CONVERSATION_NAME);
                        QuickstartConversationsManager.this.conversation = conversation;
                        QuickstartConversationsManager.this.conversation.addListener(mDefaultConversationListener);
                        QuickstartConversationsManager.this.loadPreviousMessages(conversation);
                    } else {
                        Log.d("TAG@123", "Joining Conversation: " + DEFAULT_CONVERSATION_NAME);
                        joinConversation(conversation);
                    }
                }
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                Log.e("TAG@123", "Error retrieving conversation: " + errorInfo.getMessage());
                createConversation();
            }

        });
    }

    private void createConversation() {
        Log.d("TAG@123", "Creating Conversation: " + DEFAULT_CONVERSATION_NAME);

        conversationsClient.createConversation(DEFAULT_CONVERSATION_NAME,
                new CallbackListener<Conversation>() {
                    @Override
                    public void onSuccess(Conversation conversation) {
                        if (conversation != null) {
                            Log.d("TAG@123", "Joining Conversation: " + DEFAULT_CONVERSATION_NAME);
                            joinConversation(conversation);
                        }
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        Log.e("TAG@123", "Error creating conversation: " + errorInfo.getMessage());
                    }
                });
    }


    private void joinConversation(final Conversation conversation) {
        Log.d("TAG@123", "Joining Conversation: " + conversation.getUniqueName());
        if (conversation.getStatus() == Conversation.ConversationStatus.JOINED) {

            QuickstartConversationsManager.this.conversation = conversation;
            Log.d("TAG@123", "Already joined default conversation");
            QuickstartConversationsManager.this.conversation.addListener(mDefaultConversationListener);
            return;
        }
        conversation.join(new StatusListener() {
            @Override
            public void onSuccess() {
                QuickstartConversationsManager.this.conversation = conversation;
                Log.d("TAG@123", "Joined default conversation");
                QuickstartConversationsManager.this.conversation.addListener(mDefaultConversationListener);
                QuickstartConversationsManager.this.loadPreviousMessages(conversation);
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                Log.e("TAG@123", "Error joining conversation: " + errorInfo.getMessage());
            }
        });
    }

    private void loadPreviousMessages(final Conversation conversation) {
        conversation.getLastMessages(100,
                new CallbackListener<List<Message>>() {
                    @Override
                    public void onSuccess(List<Message> result) {
                        messages.addAll(result);
                        if (conversationsManagerListener != null) {
                            conversationsManagerListener.reloadMessages();
                        }
                    }
                });
    }

    private final ConversationsClientListener mConversationsClientListener =
            new ConversationsClientListener() {

                @Override
                public void onConversationAdded(Conversation conversation) {
                    Log.d("TAG@123", "onConversationAdded");
                }

                @Override
                public void onConversationUpdated(Conversation conversation, Conversation.UpdateReason updateReason) {
                    Log.d("TAG@123", "onConversationUpdated");
                }

                @Override
                public void onConversationDeleted(Conversation conversation) {
                    Log.d("TAG@123", "onConversationDeleted");
                }

                @Override
                public void onConversationSynchronizationChange(Conversation conversation) {
                    Log.d("TAG@123", "onConversationSynchronizationChange");
                }

                @Override
                public void onError(ErrorInfo errorInfo) {
                    Log.d("TAG@123", "onError");
                }

                @Override
                public void onUserUpdated(User user, User.UpdateReason updateReason) {
                    Log.d("TAG@123", "onUserUpdated");
                }

                @Override
                public void onUserSubscribed(User user) {
                    Log.d("TAG@123", "onUserSubscribed");
                }

                @Override
                public void onUserUnsubscribed(User user) {
                    Log.d("TAG@123", "onUserUnsubscribed");
                }

                @Override
                public void onClientSynchronization(ConversationsClient.SynchronizationStatus synchronizationStatus) {
                    Log.d("TAG@123", "onClientSynchronization");

                    if (synchronizationStatus == ConversationsClient.SynchronizationStatus.COMPLETED) {
                        loadChannels();
                    }
                }

                @Override
                public void onNewMessageNotification(String s, String s1, long l) {
                    Log.d("TAG@123", "onNewMessageNotification");

                }

                @Override
                public void onAddedToConversationNotification(String s) {
                    Log.d("TAG@123", "onAddedToConversationNotification");

                }

                @Override
                public void onRemovedFromConversationNotification(String s) {
                    Log.d("TAG@123", "onRemovedFromConversationNotification");

                }

                @Override
                public void onNotificationSubscribed() {
                    Log.d("TAG@123", "onNotificationSubscribed");

                }

                @Override
                public void onNotificationFailed(ErrorInfo errorInfo) {
                    Log.d("TAG@123", "onNotificationFailed");

                }

                @Override
                public void onConnectionStateChange(ConversationsClient.ConnectionState connectionState) {
                    Log.d("TAG@123", "onConnectionStateChange");

                }

                @Override
                public void onTokenExpired() {
                    Log.d("TAG@123", "onTokenExpired");

                }

                @Override
                public void onTokenAboutToExpire() {
                    Log.d("TAG@123", "onTokenAboutToExpire");

                    conversationsClient.updateToken("token", new StatusListener() {
                        @Override
                        public void onSuccess() {
                            Log.d("TAG@123", "Refreshed access token.");
                        }
                    });
                }
            };

    private final CallbackListener<ConversationsClient> mConversationsClientCallback =
            new CallbackListener<ConversationsClient>() {
                @Override
                public void onSuccess(ConversationsClient conversationsClientP) {
                    QuickstartConversationsManager.this.conversationsClient = conversationsClientP;
                    conversationsClient.addListener(QuickstartConversationsManager.this.mConversationsClientListener);
                    Log.d("TAG@123", "Success creating Twilio Conversations Client");
                }

                @Override
                public void onError(ErrorInfo errorInfo) {
                    Log.e("TAG@123", "Error creating Twilio Conversations Client: " + errorInfo.getMessage());
                }
            };


    private final ConversationListener mDefaultConversationListener = new ConversationListener() {


        @Override
        public void onMessageAdded(final Message message) {
            Log.d("TAG@123", "Message added");
            messages.add(message);
            if (conversationsManagerListener != null) {
                conversationsManagerListener.receivedNewMessage();
            }
        }

        @Override
        public void onMessageUpdated(Message message, Message.UpdateReason updateReason) {
            Log.d("TAG@123", "Message updated: " + message.getMessageBody());
        }

        @Override
        public void onMessageDeleted(Message message) {
            Log.d("TAG@123", "Message deleted");
        }

        @Override
        public void onParticipantAdded(Participant participant) {
            Log.d("TAG@123", "Participant added: " + participant.getIdentity());
        }

        @Override
        public void onParticipantUpdated(Participant participant, Participant.UpdateReason updateReason) {
            Log.d("TAG@123", "Participant updated: " + participant.getIdentity() + " " + updateReason.toString());
        }

        @Override
        public void onParticipantDeleted(Participant participant) {
            Log.d("TAG@123", "Participant deleted: " + participant.getIdentity());
        }

        @Override
        public void onTypingStarted(Conversation conversation, Participant participant) {
            Log.d("TAG@123", "Started Typing: " + participant.getIdentity());
        }

        @Override
        public void onTypingEnded(Conversation conversation, Participant participant) {
            Log.d("TAG@123", "Ended Typing: " + participant.getIdentity());
        }

        @Override
        public void onSynchronizationChanged(Conversation conversation) {

        }
    };

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setListener(QuickstartConversationsManagerListener listener)  {
        this.conversationsManagerListener = listener;
    }
}

