package com.example.chatup

import android.util.Log
import com.example.chatup.data.ChatMessage
import com.example.chatup.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
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


    /**
     * Sets up a real-time listener for messages in a specific conversation.
     *
     * @param conversationId The Firestore document ID representing the conversation.
     *                       Used to locate the correct subcollection of messages.
     * @param onUpdate Callback invoked whenever messages are added, modified, or removed
     *                 in this conversation. Returns a List<ChatMessage> representing the current messages.
     */
    fun snapShotListener(conversationId: String, onUpdate: (List<ChatMessage>) -> Unit) {

        currentUser = Firebase.auth.currentUser ?: return

        db.collection("conversation")
            .document(conversationId)
            .collection("messages")
            .orderBy("timeStamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("!!!", e.message.toString())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val chatMessage = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ChatMessage::class.java)
                    }
                    Log.d("!!!", "Messages snapshot -- $chatMessage")
                    onUpdate(chatMessage)
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

        val chatMessage = ChatMessage(
            senderId = currentUser.uid,
            receiverId = receiverId,
            messages = chatText,
            timeStamp = System.currentTimeMillis()
        )

        // Update or create conversation metadata
        db.collection("conversation")
            .document(conversationId)
            .set(
                mapOf(
                    "users" to listOf(currentUser.uid, receiverId),
                    "lastMessage" to chatText,
                    "lastUpdated" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )

        // Add the message to the conversation
        db.collection("conversation")
            .document(conversationId)
            .collection("messages")
            .add(chatMessage)
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

    fun createConversationId(user2Id: String): String {
        currentUser = Firebase.auth.currentUser ?: return ""
        val user1Id: String = currentUser.uid
        return listOf(user1Id, user2Id).sorted().joinToString("_")
    }

}