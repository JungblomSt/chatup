package com.example.chatup

import android.util.Log
import com.example.chatup.data.ChatMessage
import com.example.chatup.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

object FirebaseManager {

    /**
     * Firestore database instance.
     * Init lazy to ensure it is only created when first accessed.
     */
    private val db by lazy { Firebase.firestore }

    /**
     * Holds the currently authenticated Firebase user.
     */
    private lateinit var currentUser: FirebaseUser

    fun markDelivered() {
        val currentUserId = Firebase.auth.currentUser?.uid ?: return

        db.collection("conversation")
            .whereArrayContains("users", currentUserId)
            .addSnapshotListener { conversations, e ->
                if (e != null) {
                    Log.e("!!!", "Failed to listen to conversations: ${e.message}")
                    return@addSnapshotListener
                }

                conversations?.documents?.forEach { conversationDoc ->

                    if (conversationDoc.getString("conversationType") == "group") {
                        return@forEach
                    }

                    val conversationId = conversationDoc.id

                    db.collection("conversation")
                        .document(conversationId)
                        .collection("messages")
//                        .whereEqualTo("receiverId", currentUserId)
//                        .whereEqualTo("delivered", false)
                        .get()
                        .addOnSuccessListener { messages ->

                            messages?.documents?.forEach { msgDoc ->

                                val message =
                                    msgDoc.toObject(ChatMessage::class.java) ?: return@forEach

                                if (message.senderId != currentUserId && !message.deliveredTo.contains(
                                        currentUserId
                                    )
                                ) {

                                    msgDoc.reference.update(
                                        "deliveredTo",
                                        FieldValue.arrayUnion(
                                            currentUserId
                                        )
                                    )

//                                    if (!message.deliveredTo.contains(currentUserId)) {
//                                        msgDoc.reference.update(
//                                            "deliveredTo",
//                                            com.google.firebase.firestore.FieldValue.arrayUnion(
//                                                currentUserId
//                                            )
//                                        )

                                }
                            }

                        }

//                            messages?.documents?.forEach { msg ->
//                                msg.reference.update("delivered", true)
//                                    .addOnSuccessListener {
//                                        Log.d("!!!", "Delivered marked for message ${msg.id}")
//                                    }
//                                    .addOnFailureListener { e3 ->
//                                        Log.e("!!!", "Failed to mark delivered: ${e3.message}")
//                                    }
//                                val lastMessageId = conversationDoc.getString("lastMessageId")
//
//                                if (msg.id == lastMessageId){
//                                    conversationDoc.reference.update(
//                                        mapOf ("lastMessageDelivered" to true,
//                                            "lastUpdated" to System.currentTimeMillis()))
//                                }
//                            }
                }
            }


    }

    // todo lägg till komentarer
    fun setTyping(conversationId: String, isTyping: Boolean) {
        val currentUserId = Firebase.auth.currentUser?.uid ?: return

        db.collection("conversation")
            .document(conversationId)
            .update("typing.$currentUserId", isTyping)
    }

    // todo lägg till komentarer

    fun typingSnapShotListener(
        conversationId: String,
        friendId: String,
        onTyping: (Boolean) -> Unit
    ): ListenerRegistration {

        return db.collection("conversation")
            .document(conversationId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    e.message?.let { Log.e("!!!", it) }
                    return@addSnapshotListener
                }
                val typing = snapshot?.get("typing") as? Map<*, *> ?: return@addSnapshotListener
                val isTyping = typing[friendId] as? Boolean ?: false

                onTyping(isTyping)

            }


    }

    /**
     * Fetches all users from the Firestore 'users' collection.
     * This function is used to display a list of all users for starting new conversations.
     *
     * @param onComplete Callback invoked when the list of users has been successfully fetched.
     *                   Returns a List<User> containing all users in the database.
     * @param onException Callback invoked if there is an error while fetching users.
     *                    Returns the Exception for logging or UI handling.
     */
    fun getAllUsers(onComplete: (List<User>) -> Unit, onException: (Exception) -> Unit) {

        val currentUserId = Firebase.auth.currentUser?.uid

        db.collection("users")
            .get()
            .addOnSuccessListener { snapshots ->
                val userList = snapshots.documents.mapNotNull { doc ->
                    doc.toObject(User::class.java)?.copy(uid = doc.id)
                }.filter { user ->
                    user.uid != currentUserId
                }

                onComplete(userList)

            }.addOnFailureListener { e ->
                onException(e)
            }
    }


    // todo Uppdatera komentarer


    /**
     * Sets up a real-time listener for messages in a specific conversation.
     *
     * @param conversationId The Firestore document ID representing the conversation.
     *                       Used to locate the correct subcollection of messages.
     * @param onUpdate Callback invoked whenever messages are added, modified, or removed
     *                 in this conversation. Returns a List<ChatMessage> representing the current messages.
     */
//    fun snapShotListener(
//        conversationId: String,
//        onUpdate: (List<ChatMessage>) -> Unit,
//        chatIsOpened: () -> Boolean
//    ): ListenerRegistration? {
//
//        val conversationRef = db.collection("conversation").document(conversationId)
//
//        val currentUserId = Firebase.auth.currentUser?.uid ?: return null
//
//        var isGroupChat = false
//
//        conversationRef.get().addOnSuccessListener { convSnapshot ->
//            isGroupChat = convSnapshot.getString("conversationType") == "group"
//        }
//
//        return db.collection("conversation")
//            .document(conversationId)
//            .collection("messages")
//            .orderBy("timeStamp")
//            .addSnapshotListener { snapshot, e ->
//                if (e != null) {
//                    Log.e("!!!", e.message.toString())
//                    return@addSnapshotListener
//                }
//
//                if (snapshot == null) return@addSnapshotListener
//
//                val chatMessages = snapshot.documents.mapNotNull { doc ->
//                    doc.toObject(ChatMessage::class.java)
//                }
//                onUpdate(chatMessages)
//
//                snapshot.documents.forEach() { doc ->
//                    val message = doc.toObject(ChatMessage::class.java) ?: return@forEach
//
//                    // gör en check för race quota här men få det att funka först
//
//                    if (!isGroupChat && !message.deliveredTo.contains(currentUserId)) {
//                        doc.reference.update(
//                            "deliveredTo",
//                            FieldValue.arrayUnion(currentUserId)
//                        )
//                    }
//
//                    if (chatIsOpened() && !message.seenBy.contains(currentUserId)) {
//                        doc.reference.update(
//                            "seenBy",
//                            FieldValue.arrayUnion(currentUserId)
//                        )
//                    }
//
//                    Log.d("DEBUG_GROUP_MSG", "MessageId=${doc.id}, sender=${message.senderId}, receiver=${message.receiverId}, text=${message.messages}")
//                    Log.d("DEBUG_GROUP_MSG", "Chat opened = ${chatIsOpened()}, deliveredTo=${message.deliveredTo}, seenBy=${message.seenBy}")
//
//                }
//
//                val lastDoc = snapshot.documents.lastOrNull() ?: return@addSnapshotListener
//                val lastMessage =
//                    lastDoc.toObject(ChatMessage::class.java) ?: return@addSnapshotListener
//
//
//
//                if (isGroupChat
//                    && !lastMessage.seenBy.contains(currentUserId)
//                    && chatIsOpened()
//                ) {
//
//                    lastDoc.reference.update(
//                        "seenBy",
//                        FieldValue.arrayUnion(currentUserId)
//                    )
//
//                    db.collection("conversation")
//                        .document(conversationId)
//                        .update(
//                            mapOf(
//                                "lastMessageSeen" to true,
//                                "lastUpdated" to System.currentTimeMillis()
//                            )
//                        )
//                }
//
//
//            }
//    }


    fun privateChatSnapshotListener(
        conversationId: String,
        otherUserId: String,
        chatIsOpened: () -> Boolean,
        onUpdate: (List<ChatMessage>) -> Unit
    ): ListenerRegistration {

        val currentUserId = Firebase.auth.currentUser!!.uid

        return db.collection("conversation")
            .document(conversationId)
            .collection("messages")
            .whereEqualTo("receiverId", otherUserId)
            .orderBy("timeStamp")
            .addSnapshotListener { snapshot, _ ->

                if (snapshot == null) return@addSnapshotListener

                val messages = snapshot.toObjects(ChatMessage::class.java)
                onUpdate(messages)

                snapshot.documents.forEach { doc ->
                    val msg = doc.toObject(ChatMessage::class.java) ?: return@forEach

                    if (chatIsOpened() && !msg.seenBy.contains(currentUserId)) {
                        doc.reference.update(
                            "seenBy",
                            FieldValue.arrayUnion(currentUserId)
                        )
                    }
                }
            }
    }

    fun groupChatSnapshotListener(
        conversationId: String,
        chatIsOpened: () -> Boolean,
        onUpdate: (List<ChatMessage>) -> Unit
    ): ListenerRegistration {

        val currentUserId = Firebase.auth.currentUser!!.uid

        return db.collection("conversation")
            .document(conversationId)
            .collection("messages")
            .whereEqualTo("receiverId", null)
            .orderBy("timeStamp")
            .addSnapshotListener { snapshot, _ ->

                if (snapshot == null) return@addSnapshotListener

                val messages = snapshot.toObjects(ChatMessage::class.java)
                onUpdate(messages)

                snapshot.documents.forEach { doc ->
                    val msg = doc.toObject(ChatMessage::class.java) ?: return@forEach

                    if (chatIsOpened() && !msg.seenBy.contains(currentUserId)) {
                        doc.reference.update(
                            "seenBy",
                            FieldValue.arrayUnion(currentUserId)
                        )
                    }
                }
            }
    }

        /**
         * Sends a chat message to a specific user.
         * Ensures the sender is authenticated.
         * Creates or updates the conversation metadata.
         * Stores the message inside the conversation's messages collection.
         *
         * @param chatText The message content that will be sent.
         * @param receiverId The unique user ID (uid) of the message receiver.
         *
         */
        fun sendChatMessage(chatText: String, receiverId: String) {

            currentUser = Firebase.auth.currentUser ?: return

            val conversationId = getConversationId(currentUser.uid, receiverId)

            val chatMessageRef = db.collection("conversation")
                .document(conversationId)
                .collection("messages")
                .document()

            val chatMessage = ChatMessage(
                senderId = currentUser.uid,
                receiverId = receiverId,
                messages = chatText,
                timeStamp = System.currentTimeMillis(),
                deliveredTo = emptyList(),
                seenBy = listOf(currentUser.uid)
//            delivered = false,
//            seen = false
            )

            chatMessageRef.set(chatMessage)

            // Update or create conversation metadata
            db.collection("conversation")
                .document(conversationId)
                .set(
                    mapOf(
                        "users" to listOf(currentUser.uid, receiverId),
                        "lastMessage" to chatText,
                        "lastUpdated" to System.currentTimeMillis(),
                        "lastMessageId" to chatMessageRef.id,
                        "lastMessageDelivered" to false,
                        "lastMessageSeen" to false
                    ),
                    SetOptions.merge()
                )

        }

        fun sendGroupMessage(conversationId: String, chatText: String, members: List<String>) {
            val currentUserId = Firebase.auth.currentUser?.uid ?: return

            val groupMessageRef = db.collection("conversation")
                .document(conversationId)
                .collection("messages")
                .document()

            val groupMessage = ChatMessage(
                senderId = currentUserId,
                messages = chatText,
                receiverId = null,
                timeStamp = System.currentTimeMillis(),
                deliveredTo = emptyList(),
                seenBy = listOf(currentUserId)
            )

            groupMessageRef.set(groupMessage)

            db.collection("conversation")
                .document(conversationId)
                .update(
                    mapOf(
                        "lastMessage" to chatText,
                        "lastMessageId" to groupMessageRef.id,
                        "lastMessageSeen" to false,
                        "lastUpdated" to System.currentTimeMillis()
                    )
                ) .addOnSuccessListener { Log.d("DEBUG_GROUP_MSG", "Conversation updated successfully") }
                .addOnFailureListener { e -> Log.e("DEBUG_GROUP_MSG", "Failed to update conversation: ${e.message}") }

//        db.collection("conversation")
//            .document(conversationId)
//            .set(
//                mapOf(
//                    "lastMessage" to chatText,
//                    "lastUpdated" to System.currentTimeMillis()
//                ), SetOptions.merge()
//            )
        }

        /**
         * Creates a unique and consistent conversation ID for a chat between two users.
         * The two user IDs are sorted alphabetically so the order is always the same, no matter which user sends or receives a message.
         * The sorted IDs are then joined into a single string using an underscore.
         *
         * @param user1Id the first user ID
         * @param user2Id the second user ID
         */
        fun getConversationId(user1Id: String, user2Id: String): String {
            return listOf(user1Id, user2Id).sorted().joinToString("_")
        }

        // todo lägg till komentarer
        fun createConversationId(user2Id: String): String {
            currentUser = Firebase.auth.currentUser ?: return ""
            val user1Id: String = currentUser.uid
            return listOf(user1Id, user2Id).sorted().joinToString("_")
        }

        fun createGroupConversation(
            groupName: String,
            members: List<String>,
            onComplete: (String) -> Unit
        ) {

            val currentUserId = Firebase.auth.currentUser?.uid ?: return


            val allMembers = (members + currentUserId).distinct()


            val groupConversation = mapOf(
                "conversationType" to "group",
                "name" to groupName,
                "users" to allMembers,
                "lastMessage" to "",
                "lastUpdated" to System.currentTimeMillis()
            )

            db.collection("conversation")
                .add(groupConversation)
                .addOnSuccessListener { doc ->
                    onComplete(doc.id)
                }


        }


    }