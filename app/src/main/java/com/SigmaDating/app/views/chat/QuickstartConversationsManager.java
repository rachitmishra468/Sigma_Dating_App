package com.SigmaDating.app.views.chat;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.SigmaDating.app.Sigmadatingapp;
import com.SigmaDating.app.views.Home;
import com.google.gson.JsonObject;
import com.twilio.conversations.Attributes;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;




public class QuickstartConversationsManager {

    public MutableLiveData<Integer> mutableLiveData = new MutableLiveData<>();
    private final static String DEFAULT_CONVERSATION_NAME = "Sigma" + Home.Companion.getMatch_id();
    final private ArrayList<Message> messages = new ArrayList<>();
    private ConversationsClient conversationsClient;
    private Conversation mconversation;
    public  SendNotification sm;

    void initializeWithAccessToken(final Context context, final String token,final SendNotification sendNotification) {
        mutableLiveData.postValue(5);
        sm=sendNotification;
        ConversationsClient.Properties props = ConversationsClient.Properties.newBuilder().setCommandTimeout(90000).createProperties();
        ConversationsClient.create(context, token, props, mConversationsClientCallback);
        Log.d("TAG@123", "DEFAULT_CONVERSATION_NAME : " + DEFAULT_CONVERSATION_NAME);
    }


    void sendMessage(String messageBody, JsonObject user) {
        if (mconversation != null) {
            mconversation.prepareMessage()
                    .setBody(messageBody)
                    .setAttributes(new Attributes(String.valueOf(user)))
                    .build()
                    .send(new CallbackListener<Message>() {
                        @Override
                        public void onSuccess(Message result) {
                            Log.d("TAG@12345", "message send call back");
                            mutableLiveData.postValue(2);
                            if (!Home.Companion.getChatFlag()) {
                                Home.Companion.setChatFlag(true);
                                Log.d("TAG@12345", "message send Notification first time ");
                                sm.send();

                            }
                        }
                    });

        } else {
            Log.d("TAG@123", "conversation is null");

        }
    }

    private void loadChannels() {
        if (conversationsClient == null || conversationsClient.getMyConversations() == null) {
            return;
        }
        List<Conversation> con = conversationsClient.getMyConversations();
        String name = DEFAULT_CONVERSATION_NAME;
        if (con.size() > 0) {
            name = con.get(0).getSid();
            Log.d("TAG@123", "con " + con.get(0).getFriendlyName());
            Log.d("TAG@123", "con " + con.get(0).getSid());
        }

        conversationsClient.getConversation(name, new CallbackListener<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                if (conversation != null) {
                    if (conversation.getStatus() == Conversation.ConversationStatus.JOINED || conversation.getStatus() == Conversation.ConversationStatus.NOT_PARTICIPATING) {
                        Log.d("TAG@123", "Already Exists in Conversation: " + DEFAULT_CONVERSATION_NAME);
                        QuickstartConversationsManager.this.mconversation = conversation;
                        QuickstartConversationsManager.this.mconversation.addListener(mDefaultConversationListener);
                        QuickstartConversationsManager.this.loadPreviousMessages(conversation);
                    } else {
                        Log.d("TAG@123", "loadChannels Joining Conversation : " + DEFAULT_CONVERSATION_NAME);
                        joinConversation(conversation);
                    }
                }
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                Log.e("TAG@123", "Error retrieving conversation: " + errorInfo.getMessage() + " Error code : " + errorInfo.getCode());
                createConversation();
            }

        });
    }

    private void createConversation() {
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
            QuickstartConversationsManager.this.mconversation = conversation;
            Log.d("TAG@123", "Already joined default conversation");
            QuickstartConversationsManager.this.mconversation.addListener(mDefaultConversationListener);
            return;
        }
        conversation.join(new StatusListener() {
            @Override
            public void onSuccess() {
                QuickstartConversationsManager.this.mconversation = conversation;
                Log.d("TAG@123", "Joined default conversation");
                QuickstartConversationsManager.this.mconversation.addListener(mDefaultConversationListener);
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
                        mutableLiveData.postValue(3);

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
                    mutableLiveData.postValue(4);

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
                    Log.d("TAG@123", "User getFriendlyName : " + user.getFriendlyName());
                    Log.d("TAG@123", "User getIdentity : " + user.getIdentity());
                    Log.d("TAG@123", "User getAttributes" + user.getAttributes().toString());
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
                    conversationsClient.registerFCMToken(new ConversationsClient.FCMToken(Sigmadatingapp.Companion.getFcm_token()), () -> {

                    });
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
            mutableLiveData.postValue(1);

        }

        @Override
        public void onMessageUpdated(Message message, Message.UpdateReason updateReason) {
            Log.d("TAG@123", "Message updated: " + message.getBody());
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


    interface SendNotification {
        public void send();
    }
}

