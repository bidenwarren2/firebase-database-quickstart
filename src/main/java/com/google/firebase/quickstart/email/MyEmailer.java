/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.quickstart.email;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.quickstart.model.Post;
import com.google.firebase.quickstart.model.User;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to send email notifications from the server.
 */
public class MyEmailer {

    /*
     * send email to user notifying them that one of their posts got a new star
     */
    public static void sendNotificationEmail(String emailAddr, String uid, String postId) {
        try {
            String smtpServer = System.getenv("SPARKPOST_SMTP_HOST");
            if (smtpServer != null) {
                System.out.println("sendNotificationEmail: " + emailAddr);
                Integer smtpPort = Integer.valueOf(System.getenv("SPARKPOST_SMTP_PORT"));
                String smtpUsername = System.getenv("SPARKPOST_SMTP_USERNAME");
                String smtpPassword = System.getenv("SPARKPOST_SMTP_PASSWORD");
                String domain = System.getenv("SPARKPOST_SANDBOX_DOMAIN");

                Email email = new SimpleEmail();
                email.setHostName(smtpServer);
                email.setSmtpPort(smtpPort);
                email.setAuthenticator(new DefaultAuthenticator(smtpUsername, smtpPassword));
                email.setFrom("test@" + domain);
                email.setSubject("New post!");
                email.setMsg(
                    "This is a notfication from your Heroku app. There was a new post from Firebase.");
                email.addTo(emailAddr);
                email.send();

                // Save the date of the last notification sent
                Map<String, Object> update = new HashMap<String, Object>();
                update.put("/posts/" + postId + "/lastNotificationTimestamp", ServerValue.TIMESTAMP);
                update.put("/user-posts/" + uid + "/" + postId + "/lastNotificationTimestamp", ServerValue.TIMESTAMP);

                FirebaseDatabase.getInstance().getReference().updateChildren(update);
            }
        } catch (Exception ex) {
            System.out.println("Whoops! Unable to send email.");
            ex.printStackTrace();
        }
    }

    public static void sendWeeklyEmail(Map<String,User> users, List<Post> topPosts) {
        // TODO(developer): send email to each user notifying them about the current top posts
        System.out.println("sendWeeklyEmail: MOCK IMPLEMENTATION");
        System.out.println("sendWeeklyEmail: there are " + users.size() + " total users.");
        System.out.println("sendWeeklyEmail: the top post is " + topPosts.get(0).title + " by " + topPosts.get(0).author);

        for (String userId : users.keySet()) {
            // Mark the last time the weekly email was sent out
            // [START basic_write]
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("lastSentWeeklyTimestamp");
            userRef.setValue(ServerValue.TIMESTAMP);
            // [END basic_write]
        }
    }
}
